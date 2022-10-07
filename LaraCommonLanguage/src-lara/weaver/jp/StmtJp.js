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
		// console.log(obj);console.log("\n")
		// console.log(StmtJp.prototype);console.log("\n")
		// console.log(Object.keys(new StmtJp(astNode)));console.log("\n")
		// console.log(obj.line);console.log("\n")
		// console.log(new StmtJp(astNode).line);console.log("\n")
		// console.log(Object.keys(new StmtJp(astNode)))
		Object.assign(obj, new StmtJp(astNode));
		// console.log(new StmtJp(astNode).line);console.log("\n")
		// console.log(new StmtJp(astNode).astNode);console.log("\n")
		// console.log(obj.line);console.log("\n")
		// console.log(obj.astNode);console.log("\n")
		// Object.setPrototypeOf(new StmtJp(astNode), obj);
		// (new StmtJp(astNode)).call(obj, astNode);
	}
}

// module.exports = StmtJp;
