package org.lara.interpreter.weaver.defaultweaver.joinpoints;

import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AClass;
import org.lara.interpreter.weaver.defaultweaver.DWWeaver;
import java.lang.Object;

public class DWClass<Self extends DWClass<Self>> extends AClass<Self> {
    protected DWClass(Object node, DWWeaver weaver) {
        super(node, weaver);
    }
}
