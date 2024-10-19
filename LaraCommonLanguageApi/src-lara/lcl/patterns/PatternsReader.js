laraImport("lcl.LaraCommonLanguage");
laraImport("lara.Io");

/*
class Pattern {
	
	// var name 
	// var members
	// var connections
}
*/
class PatternsReader {

	constructor() {

	}
	
	static readPattern(patternFile) {
	
		if (!Io.isFile(patternFile)) return null;
		if (!patternFile.endsWith(".pattern")) return null;
	
		var pattern = {
			name: null,
			members: [],
			connections: [],
		};
	
		var lines = Io.readLines(patternFile);
	
		var phase = 1; // 1 for members, 2 for connections
	
		for(var i  = 0 ; i < lines.length ; i++) {
			var line = lines[i];
	
			if (i == 0) {
				var name = line;
				pattern.name = name;
			}
			else if (line.equals("End_Members")) {
				phase = 2;
			}
			else if (line.equals("End_Connections")) {
				break;
			}
			else if (phase == 1 && !line.equals("End_Members")) {
				var strs = line.split(' ');
	
				var member = [ strs[0], 
							strs[1], 
							line.substring(strs[0].length + strs[1].length + 2)];
	
				pattern.members.push(member);
				
			}
			else if (phase == 2 && !line.equals("End_Connections")) {
				var strs = line.split(' ');
	
				var connection = [ strs[0], strs[2], strs[1] ];
	
				pattern.connections.push(connection);
			}
			
		}
	
		return pattern;
	}
}
