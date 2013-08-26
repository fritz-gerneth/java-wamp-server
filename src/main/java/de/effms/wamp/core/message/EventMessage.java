package de.effms.wamp.core.message;

public class EventMessage implements Message
{
    private String topicURI;

    private Object payload;

    public EventMessage(String topicURI, Object payload)
    {
        this.topicURI = topicURI;
        this.payload = payload;
    }

    @Override
    public int getMessageCode()
    {
        return Message.EVENT;
    }

    public String getTopicURI()
    {
        return topicURI;
    }

    public Object getPayload()
    {
        return payload;
    }
}
