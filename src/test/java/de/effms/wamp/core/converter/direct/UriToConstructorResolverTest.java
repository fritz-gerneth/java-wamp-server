package de.effms.wamp.core.converter.direct;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

public class UriToConstructorResolverTest
{
    private UriToConstructorResolver factory;

    @Before
    public void setUp()
    {
        this.factory = new UriToConstructorResolver();
    }

    @Test(expected = InvalidMessageException.class)
    public void getInstanceThrowsExceptionIfNoConstructorsAreRegisteredForURI() throws InvalidMessageException
    {
        this.factory.getConstructor("invalid uri", null);
    }

    @Test
    public void getConstructorReturnsConstructorWhenParameterTypesMatch() throws InvalidMessageException
    {
        Constructor constructor = ConstructorClass.class.getConstructors()[1];

        this.factory.registerConstructor("uri", constructor);

        Type[] types = {String.class, int.class};
        Assert.assertSame(constructor, this.factory.getConstructor("uri", types));
    }

    @Test
    public void getConstructorCanMatchNullArgumentTypes() throws InvalidMessageException
    {
        Constructor constructor = ConstructorClass.class.getConstructors()[1];

        this.factory.registerConstructor("uri", constructor);

        Type[] types = {String.class, null};
        Assert.assertSame(constructor, this.factory.getConstructor("uri", types));
    }

    @Test
    public void getConstructorCanMatchMultipleConstructorsForSameURI() throws InvalidMessageException
    {
        Constructor constructor0 = ConstructorClass.class.getConstructors()[0];
        Constructor constructor1 = ConstructorClass.class.getConstructors()[1];

        this.factory.registerConstructor("uri", constructor0);
        this.factory.registerConstructor("uri", constructor1);

        Type[] types = {String.class, int.class};
        Assert.assertSame(constructor1, this.factory.getConstructor("uri", types));
    }

    @Test(expected = InvalidMessageException.class)
    public void getConstructorThrowsExceptionOnNoTypeMatch() throws InvalidMessageException
    {
        Constructor constructor = ConstructorClass.class.getConstructors()[1];

        this.factory.registerConstructor("uri", constructor);

        Type[] types = {String.class, boolean.class};
        Assert.assertSame(constructor, this.factory.getConstructor("uri", types));
    }
}
