import path from "node:path";
import { fileURLToPath } from "node:url";

export default function resolveWeaverScript(launcherUrl: string, weaverFileName?: string): string {
  if (weaverFileName !== undefined) {
    return fileURLToPath(import.meta.resolve(weaverFileName));
  }

  const launcherPath = fileURLToPath(launcherUrl);
  return path.join(path.dirname(launcherPath), `Weaver${path.extname(launcherPath)}`);
}
