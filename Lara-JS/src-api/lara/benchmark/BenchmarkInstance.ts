import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import Io from "../Io.js";
import { JavaClasses } from "../util/JavaTypes.js";
import ProcessExecutor from "../util/ProcessExecutor.js";
import BenchmarkCompilationEngine from "./BenchmarkCompilationEngine.js";

/**
 * Represents a set of BenchmarkInstances.
 *
 * @param name - The name of this benchmark instance.
 */
export default abstract class BenchmarkInstance {
  private name: string;
  private hasLoaded: boolean = false;
  private hasCompiled: boolean = false;
  private currentExecutor = new ProcessExecutor();

  private currentExecutable: JavaClasses.File | undefined = undefined;
  private compilationEngine = this.compilationEngineProvider(this.getName());

  constructor(name: string) {
    this.name = name;
  }

  protected abstract compilationEngineProvider(
    name: string
  ): BenchmarkCompilationEngine;

  setCompilationEngine<T extends BenchmarkCompilationEngine>(
    compilationEngineProvider: new (name: string) => T
  ) {
    // Update current CMaker
    this.compilationEngine = new compilationEngineProvider(this.getName());
  }

  /**
   * @returns The name of this BenchmarkInstance.
   */
  getName(): string {
    return this.name;
  }

  /**
   * @returns The base folder for all benchmark instances. Currently is a folder 'laraBenchmarks' inside the working directory.
   */
  getBaseFolder(): JavaClasses.File {
    return Io.mkdir("laraBenchmarks");
  }

  /**
   * @returns An available CMaker that can be used to run compiled the program (implementations may vary). If used, can be used to configure the compilation.
   */
  getCompilationEngine() {
    return this.compilationEngine;
  }

  /**
   * @returns The executor that will be used to run the compiled program, can be used to configure the execution.
   */
  getExecutor(): ProcessExecutor {
    return this.currentExecutor;
  }

  /**
   * Saves the current AST and loads this benchmark into the AST.
   */
  load() {
    // Check if already loaded
    if (this.hasLoaded) {
      return;
    }

    console.log(`Parsing ${this.getName()}...`);
    this._loadPrivate();

    // Mark as loaded
    this.hasLoaded = true;
  }

  /**
   * Restores the AST previous to load().
   */
  close() {
    if (!this.hasLoaded) {
      console.log(
        `BenchmarkInstance.close(): Benchmark ${this.getName()} has not been loaded yet`
      );
      return;
    }

    this._closePrivate();

    this.hasLoaded = false;
    this.hasCompiled = false;

    this.currentExecutable = undefined;
    this.compilationEngine = this.compilationEngineProvider(this.getName());
    this.currentExecutor = new ProcessExecutor();
  }

  /**
   * Compiles the current version of the benchmark that is in the AST. Requires calling .load() first.
   */
  compile(): JavaClasses.File | undefined {
    // Check if already loaded
    if (!this.hasLoaded) {
      console.log(
        `BenchmarkInstance.compile(): Benchmark ${this.getName()} has not been loaded yet`
      );
      return;
    }

    console.log(`Compiling ${this.getName()}...`);
    const result = this._compilePrivate();

    // Mark as loaded
    this.hasCompiled = true;

    return result;
  }

  /**
   * Executes the current version of the benchmark. Requires calling .compile() first.
   *
   * @returns the ProcessExecutor used to execute this instance
   */
  execute(): ProcessExecutor {
    // Check if already compiled
    if (!this.hasCompiled) {
      this.compile();
    }

    if (this.currentExecutable === undefined) {
      throw "BenchmarkInstance._executePrivate(): no executable currently defined";
    }

    console.log(`Executing ${this.getName()}...`);

    this.currentExecutor.execute(
      // eslint-disable-next-line @typescript-eslint/no-unsafe-call
      this.currentExecutable.getAbsolutePath() as string
    );

    return this.currentExecutor;
  }

  setExecutable(executable: JavaClasses.File) {
    this.currentExecutable = executable;
  }

  /**
   * Test the current instance.
   *
   * @param worker - Function with no parameters that will be called after loading the benchmark code as AST.
   * @param executeCode - If true, executes the code after worker is applied.
   * @param outputProcessor - If execution is enabled, will be called after execution with the corresponding ProcessExecutor.
   *
   * @returns True, if finished without problems
   */
  test(
    worker: (instance: BenchmarkInstance) => boolean = (
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      _instance: BenchmarkInstance
    ) => {
      return true;
    },
    executeCode: boolean = false,
    outputProcessor: (executor: ProcessExecutor) => void = (
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      _executor: ProcessExecutor
    ) => {}
  ): boolean {
    // Load benchmark to AST
    this.load();

    // ... transformations and analysis
    if (worker(this) == false) {
      return false;
    }

    // Execute benchmark
    if (executeCode) {
      const processExecutor = this.execute();

      if (processExecutor.getReturnValue() !== 0) {
        console.log("Problems while executing " + this.getName());
        return false;
      }

      outputProcessor(processExecutor);
    }

    return true;
  }

  /*** FUNCTIONS TO IMPLEMENT ***/

  protected abstract _loadPrivate(): void;

  protected abstract _closePrivate(): void;

  protected abstract _compilePrivate(): JavaClasses.File;

  /**
   * @returns Point in the code representing the execution of the benchmark kernel, around which metrics should be measured.
   */
  abstract getKernel(): LaraJoinPoint;
}
