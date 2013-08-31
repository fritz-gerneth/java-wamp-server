package de.effms.wamp.core.message;

public enum MessageType
{
    INVALID(-1),
    WELCOME(0),
    PREFIX(1),
    CALL(2),
    CALL_RESULT(3),
    CALL_ERROR(4),
    SUBSCRIBE(5),
    UNSUBSCRIBE(6),
    PUBLISH(7),
    EVENT(8);

    private final int messageCode;

    private MessageType(int messageCode)
    {
        this.messageCode = messageCode;
    }

    public int getMessageCode()
    {
        return this.messageCode;
    }
}
