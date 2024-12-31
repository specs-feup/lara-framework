/**
 * Copyright 2020 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.lara.commonlang;

import org.lara.language.specification.dsl.LanguageSpecificationV2;
import pt.up.fe.specs.lara.commonlang.generator.CommonLangGenerator;
import pt.up.fe.specs.util.providers.ResourceProvider;

import java.util.ArrayList;
import java.util.List;

import static pt.up.fe.specs.lara.commonlang.CommonLangResource.*;

public class LaraCommonLang {


    public static LanguageSpecificationV2 getLanguageSpecification() {
        return LanguageSpecificationV2.newInstance(JOINPOINTS, ARTIFACTS, ACTIONS);
    }

    public static List<ResourceProvider> getLaraCommonLangApi() {
        List<ResourceProvider> api = new ArrayList<>();

        api.add(() -> "weaver/jp/JoinPoint.lara");
        api.add(() -> "weaver/jp/CommonJoinPointsBase.lara");
        api.add(() -> "weaver/jp/JoinPointIndex.lara");
        api.add(() -> "weaver/jp/JoinPointsCommonPath.js");
        api.addAll(CommonLangGenerator.getGeneratedResources());

        return api;
    }

    /**
     * Used by JS code.
     *
     * @param joinPointType
     * @return
     */
    public static String getDefaultAttribute(String joinPointType) {
        return getLanguageSpecification().getJoinPoint(joinPointType).getDefaultAttribute().orElse(null);
    }

}
