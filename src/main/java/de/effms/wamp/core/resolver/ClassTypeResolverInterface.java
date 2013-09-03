package de.effms.wamp.core.resolver;

import java.lang.reflect.Type;

public interface ClassTypeResolverInterface
{
    public Type tryResolve(String message);
}
