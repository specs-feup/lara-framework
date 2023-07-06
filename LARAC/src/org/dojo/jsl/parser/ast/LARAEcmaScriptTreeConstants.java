/* Generated By:JavaCC: Do not edit this line. LARAEcmaScriptTreeConstants.java Version 5.0 */
package org.dojo.jsl.parser.ast;

public interface LARAEcmaScriptTreeConstants
{
  public int JJTVOID = 0;
  public int JJTIDENTIFIER = 1;
  public int JJTSTART = 2;
  public int JJTASSIGNMENTEXPRESSION = 3;
  public int JJTIMPORT = 4;
  public int JJTFILEPATH = 5;
  public int JJTCODEDEF = 6;
  public int JJTFORMALPARAMETERLIST = 7;
  public int JJTINPUT = 8;
  public int JJTOUTPUT = 9;
  public int JJTSTATIC = 10;
  public int JJTINITIALIZE = 11;
  public int JJTFINALIZE = 12;
  public int JJTCHECK = 13;
  public int JJTASPECTDEF = 14;
  public int JJTSELECT = 15;
  public int JJTJOIN = 16;
  public int JJTPOINTCUT = 17;
  public int JJTORFILTERSEXPR = 18;
  public int JJTPOINTCUTFILTERS = 19;
  public int JJTANDFILTERSEXPR = 20;
  public int JJTFILTER = 21;
  public int JJTAROUNDAPPLY = 22;
  public int JJTAPPLIES = 23;
  public int JJTAPPLY = 24;
  public int JJTTO = 25;
  public int JJTACTION = 26;
  public int JJTCOMPOSITEREFERENCE = 27;
  public int JJTCALL = 28;
  public int JJTRUN = 29;
  public int JJTCMD = 30;
  public int JJTINSERT = 31;
  public int JJTPERFORM = 32;
  public int JJTFUNCTIONCALLPARAMETERS = 33;
  public int JJTOUTPUTACT = 34;
  public int JJTDEFINE = 35;
  public int JJTCONDITION = 36;
  public int JJTFOR = 37;
  public int JJTTHISREFERENCE = 38;
  public int JJTPARENEXPRESSION = 39;
  public int JJTLITERAL = 40;
  public int JJTARRAYLITERAL = 41;
  public int JJTEMPTYPOSITIONS = 42;
  public int JJTOBJECTLITERAL = 43;
  public int JJTLITERALFIELD = 44;
  public int JJTALLOCATIONEXPRESSION = 45;
  public int JJTPROPERTYVALUEREFERENCE = 46;
  public int JJTPROPERTYIDENTIFIERREFERENCE = 47;
  public int JJTNAMEDARGUMENT = 48;
  public int JJTPOSTFIXEXPRESSION = 49;
  public int JJTOPERATOR = 50;
  public int JJTUNARYEXPRESSION = 51;
  public int JJTBINARYEXPRESSIONSEQUENCE = 52;
  public int JJTANDEXPRESSIONSEQUENCE = 53;
  public int JJTOREXPRESSIONSEQUENCE = 54;
  public int JJTCONDITIONALEXPRESSION = 55;
  public int JJTEXPRESSIONLIST = 56;
  public int JJTBLOCK = 57;
  public int JJTSTATEMENTLIST = 58;
  public int JJTVARIABLESTATEMENT = 59;
  public int JJTVARIABLEDECLARATIONLIST = 60;
  public int JJTVARIABLEDECLARATION = 61;
  public int JJTEMPTYEXPRESSION = 62;
  public int JJTEMPTYSTATEMENT = 63;
  public int JJTEXPRESSIONSTATEMENT = 64;
  public int JJTIFSTATEMENT = 65;
  public int JJTDOSTATEMENT = 66;
  public int JJTWHILESTATEMENT = 67;
  public int JJTFORVARINSTATEMENT = 68;
  public int JJTFORINSTATEMENT = 69;
  public int JJTPREASSIGNMENTLIST = 70;
  public int JJTFORCONDITIONLIST = 71;
  public int JJTPOSTASSIGNMENTLIST = 72;
  public int JJTFORSTATEMENT = 73;
  public int JJTFORVARSTATEMENT = 74;
  public int JJTCONTINUESTATEMENT = 75;
  public int JJTBREAKSTATEMENT = 76;
  public int JJTRETURNSTATEMENT = 77;
  public int JJTYIELDSTATEMENT = 78;
  public int JJTYIELDSTAR = 79;
  public int JJTWITHSTATEMENT = 80;
  public int JJTSWITCHSTATEMENT = 81;
  public int JJTCASEGROUPS = 82;
  public int JJTCASEGROUP = 83;
  public int JJTCASEGUARD = 84;
  public int JJTLABELLEDSTATEMENT = 85;
  public int JJTTHROWSTATEMENT = 86;
  public int JJTTRYSTATEMENT = 87;
  public int JJTCATCHCLAUSE = 88;
  public int JJTFINALLYCLAUSE = 89;
  public int JJTFUNCTIONDECLARATION = 90;
  public int JJTGENERATORFUNCTIONDECLARATION = 91;
  public int JJTARROWFUNCTIONEXPRESSION = 92;
  public int JJTFUNCTIONEXPRESSION = 93;
  public int JJTGENERATORFUNCTIONEXPRESSION = 94;
  public int JJTJAVASCRIPT = 95;


  public String[] jjtNodeName = {
    "void",
    "Identifier",
    "Start",
    "AssignmentExpression",
    "Import",
    "FilePath",
    "CodeDef",
    "FormalParameterList",
    "Input",
    "Output",
    "Static",
    "Initialize",
    "Finalize",
    "Check",
    "AspectDef",
    "Select",
    "Join",
    "Pointcut",
    "OrFiltersExpr",
    "PointcutFilters",
    "ANDFiltersExpr",
    "Filter",
    "AroundApply",
    "Applies",
    "Apply",
    "To",
    "Action",
    "CompositeReference",
    "Call",
    "Run",
    "Cmd",
    "Insert",
    "Perform",
    "FunctionCallParameters",
    "OutputAct",
    "Define",
    "Condition",
    "For",
    "ThisReference",
    "ParenExpression",
    "Literal",
    "ArrayLiteral",
    "EmptyPositions",
    "ObjectLiteral",
    "LiteralField",
    "AllocationExpression",
    "PropertyValueReference",
    "PropertyIdentifierReference",
    "NamedArgument",
    "PostfixExpression",
    "Operator",
    "UnaryExpression",
    "BinaryExpressionSequence",
    "AndExpressionSequence",
    "OrExpressionSequence",
    "ConditionalExpression",
    "ExpressionList",
    "Block",
    "StatementList",
    "VariableStatement",
    "VariableDeclarationList",
    "VariableDeclaration",
    "EmptyExpression",
    "EmptyStatement",
    "ExpressionStatement",
    "IfStatement",
    "DoStatement",
    "WhileStatement",
    "ForVarInStatement",
    "ForInStatement",
    "PreAssignmentList",
    "ForConditionList",
    "PostAssignmentList",
    "ForStatement",
    "ForVarStatement",
    "ContinueStatement",
    "BreakStatement",
    "ReturnStatement",
    "YieldStatement",
    "YieldStar",
    "WithStatement",
    "SwitchStatement",
    "CaseGroups",
    "CaseGroup",
    "CaseGuard",
    "LabelledStatement",
    "ThrowStatement",
    "TryStatement",
    "CatchClause",
    "FinallyClause",
    "FunctionDeclaration",
    "GeneratorFunctionDeclaration",
    "ArrowFunctionExpression",
    "FunctionExpression",
    "GeneratorFunctionExpression",
    "JavaScript",
  };
}
/* JavaCC - OriginalChecksum=6a04a51ca8e8abd343be994a4a979c51 (do not edit this line) */