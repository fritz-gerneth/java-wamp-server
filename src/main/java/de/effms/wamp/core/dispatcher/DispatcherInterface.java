package de.effms.wamp.core.dispatcher;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;

public interface DispatcherInterface
{
    public void dispatch(Message message, Websocket socket);
}
