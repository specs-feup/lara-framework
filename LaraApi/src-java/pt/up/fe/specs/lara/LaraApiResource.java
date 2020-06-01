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

    // Benchmark
    BENCHMARK_SET("benchmark/BenchmarkSet.lara"),
    BENCHMARK_INSTANCE("benchmark/BenchmarkInstance.lara"),

    // CMake
    CMAKER("cmake/CMaker.lara"),
    CMAKER_SOURCES("cmake/CMakerSources.lara"),
    CMAKER_UTILS("cmake/CMakerUtils.lara"),

    // CMake/Compilers
    CMAKE_COMPILER("cmake/compilers/CMakeCompiler.lara"),
    GENERIC_CMAKE_COMPILER("cmake/compilers/GenericCMakeCompiler.lara"),

    // Code
    // ENERGY_BASE("code/EnergyBase.lara"),
    // LOGGER_BASE("code/LoggerBase.lara"),
    // TIMER_BASE("code/TimerBase.lara"),

    // DSE
    DSE_LOOP("dse/DseLoop.lara"),
    DSE_VALUES("dse/DseValues.lara"),
    DSE_VALUES_LIST("dse/DseValuesList.lara"),
    DSE_VALUES_SET("dse/DseValuesSet.lara"),
    DSE_VARIANT("dse/DseVariant.lara"),
    VARIABLE_VARIANT("dse/VariableVariant.lara"),

    // Iterators
    LARA_ITERATOR("iterators/LaraIterator.lara"),
    LINE_ITERATOR("iterators/LineIterator.lara"),

    // Metrics
    ENERGY_METRIC("metrics/EnergyMetric.lara"),
    EXECUTION_TIME_METRIC("metrics/ExecutionTimeMetric.lara"),
    FILE_SIZE_METRIC("metrics/FileSizeMetric.lara"),
    METRIC("metrics/Metric.lara"),
    METRIC_RESULT("metrics/MetricResult.lara"),

    // Mutation
    ITERATIVE_MUTATION("mutation/IterativeMutation.lara"),
    // ITERATIVE_MUTATOR("mutation/IterativeMutator.lara"),
    MUTATION("mutation/Mutation.lara"),
    MUTATOR("mutation/Mutator.lara"),

    // Units
    SI_MODIFIER("units/SiModifier.lara"),
    SI_UNIT("units/SiUnit.lara"),
    UNIT("units/Unit.lara"),
    TIME_MODIFIER("units/TimeModifier.lara"),
    TIME_UNIT("units/TimeUnit.lara"),
    ENERGY_UNIT("units/EnergyUnit.lara"),
    UNIT_MODIFIER("units/UnitModifier.lara"),
    UNIT_WITH_MODIFIER("units/UnitWithModifier.lara"),

    // Lara
    _JAVA_TYPES("_JavaTypes.lara"),
    CHECK("Check.lara"),
    COLLECTIONS("Collections.lara"),
    CSV("Csv.lara"),
    DEBUG("Debug.lara"),
    IO("Io.lara"),
    NUMBERS("Numbers.lara"),
    PLATFORMS("Platforms.lara"),
    STRINGS("Strings.lara"),
    SYSTEM("System.lara"),
    JAVA_INTEROP("JavaInterop.lara"),

    // Util
    ACCUMULATOR("util/Accumulator.lara"),
    CHECKPOINT("util/Checkpoint.lara"),
    CODE_INSERTER("util/CodeInserter.lara"),
    COMBINATION("util/Combinations.lara"),
    DATA_STORE("util/DataStore.lara"),
    ID_GENERATOR("util/IdGenerator.lara"),
    JP_FILTER("util/JpFilter.lara"),
    LINE_INSERTER("util/LineInserter.lara"),
    LOCAL_FOLDER("util/LocalFolder.lara"),
    PRINT_ONCE("util/PrintOnce.lara"),
    PROCESS_EXECUTOR("util/ProcessExecutor.lara"),
    REPLACER("util/Replacer.lara"),
    SEQUENTIAL_COMBINATIONS("util/SequentialCombinations.lara"),
    STRING_SET("util/StringSet.lara"),
    TIME_UNITS("util/TimeUnits.lara"),
    TUPLE_ID("util/TupleId.lara");

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
