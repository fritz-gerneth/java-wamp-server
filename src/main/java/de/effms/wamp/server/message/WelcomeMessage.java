package de.effms.wamp.server.message;

public class WelcomeMessage implements Message
{
    private String sessionId;

    private int protocolVersion;

    private String serverIdent;

    public WelcomeMessage()
    {}

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

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public int getProtocolVersion()
    {
        return this.protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion)
    {
        this.protocolVersion = protocolVersion;
    }

    public String getServerIdent()
    {
        return this.serverIdent;
    }

    public void setServerIdent(String serverIdent)
    {
        this.serverIdent = serverIdent;
    }
}
