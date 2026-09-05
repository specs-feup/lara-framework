package org.lara.weavergen2.generator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.emit.EnumDefEmitter;
import org.lara.weavergen2.emit.TypeDefEmitter;
import org.lara.weavergen2.model.GenerationProfile;

public final class EntityGenerator {

    private final WeaverModel model;
    private final TypeDefEmitter typeDefs;
    private final EnumDefEmitter enums;

    public EntityGenerator(WeaverModel model, GenerationProfile config) {
        this.model = model;
        this.typeDefs = new TypeDefEmitter(model, config);
        this.enums = new EnumDefEmitter(config);
    }

    public Map<String, String> generateTypeDefs() {
        var files = new LinkedHashMap<String, String>();
        for (var td : model.getTypeDefs().values()) {
            files.put(td.name(), typeDefs.emit(td));
        }
        return files;
    }

    public Map<String, String> generateEnumDefs() {
        var files = new LinkedHashMap<String, String>();
        for (var ed : model.getEnumDefs().values()) {
            files.put(ed.name(), enums.emit(ed));
        }
        return files;
    }
}
