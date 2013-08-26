package de.effms.wamp.core.converter.direct;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;

public class UriToConstructorResolver
{
    private Map<String, List<Constructor>> registeredConstructors;

    public UriToConstructorResolver()
    {
        this.registeredConstructors = new HashMap<String, List<Constructor>>();
    }

    public void registerConstructor(String uri, Constructor constructor)
    {
        if (!this.registeredConstructors.containsKey(uri)) {
            this.registeredConstructors.put(uri, new ArrayList<Constructor>());
        }

        List<Constructor> uriSpecificConstructors = this.registeredConstructors.get(uri);
        if (!uriSpecificConstructors.contains(constructor)) {
            uriSpecificConstructors.add(constructor);
        }
    }

    public Constructor getConstructor(String uri, Type[] argumentTypes) throws InvalidMessageException
    {
        List<Constructor> availableConstructors = this.registeredConstructors.get(uri);
        if (null == availableConstructors) {
            throw new InvalidMessageException("No message registered for " + uri);
        }

        for (Constructor constructor: availableConstructors) {
            if (this.compareTypes(constructor.getParameterTypes(), argumentTypes)) {
                return constructor;
            }
        }
        throw new InvalidMessageException("No constructor registered for given types");
    }

    private boolean compareTypes(Type[] source, Type[] target)
    {
        if (source.length != target.length) {
            return false;
        }

        for (int i = 0; i < source.length; i++) {
            if (source[i] != target[i] && null != target[i]) {
                return false;
            }
        }
        return true;
    }
}
