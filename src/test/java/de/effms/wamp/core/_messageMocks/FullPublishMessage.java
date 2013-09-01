package de.effms.wamp.core._messageMocks;

import de.effms.wamp.core.message.MessageType;

import java.util.List;

public class FullPublishMessage extends PartialPublishMessage
{
    private MessageType messageType;

    private String topicUri;

    private List<String> exclude;

    private List<String> eligible;

    public MessageType getMessageType()
    {
        return messageType;
    }

    public void setMessageType(MessageType messageType)
    {
        this.messageType = messageType;
    }

    public String getTopicUri()
    {
        return topicUri;
    }

    public void setTopicUri(String topicUri)
    {
        this.topicUri = topicUri;
    }

    public List<String> getExclude()
    {
        return exclude;
    }

    public void setExclude(List<String> exclude)
    {
        this.exclude = exclude;
    }

    public List<String> getEligible()
    {
        return eligible;
    }

    public void setEligible(List<String> eligible)
    {
        this.eligible = eligible;
    }
}
