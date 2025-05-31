import { Delta, DeltaAction, History } from "./AstHistory.js";

describe("AstHistory", () => {
  describe("History Management", () => {
    it("should add a delta group", () => {
      const history = new History();

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);
      expect(history.step).toBe(1);
    });

    it("should undo", () => {
      const history = new History();

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.undo();
      expect(history.step).toBe(0);
    });

    it("should undo 2 steps", () => {
      const history = new History();

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.undo(0);
      expect(history.step).toBe(0);
    });

    it("should not overshoot undo", () => {
      const history = new History();

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.undo(-10);
      expect(history.step).toBe(0);
    });

    it("should redo", () => {
      const history = new History();

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.undo();
      history.undo();
      history.redo();
      expect(history.step).toBe(1);
    });

    it("should redo 2 steps", () => {
      const history = new History();

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.undo(0);
      history.redo(2);
      expect(history.step).toBe(2);
    });

    it("should not overshoot redo", () => {
      const history = new History();

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.addDeltaGroup([
        new Delta(
          DeltaAction.add,
          () => {},
          () => {}
        ),
      ]);

      history.undo(0);
      history.redo(10);
      expect(history.step).toBe(2);
    });
  });
});
