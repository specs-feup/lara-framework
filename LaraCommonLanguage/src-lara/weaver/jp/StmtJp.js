laraImport("weaver.jp.JoinPoint")
// import JoinPoint from "./JoinPoint";
// const JoinPoint = require("./JoinPoint");
// const JoinPoint = load("./JoinPoint.js");

/**
 * 
 *
 * @class
 */
class StmtJp extends JoinPoint {

	constructor(astNode) {
		super(astNode);

		// JoinPoint Types
		this._JP_TYPES = new Set();
		_lara_dummy_ = this._JP_TYPES.add('stmt');
		_lara_dummy_ = this._JP_TYPES.add('joinpoint');
	}

	instanceOf(joinPointType) {
		return this._JP_TYPES.has(joinPointType);

		/*
		if(joinPointType === this.joinPointType) {
			return true;
		}
	    
		return (new JoinPoint(this.astNode)).instanceOf(joinPointType);
		*/
	}

	/**
	 * @return an Iterator with the types of this join point.
	 */
	types(joinPointType) {
		return this._JP_TYPES.values();
	}
	
	get joinPointType() { return 'stmt'; }
	
	// back compat
	static call(obj, astNode) {
		new StmtJp(astNode);
	}
}

// module.exports = StmtJp;
