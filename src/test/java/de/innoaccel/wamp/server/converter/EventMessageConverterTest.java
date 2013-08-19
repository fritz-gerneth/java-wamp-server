package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.message.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventMessageConverterTest
{
    private EventMessageConverter converter;

    @Before
    public void setUp()
    {
        this.converter = new EventMessageConverter();
    }

    @Test
    public void canConvertEventMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.EVENT));
    }

    @Test
    public void canNotConvertOtherMessage()
    {
        Assert.assertFalse(this.converter.canConvert(Message.CALL_ERROR));
    }
}
