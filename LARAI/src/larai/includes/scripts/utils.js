


// Setter test
/*
Object.defineProperty(Object.prototype, 'laraSet', { 
	get: function() { println("LARASET ON "); return 10;} ,
	set: function(val) { delete Object.getPrototypeOf(this)['laraSet']; println("SETTER: " + this.laraSet);
	//this['laraSet'] = val;
	}
});
*/

/*
Object.defineProperty(Object.prototype, 'descendants', { 
	get: function() { println("descendants getter ON for prop " + (typeof this)); printlnObject(this); if(typeof this['descendantsValue'] !== undefined){return this['descendantsValue']; } } 
	//,set: function(val) {delete this['descendants']; println("descendants setter: " + this);}
	,set: function(val) {println("descendants setter: " + this);this['descendantsValue'] = val;}
	//, set: function(val) { delete Object.getPrototypeOf(this)['laraSet']; println("SETTER: " + this.laraSet);
	//this['laraSet'] = val;
	//}
});
*/




var toType = function(obj) {
  return ({}).toString.call(obj).match(/\s(([a-zA-Z]|\.)+)/)[1].toLowerCase();
}

Array.prototype.strictIndexOf = Array.prototype.indexOf;

Array.prototype.indexOf = function(obj){
	if(toType(obj).equals("javaobject")){
		if(obj.class.equals(java.lang.String))
			obj = ""+obj;
	}
	for(var i = 0; i < this.length; i++){
		if(toType(this[i]).equals("javaobject") &&
			this[i].class.equals(java.lang.String))
				this[i] = ""+this[i];
	}
		
	
	return this.strictIndexOf(obj);
};

Array.prototype.contains = function (obj){
	return this.indexOf(obj) != -1;
}

Object.prototype.contains = function (obj){
	return this.hasOwnProperty(obj);
}

Object.defineProperty(Array.prototype, "strictIndexOf", {enumerable: false});
Object.defineProperty(Array.prototype, "contains", {enumerable: false});

Object.defineProperty(Object.prototype, "contains", {enumerable: false});

String.prototype.firstCharToUpper = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

String.prototype.firstCharToLower = function() {
    return this.charAt(0).toLowerCase() + this.slice(1);
}

var getJavaClass = function(obj){
	
	return LARASystem.getJavaClass(obj);
}

var currentTime = function(){
	return __weaver.currentTime();
}

function printCurrentTime(){
	println("[time] "+currentTime()+"ms");
}

var isJoinPoint = function(obj){

	if(obj === null || obj === undefined) //since typeof null is "object"
		return false;
	
	var type = typeof obj;
	if(type === 'object'){
		
		return Weaver.isJoinPoint(obj);
	}
	return false;

	
}

	// for(var i = 0; i < this.length; i++){
		// var thisI = this[i];
		// if(thisI === obj)
			// return i;
		// if(toType(thisI).equals("javaobject")){
			// if(thisI.class.equals(java.lang.String))
				// if(obj.equals(""+thisI))
					// return i;
		// }
	// }



function lhs(instance) {

    return Object.keys(instance)[0];
}


function rhs(instance) {

    return instance[lhs(instance)];
}

function setRhs(instance, value) {

    return instance[lhs(instance)] = value;
}
