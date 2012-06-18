

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.l3eta.tt.Bot;

public class Builder {
	private final static char fs = File.separatorChar;
	private Map<String, Boolean> settings;
	private List<File> fileDump = new ArrayList<File>();
	private File root = new File("."), home = root, lib = root, temp = root, output;
	private String[] other;

	public static void main(String[] args) {

		new Builder(new String[] { "." + fs, "Turntable-" + Bot.version });
	}

	private static String[] slice(String[] r, int index) {
		String[] s = new String[r.length - index];
		for (int i = index; i < r.length; i++) {
			s[i - index] = r[i];
		}
		return s;
	}

	public Builder(String[] args) {
		settings = new HashMap<String, Boolean>();
		settings.put("allow.root.files", false);
		settings.put("allow.root.dirs", true);
		root = new File(args[0]);
		home = new File(System.getProperty("user.dir"));
		if (home.toString().endsWith(fs + "bin")) {
			root = home;
			home = home.getParentFile();
		}

		lib = new File(home, "lib");
		output = new File(home, args[1] + ".jar");
		temp = new File(home, "temp");
		if (args.length > 2) {
			other = slice(args, 3);
		}
		if (!temp.exists())
			temp.mkdirs();
		extractLibs();
	}

	public void extractLibs() {
		try {
			for (File l : lib.listFiles()) {
				byte[] buffer = new byte[1024];
				ZipInputStream in = new ZipInputStream(new FileInputStream(l));
				FileOutputStream output = null;
				int len;
				ZipEntry x;
				while ((x = in.getNextEntry()) != null) {
					File file = new File(temp, x.getName());
					if (x.isDirectory()) {
						file.mkdir();
					} else {
						file.createNewFile();
						output = new FileOutputStream(file);
						while ((len = in.read(buffer)) > 0) {
							output.write(buffer, 0, len);
						}
						output.close();
					}
				}
				in.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		sniffFiles();
	}

	public void sniffFiles() {
		File[] dirs = { root, temp };
		for (File file : dirs)
			dumpFiles(file, true);
		if (other != null && other.length != 0) {
			for (String o : other) {
				dumpFiles(new File(o), o.endsWith("*"));
			}
		}
		makeZip();
	}

	public FilenameFilter builderFile() {
		return new FilenameFilter() {
			private String[] exts = { ".class", ".png" };

			public boolean accept(File dir, String name) {				
				File f = new File(dir, name);				
				if (dir == root) {	
					if (f.toString().indexOf("_ignore") != -1)
						return false;
					if (f.isDirectory() && settings.get("allow.root.dirs")) {
						return true;
					} else if (f.isFile() && settings.get("allow.root.files")) {
						return check(name);
					}
				} else {
					if (f.isDirectory())
						return true;
					else
						return check(name);
				}
				return false;
			}

			public boolean check(String name) {
				for (String ext : exts) {
					if (name.endsWith(ext))
						return true;
				}
				return false;
			}
		};
	}

	public void dumpFiles(File dir, boolean recursive) {
		for (File file : dir.listFiles(builderFile())) {
			if (file.isDirectory() && recursive)
				dumpFiles(file, recursive);
			else
				fileDump.add(file);
		}
	}

	public void makeZip() {
		try {
			byte[] buffer = new byte[1024];
			JarOutputStream out = new JarOutputStream(new FileOutputStream(output));
			for (File file : fileDump.toArray(new File[0])) {
				FileInputStream in = new FileInputStream(file);
				out.putNextEntry(new ZipEntry(getFileToZip(file.getPath())));
				int len;
				while ((len = in.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			makeManifest(out);
			out.close();
			cleanUp(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeManifest(ZipOutputStream out) {
		try {
			String manifest = "Manifest-Version: 1.0\n";
			out.putNextEntry(new ZipEntry("META-INF" + fs + "MANIFEST.MF"));
			out.write(manifest.getBytes());
			out.closeEntry();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cleanUp(File dir) {
		try {
			dir.deleteOnExit();
			for (File file : dir.listFiles()) {
				if (file.isDirectory())
					cleanUp(file);
				else
					file.delete();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getFileToZip(String file) {
		file = file.replace(home.getPath(), "");
		file = file.replaceFirst("(bin|temp)" + (fs == '\\' ? "\\\\" : fs), "");
		file = (file.charAt(0) == fs ? file.substring(1) : file);
		System.out.println("Adding: " + file);
		return file;
	}

}