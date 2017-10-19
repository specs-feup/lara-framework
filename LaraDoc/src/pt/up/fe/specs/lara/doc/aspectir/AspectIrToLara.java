package pt.up.fe.specs.lara.doc.aspectir;

import org.lara.interpreter.aspectir.CodeElem;
import org.lara.interpreter.aspectir.ExprId;
import org.lara.interpreter.aspectir.ExprLiteral;
import org.lara.interpreter.aspectir.ExprOp;
import org.lara.interpreter.aspectir.Expression;

import com.google.common.base.Preconditions;

import larac.objects.Enums;
import larac.objects.Enums.BinaryOperator;
import larac.objects.Enums.UnaryOperator;
import pt.up.fe.specs.util.classmap.FunctionClassMap;

public class AspectIrToLara {

    private final FunctionClassMap<CodeElem, String> codeGenerators;

    public AspectIrToLara() {
        this.codeGenerators = new FunctionClassMap<>();
        buildCodeGenerators();
    }

    private void buildCodeGenerators() {
        codeGenerators.put(ExprId.class, this::getExprIdCode);
        codeGenerators.put(ExprLiteral.class, this::getExprLiteralCode);
        codeGenerators.put(ExprOp.class, this::getExprOpCode);
        codeGenerators.put(Expression.class, this::getExpressionCode);
    }

    public String getCode(CodeElem codeElem) {
        return codeGenerators.apply(codeElem);
    }

    public String getExpressionCode(Expression expression) {
        // If Expression, xmltag should be defined?
        if (expression.xmltag == null) {
            throw new RuntimeException("Generator not implemented for class " + expression.getClass() + ".\nXML IR:"
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
        Preconditions.checkArgument(expression.exprs.size() == 2,
                "Expected expression property to have two expressions, has " + expression.exprs.size());

        return codeGenerators.apply(expression.exprs.get(0)) + "." + codeGenerators.apply(expression.exprs.get(1));
    }

    public String getExprIdCode(ExprId exprId) {
        return exprId.name;
    }

    public String getExprLiteralCode(ExprLiteral exprLiteral) {
        return exprLiteral.value;
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

            UnaryOperator unaryOp = Enums.UnaryOperator.getHelper().valueOf(op);

            return unaryOp.getOp() + getCode(exprOp.exprs.get(0));
            // return getUnaryOpCode(exprOp, unaryOp);
        }

        if (exprOp.exprs.size() == 2) {
            BinaryOperator binaryOp = Enums.BinaryOperator.getHelper().valueOf(op);

            return getCode(exprOp.exprs.get(0)) + " " + binaryOp.getOp() + " " + getCode(exprOp.exprs.get(1));
        }

        throw new RuntimeException(
                "Expected operator two have one or two parameters, it has '" + exprOp.exprs.size() + "'");

    }

}
