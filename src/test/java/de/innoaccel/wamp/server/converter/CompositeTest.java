package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import junit.framework.Assert;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

public class CompositeTest
{
    private Composite composite;

    @Mocked
    private Converter firstConverter;

    @Mocked
    private Converter secondConverter;

    @Before
    public void setUp()
    {
        LinkedList<Converter> l = new LinkedList<Converter>();
        l.add(firstConverter);
        l.add(secondConverter);

        this.composite = new Composite(l);
    }

    @Test
    public void canConvertAsksChildConvertersIfTheyCanConvertGivenCode()
    {
        new Expectations() {{
            CompositeTest.this.firstConverter.canConvert(12); times = 1; result = false;
            CompositeTest.this.secondConverter.canConvert(12); times = 1; result = false;
        }};

        this.composite.canConvert(12);
    }

    @Test
    public void canConvertReturnsTrueIfAnyChildCanConvertMessageCode()
    {
        new Expectations() {{
            CompositeTest.this.firstConverter.canConvert(12); result = false;
            CompositeTest.this.secondConverter.canConvert(12); result = true;
        }};

        Assert.assertTrue(this.composite.canConvert(12));
    }

    @Test
    public void canConvertReturnsFalseIfNoChildCanConvertMessageCode()
    {
        new Expectations() {{
            CompositeTest.this.firstConverter.canConvert(anyInt); result = false;
            CompositeTest.this.secondConverter.canConvert(anyInt); result = false;
        }};

        Assert.assertFalse(this.composite.canConvert(12));
    }

    @Test
    public void getConverterReturnsFirstConverterFound()
    {
        new NonStrictExpectations() {{
            CompositeTest.this.firstConverter.canConvert(anyInt); result = true;
            CompositeTest.this.secondConverter.canConvert(anyInt); result = true;
        }};

        Assert.assertSame(this.composite.getConverter(45), this.firstConverter);
    }

    @Test
    public void getConverterReturnsNullIfNoConverterFound()
    {
        new Expectations() {{
            CompositeTest.this.firstConverter.canConvert(anyInt); result = false;
            CompositeTest.this.secondConverter.canConvert(anyInt); result = false;
        }};

        Assert.assertNull(this.composite.getConverter(4));
    }

    @Test(expected = InvalidMessageCode.class)
    public void serializeThrowsExceptionsIfNoConverterApplies(Message message, Websocket socket)
        throws InvalidMessageCode
    {
        new Expectations() {{
            CompositeTest.this.firstConverter.canConvert(anyInt); result = false;
            CompositeTest.this.secondConverter.canConvert(anyInt); result = false;
        }};

        this.composite.serialize(message, socket);
    }

    @Test
    public void serializeReturnsResultOfFirstResponsibleConverter(final Message message, final Websocket socket)
        throws InvalidMessageCode
    {
        new NonStrictExpectations() {{
            message.getMessageCode(); result = 1;
            CompositeTest.this.firstConverter.canConvert(anyInt); result = true;
            CompositeTest.this.secondConverter.canConvert(anyInt); result = true;
            CompositeTest.this.firstConverter.serialize(message, socket); result = "resultString";
            CompositeTest.this.secondConverter.serialize(message, socket); result = "wrongString";
        }};

        Assert.assertEquals("resultString", this.composite.serialize(message, socket));
    }

    @Test(expected = MessageParseError.class)
    public void deserializeThrowsExceptionIfNoMessageNumberIsPresent(final Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        this.composite.deserialize("[ad,", socket);
    }

    @Test(expected = InvalidMessageCode.class)
    public void deserializeThrowsExceptionIfNoConverterCanHandleMessage(final Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        this.composite.deserialize("[1, ", socket);
    }

    @Test
    public void deserializeReturnsResultOfFirstDelegatedConverter(final Message messageOne, final Message messageTwo, final Websocket socket)
        throws MessageParseError, InvalidMessageCode
    {
        new NonStrictExpectations() {{
            CompositeTest.this.firstConverter.canConvert(anyInt); result = true;
            CompositeTest.this.secondConverter.canConvert(anyInt); result = true;
            CompositeTest.this.firstConverter.deserialize("[1, ", socket); result = messageOne;
            CompositeTest.this.secondConverter.deserialize("[1, ", socket); result = messageTwo;
        }};

        Assert.assertSame(messageOne, this.composite.deserialize("[1, ", socket));
    }
}
