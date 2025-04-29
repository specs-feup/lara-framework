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

import org.lara.interpreter.weaver.interf.JoinPoint;

import pt.up.fe.specs.util.collections.AccumulatorMap;

public class WeavingReport {

    private AccumulatorMap<String> calledAspects;
    private AccumulatorMap<String> actions;
    private AccumulatorMap<ReportField> metrics;

    public WeavingReport() {
        metrics = new AccumulatorMap<>();
        calledAspects = new AccumulatorMap<>();
        actions = new AccumulatorMap<>();
        // reset();
    }

    public void aspectCalled(String aspectName) {
        calledAspects.add(aspectName.replace("$", "."));
    }

    public void actionPerformed(String actionName, JoinPoint joinPoint) {
        actions.add(actionName);
        // advisedJPs.add(joinPoint.getNode());
    }

    public void inc(ReportField field) {
        metrics.add(field);
        //
        // // When incrementing native locs, increment also total locs
        // if (field == ReportField.NATIVE_LOCS) {
        // metrics.add(ReportField.TOTAL_LOCS);
        // }
    }

    public void inc(ReportField field, int amount) {
        metrics.add(field, amount);
        //
        // // When incrementing native locs, increment also total locs
        // if (field == ReportField.NATIVE_LOCS) {
        // metrics.add(ReportField.TOTAL_LOCS, amount);
        // }
    }

    public int get(ReportField field) {
        return metrics.getCount(field);
    }

    public int set(ReportField field, int value) {
        return metrics.set(field, value);
    }

    /**
     * @return the actions
     */
    public long getNumActions() {
        return actions.getSum();
    }

    public Map<String, Integer> getActionsMap() {
        return actions.getAccMap();
    }

    /* 
       public void incJoinPoints() {
        joinPoints++;
    }
    
    public void incFilteredJoinPoints() {
        filteredJoinPoints++;
    }
    
    public void incFilteredJoinPoints(int value) {
        filteredJoinPoints += value;
    }
    
    public int getJoinPoints() {
        return joinPoints;
    }
    
    public void setJoinPoints(int joinPoints) {
        this.joinPoints = joinPoints;
    }
    
    public int getFilteredJoinPoints() {
        return filteredJoinPoints;
    }
    
    public void setFilteredJoinPoints(int filteredJoinPoints) {
        this.filteredJoinPoints = filteredJoinPoints;
    }
    
    public void setNumTokens(int numMainLaraTokens) {
        this.numTokens = numMainLaraTokens;
    }
    
    public int getNumTokens() {
        return this.numTokens;
    }
    */
}
