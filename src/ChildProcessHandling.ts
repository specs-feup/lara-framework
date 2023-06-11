import Debug from "debug";
import { fork, spawn } from "child_process";

const debug = Debug("ChildProcessHandling");

export const activeChildProcesses: Record<
  number,
  ReturnType<typeof fork> | ReturnType<typeof spawn>
> = {};

export function addActiveChildProcess(
  child: ReturnType<typeof fork> | ReturnType<typeof spawn>
): void {
  if (child.pid === undefined) {
    throw new Error("Child process doesn't have a pid");
  }
  const pid = child.pid;

  activeChildProcesses[pid] = child;

  // Listen for the 'exit' event of the child process
  child.on("exit", () => {
    delete activeChildProcesses[pid];
  });
}

export async function handleExit(exitProcess = true): Promise<void> {
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
  await Promise.all(closingChildren);
  debug("Closed all child processes");

  if (exitProcess) {
    process.exit();
  }
}

// Listen for termination signals
process.on("SIGINT", handleExit);
process.on("SIGTERM", handleExit);
process.on("SIGQUIT", handleExit);
process.on("uncaughtException", handleExit);
process.on("unhandledRejection", handleExit);
