package de.innoaccel.wamp.server.converter;

public class InvalidMessageCodeException extends Exception
{
    private int messageCode;

    public InvalidMessageCodeException(int messageCode)
    {
        super("Invalid message code");

        this.messageCode = messageCode;
    }
}
