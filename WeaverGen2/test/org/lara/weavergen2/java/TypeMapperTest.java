package org.lara.weavergen2.java;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.lara.langspec2.types.JpDataType;

class TypeMapperTest {

    @Test
    void mapsNestedTypes() {
        assertThat(TypeMapper.toJavaType(new JpDataType.ArrayType(new JpDataType.SelfType()), "Self",
                name -> name, name -> name, name -> name)).isEqualTo("Self[]");
        assertThat(TypeMapper.toJavaType(new JpDataType.ParameterizedType(new JpDataType.DirectType("List"),
                List.of(new JpDataType.JpRefType("node"))), "Self", name -> "A" + name, name -> name,
                name -> name)).isEqualTo("List<Anode>");
    }
}
