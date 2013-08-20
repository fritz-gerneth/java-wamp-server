package de.innoaccel.wamp.server.converter;

public class MessageParseException extends Exception
{
    private String message;

    public MessageParseException(String message)
    {
        super("Cannot parse message");

        this.message = message;
    }

    public String getRawMessage()
    {
        return this.message;
    }
}
