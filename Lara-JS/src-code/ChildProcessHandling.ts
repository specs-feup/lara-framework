import Debug from "debug";
import { ChildProcess } from "child_process";

const debug = Debug("ChildProcessHandling");

const activeChildProcesses: Record<number, ChildProcess> = {};

export function getActiveChildProcesses(): typeof activeChildProcesses {
  return activeChildProcesses;
}

export function addActiveChildProcess(child: ChildProcess) {
  if (child.pid === undefined) {
    throw new Error("Child process doesn't have a pid");
  }
  const pid = child.pid;

  activeChildProcesses[pid] = child;

  // Listen for the 'exit' event of the child process
  child.once("exit", () => {
    delete activeChildProcesses[pid];
  });
}

export function handleExit(exitProcess = true): void {
  const closingChildren: Promise<number | null>[] = [];

  for (const child of Object.values(activeChildProcesses)) {
    const promise: Promise<number | null> = new Promise((resolve) => {
      child.once("exit", (code) => {
        resolve(code);
      });
    });
    closingChildren.push(promise);

    child.kill();
  }
  Promise.all(closingChildren)
    .then(() => {
      debug("Closed all child processes");
      if (exitProcess) {
        process.exit();
      }
    })
    .catch((err: Error) => {
      debug(`Error closing child processes: ${err.toString()}`);
    });
}

export function listenForTerminationSignals(): void {
  process.on("SIGINT", handleExit);
  process.on("SIGTERM", handleExit);
  process.on("SIGQUIT", handleExit);
  process.on("uncaughtException", handleExit);
  process.on("unhandledRejection", handleExit);
}
