package org.lara.weavergen2.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.lara.langspec2.model.Action;
import org.lara.langspec2.model.Attribute;
import org.lara.langspec2.model.Parameter;
import org.lara.langspec2.types.JpDataType;

class JoinPointMemberTest {

    @Test
    void derivesAttributeMethodNames() {
        var member = JoinPointMember.attribute(new Attribute("type", new JpDataType.DirectType("String"), List.of(),
                null));

        assertThat(member.wrapperName()).isEqualTo("type");
        assertThat(member.implementationName()).isEqualTo("getTypeImpl");
    }

    @Test
    void sanitizesWrapperNameWithoutChangingImplName() {
        var member = JoinPointMember.action(new Action("class", new JpDataType.DirectType("void"),
                List.of(new Parameter("for", new JpDataType.DirectType("int"), null)), null));

        assertThat(member.wrapperName()).isEqualTo("_class");
        assertThat(member.implementationName()).isEqualTo("classImpl");
        assertThat(member.signature().asText()).isEqualTo("class(DirectType[name=int])");
    }
}
