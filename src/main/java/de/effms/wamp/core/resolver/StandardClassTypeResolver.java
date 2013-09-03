package de.effms.wamp.core.resolver;

import java.lang.reflect.Type;

public class StandardClassTypeResolver implements ClassTypeResolverInterface
{
    private final Type resolveTo;

    public StandardClassTypeResolver(Type resolveTo)
    {
        this.resolveTo = resolveTo;
    }

    public Type tryResolve(String message)
    {
        return this.resolveTo;
    }
}
