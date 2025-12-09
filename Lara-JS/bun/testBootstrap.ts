import { beforeAll, afterAll } from "bun:test";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { Weaver } from "../code/Weaver.js";
import WeaverConfiguration from "../code/WeaverConfiguration.js";

export function bootstrap(weaverConfig: WeaverConfiguration) {
    beforeAll(async () => {
        // Only setup Java environment if the JARs exist
        if (fs.existsSync(weaverConfig.jarPath)) {
            console.log(
                "[bun:preload] Setting up Java environment from:",
                weaverConfig.jarPath
            );

            // Initialize SpecsSystem for properly formatted prints
            try {
                // Setup the Weaver for tests that need it
                const weaverMessageFromLauncher = {
                    args: {
                        _: [],
                        $0: "",
                    },
                    config: weaverConfig,
                };

                await Weaver.setupWeaver(
                    weaverMessageFromLauncher.args,
                    weaverMessageFromLauncher.config
                );
                Weaver.start();
                console.log("[bun:preload] Weaver initialized successfully");
            } catch (e) {
                console.warn("[bun:preload] Failed to initialize Java/Weaver:", e);
            }
        } else {
            console.warn(
                `[bun:preload] ${weaverConfig.weaverPrettyName} JARs not found at:`,
                weaverConfig.jarPath
            );
            console.warn(
                `[bun:preload] Tests requiring Java will fail. Run 'gradle installDist' in ${weaverConfig.weaverName} to build the Java components.`
            );
        }
    });

    afterAll(async () => {
        // Clean up Java environment if needed
        try {
            Weaver.shutdown();
            console.log("[bun:preload] Weaver stopped successfully");
        } catch (e) {
            console.warn("[bun:preload] Failed to stop Weaver:", e);
        }
    });
}
