package de.effms.wamp.core._messageMocks;

import de.effms.wamp.core.message.MessageType;

public class FullWelcomeMessage extends PartialWelcomeMessage
{
    private MessageType messageType;

    private int protocolVersion;

    public MessageType getMessageType()
    {
        return messageType;
    }

    public void setMessageType(MessageType messageType)
    {
        this.messageType = messageType;
    }

    public int getProtocolVersion()
    {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion)
    {
        this.protocolVersion = protocolVersion;
    }
}
