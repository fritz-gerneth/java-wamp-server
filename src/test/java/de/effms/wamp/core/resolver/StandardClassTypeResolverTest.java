package de.effms.wamp.core.resolver;

import de.effms.wamp.core.Websocket;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StandardClassTypeResolverTest
{
    private StandardClassTypeResolver resolver;

    @Mocked
    Websocket socket;

    @Before
    public void setUp()
    {
        this.resolver = new StandardClassTypeResolver(StandardClassTypeResolverTest.class);
    }

    @Test
    public void returnsTypeOfConstructionOnAnyInput()
    {
        Assert.assertEquals(StandardClassTypeResolverTest.class, this.resolver.tryResolve("test", this.socket));
        Assert.assertEquals(StandardClassTypeResolverTest.class, this.resolver.tryResolve(null, this.socket));
    }
}
