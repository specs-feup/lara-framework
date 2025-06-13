package org.lara.interpreter.weaver.defaultweaver.exceptions;

import pt.up.fe.specs.tools.lara.exception.BaseException;

/**
 * This class can be used as the exception of this weaver in order to follow the message pretty print used by the interpreter
 */
public class DefaultWeaverException extends BaseException {

    //Fields
    private static final long serialVersionUID = 1L;
    private final String event;

    //Constructors
    /**
     * Create a new exception with the cause and the triggering event
     * @param event the event that caused the exception
     * @param cause the cause of this exception
     */
    public DefaultWeaverException(String event, Throwable cause){
        super(cause);
        this.event = event;
    }
    //Methods
    /**
     * 
     * @see pt.up.fe.specs.tools.lara.exception.BaseException#generateSimpleMessage()
     */
    @Override
    protected String generateSimpleMessage() {
        return " [DefaultWeaver] " +this.event;
    }

    /**
     * 
     * @see pt.up.fe.specs.tools.lara.exception.BaseException#generateMessage()
     */
    @Override
    protected String generateMessage() {
        return "Exception in "+this.generateSimpleMessage();
    }
}
