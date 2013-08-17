package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.message.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnsubscribeMessageConverterTest
{
    private UnsubscribeMessageConverter converter;

    @Before
    public void setUp()
    {
        this.converter = new UnsubscribeMessageConverter();
    }

    @Test
    public void canConvertUnsubscribeMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.UNSUBSCRIBE));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.SUBSCRIBE));
    }
}
