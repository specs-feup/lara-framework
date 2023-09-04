export default abstract class Tool {
  toolName: string;
  disableWeaving: boolean;

  constructor(toolName: string, disableWeaving: boolean = false) {
    this.toolName = toolName;
    this.disableWeaving = disableWeaving;
  }
}
