package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core._messageMocks.FullSubscribeMessage;
import de.effms.wamp.core._messageMocks.PartialPrefixMessage;
import de.effms.wamp.core._messageMocks.PartialSubscribeMessage;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SubscribeMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, SubscribeMessageDeserializer deserializer)
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("testing", new Version(1, 0, 0, null, null, null))
            .addDeserializer(classType, deserializer);
        mapper.registerModule(module);
        return mapper;
    }

    @Test
    public void deserializeThrowsExceptionOnMissingOrWrongFields() throws IOException
    {
        SubscribeMessageDeserializer<FullSubscribeMessage> deserializer =
            new SubscribeMessageDeserializer<FullSubscribeMessage>(FullSubscribeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullSubscribeMessage.class, deserializer);

        String[] messages = {
            "[45, \"uri\"]",
            "",
            "5, \"uri\"]",
            "[5, null, 55]",
            "[5]",
            "[5",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, FullSubscribeMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateObjectPartially() throws IOException
    {
        SubscribeMessageDeserializer<PartialSubscribeMessage> deserializer =
            new SubscribeMessageDeserializer<PartialSubscribeMessage>(PartialSubscribeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(PartialSubscribeMessage.class, deserializer);

        PartialSubscribeMessage message = mapper.readValue(
            "[5, \"uri\"]",
            PartialSubscribeMessage.class
        );

        Assert.assertEquals("uri", message.getTopicUri());
    }

    @Test
    public void deserializeCanPopulateObjectFully() throws IOException
    {
        SubscribeMessageDeserializer<FullSubscribeMessage> deserializer =
            new SubscribeMessageDeserializer<FullSubscribeMessage>(FullSubscribeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullSubscribeMessage.class, deserializer);

        FullSubscribeMessage message = mapper.readValue(
            "[5, \"uri\"]",
            FullSubscribeMessage.class
        );

        Assert.assertEquals(MessageType.SUBSCRIBE, message.getMessageType());
        Assert.assertEquals("uri", message.getTopicUri());
    }
}
