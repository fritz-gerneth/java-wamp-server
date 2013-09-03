package de.effms.wamp.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.effms.wamp.core.resolver.MessageTypeResolver;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DeserializerTest
{
    private Deserializer deserializer;

    @Mocked
    private ObjectMapper mapper;

    @Mocked
    private MessageTypeResolver typeResolver;

    @Before
    public void setUp()
    {
        this.deserializer = new Deserializer(this.mapper, this.typeResolver);
    }

    @Test
    public void deserializeResolvesTypeByMessageTypeResolver(final Websocket socket) throws IOException
    {
        this.deserializer.deserialize("m", socket);

        new Verifications() {{
            DeserializerTest.this.typeResolver.resolve("m", socket);
        }};
    }

    @Test
    public void deserializeDelegatesDeserializationOfMessageToObjectMapper(final Websocket socket) throws IOException
    {
        new NonStrictExpectations() {{
            DeserializerTest.this.typeResolver.resolve(anyString, (Websocket) any); result = TestType.class;
        }};

        this.deserializer.deserialize("m", socket);

        new Verifications() {{
            DeserializerTest.this.mapper.readValue("m", TestType.class);
        }};
    }

    @Test
    public void deserializeReturnsResultObjectFromObjectMapper(final Websocket socket) throws IOException
    {
        final TestType resultObject = new TestType();
        new NonStrictExpectations() {{
            DeserializerTest.this.mapper.readValue(anyString, (Class) any); result = resultObject;
        }};

        Assert.assertSame(resultObject, this.deserializer.deserialize("",  socket));
    }

    private static class TestType {}
}
