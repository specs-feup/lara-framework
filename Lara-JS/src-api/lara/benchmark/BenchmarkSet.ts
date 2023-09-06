import lara.benchmark.BenchmarkInstance;

/**
 * Represents a set of BenchmarkInstances.
 *
 * @param{String} setName - The name of this benchmark set.
 */
var BenchmarkSet = function(name) {
	this._name = name;
	
	this._cmakerProvider = undefined;
};

BenchmarkSet.prototype.getName = function() {
	return this._name;
}


BenchmarkSet.prototype.setCMakerProvider = function(cmakerProviderFunction) {
	// Set provider
	this._cmakerProvider = cmakerProviderFunction;
}


/**
 * Generator function that automatically handles loading/closing BenchmarkInstances.
 */
BenchmarkSet.prototype[Symbol.iterator] = function* () {
	var benchmarks = this.getInstances();
	for(var benchmark of benchmarks) {
		// Load
		benchmark.load();
		
		// Return
		yield benchmark;
		
		// Close
		benchmark.close();
	}
}

/**
 * Instances of benchmarks, according to the current configuration.
 * 
 * @return {BenchmarkInstance[]} an array of BenchmarkInstance.
 */
BenchmarkSet.prototype.getInstances = function() {
	var instances = this._getInstancesPrivate();
	
	// If custom CMaker provider, set all instances
	if(this._cmakerProvider !== undefined) {
		for(var instance of instances) {
			instance.setCMakerProvider(this._cmakerProvider);
		}
	}

	
	return instances;
}


/**
 * Test the current benchmark set.
 *
 * @param {function()} [worker = undefined] - Function with no parameters that will be called after loading the bencharmk code as AST.
 * @param {boolean} [executeCode = false] - If true, executes the code after worker is applied.
 * @param {function(lara.util.ProcessExecutor)} [outputProcessor = undefined] - If execution is enabled, will be called after execution with the corresponding ProcessExecutor.
 *
 * @return {String[]} an array with the names of benchmarks that finished with problemas, or an empty array if everything was fine.
 */
BenchmarkSet.prototype.test = function(worker, executeCode, outputProcessor) {		

	var benchWithProblems = [];
	
	for(var bench of this) {
	
		var success = bench.test(worker, executeCode, outputProcessor);
		
		if(!success) {
			benchWithProblems.push(bench.getName());
		}
	}


	return benchWithProblems;
}



/*** TO IMPLEMENT ***/

BenchmarkSet.prototype._getInstancesPrivate = function() {
	throw "BenchmarkSet._getInstancesPrivate not implemented for " + this.getName();
}
