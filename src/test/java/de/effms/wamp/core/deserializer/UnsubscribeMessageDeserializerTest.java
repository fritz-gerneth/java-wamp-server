package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core._messageMocks.FullSubscribeMessage;
import de.effms.wamp.core._messageMocks.FullUnsubscribeMessage;
import de.effms.wamp.core._messageMocks.PartialSubscribeMessage;
import de.effms.wamp.core._messageMocks.PartialUnsubscribeMessage;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class UnsubscribeMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, UnsubscribeMessageDeserializer deserializer)
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
        UnsubscribeMessageDeserializer<FullUnsubscribeMessage> deserializer =
            new UnsubscribeMessageDeserializer<FullUnsubscribeMessage>(FullUnsubscribeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullUnsubscribeMessage.class, deserializer);

        String[] messages = {
            "[45, \"uri\"]",
            "",
            "6, \"uri\"]",
            "[6, null, 55]",
            "[6]",
            "[6",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, FullUnsubscribeMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateObjectPartially() throws IOException
    {
        UnsubscribeMessageDeserializer<PartialUnsubscribeMessage> deserializer =
            new UnsubscribeMessageDeserializer<PartialUnsubscribeMessage>(PartialUnsubscribeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(PartialUnsubscribeMessage.class, deserializer);

        PartialUnsubscribeMessage message = mapper.readValue(
            "[6, \"uri\"]",
            PartialUnsubscribeMessage.class
        );

        Assert.assertEquals("uri", message.getTopicUri());
    }

    @Test
    public void deserializeCanPopulateObjectFully() throws IOException
    {
        UnsubscribeMessageDeserializer<FullUnsubscribeMessage> deserializer =
            new UnsubscribeMessageDeserializer<FullUnsubscribeMessage>(FullUnsubscribeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullUnsubscribeMessage.class, deserializer);

        FullUnsubscribeMessage message = mapper.readValue(
            "[6, \"uri\"]",
            FullUnsubscribeMessage.class
        );

        Assert.assertEquals(MessageType.UNSUBSCRIBE, message.getMessageType());
        Assert.assertEquals("uri", message.getTopicUri());
    }
}

