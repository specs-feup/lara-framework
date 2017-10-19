package pt.up.fe.specs.lara.doc.aspectir;

import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.lara.interpreter.aspectir.CodeElem;
import org.lara.interpreter.aspectir.ExprLiteral;
import org.lara.interpreter.aspectir.Expression;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.StringProvider;
import tdrc.utils.StringUtils;

public class CodeElems {

    private final static Lazy<AspectIrToLara> CODE_GENERATOR = Lazy.newInstance(() -> new AspectIrToLara());

    public static String parseStringLiteralExpr(Expression stringLiteral) {
        // System.out.println("CLASS:" + stringLiteralExpr.getClass());
        // Preconditions.checkArgument(stringLiteralExpr instanceof
        // Expression.class, "Expected a code element of type Expression, got
        // "+stringLiteralExpr.);
        Preconditions.checkArgument(stringLiteral.exprs.size() == 1,
                "Expected to have one expression, has " + stringLiteral.exprs.size());
        Expression expression = stringLiteral.exprs.get(0);

        Preconditions.checkArgument(expression instanceof ExprLiteral,
                (StringProvider) (() -> "Expected first expression to be a literal, is a "
                        + expression.getClass().getSimpleName()));

        ExprLiteral exprLiteral = (ExprLiteral) expression;

        Preconditions.checkArgument(exprLiteral.type.equals("string"),
                "Expected type to be string, is " + exprLiteral.type);

        return exprLiteral.value;

    }

    public static String getLaraCode(CodeElem codeElem) {
        return CODE_GENERATOR.get().getCode(codeElem);
    }

    public static String toXml(CodeElem codeElem) {
        try {
            return StringUtils.xmlToStringBuffer(codeElem.getXmlDocument(), 3).toString();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new RuntimeException("Could not convert aspect IR node to XML: ", e);
        }
    }

    public static <T extends CodeElem> T get(int index, List<? extends CodeElem> elements, Class<T> elemClass) {
        CodeElem element = elements.get(index);

        Preconditions.checkArgument(elemClass.isInstance(element),
                "Expected code element at index " + index + " to be a " + elemClass.getSimpleName());

        return elemClass.cast(element);
    }
}
