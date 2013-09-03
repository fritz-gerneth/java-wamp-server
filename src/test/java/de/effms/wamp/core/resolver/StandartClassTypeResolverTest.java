package de.effms.wamp.core.resolver;

import de.effms.wamp.core.Websocket;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StandartClassTypeResolverTest
{
    private StandardClassTypeResolver resolver;

    @Mocked
    Websocket socket;

    @Before
    public void setUp()
    {
        this.resolver = new StandardClassTypeResolver(StandartClassTypeResolverTest.class);
    }

    @Test
    public void returnsTypeOfConstructionOnAnyInput()
    {
        Assert.assertEquals(StandartClassTypeResolverTest.class, this.resolver.tryResolve("test", this.socket));
        Assert.assertEquals(StandartClassTypeResolverTest.class, this.resolver.tryResolve(null, this.socket));
    }
}
