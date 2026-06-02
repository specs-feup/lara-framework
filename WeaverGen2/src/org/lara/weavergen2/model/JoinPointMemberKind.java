package org.lara.weavergen2.model;

public enum JoinPointMemberKind {
    ATTRIBUTE("Attribute"),
    ACTION("Action");

    private final String eventName;

    JoinPointMemberKind(String eventName) {
        this.eventName = eventName;
    }

    public String eventName() {
        return eventName;
    }
}
