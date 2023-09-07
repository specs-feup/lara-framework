/**
 * Represents a set of BenchmarkInstances.
 *
 * @param name - The name of this benchmark set.
 *
 * @deprecated Use javascript's builtin Set instead to build a set of BenchmarkInstances (e.g. new Set<BenchmarkInstance>()).
 */
export default class BenchmarkSet {
    _name;
    compilationEngineProvider = undefined;
    constructor(name) {
        this._name = name;
    }
    getName() {
        return this._name;
    }
    setCompilationEngine(compilationEngineProviderFunction) {
        // Set provider
        this.compilationEngineProvider = compilationEngineProviderFunction;
    }
    /**
     * Generator function that automatically handles loading/closing BenchmarkInstances.
     */
    *[Symbol.iterator]() {
        const benchmarks = this.getInstances();
        for (const benchmark of benchmarks) {
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
     * @returns An array of BenchmarkInstance.
     */
    getInstances() {
        const instances = this._getInstancesPrivate();
        // If custom CMaker provider, set all instances
        if (this.compilationEngineProvider !== undefined) {
            for (const instance of instances) {
                instance.setCompilationEngine(this.compilationEngineProvider);
            }
        }
        return instances;
    }
    /**
     * Test the current benchmark set.
     *
     * @param worker - Function with no parameters that will be called after loading the bencharmk code as AST.
     * @param executeCode - If true, executes the code after worker is applied.
     * @param outputProcessor - If execution is enabled, will be called after execution with the corresponding ProcessExecutor.
     *
     * @returns An array with the names of benchmarks that finished with problemas, or an empty array if everything was fine.
     */
    test(worker = (
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _instance) => {
        return true;
    }, executeCode = false, outputProcessor = (
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _executor) => { }) {
        const benchesWithProblems = [];
        for (const bench of this) {
            const success = bench.test(worker, executeCode, outputProcessor);
            if (!success) {
                benchesWithProblems.push(bench.getName());
            }
        }
        return benchesWithProblems;
    }
}
//# sourceMappingURL=BenchmarkSet.js.map