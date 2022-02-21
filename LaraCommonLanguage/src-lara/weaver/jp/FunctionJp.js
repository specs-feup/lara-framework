// laraImport("weaver.jp.DeclJp");
// import DeclJp from "./DeclJp";
const DeclJp = require("./DeclJp");

/**
 * 
 *
 * @class
 */
class FunctionJp extends DeclJp {

	constructor(astNode) {
		// Parent constructor
		super(astNode);

		// JoinPoint Types
		this._JP_TYPES = new Set();
		_lara_dummy_ = this._JP_TYPES.add('function');
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
	    
		return (new DeclJp(this.astNode)).instanceOf(joinPointType);
		*/
	}

	/**
	 * @return an Iterator with the types of this join point.
	 */
	types(joinPointType) {
		return this._JP_TYPES.values();
	} 

	get joinPointType() { return 'function'; }

	get id() { throw this.constructor.name + '.id not implemented'; }

	get name() { throw this.constructor.name + '.name not implemented'; }

	get signature() { throw this.constructor.name + '.signature not implemented'; }

	get returnType() { throw this.constructor.name + '.returnType not implemented'; }

	get params() { throw this.constructor.name + '.params not implemented'; }

	get stmts() { throw this.constructor.name + '.stmts not implemented'; }

	get hasBody() { throw this.constructor.name + '.hasBody not implemented'; }

	// 
	// get LEL() { return "UPS"; }

}

module.exports = FunctionJp;
