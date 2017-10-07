/**
 * Copyright 2017 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.tools.lara.logging;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.logging.SpecsLogger;

public class LaraLog extends SpecsLogger {

    private static final String LARAI_TAG = buildLoggerName(LaraLog.class);
    private static final Lazy<LaraLog> LOGGER = buildLazy(LaraLog::new);
    private static boolean debug;
    // private static String default_debug = "false";
    private static String default_debug = "true";

    static {
        String laraLog = System.getProperty("LARA_LOG", default_debug);
        LaraLog.setDebug(Boolean.parseBoolean(laraLog));
    }

    public static LaraLog logger() {
        return LOGGER.get();
    }

    private LaraLog() {
        super(LARAI_TAG);
    }

    public static void info(String message) {
        logger().msgInfo(message);
    }

    // public static void warning(Pragma pragma, String message) {
    // warning(message + " (" + pragma.getLocation() + ")");
    // // LoggingUtils.msgInfo("[Warning] " + message + " (" + pragma.getLocation() + ")");
    // }

    public static void warning(String message) {

        SpecsLogs.msgInfo("[Warning] " + message);
    }

    public static void debug(String message) {
        if (isDebug()) {
            SpecsLogs.msgInfo("[DEBUG] " + message);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        LaraLog.debug = debug;
        // if (debug == true) {
        // KadabraLog.debug("Debug mode is active, to deativate use " + KadabraLog.class.getCanonicalName()
        // + ".seDebug(false)");
        // }
    }

}
