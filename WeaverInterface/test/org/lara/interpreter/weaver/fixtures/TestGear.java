package org.lara.interpreter.weaver.fixtures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.AttributeEvent;

public class TestGear extends AGear {

    private final List<ActionEvent> actionEvents = Collections.synchronizedList(new ArrayList<>());
    private final List<AttributeEvent> attributeEvents = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void onAction(ActionEvent data) {
        actionEvents.add(data);
    }

    @Override
    public void onAttribute(AttributeEvent data) {
        attributeEvents.add(data);
    }

    public List<ActionEvent> getActionEvents() {
        return actionEvents;
    }

    public List<AttributeEvent> getAttributeEvents() {
        return attributeEvents;
    }

    public void clear() {
        actionEvents.clear();
        attributeEvents.clear();
    }
}
