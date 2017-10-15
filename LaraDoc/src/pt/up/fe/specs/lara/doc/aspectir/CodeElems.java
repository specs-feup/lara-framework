package pt.up.fe.specs.lara.doc.aspectir;

import org.lara.interpreter.aspectir.ExprLiteral;
import org.lara.interpreter.aspectir.Expression;

import com.google.common.base.Preconditions;

import pt.up.fe.specs.util.providers.StringProvider;

public class CodeElems {

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
}
