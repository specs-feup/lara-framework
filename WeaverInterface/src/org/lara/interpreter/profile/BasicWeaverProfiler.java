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

package org.lara.interpreter.profile;

import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.ApplyEvent;
import org.lara.interpreter.weaver.interf.events.data.ApplyIterationEvent;
import org.lara.interpreter.weaver.interf.events.data.AttributeEvent;
import org.lara.interpreter.weaver.interf.events.data.JoinPointEvent;
import org.lara.interpreter.weaver.interf.events.data.WeaverEvent;

public abstract class BasicWeaverProfiler extends WeaverProfiler {

    @Override
    protected void resetImpl() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onWeaverImpl(WeaverEvent data) {

    }

    @Override
    protected void onJoinPointImpl(JoinPointEvent data) {

    }

    @Override
    protected void onApplyImpl(ApplyEvent data) {

    }

    @Override
    protected void onApplyImpl(ApplyIterationEvent data) {

    }

    @Override
    protected void onAttributeImpl(AttributeEvent data) {

    }

    @Override
    protected void onActionImpl(ActionEvent data) {

    }

    public static BasicWeaverProfiler emptyProfiler() {
        return new BasicWeaverProfiler() {

            @Override
            protected void buildReport(ReportWriter writer) {

            }
        };
    }
}
