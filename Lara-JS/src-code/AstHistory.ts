export class Waypoint {
  private valid: boolean = true;
  private historyStep: number;
  private history: History;

  constructor(history: History, historyStep: number) {
    this.history = history;
    this.historyStep = historyStep;
  }

  invalidate() {
    this.valid = false;
  }
}

export enum DeltaAction {
  "add",
  "remove",
  "update",
}

class Tree {}

export class Delta {
  protected type: DeltaAction;
  private undoFn: (ast: Tree) => void;
  private redoFn: (ast: Tree) => void;
  private ast: Tree;

  constructor(
    ast: Tree,
    actionType: DeltaAction,
    undo: (ast: Tree) => void,
    redo: (ast: Tree) => void
  ) {
    this.ast = ast;
    this.type = actionType;
    this.undoFn = undo;
    this.redoFn = redo;
  }

  undo() {
    this.undoFn(this.ast);
  }

  redo() {
    this.redoFn(this.ast);
  }
}

export class History {
  private deltaGroups: Delta[][] = [];
  private currentStep: number = 0;

  constructor(seed?: Delta[]) {
    if (seed) {
      this.deltaGroups = [seed];
      this.currentStep = 1;
    }
  }

  get waypoint(): Waypoint {
    return new Waypoint(this, this.currentStep);
  }

  addDeltaGroup(deltaGroup: Delta[]) {
    this.deltaGroups.length = this.currentStep;
    this.deltaGroups.push(deltaGroup);
    this.currentStep++;
  }

  undo(targetStep: number = this.currentStep - 1) {
    while (this.currentStep > targetStep && this.currentStep > 0) {
      this.deltaGroups[this.currentStep - 1].forEach((delta) => delta.undo());
      this.currentStep--;
    }
  }

  redo(targetStep: number = this.currentStep + 1) {
    while (
      this.currentStep < targetStep &&
      this.currentStep < this.deltaGroups.length
    ) {
      this.currentStep++;
      this.deltaGroups[this.currentStep - 1].forEach((delta) => delta.redo());
    }
  }
}
