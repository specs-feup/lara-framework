/* Generated By:JJTree: Do not edit this line. ASTRun.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.objects.Enums.AssignOperator;
import larac.objects.Enums.Types;
import larac.utils.OrganizeUtils;

public @SuppressWarnings("all") class ASTRun extends SimpleNode {
	private String toolName;
	private static final int numOfJSRunMethod = 6;

	public ASTRun(int id) {
		super(id);
	}

	public ASTRun(LARAEcmaScript p, int id) {
		super(p, id);
	}

	@Override
	public Object organize(Object obj) {
		int pos = 0;
		if (!(children[pos] instanceof ASTFunctionCallParameters)) {
			pos++;
		}

		final ASTFunctionCallParameters args = (ASTFunctionCallParameters) children[pos];
		args.organize(obj);
		if (args.jjtGetNumChildren() == 0 && toolName == null) {
			throw newException("No argument was given in 'run'. At least 'tool' is required to execute.");
		}
		if (toolName != null) {

			return null;
		}
		final int remainingArgs = ASTRun.numOfJSRunMethod - args.jjtGetNumChildren();

		if (remainingArgs < 0) {
			getLara().warnln("A total of " + args.jjtGetNumChildren()
					+ " arguments was used in the tool execution, the required number of arguments are: "
					+ (ASTRun.numOfJSRunMethod - 1) + ". This may lead to issues in the tool execution.");
		} else if (remainingArgs == 0 && (pos < children.length)) {
			getLara().warnln(ASTRun.numOfJSRunMethod
					+ " arguments and a pipe in the 'run' method. However, the last argument overrides the used pipe.");
		}

		return null;
	}

	@Override
	public void toXML(Document doc, Element parent) {
		final Element statementEl = doc.createElement("statement");
		statementEl.setAttribute("coord", getCoords());
		statementEl.setAttribute("name", "expr");
		parent.appendChild(statementEl);

		final Element exprEl = doc.createElement("expression");
		statementEl.appendChild(exprEl);
		int pos = 0;
		Element current = exprEl;
		if (!(children[pos] instanceof ASTFunctionCallParameters)) {
			// Then it is a left hand side expression
			final Element opEl = doc.createElement("op");
			opEl.setAttribute("name", AssignOperator.ASSIGN.name());
			exprEl.appendChild(opEl);
			((SimpleNode) children[pos]).toXML(doc, opEl);
			pos++;
			current = opEl;
		}

		final Element callEl = doc.createElement("call");
		current.appendChild(callEl);
		final Element methodEl = doc.createElement("method");
		callEl.appendChild(methodEl);
		final Element runIDEl = doc.createElement("id");
		runIDEl.setAttribute("name", "run");
		methodEl.appendChild(runIDEl);
		final ASTFunctionCallParameters args = (ASTFunctionCallParameters) children[pos++];

		if (toolName != null) {
			toXMLOfNewVersion(doc, pos, callEl, args);
		} else { // old version was used

			toXMLOldVersion(doc, pos, callEl, args);
		}
	}

	/**
	 * Generate the arguments of the use of old run method
	 * 
	 * @param doc
	 * @param pos
	 * @param callEl
	 * @param args
	 */
	private void toXMLOldVersion(Document doc, int pos, Element callEl, ASTFunctionCallParameters args) {
		args.toXML(doc, callEl);
		final int remainingArgs = ASTRun.numOfJSRunMethod - args.jjtGetNumChildren();
		if (remainingArgs <= 0) {
			return;
		}

		final Element pipeEl = doc.createElement("argument");
		if (args.areNamed) {
			pipeEl.setAttribute("name", "pipe");
		} else {
			// Fill the missing arguments with 'undefined'
			for (int i = 0; i < remainingArgs - 1; i++) {
				final Element argEl = doc.createElement("argument");
				callEl.appendChild(argEl);
				OrganizeUtils.createLiteralUndefined(doc, argEl);
			}

		}

		callEl.appendChild(pipeEl);

		if (pos < children.length) {
			final SimpleNode pipeExpr = (SimpleNode) children[pos];
			pipeExpr.toXML(doc, pipeEl);
		} else {
			OrganizeUtils.createLiteralUndefined(doc, pipeEl);

		}
	}

	/**
	 * Generate the arguments of the use of new run method
	 */
	private void toXMLOfNewVersion(Document doc, int pos, Element callEl, ASTFunctionCallParameters args) {
		// Then is the new run version
		Element argEl = doc.createElement("argument");
		argEl.setAttribute("name", "tool");
		callEl.appendChild(argEl);
		Element litEl = doc.createElement("literal");
		litEl.setAttribute("type", Types.String.toString());
		litEl.setAttribute("value", toolName);
		argEl.appendChild(litEl);
		setArgumentsAsList(args, doc, callEl);

		argEl = doc.createElement("argument");
		argEl.setAttribute("name", "verbose");
		callEl.appendChild(argEl);
		litEl = doc.createElement("literal");
		litEl.setAttribute("type", Types.Int.toString());
		litEl.setAttribute("value", String.valueOf(2));
		argEl.appendChild(litEl);
		argEl = doc.createElement("argument");
		argEl.setAttribute("name", "pipe");
		callEl.appendChild(argEl);
		if (pos < children.length) {
			final SimpleNode pipeExpr = (SimpleNode) children[pos];
			pipeExpr.toXML(doc, argEl);
		} else {
			OrganizeUtils.createLiteralUndefined(doc, argEl);
		}
	}

	private void setArgumentsAsList(ASTFunctionCallParameters args, Document doc, Element parentEl) {
		final Element argEl = doc.createElement("argument");
		parentEl.appendChild(argEl);
		argEl.setAttribute("name", "args");
		final Element litEl = doc.createElement("literal");
		argEl.appendChild(litEl);
		if (args.areNamed) {
			litEl.setAttribute("type", "object");
			for (final Node arg : args.getChildren()) {
				final ASTNamedArgument na = (ASTNamedArgument) arg;
				final Element keyEl = doc.createElement("key");
				na.toXML(doc, keyEl);
				litEl.appendChild(keyEl);
			}
		} else {
			litEl.setAttribute("type", "array");
			int i = 0;
			for (final Node arg : args.getChildren()) {
				final Element keyEl = doc.createElement("key");
				keyEl.setAttribute("name", String.valueOf(i++));
				litEl.appendChild(keyEl);
				((SimpleNode) arg).toXML(doc, keyEl);
			}
		}

	}

	/**
	 * @return the toolName
	 */
	public String getToolName() {
		return toolName;
	}

	/**
	 * @param toolName
	 *            the toolName to set
	 */
	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

}
/*
 * JavaCC - OriginalChecksum=a983f8b6f04dcc338add9564fb6fdd01 (do not edit this
 * line)
 */
