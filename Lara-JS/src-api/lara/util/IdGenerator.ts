/**
 * @class
 */
class IdGenerator {
	idCounter:{[key: string]: number};

    constructor(){
        this.idCounter={};
    }

    next(key:string|undefined) {

	    if(key === undefined) {
		    key = "";
	    }

	    let currentId:number|undefined = this.idCounter[key];

	    if(currentId === undefined) {
		    currentId = 0;
	    }

	    this.idCounter[key] = currentId + 1;
        
	    return key + currentId;
    }
}
