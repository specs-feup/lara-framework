/**
 * Copyright 2013 SPeCS Research Group.
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

package org.lara.language.specification.joinpointmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

import org.lara.language.specification.joinpointmodel.constructor.JoinPointModelConstructor;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.joinpointmodel.schema.JoinPointsList;
import org.lara.language.specification.joinpointmodel.schema.Select;
import org.xml.sax.SAXException;

public class JoinPointModel extends JoinPointModelConstructor {

    public JoinPointModel(File joinPointModelFile, boolean validate)
	    throws JAXBException, SAXException, XMLParseException, IOException {
	super(joinPointModelFile, validate);
    }

    public JoinPointModel(Source joinPointModelSource, String sourceName, boolean validate)
	    throws JAXBException, SAXException, XMLParseException, IOException {

	super(joinPointModelSource, sourceName, validate);
    }

    public JoinPointModel(JoinPointsList jps) {
	super(jps);
    }

    public static JoinPointType toJoinPointType(Object obj) {
	return (JoinPointType) obj;
    }

    /**
     * Get all selects available for a joinpoint, including the ones from the extended join point;
     * 
     * @param joinpoint
     * @return
     */
    public List<Select> getAllSelects(JoinPointType joinpoint) {
	List<Select> selects = new ArrayList<>();
	List<Select> selectJ = joinpoint.getSelect();
	if (selectJ != null && !selectJ.isEmpty()) {
	    selects.addAll(selectJ);
	}
	JoinPointType superType = (JoinPointType) joinpoint.getExtends();
	if (!joinpoint.equals(superType)) {
	    selects.addAll(getAllSelects(superType));
	}
	return selects;
    }

}
