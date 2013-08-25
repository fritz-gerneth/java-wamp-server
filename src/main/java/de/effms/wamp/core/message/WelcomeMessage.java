package de.effms.wamp.core.message;

public class WelcomeMessage implements Message
{
    private String sessionId;

    private int protocolVersion;

    private String serverIdent;

    public WelcomeMessage(String serverIdent)
    {
        this(serverIdent, 1);
    }

    public WelcomeMessage(String serverIdent, int protocolVersion)
    {
        this.protocolVersion = protocolVersion;
        this.serverIdent = serverIdent;
    }

    public WelcomeMessage(String sessionId, int protocolVersion, String serverIdent)
    {
        this.sessionId = sessionId;
        this.protocolVersion = protocolVersion;
        this.serverIdent = serverIdent;
    }

    public int getMessageCode()
    {
        return Message.WELCOME;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public int getProtocolVersion()
    {
        return this.protocolVersion;
    }

    public String getServerIdent()
    {
        return this.serverIdent;
    }
}
