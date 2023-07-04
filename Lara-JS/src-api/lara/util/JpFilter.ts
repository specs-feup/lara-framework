import StringSet from "./StringSet.js";
import { println } from "../../core/output.js";
import Check from "../Check.js"


/**
 * Filters join points according to the given rules.
 *
 * @param rules Object where each key represents the name of a join point attribute, and the value the pattern that we will use to match against the attribute.
 * The pattern can be a string (exact match), a regex or a function that receives the attribute and returns a boolean.
 */
class JpFilter{
    attributes: any;
    patterns: any;
    
    constructor(rules:any){
        this.attributes = [];
        this.patterns = [];
        
        for(let key in rules) {	
            const attribute = undefined;
            const rxPrefix: string = "rx_";
		    if(key.startsWith(rxPrefix)) {
			    println("JpFilter: using prefix 'rx_' to identify regexes is deprecated, just use a regex as value (e.g., /a/)"); 
                this.attributes.push(key.substring(rxPrefix.length));
                this.patterns.push(new RegExp(rules[key]));
            } 
            else {
                this.attributes.push(key);
                this.patterns.push(rules[key]);
            }
        }
    };

    /**
     * Filters an array of join points.
     
    * @return an array of the join points that pass the filter
    */
    filter($jps: unknown) {
        return this._filterAnd($jps);
    }

    _filterAnd($jps:any){
        
        let $filteredJps = [];
        // For each join points, check if it passes all the filters
        for(var $jp of $jps) {
            var passesFilters = true;
            for(var index = 0; index <  this.attributes.length; index++) {
                var matches = this._match($jp, index);
                if(!matches) {
                    passesFilters = false;
                }
            }
            if(passesFilters) {
                $filteredJps.push($jp);
            }
        }
        return $filteredJps;
    }

    _filterOr($jps:any) {

	    const seenNodes:StringSet = new StringSet();
        const $filteredJps: any = [];
	
	    // For each join points, check if it passes all the filters
        return $filteredJps;
    }


    _match($jp: any, i: number) {
	    const attributeValue = $jp[this.attributes[i]];
        let check = new Check();

	    if(check.isUndefined(attributeValue)) {
            return false;
        }

        let testString: string = "" + attributeValue;
        const pattern = this.patterns[i];

        // Regex
        if(pattern instanceof RegExp) {
            return attributeValue.match(pattern);
        } 

        // Function
        else if(typeof pattern === 'function') {
            return pattern(attributeValue);
        }

        else {
            return attributeValue === pattern;
        }
    }
}