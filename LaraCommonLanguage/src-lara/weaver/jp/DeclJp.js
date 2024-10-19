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
		Object.assign(obj, new DeclJp(astNode));
	}

}

// module.exports = DeclJp;