package de.effms.wamp.core.message;

import java.util.ArrayList;
import java.util.List;

public class CallMessage implements Message
{
    private String callId;

    private String procURI;

    private List<Object> args;

    public CallMessage(String callId, String procURI)
    {
        this(callId, procURI, new ArrayList<Object>());
    }

    public CallMessage(String callId, String procURI, List<Object> args)
    {
        this.callId = callId;
        this.procURI = procURI;
        this.args = args;
    }

    @Override
    public int getMessageCode()
    {
        return Message.CALL;
    }

    public String getCallId()
    {
        return this.callId;
    }

    public String getProcURI()
    {
        return this.procURI;
    }

    public List<Object> getArgs()
    {
        return this.args;
    }
}
