package de.effms.wamp.core.resolver;

import java.lang.reflect.Type;

public interface ClassTypeResolverInterface
{
    public Class tryResolve(String message);
}
