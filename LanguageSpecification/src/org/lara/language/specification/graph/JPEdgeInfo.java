/**
 * Copyright 2015 SPeCS Research Group.
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

package org.lara.language.specification.graph;

public class JPEdgeInfo {

	private boolean extend;
	private String sourceId;
	private String targetId;
	private String label;

	private JPEdgeInfo(String sourceId, String targetId) {
		this.sourceId = sourceId;
		this.targetId = targetId;
		extend = false;
	}

	public static JPEdgeInfo newSelects(String alias, String sourceId, String targetId) {
		final JPEdgeInfo jpEdgeInfo = new JPEdgeInfo(sourceId, targetId);
		jpEdgeInfo.label = alias;
		return jpEdgeInfo;
	}

	public static JPEdgeInfo newExtends(String sourceId, String targetId) {
		final JPEdgeInfo info = new JPEdgeInfo(sourceId, targetId);
		info.extend = true;
		return info;
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @return the targetId
	 */
	public String getTargetId() {
		return targetId;
	}

	/**
	 * @param sourceId
	 *            the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @param targetId
	 *            the targetId to set
	 */
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getLabel() {
		return label;
	}

	public boolean hasLabel() {
		return label != null && !label.isEmpty();
	}

	public boolean isExtend() {
		return extend;
	}

	@Override
	public String toString() {
		// if (extend) {
		// return "[arrowhead=\"empty\",style=\"dashed\",label=\"extend\"]";
		// }
		// String string = "[";
		// if (hasLabel()) {
		//
		// string += "label=\"" + label + "\",";
		// }
		// string += "concentrate=true]";

		if (hasLabel()) {
			return label;
		}
		if (extend) {
			return "extends";
		}
		return "";
	}
}
