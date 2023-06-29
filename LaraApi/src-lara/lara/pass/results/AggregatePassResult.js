laraImport("lara.pass.results.PassResult");

class AggregatePassResult extends PassResult {

  /**
   * Intermediate results collected from the pass
   * @type {PassResult}
   */
  _intermediateResults;

  /**
   * Errors thrown where the pass transformation failed
   * @type {PassTransformationError[]}
   */
  _transformationErrors;


  /**
   * @param {Pass} pass Pass which generated this result
   * @param {JoinPoint} $jp Join point related to this pass result
   */
  constructor(pass, $jp) {
    super(pass, $jp, {appliedPass: false, insertedLiteralCode: false});
    
    this._casesApplied = 0;
    this._intermediateResults = [];
    this._transformationErrors = [];
  }

  /**
   * @returns {boolean} True if the pass was applied successfully at least once
   */
  get appliedPass() {
    return this._casesApplied > 0;
  }

  /**
   * @returns {number} Total number of cases where this pass applied, independently of its success
   */
  get casesFound() {
    return this.casesFailed + this._intermediateResults.length;
  }

  /**
   * @returns {number} Number of cases where this pass was successfully applied
   */
  get casesApplied() {
    return this._casesApplied;
  }

  /**
   * @returns {number} Number of cases that resulted in an error
   */
  get casesFailed() {
    return this._transformationErrors.length;
  }

  /**
   * @returns {PassResult[]} List of results registered during the pass
   */
  get results() {
    return this._intermediateResults;
  }

  /**
   * @returns {PassTransformationError[]} List of errors registered during the pass
   */
  get errors() {
    return this._transformationErrors;
  }


  /**
   * Register a new error
   * @param {PassTransformationError} error 
   */
  pushError(error) {
    this._transformationErrors.push(Object.freeze(error));
  }

  /**
   * Register a new partial result
   * @param {PassResult} result PassResult from applying a predicate to a joinpoint
   */
  pushResult(result) {
    this._intermediateResults.push(Object.freeze(result));
    if (result.appliedPass) {
      this._appliedPass = true;
      this._casesApplied += 1;
    }
    this._insertedLiteralCode = this._insertedLiteralCode || result.insertedLiteralCode;
  }

  toString() {
    return `PassResult { name: ${this.name}; ` +
      `appliedPass: ${this.appliedPass}; ` +
      `insertedLiteralCode: ${this.insertedLiteralCode}; ` +
      `casesFound: ${this.casesFound}; ` +
      `casesApplied: ${this.casesApplied}; ` +
      `casesFailed: ${this.casesFailed} }`;
  }

}
