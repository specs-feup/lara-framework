/**
 * Copyright 2014 SPeCS.
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

package larai.test;

import java.util.Map;

public class TestClasses {

	public static void test(Object... objects) {

		for (final Object obj : objects) {
			System.out.println(obj.getClass().getCanonicalName());
		}

		for (final Object obj : objects) {
			System.out.print(obj + ", ");
		}
		System.out.println();
	}

	public static void test2(int num, int[] array, Map<String, String> map, String string) {

		System.out.println(num + ", " + array + ", " + map + ", " + string);
		for (final int str : array) {

			System.out.println("ARRAY: " + str);
		}
	}

}
