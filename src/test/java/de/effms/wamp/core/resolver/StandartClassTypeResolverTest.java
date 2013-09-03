package de.effms.wamp.core.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StandartClassTypeResolverTest
{
    private StandardClassTypeResolver resolver;

    @Before
    public void setUp()
    {
        this.resolver = new StandardClassTypeResolver(StandartClassTypeResolverTest.class);
    }

    @Test
    public void returnsTypeOfConstructionOnAnyInput()
    {
        Assert.assertEquals(StandartClassTypeResolverTest.class, this.resolver.tryResolve("test"));
        Assert.assertEquals(StandartClassTypeResolverTest.class, this.resolver.tryResolve(null));
    }
}
