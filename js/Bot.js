//Imports 
var Socket, bot, line;
var net = require('net');

net.createServer(function(socket) {
	socket.on('connect', function() {
		Socket = socket;
		console.log('Connection Connected.');
	});
	socket.on('data', function(data) {
		var msg = data.toString();
		if (msg[msg.length - 1] == '\n') {
			handleMessage(msg.substr(0, msg.length - 1));
		}
	});
	socket.on('end', function() {
		console.log('Connection Closed');
	});
}).listen(23, '127.0.0.1');

function start(data) {
	var info = new String(data).split(":");
	var Bot = require('./ttapi').Bot;
	bot = new Bot(info[0], info[1], info[2]);
	bot.on('roomChanged', function(data) {
		write(format(data));
	});

	bot.on('update_user', function(data) {
		write(format(data));
	});

	bot.on('speak', function(data) {
		write(format(data));
	});

	bot.on('newsong', function(data) {
		write(format(data));
	});

	bot.on('nosong', function(data) {
		write(format(data));
	});

	bot.on('update_votes', function(data) {
		write(format(data));
	});

	bot.on('snagged', function(data) {
		write(format(data));
	});

	bot.on('registered', function(data) {
		write(format(data));
	});

	bot.on('deregistered', function(data) {
		write(format(data));
	});

	bot.on('booted_user', function(data) {
		write(format(data));
	});

	bot.on('add_dj', function(data) {
		write(format(data));
	});

	bot.on('rem_dj', function(data) {
		write(format(data));
	});

	bot.on('new_moderator', function(data) {
		write(format(data));
	});

	bot.on('rem_moderator', function(data) {
		write(format(data));
	});
}

function write(data) {
	Socket.write(data + '\n');
}

function handleMessage(data) {
	line = new String(data);
	//console.log(line.toString());
	if (s('say:')) {
		r('say:');
		if (s('song:')) {
			r('song:');
			bot.speak(line.replace('(SNAG)', '♥'));
		} else {
			bot.speak(line);
		}
	} else if (s('vote:')) {
		bot.vote(line.replace('vote:', ''));
	} else if (s('dj:')) {
		r('dj:');
		if(s('dj:')) {
			bot.addDj();
		} else if(s('skip:')) {
			bot.stopSong();
		} else if(s('rdj:')) {
			bot.remDj();
		}
	} else if (s('playlist:')) {
		r('playlist:');
		if (s('add:')) {
			r('add:');
			bot.addSong(line);
		} else if(s('remove:')) {
			bot.playlistRemove();
		}
	} else if(s('mod:')) {
		r('mod:');
		if(s('rem:')) {
			r('rem:');
			bot.remModerator(line);
		} else if(s('add:')) {
			r('add:');
			bot.addModerator(line);
		} else if(s('boot:')) {
			r('boot:');
			var info = line.split('-');
			bot.bootUser(info[0], info[1]);
		}
	} else if (s('start:')) {
		r('start:');
		start(line);
	}
}

function r(str) {
	line = line.replace(str, '');
}

function s(str) {
	return line.substring(0, str.length) == str;
}

function format(o) {
	var parse = function(_o) {
		var a = [], t;
		for ( var p in _o) {
			if (_o.hasOwnProperty(p)) {
				t = _o[p];
				if (t && typeof t == "object") {
					a[a.length] = p + ":{ " + arguments.callee(t).join(", ") + "}";
				} else {
					if (typeof t == "string") {
						a[a.length] = [ p + ": \"" + t + "\"" ];
					} else {
						a[a.length] = [ p + ": " + t ];
					}
				}
			}
		}
		return a;
	};
	return "{" + parse(o).join(", ") + "}";
}
