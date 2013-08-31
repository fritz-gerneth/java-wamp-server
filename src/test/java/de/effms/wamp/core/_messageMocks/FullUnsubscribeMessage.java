package de.effms.wamp.core._messageMocks;

import de.effms.wamp.core.message.MessageType;

public class FullUnsubscribeMessage extends PartialSubscribeMessage
{
    private MessageType messageType;

    public MessageType getMessageType()
    {
        return messageType;
    }

    public void setMessageType(MessageType messageType)
    {
        this.messageType = messageType;
    }
}
