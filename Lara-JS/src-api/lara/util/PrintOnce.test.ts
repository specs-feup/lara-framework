import PrintOnce from "./PrintOnce.js"

describe("PrintOnce", () => {
  describe("message", () => {
    it("prints the string it receives ignoring duplicates", () => {
      const originalConsoleLog = console.log;
      const logs: string[] = [];

      console.log = (...args: string[]) => {
        logs.push(args.join(" "));
      };

      PrintOnce.message("a");
      PrintOnce.message("b");
      PrintOnce.message("a");

      console.log = originalConsoleLog;

      expect(logs).toEqual(["a", "b"]);
    });
  });
});

  