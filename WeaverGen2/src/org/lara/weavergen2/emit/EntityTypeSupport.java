package org.lara.weavergen2.emit;

import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.ArrayType;
import org.lara.langspec2.types.JpDataType.EnumRefType;
import org.lara.langspec2.types.JpDataType.JpRefType;
import org.lara.langspec2.types.JpDataType.ParameterizedType;
import org.lara.langspec2.types.JpDataType.SelfType;
import org.lara.langspec2.types.JpDataType.WildcardType;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.java.TypeMapper;

public final class EntityTypeSupport {

    private final GenerationProfile config;

    public EntityTypeSupport(GenerationProfile config) {
        this.config = config;
    }

    public String mapType(JpDataType type) {
        return TypeMapper.toJavaType(
                type,
                "Object",
                name -> TypeMapper.abstractClassName(name) + (config.hasBaseSpec() ? "<?>" : "<?, ?>"),
                TypeMapper::capitalize,
                TypeMapper::capitalize
        );
    }

    public boolean containsJoinpointRef(JpDataType type) {
        if (type instanceof JpRefType || type instanceof SelfType) {
            return true;
        }
        if (type instanceof ArrayType arr) {
            return containsJoinpointRef(arr.element());
        }
        if (type instanceof ParameterizedType pt) {
            if (containsJoinpointRef(pt.base())) {
                return true;
            }
            return pt.args().stream().anyMatch(this::containsJoinpointRef);
        }
        if (type instanceof WildcardType wt && wt.bound() != null) {
            return containsJoinpointRef(wt.bound());
        }
        return false;
    }

    public boolean containsEnumRef(JpDataType type) {
        if (type instanceof EnumRefType) {
            return true;
        }
        if (type instanceof ArrayType arr) {
            return containsEnumRef(arr.element());
        }
        if (type instanceof ParameterizedType pt) {
            if (containsEnumRef(pt.base())) {
                return true;
            }
            return pt.args().stream().anyMatch(this::containsEnumRef);
        }
        if (type instanceof WildcardType wt && wt.bound() != null) {
            return containsEnumRef(wt.bound());
        }
        return false;
    }
}
