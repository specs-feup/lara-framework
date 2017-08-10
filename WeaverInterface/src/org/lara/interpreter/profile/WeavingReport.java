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

import java.util.Map;

import pt.up.fe.specs.util.collections.AccumulatorMap;

public class WeavingReport {
    private AccumulatorMap<String> calledAspects;
    private AccumulatorMap<String> actions;
    // TODO (aka NEVERDO) - replace fields with AccumulatorMap and use an enum with names of fields
    private int inserts;
    private int selects;
    private int applies;
    private int attributes;
    private int nativeLOCs;
    private int totalLOCs;
    private int runs;

    public WeavingReport() {
        calledAspects = new AccumulatorMap<>();
        actions = new AccumulatorMap<>();
        reset();
    }

    public void aspectCalled(String aspectName) {
        calledAspects.add(aspectName.replace("$", "."));
    }

    public void actionPerformed(String actionName) {
        actions.add(actionName);
    }

    public void incSelects() {
        selects++;
    }

    public void incApplies() {
        applies++;
    }

    public void incInserts() {
        inserts++;
    }

    /**
     * Increment NativeLOCs. Also increments total LOCs
     * 
     * @param LOCs
     */
    public void incNativeLOCs(int LOCs) {
        nativeLOCs += LOCs;
        incTotalLOCs(LOCs);
    }

    /**
     * Increment NativeLOCs
     * 
     * @param LOCs
     */
    public void incTotalLOCs(int LOCs) {
        totalLOCs += LOCs;
    }

    public void runs() {
        runs++;
    }

    /**
     * @return the actions
     */
    public long getNumActions() {
        return actions.getSum();
    }

    /**
     * 
     * @return
     */
    public int getInserts() {
        return inserts;
    }

    /**
     * @return the selects
     */
    public int getSelects() {
        return selects;
    }

    /**
     * @return the applies
     */
    public int getApplies() {
        return applies;
    }

    /**
     * @return the aspectCalls
     */
    public long getNumAspectCalls() {
        return calledAspects.getSum();
    }

    /**
     * @return the attributes
     */
    public int getAttributes() {
        return attributes;
    }

    /**
     * @return the nativeLOCs
     */
    public int getNativeLOCs() {
        return nativeLOCs;
    }

    /**
     * @return the totalNativeLOCs
     */
    public int getTotalLOCs() {
        return totalLOCs;
    }

    /**
     * @return the runs
     */
    public int getRuns() {
        return runs;
    }

    public void reset() {
        calledAspects = new AccumulatorMap<>();
        actions = new AccumulatorMap<>();
        inserts = 0;
        selects = 0;
        applies = 0;
        attributes = 0;
        nativeLOCs = 0;
        totalLOCs = 0;
        runs = 0;
    }

    public void incAttributes() {
        attributes++;
    }

    public Map<String, Integer> getAspectsMap() {
        return calledAspects.getAccMap();
    }

    public Map<String, Integer> getActionsMap() {
        return actions.getAccMap();
    }

}
