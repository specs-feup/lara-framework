/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.utils;

/**
 * Class used for filtering a join point in a select
 * 
 * @author Tiago
 *
 */
public class FilterExpression {

    private String attribute;
    private String operator;
    private Object expected;
    private String expectedStr;
    private boolean filterComparator;
    private boolean empty;
    private boolean isMatch;

    /**
     * Create new filter with the attribute, the boolean operator and the expected value
     * 
     * @param attribute
     * @param operator
     * @param expected
     */
    public static FilterExpression newEmpty() {
	return new FilterExpression();
    }

    public static FilterExpression newComparator(String operator) {
	return new FilterExpression(operator);
    }

    public static FilterExpression newMatch(String attribute, Object expected, String expectedStr) {
	return new FilterExpression(attribute, "~=", expected, expectedStr, true);
    }

    public static FilterExpression newInstance(String attribute, String operator, Object expected, String expectedStr) {
	return new FilterExpression(attribute, operator, expected, expectedStr, false);
    }

    private FilterExpression(String attribute, String operator, Object expected, String expectedStr, boolean isMatch) {
	this.attribute = attribute;
	setOperator(operator);
	setExpected(expected);
	setExpectedStr(expectedStr);
	setMatch(isMatch);
	setEmpty(false);
	setFilterComparator(false);
    }

    private FilterExpression(String operator) {
	setOperator(operator);
	setMatch(false);
	setEmpty(false);
	setFilterComparator(true);
    }

    private FilterExpression() {
	setEmpty(true);
	setFilterComparator(false);
    }

    public String getAttribute() {
	return attribute;
    }

    void setAttribute(String attribute) {
	this.attribute = attribute;
    }

    public Object getExpected() {
	return expected;
    }

    void setExpected(Object expected) {
	this.expected = expected;
    }

    public String getOperator() {
	return operator;
    }

    void setOperator(String operator) {
	this.operator = operator;
    }

    @Override
    public String toString() {
	return attribute + " " + operator + " " + expectedStr;
    }

    public String toStringValue() {
	return attribute + " " + operator + " " + expected.toString();
    }

    public boolean isEmpty() {
	return empty;
    }

    void setEmpty(boolean empty) {
	this.empty = empty;
    }

    public String getExpectedStr() {
	return expectedStr;
    }

    void setExpectedStr(String expectedStr) {
	this.expectedStr = expectedStr;
    }

    public boolean isMatch() {
	return isMatch;
    }

    public void setMatch(boolean isMatch) {
	this.isMatch = isMatch;
    }

    public boolean isFilterComparator() {
	return filterComparator;
    }

    public void setFilterComparator(boolean filterComparator) {
	this.filterComparator = filterComparator;
    }
}
