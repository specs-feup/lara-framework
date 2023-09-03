/**
 * @class
 */
var Debug = {};

/**
 * Collects the properties of an object that are of type function.
 * Taken from here: https://stackoverflow.com/questions/5842654/how-to-get-an-objects-methods
 * 
 * @param obj - An object 
 */
Debug.getFunctions = function(obj)
{
    var res = [];
    for(var m in obj) {
        if(typeof obj[m] == "function") {
            res.push(m);
        }
    }
    return res;
}

/**
 * Collects the properties of an object that are of type object.
 */
Debug.getObjects = function(obj)
{
    var res = [];
    for(var m in obj) {
		 if(typeof obj[m] == "object") {
            res.push(m);
        }
    }
    return res;
}
