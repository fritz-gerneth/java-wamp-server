package de.effms.wamp.server.message;

public class UnsubscribeMessage implements Message
{
    private String topicURI;

    public UnsubscribeMessage(String topicURI)
    {
        this.topicURI = topicURI;
    }

    @Override
    public int getMessageCode()
    {
        return Message.UNSUBSCRIBE;
    }

    public String getTopicURI()
    {
        return this.topicURI;
    }
}
