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

package pt.up.fe.specs.lara.langspec;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.xml.XmlDocument;

public class LanguageSpecificationTest {

    @BeforeClass
    public static void init() {
        SpecsSystem.programStandardInit();
    }

    @Test
    public void testSchema() {
        XmlDocument.newInstance(LanguageSpecificationTestResource.ACTION_MODEL.toStream(),
                SchemaResource.ACTION_SCHEMA.toStream());
        XmlDocument.newInstance(LanguageSpecificationTestResource.ATTRIBUTE_MODEL.toStream(),
                SchemaResource.ATTRIBUTE_SCHEMA.toStream());
        XmlDocument.newInstance(LanguageSpecificationTestResource.JOIN_POINT_MODEL.toStream(),
                SchemaResource.JOIN_POINT_SCHEMA.toStream());
    }

}
