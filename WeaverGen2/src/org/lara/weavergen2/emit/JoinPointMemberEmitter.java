package org.lara.weavergen2.emit;

import java.util.List;
import java.util.stream.Stream;

import org.lara.langspec2.model.Action;
import org.lara.langspec2.model.Attribute;
import org.lara.langspec2.model.JpClass;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.model.JoinPointMember;
import org.lara.weavergen2.model.MemberSignature;
import org.lara.weavergen2.model.WrapperSignature;

public final class JoinPointMemberEmitter {

    private final JpClass jpClass;
    private final GenerationProfile profile;
    private final JoinPointTypeRenderer types;
    private final PublicWrapperEmitter wrappers;

    public JoinPointMemberEmitter(JpClass jpClass, GenerationProfile profile, JoinPointTypeRenderer types) {
        this.jpClass = jpClass;
        this.profile = profile;
        this.types = types;
        this.wrappers = new PublicWrapperEmitter(types);
    }

    public void emit(JavaSourceBuilder sb) {
        ownMembers().forEach(member -> emitMember(sb, member));
    }

    private Stream<JoinPointMember> ownMembers() {
        return Stream.concat(
                jpClass.getOwnAttributes().stream().map(JoinPointMember::attribute),
                jpClass.getOwnActions().stream().map(JoinPointMember::action));
    }

    private void emitMember(JavaSourceBuilder sb, JoinPointMember member) {
        var javaRetType = types.javaType(member.type());

        sb.line("public abstract " + javaRetType + " " + member.implementationName() + "("
                + types.formatImplParams(member.parameters()) + ");");
        sb.line();

        if (shouldGenerateWrapper(member)) {
            wrappers.emit(sb, member);
        }
    }

    private boolean shouldGenerateWrapper(JoinPointMember member) {
        if (profile.hasBaseSpec()
                && profile.inheritedFinalWrapperSignatures()
                        .contains(new WrapperSignature(member.wrapperName(), member.parameters().size()))) {
            return false;
        }

        var signature = member.signature();
        if (profile.baseMemberSignatures().contains(signature)) {
            return false;
        }

        var parent = jpClass.getParent().orElse(null);
        while (parent != null) {
            if (hasMatchingSignature(parent.getOwnAttributes(), signature)
                    || hasMatchingSignature(parent.getOwnActions(), signature)) {
                return false;
            }

            parent = parent.getParent().orElse(null);
        }

        return true;
    }

    private boolean hasMatchingSignature(List<?> members, MemberSignature signature) {
        for (var member : members) {
            if (member instanceof Attribute attr) {
                if (JoinPointMember.attribute(attr).signature().equals(signature)) {
                    return true;
                }
                continue;
            }

            if (member instanceof Action action
                    && JoinPointMember.action(action).signature().equals(signature)) {
                return true;
            }
        }

        return false;
    }
}
