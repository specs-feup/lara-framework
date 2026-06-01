package org.lara.weavergen2.emit;

import java.util.List;
import java.util.function.Function;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.model.Parameter;
import org.lara.langspec2.model.WeaverModel;
import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.ArrayType;
import org.lara.langspec2.types.JpDataType.DirectType;
import org.lara.langspec2.types.JpDataType.EnumRefType;
import org.lara.langspec2.types.JpDataType.ParameterizedType;
import org.lara.langspec2.types.JpDataType.ReferenceType;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.java.TypeMapper;

public final class JoinPointTypeRenderer {

    private final WeaverModel model;
    private final GenerationProfile config;

    public JoinPointTypeRenderer(WeaverModel model, GenerationProfile config) {
        this.model = model;
        this.config = config;
    }

    public String mapReturnType(JpDataType type) {
        return mapType(type);
    }

    public String mapParameterType(JpDataType type) {
        return mapType(type);
    }

    public String mapPublicReturnType(JpDataType type) {
        if (type instanceof DirectType primitiveType) {
            return primitiveType.name();
        }
        if (type instanceof ReferenceType) {
            return "Object";
        }
        if (type instanceof EnumRefType) {
            return "String";
        }
        if (type instanceof ArrayType arrayType) {
            return mapPublicReturnType(arrayType.element()) + "[]";
        }

        return "Object";
    }

    public String mapPublicParameterType(JpDataType type) {
        if (type instanceof DirectType primitiveType) {
            return primitiveType.name();
        }

        if (type instanceof ReferenceType referenceType) {
            return referenceType.name();
        }

        if (type instanceof EnumRefType) {
            return "String";
        }

        if (type instanceof ArrayType arrayType) {
            return mapPublicParameterType(arrayType.element()) + "[]";
        }

        if (type instanceof ParameterizedType parameterizedType && parameterizedType.base() instanceof DirectType) {
            return mapType(parameterizedType);
        }

        return "Object";
    }

    public String formatImplParams(List<Parameter> params) {
        return params.stream()
                .map(p -> mapParameterType(p.type()) + " " + TypeMapper.sanitizeJavaIdentifier(p.name()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    public List<String> formatPublicParams(List<Parameter> params) {
        return params.stream()
                .map(p -> String.join(" ", mapPublicParameterType(p.type()),
                        TypeMapper.sanitizeJavaIdentifier(p.name())))
                .toList();
    }

    public String toImplArgument(JpDataType type, String paramName) {
        if (type instanceof DirectType) {
            return paramName;
        }

        if (type == WeaverSpec.OBJECT) {
            return paramName;
        }

        if (type instanceof EnumRefType ref) {
            return TypeMapper.capitalize(ref.enumName()) + ".fromDisplay(" + paramName + ")";
        }

        if (type instanceof ArrayType arrayType) {
            if (arrayType.element() instanceof DirectType) {
                return paramName;
            }
            if (arrayType.element() == WeaverSpec.OBJECT) {
                return paramName;
            }

            if (isEnumLike(arrayType.element())) {
                return toImplEnumArrayArgument(arrayType, paramName);
            }

            String castType = mapArrayCastType(arrayType);
            switch (castType) {
                case "Jp":
                    castType = "jpTypeArrayFactory()";
                    break;
                case "Self":
                    castType = "selfTypeArrayFactory()";
                    break;
                default:
                    castType += ".class";
                    break;
            }

            return "pt.up.fe.specs.util.SpecsCollections.cast(" + paramName + ", " + castType + ")";
        }

        if (type instanceof ParameterizedType parameterizedType && parameterizedType.base() instanceof DirectType) {
            return paramName;
        }

        return "(" + mapParameterType(type) + ") " + paramName;
    }

    public boolean isEnumLike(JpDataType type) {
        if (type instanceof EnumRefType) {
            return true;
        }

        if (type instanceof ArrayType arrayType) {
            return isEnumLike(arrayType.element());
        }

        return false;
    }

    public String toPublicEnumReturnExpression(JpDataType type, StringBuilder implCall) {
        if (type instanceof EnumRefType) {
            return implCall + ".getDisplay()";
        }

        if (type instanceof ArrayType arrayType && arrayType.element() instanceof EnumRefType ref) {
            var enumType = TypeMapper.capitalize(ref.enumName());
            return implCall + ".map(" + enumType + "::getDisplay).toArray(String[]::new)";
        }

        throw new IllegalArgumentException("Unsupported enum bridge type: " + type);
    }

    private String toImplEnumArrayArgument(ArrayType arrayType, String sourceExpression) {
        var element = arrayType.element();

        if (element instanceof EnumRefType ref) {
            var enumType = TypeMapper.capitalize(ref.enumName());
            return "Arrays.stream(" + sourceExpression + ")"
                    + ".map(" + enumType + "::fromDisplay)"
                    + ".toArray(" + enumType + "[]::new)";
        }

        if (element instanceof ArrayType innerArray) {
            var lambdaVar = "value";
            var innerExpression = toImplEnumArrayArgument(innerArray, lambdaVar);
            return "Arrays.stream(" + sourceExpression + ")"
                    + ".map(" + lambdaVar + " -> " + innerExpression + ")"
                    + ".toArray(" + mapParameterType(arrayType) + "::new)";
        }

        throw new IllegalArgumentException("Unsupported enum array type: " + arrayType);
    }

    private String mapType(JpDataType type) {
        boolean useRootJoinPointAlias = !config.hasBaseSpec();

        Function<String, String> jpMapper = name -> {
            if (useRootJoinPointAlias && name.equals(model.getGlobal().getName())) {
                return "Jp";
            }

            if (name.equals("joinpoint") || name.equals(model.getGlobal().getName())) {
                return TypeMapper.abstractClassName(model.getGlobal().getName())
                        + (config.hasBaseSpec() ? "<?>" : "<?, ?>");
            }
            return TypeMapper.abstractClassName(name) + "<?>";
        };

        return TypeMapper.toJavaType(
                type,
                "Self",
                jpMapper,
                TypeMapper::capitalize,
                TypeMapper::capitalize);
    }

    private String mapArrayCastType(ArrayType type) {
        boolean useRootJoinPointAlias = !config.hasBaseSpec();

        Function<String, String> jpMapper = name -> {
            if (useRootJoinPointAlias && name.equals(model.getGlobal().getName())) {
                return "Jp";
            }

            if (name.equals("joinpoint") || name.equals(model.getGlobal().getName())) {
                return TypeMapper.abstractClassName(model.getGlobal().getName());
            }
            return TypeMapper.abstractClassName(name);
        };

        return TypeMapper.toJavaType(
                type.element(),
                "Self",
                jpMapper,
                TypeMapper::capitalize,
                TypeMapper::capitalize);
    }
}
