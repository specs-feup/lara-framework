/**
 * Base class for all LARA join points.
 *
 * @class
 */

class JoinPoint {

	constructor(astNode) {
		this.astNode = astNode;
	}

	instanceOf(joinPointType) {
		return joinPointType === 'joinpoint';
		// return joinPointType === this.joinPointType;
	}

	/**
	 * @return an Iterator with the types of this join point.
	 */
	types(joinPointType) {
		return ['joinpoint'].values();
	}

	sameTypeAs(joinPoint) {
		if (typeof joinPoint !== typeof this)
			return false;

		if (joinPoint.joinPointType !== this.joinPointType)
			return false;

		return true;
	}

	toString() {
		return "CommonJp<" + this.joinPointType + ">";
	}

	_descendantsPrivate(accum) {
		for (child of this.children) {
			accum.push(child);
			child._descendantsPrivate(accum);
		}
	}

	*descendantsIt() {
		for (child of this.children) {
			yield child;

			yield* child.descendantsIt();
		}
	}

	ancestor(type) {

		var ancestor = this.parent;

		while (ancestor !== undefined && !ancestor.instanceOf(type)) {
			ancestor = ancestor.parent;
		}

		return ancestor;
	}

	hasAncestor(type) {

		var ancestor = this.parent;

		while (ancestor !== undefined && !ancestor.instanceOf(type)) {
			ancestor = ancestor.parent;
		}

		return ancestor === undefined ? false : true;
	}

	equals(joinPoint) {
		throw this.constructor.name + '.equals not implemented';
	}

	

	get joinPointType() { return 'joinpoint'; }

	get parent() { 
			return (new JoinPoints()).getParent(this);
		}

	get _children() {
			//return (new JoinPoints()).getChildren(this); 
			return JoinPoints.getInstance().getChildren(this);
		}

	get children() {
			return this._children;
		}

	get _descendants() {
			return JoinPoints.getInstance().getDescendants(this);
			/*
					accum = [];

					this._descendantsPrivate(accum);
			*/	
			/*
					for(child of this.children){
						accum.push(child);
						for(var desc of child.descendants) {
							accum.push(desc);
						}	    	
						//accum = accum.concat(child.descendants);
					}
			*/
			/*	    
					return accum;
			*/		
		}

	get descendants() {
			return this._descendants;
		}

	get hasChildren() {
			return this.children && this.children.length > 0; 
		}

	get endLine() { throw this.constructor.name + '.endLine not implemented'; }

	get code() { throw this.constructor.name + '.code not implemented'; }

	get line() { throw this.constructor.name + '.line not implemented'; }

	get astId() { throw this.constructor.name + '.astId not implemented'; }
	
	// back compat
	static call(obj, astNode) {
		Object.assign(obj, new JoinPoint(astNode));
	}

}

// module.exports = JoinPoint;

// let point = new JoinPoint("arg");
// console.log(point)
// console.log(point.toString())
