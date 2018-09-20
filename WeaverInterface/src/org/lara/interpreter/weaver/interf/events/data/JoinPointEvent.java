/**
 * Copyright 2014 SPeCS Research Group.
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

package org.lara.interpreter.weaver.interf.events.data;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.utils.FilterExpression;

public class JoinPointEvent extends BaseEvent {

    private String joinPointClass;
    private String alias;
    private FilterExpression[] filter;
    private JoinPoint joinPoint;
    private boolean approvedByFilter;

    /**
     * @param stage
     * @param joinPointClass
     * @param alias
     * @param filter2
     * @param joinPoint
     */
    public JoinPointEvent(Stage stage, String joinPointClass, String alias, FilterExpression[] filter2,
            JoinPoint joinPoint,
            boolean approvedByFilter) {
        super(stage);
        this.joinPointClass = joinPointClass;
        this.alias = alias;
        this.filter = filter2;
        this.joinPoint = joinPoint;
    }

    /**
     * @return the joinPointClass
     */
    public String getJoinPointClass() {
        return joinPointClass;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @return the filter
     */
    public FilterExpression[] getFilter() {
        return filter;
    }

    /**
     * @return the joinPoint
     */
    public JoinPoint getJoinPoint() {
        return joinPoint;
    }

    /**
     * @param joinPointClass
     *            the joinPointClass to set
     */
    protected void setJoinPointClass(String joinPointClass) {
        this.joinPointClass = joinPointClass;
    }

    /**
     * @param alias
     *            the alias to set
     */
    protected void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @param filter
     *            the filter to set
     */
    protected void setFilter(FilterExpression[] filter) {
        this.filter = filter;
    }

    /**
     * @param joinPoint
     *            the joinPoint to set
     */
    protected void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

    public boolean isApprovedByFilter() {
        return approvedByFilter;
    }

    public void setApprovedByFilter(boolean approvedByFilter) {
        this.approvedByFilter = approvedByFilter;
    }

}
