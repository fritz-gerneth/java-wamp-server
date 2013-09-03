package de.effms.wamp.core.resolver;

import de.effms.wamp.core.Websocket;

import java.lang.reflect.Type;

public interface ClassTypeResolverInterface
{
    public Class tryResolve(String message, Websocket socket);
}
