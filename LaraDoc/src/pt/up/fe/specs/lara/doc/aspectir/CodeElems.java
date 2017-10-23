package pt.up.fe.specs.lara.doc.aspectir;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.lara.interpreter.aspectir.Code;
import org.lara.interpreter.aspectir.CodeElem;
import org.lara.interpreter.aspectir.ExprBody;
import org.lara.interpreter.aspectir.ExprLiteral;
import org.lara.interpreter.aspectir.ExprOp;
import org.lara.interpreter.aspectir.Expression;
import org.lara.interpreter.aspectir.Statement;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.classmap.FunctionClassMap;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.StringProvider;
import tdrc.utils.StringUtils;

public class CodeElems {

    private final static Lazy<AspectIrToLara> CODE_GENERATOR = Lazy.newInstance(() -> new AspectIrToLara());

    private final static FunctionClassMap<CodeElem, Stream<CodeElem>> CODE_ELEM_TO_STREAM;
    static {
        CODE_ELEM_TO_STREAM = new FunctionClassMap<>();

        CODE_ELEM_TO_STREAM.put(ExprBody.class, exprBody -> CodeElems.toElemStream(exprBody, exprBody.code));
        CODE_ELEM_TO_STREAM.put(Expression.class, CodeElems::getDescendantsAndSelf);
        CODE_ELEM_TO_STREAM.put(Code.class, CodeElems::toElemStream);
    }

    public static Stream<CodeElem> toElemStream(CodeElem codeElem) {
        return CODE_ELEM_TO_STREAM.apply(codeElem);
    }

    private static Stream<CodeElem> toElemStream(Code code) {
        if (code.statements == null) {
            return Stream.empty();
        }

        return code.statements.stream().flatMap(CodeElems::toElemStream);
    }

    public static Stream<CodeElem> toElemStream(Statement statement) {
        if (statement.components == null) {
            return Stream.empty();
        }

        return statement.components.stream().flatMap(CODE_ELEM_TO_STREAM::apply);
    }

    private static Stream<CodeElem> getDescendantsAndSelf(Expression expression) {
        Stream<CodeElem> descendants = getDescendants(expression);

        return Stream.concat(Stream.of(expression), descendants);
    }

    private static Stream<CodeElem> getDescendants(Expression expression) {
        return expression.exprs != null ? expression.exprs.stream().flatMap(CODE_ELEM_TO_STREAM::apply)
                : Stream.empty();
    }

    // private static Stream<CodeElem> toElemStream(ExprBody exprBody) {
    private static Stream<CodeElem> toElemStream(Expression expression, Code code) {
        Stream<CodeElem> descendants = getDescendants(expression);

        return Stream.concat(descendants, toElemStream(code));
    }

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

    public static Optional<ExprOp> getOp(Expression expression, String opName) {
        // Expect one child
        if (expression.exprs.size() != 1) {
            return Optional.empty();
        }

        Expression maybeOp = expression.exprs.get(0);
        if (!(maybeOp instanceof ExprOp)) {
            return Optional.empty();
        }

        ExprOp op = (ExprOp) maybeOp;

        return op.name.equals(opName) ? Optional.of(op) : Optional.empty();
    }

    public static ExprBody getBody(ExprOp function) {
        return function.exprs.stream()
                .filter(ExprBody.class::isInstance)
                .map(ExprBody.class::cast)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expected function to have a body: " + function));
    }

    // public static Stream<CodeElem> getElemsStream(CodeElem elem) {
    //
    // }

    /*
    public static Stream<Expression> getExpressionStream(Expression expression) {
        // Get expressions
        Stream<Expression> expressions = expression.exprs != null ? expression.exprs.stream() : Stream.empty();
    
        // Get code / statements
        Optional<Code> code = getCode(expression);
        // if (expression instanceof ExprBody) {
        // Code code = ((ExprBody) expression).code;
        // if (code == null) {
        // return Stream.empty();
        // }
        //
        // // Return the expressions inside the statements
        // }
    
        return expression.exprs.stream();
    }
    
    public static Optional<Code> getCode(Expression expression) {
        if (expression instanceof ExprBody) {
            return Optional.of(((ExprBody) expression).code);
        }
    
        return Optional.empty();
    }
    
    public static Stream<Expression> getDescendantsAndSelfStream(Expression expression) {
        return Stream.concat(Stream.of(expression), CodeElems.getDescendantsStream(expression));
    }
    
    public static Stream<Expression> getDescendantsStream(Expression expression) {
        return CodeElems.getExpressionStream(expression).flatMap(expr -> CodeElems.getDescendantsAndSelfStream(expr));
    }
    */

}
