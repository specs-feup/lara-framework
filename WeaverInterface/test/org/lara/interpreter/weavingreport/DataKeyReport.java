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

package org.lara.interpreter.weavingreport;

import org.suikasoft.jOptions.DataStore.ADataClass;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

public class DataKeyReport extends ADataClass<DataKeyReport> {

    public static final DataKey<Integer> JOIN_POINTS = KeyFactory.integer("joinPoints");

    @Override
    public Integer incInt(DataKey<Integer> metric) {
        var previousValue = get(metric);
        set(metric, previousValue + 1);
        return previousValue;
    }

    @Override
    public Integer incInt(DataKey<Integer> metric, int amount) {
        var previousValue = get(metric);
        set(metric, previousValue + amount);
        return previousValue;
    }

    /*
    public void inc(ReportField field, int amount) {
        metrics.add(field, amount);
        //
        // // When incrementing native locs, increment also total locs
        // if (field == ReportField.NATIVE_LOCS) {
        // metrics.add(ReportField.TOTAL_LOCS, amount);
        // }
    }
    */
}
