package org.l3eta.tt.user;

import com.mongodb.BasicDBObject;

public class Profile {
	private String name, website, twitter, facebook, about, topartists,
			hangout;

	public Profile() {
		this.setName(null);
		this.setWebsite(null);
		this.setTwitter(null);
		this.setFacebook(null);
		this.setAbout(null);
		this.setTopartists(null);
		this.setHangout(null);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getTopartists() {
		return topartists;
	}

	public void setTopartists(String topartists) {
		this.topartists = topartists;
	}

	public String getHangout() {
		return hangout;
	}

	public void setHangout(String hangout) {
		this.hangout = hangout;
	}

	public BasicDBObject toBasic() {
		BasicDBObject basic = new BasicDBObject();
		basic.put("about", about);
		basic.put("topartists", topartists);
		basic.put("hangout", hangout);
		basic.put("facebook", facebook);
		basic.put("twitter", twitter);
		basic.put("website", website);
		basic.put("name", name);
		return basic;
	}
}
