package de.effms.wamp.core.resolver;

public class StandardClassTypeResolver implements ClassTypeResolverInterface
{
    private final Class resolveTo;

    public StandardClassTypeResolver(Class resolveTo)
    {
        this.resolveTo = resolveTo;
    }

    public Class tryResolve(String message)
    {
        return this.resolveTo;
    }
}
