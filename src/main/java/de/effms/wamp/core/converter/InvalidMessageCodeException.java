package de.effms.wamp.core.converter;

public class InvalidMessageCodeException extends Exception
{
    private int messageCode;

    public InvalidMessageCodeException(int messageCode)
    {
        super("Invalid message code");

        this.messageCode = messageCode;
    }
}
