package de.effms.wamp.server.dispatcher;

import de.effms.wamp.server.message.Message;

public interface DispatcherInterface
{
    public void dispatch( Message message);
}
