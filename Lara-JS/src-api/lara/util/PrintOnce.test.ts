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


      console.log("AA")       // Just to test if console.log is back to normal remove it after
      console.log("bb")


      expect(logs).toEqual(["a", "b", "a"]);
    });
  });
});

  