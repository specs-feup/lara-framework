package org.lara.interpreter.weaver.options;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import pt.up.fe.specs.util.SpecsCollections;

import java.util.ArrayList;
import java.util.List;

public class WeaverOptionUtils {


    public static List<WeaverOption> toWeaverOption(StoreDefinition firstStoreDef, StoreDefinition... otherStoreDefs) {
        return toWeaverOption(SpecsCollections.concat(firstStoreDef, List.of(otherStoreDefs)));
    }

    public static List<WeaverOption> toWeaverOption(List<StoreDefinition> storeDefinitions) {
        var options = new ArrayList<WeaverOption>();

        for (var storeDefinition : storeDefinitions) {
            options.addAll(toWeaverOptionPrivate(storeDefinition));
        }

        return options;
    }

    private static List<WeaverOption> toWeaverOptionPrivate(StoreDefinition storeDefinition) {

        var options = new ArrayList<WeaverOption>();

        for (var key : storeDefinition.getKeys()) {
            options.add(toOption(key));
        }

        return options;
    }

    public static WeaverOption toOption(DataKey<?> key) {
        return toOption(key, null, key.getName(), key.getLabel());
    }

    public static WeaverOption toOption(DataKey<?> key, String shortOption, String longOption, String description) {
        // Check if boolean
        if (Boolean.class.isAssignableFrom(key.getValueClass())) {
            return WeaverOptionBuilder.build(shortOption, longOption, description, key);
        }

        // Consider one argument option for other cases
        return WeaverOptionBuilder.build(shortOption, longOption, OptionArguments.ONE_ARG, "<arg>", description, key);
    }

}
