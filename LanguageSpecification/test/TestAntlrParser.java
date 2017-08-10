/**
 * Copyright 2016 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class TestAntlrParser {
    /*
    extends LanguageSpecificationBaseListener {
    
    public static void main(String[] args) {
    String input = IoUtils.read(new File("test/test.ls"));
    String s = "klsdfjkaldsfj\\'";
    ANTLRInputStream in = new ANTLRInputStream(input);
    LanguageSpecificationLexer lexer = new LanguageSpecificationLexer(in);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    LanguageSpecificationParser parser = new LanguageSpecificationParser(tokens);
    
    parser.addParseListener(new TestAntlrParser());
    
    System.out.println("-->" + parser.file().getChild(0).getChildCount());
    }
    /*
    @Override
    public void enterFile(FileContext ctx) {
    
    	super.enterFile(ctx);
    }
    
    @Override
    public void enterRoot(RootContext ctx) {
    	System.out.println("ROOT ENTER: " + ctx.getText() + "\nChild:" + ctx.getChildCount());
    }
    
    @Override
    public void exitRoot(RootContext ctx) {
    	assert ctx.getChild(0).getText().equals("root");
    	String targetJoinpoint = ctx.getChild(1).getText();
    
    	String alias = null;
    	if (ctx.getChildCount() > 2) {
    	    assert ctx.getChildCount() == 4;
    	    alias = ctx.getChild(3).getText();
    	}
    
    	RootNode root = new RootNode(targetJoinpoint, alias);
    	System.out.println("ROOT " + root);
    	System.out.println("ROOT EXIT: " + ctx.getText() + "\nChild:" + ctx.children);
    
    }*/
}
