laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

class DetectionAlgorithm {

	constructor(members, connections) {
		this.members= members;
		this.connections = connections;
		this.detections = [];
	}
	
	detect() {
		this.detections = [];
		
		var classTypes = Query.search("classType").get();
	
		this.recursive(classTypes, [], 0);
	
		return this.detections;
	}
	
	recursive(classTypes, candidates, depth) {
	
		if (depth < this.members.length) {
	
			for (var i = 0 ; i < classTypes.length ; i++) {
	
				var classType = classTypes[i];
				
				var isAbstraction = this.checkAbstraction(classType, this.members[depth][1]);
				if (!isAbstraction) continue;
				
				var isConnections = this.checkConnections(classType, candidates, depth);
				if (!isConnections) continue;
	
				var newClassTypes = classTypes.map(x => x);
				var newCandidates = candidates.map(x => x);
	
				newClassTypes.splice(i, 1);
				newCandidates.push(classType);
	
				this.recursive(newClassTypes, newCandidates, depth + 1);
			}
		}
		else {
			this.detections.push(candidates.map(x => x.name));
			// println(":: " + candidates.map(x => x.name));
		}
	}
	
	checkAbstraction(classType, abstractionLevel) {
		if (abstractionLevel == "Abstracted" && (classType.instanceOf("interface") || classType.instanceOf("class"))) return true;
		if (abstractionLevel == "Abstract" && classType.instanceOf("class")) return true;
		if (abstractionLevel == "Interface" && classType.instanceOf("interface")) return true;
		if (abstractionLevel == "Normal" && classType.instanceOf("class")) return true;
		if (abstractionLevel == "Any" && classType.instanceOf("classType")) return true;
		return false;
	}
	
	checkConnections(classType, candidates, depth) {
	
		for (var connection of this.connections) {
			var fromPatternConnectionId = connection[0].charCodeAt(0) - 65;
			var toPatternConnectionId = connection[1].charCodeAt(0) - 65;
			var fromClassObjectId = connection[0].charCodeAt(0) - 65;
			var toClassObjectId = connection[1].charCodeAt(0) - 65;
	
			if (fromPatternConnectionId == depth && toClassObjectId < depth) {
				var isRelation = this.checkRelation(connection[2], classType, candidates[toClassObjectId]);
				if (!isRelation) return false;
			}
			else if (toPatternConnectionId == depth && fromClassObjectId < depth) {
				var isRelation = this.checkRelation(connection[2], candidates[fromClassObjectId], classType);
				if (!isRelation) return false;
			}
		}
		
		return true;
	}
	
	checkRelation(relationType, fromObj, toObj) {
	
		// calls, creates, references, uses, inherits, has, relates
		if (relationType == "calls") {
			return this.checkRelationCalls(fromObj, toObj);
		}
		if (relationType == "references") {
			return this.checkRelationReferences(fromObj, toObj);
		}
		if (relationType == "inherits") {
			return this.checkRelationInherits(fromObj, toObj);
		}
		if (relationType == "has") {
			return this.checkRelationHas(fromObj, toObj);
		}
		if (relationType == "creates") {
			return this.checkRelationCreates(fromObj, toObj);
		}
		if (relationType == "uses") {
			return this.checkRelationUses(fromObj, toObj);
		}
	
		return true;
	}
	
	checkRelationCalls(fromObj, toObj) {
	
		var $calls = Query.searchFrom(fromObj, "call").get();
		
		for(var $call of $calls) {
	
			// filter out constructor calls
			if ($call.instanceOf("constructor")) continue;
			if ($call.method.isStatic) continue;
	
			if (toObj.name == $call.method.class.name) return true;
		}
	
		return false;
	}
	
	checkRelationCreates(fromObj, toObj) {
		
		var $constructorCalls = Query.searchFrom(fromObj, "constructorCall").get();
		
		for(var $constructorCall of $constructorCalls) {
			if (toObj.name == $constructorCall.method.class.name) return true;
		}
		
		return false;
	}
	
	checkRelationHas(fromObj, toObj) {
		
		var $fields = fromObj.fields;
		
		if ($fields == undefined) return false;
	
		for (var $field of $fields) {
			var classType = $field.type.classType;
			if (classType == null) continue;
			
			if (toObj.name == classType.name) return true;
		}
		
		return false;
	}
	
	checkRelationInherits(fromObj, toObj) {
	
		var superClasses = fromObj.allSuperClasses;
		var interfaces = fromObj.allInterfaces;
	
		var names = superClasses.map(c => c.name)
			.concat(interfaces.map(i => i.name));
	
		for (var name of names) {
			if (toObj.name == name) return true;
		}
		return false;
	}
	
	checkRelationReferences(fromObj, toObj) {
	
		// 
		for(var $method of fromObj.allMethods) {
			if ($method.instanceOf("constructor")) continue;
			if ($method.isStatic) continue;
			
			for (var $param of $method.params) {
	
				var classType = $param.type.classType;
				if (classType == null) continue;
	
				if (toObj.name == classType.name) return true;
			}
		}
	
		return false;
	}
	
	checkRelationUses(fromObj, toObj) {
	
		// 
		for(var $method of fromObj.allMethods) {
	
			// filter constructors, and static
			if ($method.instanceOf("constructor")) continue;
			if ($method.isStatic) continue;
			
			var classType = $method.returnType.classType;
			if (classType == null) continue;
			
			if (toObj.name == classType.name) return true;
		}
	
		return false;
	}
}
