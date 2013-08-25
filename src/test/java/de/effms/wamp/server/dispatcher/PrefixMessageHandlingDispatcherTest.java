package de.effms.wamp.server.dispatcher;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.PrefixMessage;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PrefixMessageHandlingDispatcherTest
{
    protected PrefixMessageHandlingDispatcher dispatcher;

    @Before
    public void setUp()
    {
        this.dispatcher = new PrefixMessageHandlingDispatcherImpl();
    }

    @Test
    public void addsPrefixWithPrefixOfMessageToSocket(final PrefixMessage message, final Websocket socket)
    {
        new NonStrictExpectations() {{
            message.getPrefix(); result = "prefix";
            message.getURI(); result = "URI";
        }};

        this.dispatcher.dispatch(message, socket);

        new Verifications() {{
            socket.addPrefix("prefix", anyString);
        }};
    }

    @Test
    public void addsPrefixWithURIOfMessageToSocket(final PrefixMessage message, final Websocket socket)
    {
        new NonStrictExpectations() {{
            message.getPrefix(); result = "prefix";
            message.getURI(); result = "URI";
        }};

        this.dispatcher.dispatch(message, socket);

        new Verifications() {{
            socket.addPrefix(anyString, "URI");
        }};
    }

    private class PrefixMessageHandlingDispatcherImpl extends PrefixMessageHandlingDispatcher
    {
        @Override
        public void dispatch(Message message, Websocket socket)
        {
            throw new NotImplementedException();
        }
    }
}
