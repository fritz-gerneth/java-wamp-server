package de.effms.wamp.core.message;

public class PrefixMessage implements Message
{
    private String prefix;

    private String uri;

    public PrefixMessage(String prefix, String uri)
    {
        this.prefix = prefix;
        this.uri = uri;
    }

    @Override
    public int getMessageCode()
    {
        return Message.PREFIX;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public String getURI()
    {
        return this.uri;
    }
}
