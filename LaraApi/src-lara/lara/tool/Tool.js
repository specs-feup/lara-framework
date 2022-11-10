"use strict";

class Tool {
    toolName;
    disableWeaving;

    constructor(toolName, disableWeaving) {
        if (this.constructor == Tool) {
            throw new Error("Class 'Tool' is abstract and cannot be instantiated");
        }

        this.toolName = toolName;
        this.disableWeaving = defaultValue(disableWeaving, false);
    }
}