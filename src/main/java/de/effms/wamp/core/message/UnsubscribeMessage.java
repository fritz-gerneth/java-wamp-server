package de.effms.wamp.core.message;

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
