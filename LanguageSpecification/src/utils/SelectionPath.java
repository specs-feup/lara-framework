/**
 * Copyright 2017 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package utils;

import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.Select;
import tdrc.utils.StringUtils;

import java.util.*;

/**
 * Will contain a path from a given join point to the destiny. <br>
 *
 * @author tdrc
 *
 */
public class SelectionPath {

    private Stack<Select> path;
    private Optional<Stack<Select>> secondaryPath;
    JoinPointClass origin;
    String destiny;
    private String tieBreakReason;

    public SelectionPath(JoinPointClass origin, String destiny) {
        this.origin = origin;
        this.destiny = destiny;
        this.setTieBreakReason("");
        setSecondaryPath(Optional.empty());
    }

    public boolean hasPath() {
        return getPath() != null;
    }

    public boolean hasSecondaryPath() {
        return getSecondaryPath().isPresent();
    }

    // public Stack<Select> getPath() {
    // return path;
    // }

    public Stack<Select> getPath() {
        return path;
    }

    public List<Select> getReversedPath() {
        if (path == null) {
            return null;
        }
        List<Select> list = new ArrayList<>(path);
        Collections.reverse(list);
        return list;
    }

    public void setPath(Stack<Select> path) {
        this.path = path;
    }

    public Optional<Stack<Select>> getSecondaryPath() {
        return secondaryPath;
    }

    public Optional<List<Select>> getReversedSecondaryPath() {
        if (!hasSecondaryPath()) {
            return Optional.empty();
        }
        List<Select> selects = new ArrayList<Select>(secondaryPath.get());
        Collections.reverse(selects);
        return Optional.of(selects);
    }

    public void setSecondaryPath(Optional<Stack<Select>> secondaryPath) {
        this.secondaryPath = secondaryPath;
    }

    public void setSecondaryPath(Stack<Select> secondaryPath) {
        this.secondaryPath = Optional.ofNullable(secondaryPath);
    }

    public String getTieBreakReason() {
        return tieBreakReason;
    }

    public void setTieBreakReason(String tieBreakReason) {
        this.tieBreakReason = tieBreakReason;
    }

    @Override
    public String toString() {
        String clazz = origin != null ? origin.getName() : "unknown";
        String toString = clazz + "->" + destiny;
        if (hasPath()) {
            List<Select> reversedPath = getReversedPath();

            if (!reversedPath.isEmpty()) {

                toString += "\n\tMain path: " + StringUtils.join(reversedPath, ".");
            }
        }
        if (hasSecondaryPath()) {
            List<Select> list = getReversedSecondaryPath().get();
            toString += "\n\t 2nd path: " + StringUtils.join(list, ".");
            if (!tieBreakReason.isEmpty()) {
                toString += "\n\tTie break: " + tieBreakReason;
            }
        }
        return toString;
    }
}
