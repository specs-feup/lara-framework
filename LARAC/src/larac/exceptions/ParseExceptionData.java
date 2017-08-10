package larac.exceptions;

import java.util.ArrayList;

import org.dojo.jsl.parser.ast.Node;
import org.dojo.jsl.parser.ast.ParseException;
import org.dojo.jsl.parser.ast.Token;

/**
 * This class is used to store the exception data, which will be stored in a {@link Node}.<br>
 * <B>Note:</B> This data may be used in the future for a syntax highlighter.
 * 
 * @author tiago
 *
 */
public class ParseExceptionData {

    private final ArrayList<Token> skippedTokens;
    private ParseException exception;
    private int skippedToTokenKind;

    public ParseExceptionData() {
	skippedTokens = new ArrayList<>();
	exception = null;
	skippedToTokenKind = -1;
    }

    public ParseExceptionData(ParseException e) {
	skippedTokens = new ArrayList<>();
	exception = e;
	skippedToTokenKind = -1;
    }

    public void addSkippedToken(Token token) {
	skippedTokens.add(token);
    }

    public ArrayList<Token> getSkippedTokens() {
	return skippedTokens;
    }

    public void setSkippedToToken(int kind) {
	skippedToTokenKind = kind;
    }

    public int getSkippedToToken() {
	return skippedToTokenKind;
    }

    public void setException(ParseException e) {
	exception = e;
    }

    public Throwable getException() {
	return exception;
    }

}