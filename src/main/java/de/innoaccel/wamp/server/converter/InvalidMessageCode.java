package de.innoaccel.wamp.server.converter;

public class InvalidMessageCode extends Exception
{
    private int messageCode;

    public InvalidMessageCode(int messageCode)
    {
        super("Invalid message code");

        this.messageCode = messageCode;
    }
}
