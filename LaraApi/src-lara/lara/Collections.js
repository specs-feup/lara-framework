import lara._JavaTypes;
import lara.JavaInterop;


/**
 *  Utility methods related to Collections.
 *
 * @class
 */
var Collections = {};


/**
 * @param {Object[]|java.util.List} values - Values to sort in-place.
 *
 * @return the sorted collection
 */
Collections.sort = function(values) {
	


	// If array
	if(isArray(values)) {
		//println("IS ARRAY");	
		values.sort();
		return values;
	}
	
	
	// If Java List
	if(JavaInterop.isList(values)) {
		//println("IS LIST");
		_JavaTypes.getCollections().sort(values);
		return values;
	}

	
	throw "Expected either an array or a Java List: " + values;
	
}