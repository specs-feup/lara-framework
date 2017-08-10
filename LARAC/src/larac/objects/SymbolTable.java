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
package larac.objects;

import java.util.LinkedHashMap;

/**
 * @param <K>
 *            Key
 * @param <V>
 *            Value
 * 
 */
public class SymbolTable<K, V> extends LinkedHashMap<K, V> {
	private static final String MIDDLE = "_U_";
	private static final long serialVersionUID = 1L;
	private static int unique_count = 0;

	public String getUniqueName(String key) {
		if (containsKey(key)) {
			key += SymbolTable.MIDDLE + SymbolTable.unique_count++;
		}
		return key;
	}
}
