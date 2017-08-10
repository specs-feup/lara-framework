/*
 * Copyright 2013 SPeCS.
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
/**
 * 
 */
package larac.utils.xml;

/**
 * @author Tiago
 * 
 */
public class Pair<T1, T2> {
	private T1 left;
	private T2 right;

	public Pair(T1 left, T2 right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * @param left
	 *            the left to set
	 */
	public void setLeft(T1 left) {
		this.left = left;
	}

	/**
	 * @return the left
	 */
	public T1 getLeft() {
		return this.left;
	}

	/**
	 * @param right
	 *            the right to set
	 */
	public void setRight(T2 right) {
		this.right = right;
	}

	/**
	 * @return the right
	 */
	public T2 getRight() {
		return this.right;
	}

	@Override
	public String toString() {
		return "(" + this.left + ", " + this.right + ")";
	}
}
