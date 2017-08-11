var Debug = {};

/**
 * Taken from here: https://stackoverflow.com/questions/5842654/how-to-get-an-objects-methods
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