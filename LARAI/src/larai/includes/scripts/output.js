//Print a message
var outputStream = java.lang.System.out;
var errorStream = java.lang.System.err;


function setPrintStream(stream){
	outputStream = stream;
	errorStream = stream;
}

function printTo(message, stream){

	if(message === null || message === undefined){
		stream.print(message);
	}else{
		stream.print(message.toString());
	}
}

function printToln(message, stream){
	if(message === null || message === undefined){
		stream.println(message);
	}else{
		stream.println(message.toString());
	}
}

function print(message){
	if(arguments.length == 0){
		return;
	}
	printTo(message,outputStream);
	// if(message === null || message === undefined){
		// outputStream.print(message);
	// }
	// else{
		// outputStream.print(message.toString());
	// }

}

//Print a message and ends it with a new line
function println(message){
	if(arguments.length == 0){
		outputStream.println();
		return;
	}
	printToln(message,outputStream);
	// if(message === null || message === undefined){
		// outputStream.println(message);
	// }
	// else{
		// outputStream.println(message.toString());
	// }
}

//Print an error message
function error (message){
	if(arguments.length == 0){
		return;
	}
	printTo(message,errorStream);
	// if(message === null || message === undefined){
		// outputStream.println(message);
	// }
	// else{
		// errorStream.print(message.toString());
	// }
}
//Print an error message and ends it with a new line	
function errorln (message){
		if(arguments.length == 0){
		errorStream.println();
		return;
	}
	printToln(message,errorStream);
	// if(message === null)
		// message = "null"
	// errorStream.println(message.toString())
}
/*
function kill(message){
	if(message === null)
		message = "null";
	errorStream.println(message.toString());
	
}*/

var INDENT_CHAR = '   ';
var JAVA_OBJECT_ANNOTATION = '[@Java Object] ';
	
function printObject(obj, space){
	var str = object2string(obj, space);
	print(str);
}

function object2string(obj, space){
	if(space === undefined)
		space = '';
	if(obj === null) //since typeof null is "object"
		return space+'null';
	
	var type = typeof obj;
	if(type === 'object'){
		
		if(Java.isJavaObject(obj)){
//			print(space+obj.toString());
			return (space+JAVA_OBJECT_ANNOTATION+obj.toString());
		}else if(Array.isArray(obj)){
			var ar = (space+'[\n');
			var content = [];
			for(var prop in obj){
				var prop2String = object2string(obj[prop],space+INDENT_CHAR);
				content.push(prop2String);
			}
			ar+=content.join(",\n");
			ar+=('\n'+space+']');
			return ar;
		}else{
			var ob =(space+'{\n');
			var content = [];
			for(var prop in obj){
				var prop2String = (space+INDENT_CHAR+prop+":\n");
				prop2String += object2string(obj[prop],space+INDENT_CHAR+INDENT_CHAR);
				content.push(prop2String);
			}
			ob+=content.join(",\n");
			ob+=('\n'+space+'}');
			return ob;
		}
	}else if(type === "function"){
		var name = obj.name;// getFnName(obj);
		var params = getFnParamNames(obj);
		return (space+"function "+name+"("+params.join(",")+")");
	}
	
	else{
		return (space+obj);
	}
}

function getFnParamNames(fn){
    var fstr = fn.toString();
    return fstr.match(/\(.*?\)/)[0].replace(/[()]/gi,'').replace(/\s/gi,'').split(',');
}

function getFnName(fn){
    var fstr = fn.toString();
    return fstr.match(/function (.*)\)/)[0];
}
//	
//	switch(type){
//	
//	case object:
//		printObjectByType();
//	default: 
//		
//	}
	/*
	if(!(obj instanceof Object)){
		println(space+obj.toString());
        return;
	}*/
	
		/*
		print(space);
		if(Array.isArray(obj)){
			print("["+prop+"] ");
		}else print(prop+": ");
		var value = obj[prop];
		if( value == undefined || value == null){
			println(value);
			return;
		}
		
		if(Array.isArray(value)){
			printObject(value);
			return;
		}
		
		println("->"+typeof value);
		if(obj[prop] instanceof java.lang.Object){
			println(obj[prop].toString());
		}else
		if ( obj[prop].constructor==Object || obj[prop].constructor==Array){
			println("");
			printObject(obj[prop], space+"  ");
		} else {
			print(obj[prop].toString());
			println("");
		}
	}*/
//}

//Insert save to file functions (and others) here!
function writeFile(path, content) {
	var file = new java.io.File(path);
	SpecsIo.write(file,content);
}

function JSONtoFile(path, object) {
	
	var content = JSON.stringify(object);
	writeFile(path,content);
}
