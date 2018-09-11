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

import pt.up.fe.specs.util.logging.StringLogger;

// public class LaraLog extends SpecsLogger {
public class LaraLog {

    // private static final String LARAI_TAG = buildLoggerName(LaraLog.class);
    // private static final Lazy<LaraLog> LOGGER = buildLazy(LaraLog::new);
    private static final StringLogger LOGGER = new StringLogger(LaraLog.class.getName());
    private static boolean debug;
    private static String default_debug = "false";
    // private static String default_debug = "true";

    static {
        String laraLog = System.getProperty("LARA_LOG", default_debug);
        LaraLog.setDebug(Boolean.parseBoolean(laraLog));
    }

    // public static LaraLog logger() {
    public static StringLogger logger() {
        // return LOGGER.get();
        return LOGGER;
    }

    // private LaraLog() {
    // super(LARAI_TAG);
    // }

    public static void info(String message) {
        // logger().msgInfo(message);
        logger().info(message);
    }

    // public static void warning(Pragma pragma, String message) {
    // warning(message + " (" + pragma.getLocation() + ")");
    // // LoggingUtils.msgInfo("[Warning] " + message + " (" + pragma.getLocation() + ")");
    // }

    public static void warning(String message) {
        // SpecsLogs.msgInfo("[Warning] " + message);
        logger().info("Warning", message);
    }

    public static void debug(String message) {
        if (isDebug()) {
            logger().debug(message);
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

    public static void printMemory(String message) {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        long usedMemory = heapSize - heapFreeSize;
        // long kbFactor = (long) Math.pow(1024, 1);
        long mbFactor = (long) Math.pow(1_048_576, 1);

        long heapSizeMb = (int) (heapSize / mbFactor);
        long currentSizeMb = (int) (usedMemory / mbFactor);
        debug(message + ": " + currentSizeMb + "/" + heapSizeMb + "Mb");
    }

}
