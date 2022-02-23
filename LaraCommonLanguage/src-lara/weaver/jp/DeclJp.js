laraImport("weaver.jp.StmtJp")
// import StmtJp from "./StmtJp";
// const StmtJp = require("./StmtJp");
// const StmtJp = laraImport("./StmtJp.js");

/**
 * 
 *
 * @class
 */
class DeclJp extends StmtJp {

	constructor(astNode) {
		super(astNode);
		
		// JoinPoint Types
		this._JP_TYPES = new Set();
		_lara_dummy_ = this._JP_TYPES.add('decl');
		_lara_dummy_ = this._JP_TYPES.add('stmt');
		_lara_dummy_ = this._JP_TYPES.add('joinpoint');
	}

	instanceOf(joinPointType) {
		return this._JP_TYPES.has(joinPointType);

		/*
		if(joinPointType === this.joinPointType) {
			return true;
		}
	    
		return (new StmtJp(this.astNode)).instanceOf(joinPointType);
		*/
	}

	/**
	 * @return an Iterator with the types of this join point.
	 */
	types(joinPointType) {
		return this._JP_TYPES.values();
	}
	
	get joinPointType() { return 'decl'; }
	
	// back compat
	static call(obj, astNode) {
		/*
		const classToObject = theClass => {
		  const originalClass = theClass || {}
		  const keys = Object.getOwnPropertyNames(Object.getPrototypeOf(originalClass))
		  return keys.reduce((classAsObj, key) => {
		    classAsObj[key] = originalClass[key]
		    return classAsObj
		  }, {})
		}
		*/
		new DeclJp(astNode);
		// classToObject(new DeclJp(astNode)).call(obj, astNode);
	}

}

// module.exports = DeclJp;