/**
 * Copyright 2013 SuikaSoft.
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

package pt.up.fe.specs.lara;

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum LaraApiResource implements LaraResourceProvider {

    // CMake
    CMAKER("cmake/CMaker.lara"),
    CMAKER_SOURCES("cmake/CMakerSources.lara"),

    // Code
    ENERGY_BASE("code/EnergyBase.lara"),
    LOGGER_BASE("code/LoggerBase.lara"),
    TIMER_BASE("code/TimerBase.lara"),

    // DSE
    DSE_VALUES("dse/DseValues.lara"),
    DSE_VALUES_LIST("dse/DseValuesList.lara"),
    DSE_VALUES_SET("dse/DseValuesSet.lara"),

    // Metrics
    ENERGY_METRIC("metrics/EnergyMetric.lara"),
    EXECUTION_TIME_METRIC("metrics/ExecutionTimeMetric.lara"),
    FILE_SIZE_METRIC("metrics/FileSizeMetric.lara"),
    METRIC("metrics/Metric.lara"),
    METRIC_RESULT("metrics/MetricResult.lara"),

    // Units
    SI_MODIFIER("units/SiModifier.lara"),
    SI_UNIT("units/SiUnit.lara"),
    UNIT("units/Unit.lara"),
    TIME_MODIFIER("units/TimeModifier.lara"),
    TIME_UNIT("units/TimeUnit.lara"),
    UNIT_MODIFIER("units/UnitModifier.lara"),
    UNIT_WITH_MODIFIER("units/UnitWithModifier.lara"),

    // Lara
    CSV("Csv.lara"),
    DEBUG("Debug.lara"),
    IO("Io.lara"),
    JOIN_POINTS("JoinPoints.lara"),
    NUMBERS("Numbers.lara"),
    PLATFORMS("Platforms.lara"),
    STRINGS("Strings.lara"),
    SYSTEM("System.lara"),

    // Util
    ACCUMULATOR("util/Accumulator.lara"),
    CHECKPOINT("util/Checkpoint.lara"),
    DATA_STORE("util/DataStore.lara"),
    ID_GENERATOR("util/IdGenerator.lara"),
    LOCAL_FOLDER("util/LocalFolder.lara"),
    PRINT_ONCE("util/PrintOnce.lara"),
    PROCESS_EXECUTOR("util/ProcessExecutor.lara"),
    REPLACER("util/Replacer.lara"),
    SEQUENTIAL_COMBINATIONS("util/SequentialCombinations.lara"),
    STRING_SET("util/StringSet.lara"),
    TIME_UNITS("util/TimeUnits.lara");

    private final String resource;

    private static final String BASE_PACKAGE = "lara/";

    /**
     * @param resource
     */
    private LaraApiResource(String resource) {
        this.resource = BASE_PACKAGE + resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
