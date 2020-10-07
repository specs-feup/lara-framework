/**
 * Copyright 2020 SPeCS.
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

package pt.up.fe.specs.lara.commonlang;

import static pt.up.fe.specs.lara.commonlang.CommonLangResource.ACTIONS;
import static pt.up.fe.specs.lara.commonlang.CommonLangResource.ARTIFACTS;
import static pt.up.fe.specs.lara.commonlang.CommonLangResource.JOINPOINTS;

import java.util.ArrayList;
import java.util.List;

import org.lara.language.specification.LanguageSpecification;

import pt.up.fe.specs.lara.commonlang.generator.CommonLangGenerator;
import pt.up.fe.specs.util.providers.ResourceProvider;

public class LaraCommonLang {

    public static LanguageSpecification getLanguageSpecification() {
        return LanguageSpecification.newInstance(JOINPOINTS, ARTIFACTS, ACTIONS, false);
    }

    public static List<ResourceProvider> getLaraCommonLangApi() {
        List<ResourceProvider> api = new ArrayList<>();

        api.add(() -> "weaver/jp/JoinPoint.lara");
        api.add(() -> "weaver/jp/JoinPoints.lara");
        api.add(() -> "weaver/jp/JoinPointIndex.lara");
        api.addAll(CommonLangGenerator.getGeneratedResources());

        return api;
    }

}
