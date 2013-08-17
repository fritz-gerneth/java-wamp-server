package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.message.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PrefixMessageConverterTest
{
    private PrefixMessageConverter converter;

    @Before
    public void setUp()
    {
        this.converter = new PrefixMessageConverter();
    }

    @Test
    public void canConvertPrefixMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.PREFIX));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.WELCOME));
    }
}
