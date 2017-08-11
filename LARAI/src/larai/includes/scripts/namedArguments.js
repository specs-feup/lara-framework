function  extractArgumentMap( functionCode ){
  // Extract the argument string.
  var argumentStringMatch = functionCode.match(
     new RegExp( "\\([^)]*\\)", "" ));

  // Now, extract the arguments.
  var argumentMap = argumentStringMatch[ 0 ].match(
     new RegExp( "[^\\s,()]+", "g" )
  );

  // Return the argument map.
  return( argumentMap );
}

function invoke (context, fn, namedArguments) {

  if (!("argumentMap" in fn)) {
     fn.argumentMap = extractArgumentMap(fn.toString());
  }

  var orderedArguments = [];

  for (var i = 0 ; i < fn.argumentMap.length ; i++){

     if (fn.argumentMap[ i ] in namedArguments) {
       orderedArguments.push(namedArguments[fn.argumentMap[i]]);
     } else {
       orderedArguments.push( undefined );
     }
  }

 if (this.constructor == invoke) {
    this.__proto__ = fn.prototype;
    context = this;
 }

  return(fn.apply(context, orderedArguments));
 
}