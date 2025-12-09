/**
 * @deprecated Stupid class that is not used anymore.
 */
export default class PassApplyArg {
  static PASS_CLASS = new PassApplyArg("PASS_CLASS");
  static PASS_INSTANCE = new PassApplyArg("PASS_INSTANCE");
  static FUNCTION = new PassApplyArg("FUNCTION");
  static ARRAY_ARG = new PassApplyArg("ARRAY_ARG", true);
  static OBJECT_ARG = new PassApplyArg("OBJECT_ARG", true);

  private _name: string;
  private _isArg: boolean;

  constructor(name: string, isArg: boolean = false) {
    this._name = name;
    this._isArg = isArg !== undefined ? isArg : false;
  }

  get name(): string {
    return this._name;
  }

  get isArg(): boolean {
    return this._isArg;
  }

  toString(): string {
    return this._name;
  }
}
