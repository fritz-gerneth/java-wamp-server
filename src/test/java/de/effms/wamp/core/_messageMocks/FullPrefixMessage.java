package de.effms.wamp.core._messageMocks;

import de.effms.wamp.core.message.MessageType;

public class FullPrefixMessage extends PartialPrefixMessage
{
    private String uri;

    private MessageType messageType;

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public MessageType getMessageType()
    {
        return messageType;
    }

    public void setMessageType(MessageType messageType)
    {
        this.messageType = messageType;
    }
}
