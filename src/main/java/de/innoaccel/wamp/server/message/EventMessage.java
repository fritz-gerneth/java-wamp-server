package de.innoaccel.wamp.server.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
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
