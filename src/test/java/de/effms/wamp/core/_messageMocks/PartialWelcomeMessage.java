package de.effms.wamp.core._messageMocks;

public class PartialWelcomeMessage
{
    private String sessionId;

    private String serverIdent;

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getServerIdent()
    {
        return serverIdent;
    }

    public void setServerIdent(String serverIdent)
    {
        this.serverIdent = serverIdent;
    }
}
