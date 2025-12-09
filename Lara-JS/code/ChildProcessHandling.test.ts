import { describe, it, expect, spyOn, mock } from "bun:test";
import {
  addActiveChildProcess,
  handleExit,
  getActiveChildProcesses,
} from "./ChildProcessHandling.js";

describe("ChildProcessHandling", () => {
  describe("addActiveChildProcess", () => {
    it("should add the child process to the activeChildProcesses object", () => {
      const childProcess = {
        pid: 123,
        once: (code: string, callback: () => {}) => {},
      } as any;
      addActiveChildProcess(childProcess);
      expect(getActiveChildProcesses()[childProcess.pid]).toBe(childProcess);
    });
  });

  describe("handleExit", () => {
    it("should close all child processes", async () => {
      const childProcess1 = {
        pid: 123,
        kill: () => {},
        once: (code: string, callback: () => {}) => {
          callback();
        },
      } as any;
      const childProcess2 = {
        pid: 456,
        kill: () => {},
        once: (code: string, callback: () => {}) => {
          callback();
        },
      } as any;
      getActiveChildProcesses()[childProcess1.pid] = childProcess1;
      getActiveChildProcesses()[childProcess2.pid] = childProcess2;

      const childProcessKillSpy1 = spyOn(childProcess1, "kill");
      const childProcessOnceSpy1 = spyOn(childProcess1, "once");
      const childProcessKillSpy2 = spyOn(childProcess2, "kill");
      const childProcessOnceSpy2 = spyOn(childProcess2, "once");

      handleExit(false);

      expect(childProcessKillSpy1).toHaveBeenCalledTimes(1);
      expect(childProcessOnceSpy1).toHaveBeenCalledTimes(1);
      expect(childProcessKillSpy2).toHaveBeenCalledTimes(1);
      expect(childProcessOnceSpy2).toHaveBeenCalledTimes(1);

      childProcessKillSpy1.mockRestore();
      childProcessOnceSpy1.mockRestore();
      childProcessKillSpy2.mockRestore();
      childProcessOnceSpy2.mockRestore(); 
    });
  });
});
