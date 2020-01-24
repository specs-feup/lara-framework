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

package pt.up.fe.specs.lara.ast.utils;

import org.suikasoft.jOptions.DataStore.ADataClass;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

public class SourceLocation extends ADataClass<SourceLocation> {

    public static final DataKey<Position> START = KeyFactory.object("start", Position.class);

    public static final DataKey<Position> END = KeyFactory.object("end", Position.class);

    public static final DataKey<String> SOURCE = KeyFactory.string("source").setDefaultString("<Unknown Source>");

    private static final SourceLocation UNKNOWN_SOURCE_LOCATION = newInstance(Position.newInstance(-1, -1),
            Position.newInstance(-1, -1))
                    .lock();

    public static SourceLocation newInstance(Position start, Position end) {
        return new SourceLocation().set(START, start).set(END, end);
    }

    public static SourceLocation getUnknownSourceLocation() {
        return UNKNOWN_SOURCE_LOCATION;
    }

}
