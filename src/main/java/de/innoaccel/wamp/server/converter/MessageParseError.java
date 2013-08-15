package de.innoaccel.wamp.server.converter;

public class MessageParseError extends Exception
{
    private String message;

    public MessageParseError(String message)
    {
        super("Cannot parse message");

        this.message = message;
    }

    public String getRawMessage()
    {
        return this.message;
    }
}
