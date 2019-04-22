package larac.code;

import java.util.stream.Collectors;

import larac.objects.Enums;
import larac.objects.Enums.BinaryOperator;
import larac.objects.Enums.UnaryOperator;
import pt.up.fe.specs.lara.aspectir.Argument;
import pt.up.fe.specs.lara.aspectir.CodeElem;
import pt.up.fe.specs.lara.aspectir.ExprCall;
import pt.up.fe.specs.lara.aspectir.ExprId;
import pt.up.fe.specs.lara.aspectir.ExprLiteral;
import pt.up.fe.specs.lara.aspectir.ExprOp;
import pt.up.fe.specs.lara.aspectir.Expression;
import pt.up.fe.specs.lara.aspectir.Parameter;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.classmap.FunctionClassMap;

public class AspectIrToLara {

    private final FunctionClassMap<CodeElem, String> codeGenerators;

    public AspectIrToLara() {
        this.codeGenerators = new FunctionClassMap<>();
        buildCodeGenerators();
    }

    private void buildCodeGenerators() {
        codeGenerators.put(Argument.class, this::getArgumentCode);
        codeGenerators.put(ExprId.class, this::getExprIdCode);
        codeGenerators.put(ExprLiteral.class, this::getExprLiteralCode);
        codeGenerators.put(ExprOp.class, this::getExprOpCode);
        codeGenerators.put(ExprCall.class, this::getExprCallCode);
        codeGenerators.put(Expression.class, this::getExpressionCode);
    }

    public String getCode(CodeElem codeElem) {
        return codeGenerators.apply(codeElem);
    }

    public String getExpressionCode(Expression expression) {
        // If Expression, xmltag should be defined? Implement as normal call
        if (expression.xmltag == null) {
            throw new RuntimeException("Generator not implemented for class " + expression.getClass() + ".\nXML IR: "
                    + CodeElems.toXml(expression));
        }

        switch (expression.xmltag) {
        case "property":
            return getPropertyCode(expression);
        default:
            throw new RuntimeException("Expression code not implement for xml tag '" + expression.xmltag + "'");
        }

    }

    private String getPropertyCode(Expression expression) {
        // SpecsCheck.checkArgument(expression.exprs.size() == 2,
        // () -> "Expected expression property to have two expressions, has " + expression.exprs.size());
        SpecsCheck.checkSize(expression.exprs, 2);

        return codeGenerators.apply(expression.exprs.get(0)) + "." + codeGenerators.apply(expression.exprs.get(1));
    }

    public String getExprIdCode(ExprId exprId) {
        return exprId.name;
    }

    public String getExprLiteralCode(ExprLiteral exprLiteral) {
        // if ("Integer.parseInt(System.getProperty(".equals(exprLiteral.value) || "stringify".equals(exprLiteral.value)
        // || "weaver.kadabra.concurrent".equals(exprLiteral.value)) {
        // System.out.println("EXPR LITERAL: " + CodeElems.toXml(exprLiteral));
        // System.out.println("PARENT:" + exprLiteral.parent);
        // }

        String value = exprLiteral.value;

        // HACK: Is there a better way?
        if ((exprLiteral.parent instanceof ExprOp || exprLiteral.parent instanceof Parameter)
                && "string".equals(exprLiteral.type)) {
            return "\"" + value + "\"";
        }

        return value;
    }

    public String getExprOpCode(ExprOp exprOp) {
        String op = exprOp.name;

        // Get operator
        if (exprOp.exprs.size() == 1) {
            // Special case: suffix ops
            if (op.equals("INCS")) {
                return getCode(exprOp.exprs.get(0)) + "++";
            }

            if (op.equals("DECS")) {
                return getCode(exprOp.exprs.get(0)) + "++";
            }

            UnaryOperator unaryOp = Enums.UnaryOperator.getHelper().fromValue(op);

            return unaryOp.getOp() + getCode(exprOp.exprs.get(0));
            // return getUnaryOpCode(exprOp, unaryOp);
        }

        if (exprOp.exprs.size() == 2) {
            BinaryOperator binaryOp = Enums.BinaryOperator.getHelper().fromValue(op);
            return getCode(exprOp.exprs.get(0)) + " " + binaryOp.getOp() + " " + getCode(exprOp.exprs.get(1));
        }

        throw new RuntimeException(
                "Expected operator two have one or two parameters, it has '" + exprOp.exprs.size() + "'");

    }

    public String getExprCallCode(ExprCall exprCall) {
        String args = exprCall.arguments.stream().map(this::getCode).collect(Collectors.joining(", "));

        SpecsCheck.checkSize(exprCall.method.exprs, 1);
        Expression methodExpr = exprCall.method.exprs.get(0);

        SpecsCheck.checkSize(methodExpr.exprs, 2);

        Expression instance = methodExpr.exprs.get(0);
        Expression memberName = methodExpr.exprs.get(1);

        String exprCode = getCode(instance) + "." + getCode(memberName) + "(" + args + ")";
        // System.out.println("EXPR CALL CODE: " + exprCode);

        return exprCode;

        // throw new RuntimeException("Generator not implemented for class " + exprCall.getClass() + ".\nXML IR: "
        // + CodeElems.toXml(exprCall));
    }

    public String getArgumentCode(Argument argument) {
        SpecsCheck.checkSize(argument.exprs, 1);

        return getCode(argument.exprs.get(0));
    }

}
