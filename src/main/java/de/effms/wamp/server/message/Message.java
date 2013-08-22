package de.effms.wamp.server.message;

public interface Message
{
    public static final int INVALID = -1;

    public static final int WELCOME = 0;

    public static final int PREFIX = 1;

    public static final int CALL = 2;

    public static final int CALL_RESULT = 3;

    public static final int CALL_ERROR = 4;

    public static final int SUBSCRIBE = 5;

    public static final int UNSUBSCRIBE = 6;

    public static final int PUBLISH = 7;

    public static final int EVENT = 8;

    public int getMessageCode();
}
