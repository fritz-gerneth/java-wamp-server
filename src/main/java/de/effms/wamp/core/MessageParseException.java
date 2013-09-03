package de.effms.wamp.core;

import java.io.IOException;

public class MessageParseException extends IOException
{
    public MessageParseException() {}

    public MessageParseException(String message)
    {
        super(message);
    }

    public MessageParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MessageParseException(Throwable cause)
    {
        super(cause);
    }
}
