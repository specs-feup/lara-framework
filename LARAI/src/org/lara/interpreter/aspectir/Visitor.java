package org.lara.interpreter.aspectir;

/****************************** Interface Visitor ******************************/
public interface Visitor {
	public void visit(CodeElem codeElem);
	public void visit(Expression expression);
	public void visit(Argument argument);
	public void visit(ExprCall exprCall);
	public void visit(ExprId exprId);
	public void visit(ExprKey exprKey);
	public void visit(ExprLiteral exprLiteral);
	public void visit(ExprOp exprOp);
	public void visit(Parameter parameter);
	public void visit(ParameterList parameterList);
	public void visit(ParameterSection parameterSection);
	public void visit(Statement statement);
	public void visit(Code code);
	public void visit(Aspect aspect);
	public void visit(Aspects aspects);
	public void visit(ExprBody exprBody);
}
/********************************************************************************/
