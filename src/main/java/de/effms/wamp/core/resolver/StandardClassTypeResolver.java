package de.effms.wamp.core.resolver;

import de.effms.wamp.core.Websocket;

public class StandardClassTypeResolver implements ClassTypeResolverInterface
{
    private final Class resolveTo;

    public StandardClassTypeResolver(Class resolveTo)
    {
        this.resolveTo = resolveTo;
    }

    public Class tryResolve(String message, Websocket socket)
    {
        return this.resolveTo;
    }
}
