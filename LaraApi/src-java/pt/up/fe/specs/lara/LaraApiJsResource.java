
/**
 * Copyright 2023 SPeCS.
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
 * This file has been automatically generated.
 * 
 * @author Joao Bispo, Luis Sousa
 *
 */
public enum LaraApiJsResource implements LaraResourceProvider {

    LARAJOINPOINT_JS("LaraJoinPoint.js"),
    OUTPUT_JS("core/output.js"),
    CORE_JS("core.js"),
    CHECK_JS("lara/Check.js"),
    COLLECTIONS_JS("lara/Collections.js"),
    CSV_JS("lara/Csv.js"),
    DEBUG_JS("lara/Debug.js"),
    IO_JS("lara/Io.js"),
    JAVAINTEROP_JS("lara/JavaInterop.js"),
    NUMBERS_JS("lara/Numbers.js"),
    PLATFORMS_JS("lara/Platforms.js"),
    STRINGS_JS("lara/Strings.js"),
    SYSTEM_JS("lara/System.js"),
    BENCHMARKCOMPILATIONENGINE_JS("lara/benchmark/BenchmarkCompilationEngine.js"),
    BENCHMARKINSTANCE_JS("lara/benchmark/BenchmarkInstance.js"),
    BENCHMARKSET_JS("lara/benchmark/BenchmarkSet.js"),
    ENERGYBASE_JS("lara/code/EnergyBase.js"),
    LOGGERBASE_JS("lara/code/LoggerBase.js"),
    TIMERBASE_JS("lara/code/TimerBase.js"),
    LARACORE_JS("lara/core/LaraCore.js"),
    DOTFORMATTER_JS("lara/graphs/DotFormatter.js"),
    EDGEDATA_JS("lara/graphs/EdgeData.js"),
    GRAPH_JS("lara/graphs/Graph.js"),
    GRAPHEDGE_JS("lara/graphs/GraphEdge.js"),
    GRAPHNODE_JS("lara/graphs/GraphNode.js"),
    GRAPHS_JS("lara/graphs/Graphs.js"),
    NODEDATA_JS("lara/graphs/NodeData.js"),
    LARAITERATOR_JS("lara/iterators/LaraIterator.js"),
    LINEITERATOR_JS("lara/iterators/LineIterator.js"),
    FILESIZEMETRIC_JS("lara/metrics/FileSizeMetric.js"),
    METRIC_JS("lara/metrics/Metric.js"),
    METRICRESULT_JS("lara/metrics/MetricResult.js"),
    ITERATIVEMUTATION_JS("lara/mutation/IterativeMutation.js"),
    ITERATIVEMUTATOR_JS("lara/mutation/IterativeMutator.js"),
    MUTATION_JS("lara/mutation/Mutation.js"),
    MUTATIONRESULT_JS("lara/mutation/MutationResult.js"),
    MUTATOR_JS("lara/mutation/Mutator.js"),
    ADAPTERPASS_JS("lara/pass/AdapterPass.js"),
    PASS_JS("lara/pass/Pass.js"),
    PASSTRANSFORMATIONERROR_JS("lara/pass/PassTransformationError.js"),
    SIMPLEPASS_JS("lara/pass/SimplePass.js"),
    PASSAPPLYARG_JS("lara/pass/composition/PassApplyArg.js"),
    PASSES_JS("lara/pass/composition/Passes.js"),
    AGGREGATEPASSRESULT_JS("lara/pass/results/AggregatePassResult.js"),
    PASSRESULT_JS("lara/pass/results/PassResult.js"),
    TOOL_JS("lara/tool/Tool.js"),
    TOOLUTILS_JS("lara/tool/ToolUtils.js"),
    ENERGYUNIT_JS("lara/units/EnergyUnit.js"),
    SIMODIFIER_JS("lara/units/SiModifier.js"),
    SIUNIT_JS("lara/units/SiUnit.js"),
    TIMEMODIFIER_JS("lara/units/TimeModifier.js"),
    TIMEUNIT_JS("lara/units/TimeUnit.js"),
    UNIT_JS("lara/units/Unit.js"),
    UNITMODIFIER_JS("lara/units/UnitModifier.js"),
    UNITWITHMODIFIER_JS("lara/units/UnitWithModifier.js"),
    ABSTRACTCLASSERROR_JS("lara/util/AbstractClassError.js"),
    ACCUMULATOR_JS("lara/util/Accumulator.js"),
    CHECKPOINT_JS("lara/util/Checkpoint.js"),
    COMBINATIONS_JS("lara/util/Combinations.js"),
    DATASTORE_JS("lara/util/DataStore.js"),
    IDGENERATOR_JS("lara/util/IdGenerator.js"),
    JAVATYPES_JS("lara/util/JavaTypes.js"),
    JPFILTER_JS("lara/util/JpFilter.js"),
    LINEINSERTER_JS("lara/util/LineInserter.js"),
    LOCALFOLDER_JS("lara/util/LocalFolder.js"),
    PREDEFINEDSTRINGS_JS("lara/util/PredefinedStrings.js"),
    PRINTONCE_JS("lara/util/PrintOnce.js"),
    PROCESSEXECUTOR_JS("lara/util/ProcessExecutor.js"),
    REPLACER_JS("lara/util/Replacer.js"),
    SEQUENTIALCOMBINATIONS_JS("lara/util/SequentialCombinations.js"),
    STRINGSET_JS("lara/util/StringSet.js"),
    TIMEUNITS_JS("lara/util/TimeUnits.js"),
    TUPLEID_JS("lara/util/TupleId.js"),
    CYTOSCAPE_3_26_0_JS("libs/cytoscape-3.26.0.js"),
    AST_JS("weaver/Ast.js"),
    JOINPOINTS_JS("weaver/JoinPoints.js"),
    QUERY_JS("weaver/Query.js"),
    SCRIPT_JS("weaver/Script.js"),
    SELECTOR_JS("weaver/Selector.js"),
    TRAVERSALTYPE_JS("weaver/TraversalType.js"),
    WEAVER_JS("weaver/Weaver.js"),
    WEAVERLAUNCHERBASE_JS("weaver/WeaverLauncherBase.js"),
    WEAVEROPTIONS_JS("weaver/WeaverOptions.js"),
    ACTIONAWARECACHE_JS("weaver/util/ActionAwareCache.js"),
    WEAVERDATASTORE_JS("weaver/util/WeaverDataStore.js");

    private final String resource;

    private static final String WEAVER_PACKAGE = "";

    /**
     * @param resource
     */
    private LaraApiJsResource (String resource) {
      this.resource = WEAVER_PACKAGE + getSeparatorChar() + resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
