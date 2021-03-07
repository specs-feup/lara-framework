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

package pt.up.fe.specs.lara.lcl;

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * @author Joao Bispo
 * 
 */
public enum LaraCommonLanguageApiResource implements LaraResourceProvider {

    // Lara Common Language
    LARA_COMMON_LANGUAGE("LaraCommonLanguage.lara"),
    
    // Metrics
    METRIC("metrics/Metric.lara"),
    METRICS("metrics/Metrics.lara"),
    
    //Other Metrics
    COGNI_COMPLEX("metrics/om/CogniComplex.lara"),
    CYCLO_COMPLEX("metrics/om/CycloComplex.lara"),
    LOC("metrics/om/LOC.lara"),
    NOCL("metrics/om/NOCl.lara"),
    NOFI("metrics/om/NOFi.lara"),
    NOFU("metrics/om/NOFu.lara"),
    NOL("metrics/om/NOL.lara"),
    OTHER_METRICS_INDEX("metrics/om/OtherMetricsIndex.lara");



    private final String resource;

    private static final String BASE_PACKAGE = "lcl/";

    /**
     * @param resource
     */
    private LaraCommonLanguageApiResource(String resource) {
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
