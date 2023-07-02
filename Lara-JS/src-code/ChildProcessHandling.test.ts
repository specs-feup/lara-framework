import { jest } from "@jest/globals";
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

      const childProcessKillSpy1 = jest
        .spyOn(childProcess1, "kill")
        .mockClear();
      const childProcessOnceSpy1 = jest
        .spyOn(childProcess1, "once")
        .mockClear();
      const childProcessKillSpy2 = jest
        .spyOn(childProcess2, "kill")
        .mockClear();
      const childProcessOnceSpy2 = jest
        .spyOn(childProcess2, "once")
        .mockClear();

      handleExit(false);

      expect(childProcessKillSpy1).toHaveBeenCalledTimes(1);
      expect(childProcessOnceSpy1).toHaveBeenCalledTimes(1);
      expect(childProcessKillSpy2).toHaveBeenCalledTimes(1);
      expect(childProcessOnceSpy2).toHaveBeenCalledTimes(1);

      jest.restoreAllMocks();
    });
  });
});
