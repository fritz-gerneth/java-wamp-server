package de.effms.wamp.server.dispatcher;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.dispatcher.DispatcherInterface;
import de.effms.wamp.core.message.PrefixMessage;

abstract public class PrefixMessageHandlingDispatcher implements DispatcherInterface
{
    public void dispatch(PrefixMessage message, Websocket socket)
    {
        socket.addPrefix(message.getPrefix(), message.getURI());
    }
}
