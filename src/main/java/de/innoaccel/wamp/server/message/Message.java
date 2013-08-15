package de.innoaccel.wamp.server.message;

public interface Message
{
    public static final int WELCOME = 0;

    public int getMessageCode();
}
