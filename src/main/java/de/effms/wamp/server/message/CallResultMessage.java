package de.effms.wamp.server.message;

public class CallResultMessage implements Message
{
    private String callId;

    private Object result;

    public CallResultMessage(String callId, Object result)
    {
        this.callId = callId;
        this.result = result;
    }

    @Override
    public int getMessageCode()
    {
        return Message.CALL_RESULT;
    }

    public String getCallId()
    {
        return this.callId;
    }

    public Object getResult()
    {
        return this.result;
    }
}
