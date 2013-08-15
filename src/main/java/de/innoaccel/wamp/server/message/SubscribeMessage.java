package de.innoaccel.wamp.server.message;

public class SubscribeMessage implements Message
{
    private String topicURI;

    public SubscribeMessage(String topicURI)
    {
        this.topicURI = topicURI;
    }

    @Override
    public int getMessageCode()
    {
        return Message.SUBSCRIBE;
    }

    public String getTopicURI()
    {
        return this.topicURI;
    }
}
