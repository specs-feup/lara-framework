/** Select action for DSE
 The chain available is:
	file (attrs: name)
	  \_function (attrs: name)
*/

function getRoot(){ return __weaver.getRoot();};

//function select(weaver, joinpointUtils, jpChain, aliasChain, filterChain, workspace){
	//case for the first element
	

	/*
	var fileNameFilter = ".*\\.c";
	var functionNameFilter = "";
	var jpClass = args[0];
	var opFilter = args[1];
	var pos = 0;
	if(jpClass.equals('file')){
		if(!opFilter.equals('.*')){
			var attr = args[2];
			if(attr.equals('name')){
				if(!opFilter.equals("EQ") || !opFilter.equals("MATCH")){	
					fileNameFilter = args[3];
					pos = 4;
				}else{
					throw 'For attribute '+attr+' in joinpoint '+jpClass+ ' can only use the operators "==" and "~="';
				}
			}else{
				throw 'Attribute "'+attr+'" for joinpoint "'+jpClass+'" does not exist!';
			}
		}else{
			pos = 2;
		}
	}else {
		throw 'Join point selection is wrong!\n\t Started with "'+jpClass+'"';
	}
	
	if(pos+1 < args.length){
		var jpClass = args[pos];
		var opFilter = args[pos+1];
		if(jpClass.equals('function')){
			if(!opFilter.equals('.*')){
				var attr = args[pos+2];
				if(attr.equals('name')){
					if(!opFilter.equals("EQ") || !opFilter.equals("MATCH")){	
						functionNameFilter = args[pos+3];
						pos = pos+4;
					}else throw 'For attribute '+attr+' in joinpoint '+jpClass+ ' can only use the operators "==" and "~="';
				}else throw 'Attribute "'+attr+'" for joinpoint "'+jpClass+'" does not efilexist!';
			}else{
				
				functionNameFilter = '.*';
				pos = pos+2;
			}
		}else throw 'Join point selection is wrong!\n\t In "'+jpClass+'"';
	}
	println("SELECTING FROM: "+workspace);
	var script = ""+((new org.reflect.weaving.Report()).getFilesAndFunctions(workspace, fileNameFilter, functionNameFilter));
	
	return eval("var __temp__ = "+script+"; __temp__");
*/
//}
