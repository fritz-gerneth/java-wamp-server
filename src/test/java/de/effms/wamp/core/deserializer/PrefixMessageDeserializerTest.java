package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core._messageMocks.FullPrefixMessage;
import de.effms.wamp.core._messageMocks.FullWelcomeMessage;
import de.effms.wamp.core._messageMocks.PartialPrefixMessage;
import de.effms.wamp.core._messageMocks.PartialWelcomeMessage;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PrefixMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, PrefixMessageDeserializer deserializer)
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
        PrefixMessageDeserializer<FullPrefixMessage> deserializer =
            new PrefixMessageDeserializer<FullPrefixMessage>(FullPrefixMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullPrefixMessage.class, deserializer);

        String[] messages = {
            "[45, \"prefix\", \"uri\"]",
            "",
            "1, \"prefix\", \"uri\"]",
            "[1, \"mySessionId\"]",
            "[1, 53, 1]",
            "[1, \"mySessionId\", null]",
            "[1]",
            "[1",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, FullPrefixMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateObjectPartially() throws IOException
    {
        PrefixMessageDeserializer<PartialPrefixMessage> deserializer =
            new PrefixMessageDeserializer<PartialPrefixMessage>(PartialPrefixMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(PartialPrefixMessage.class, deserializer);

        PartialPrefixMessage message = mapper.readValue(
            "[1, \"prefix\", \"uri\"]",
            PartialPrefixMessage.class
        );

        Assert.assertEquals("prefix", message.getPrefix());
    }

    @Test
    public void deserializeCanPopulateObjectFully() throws IOException
    {
        PrefixMessageDeserializer<FullPrefixMessage> deserializer =
            new PrefixMessageDeserializer<FullPrefixMessage>(FullPrefixMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullPrefixMessage.class, deserializer);

        FullPrefixMessage message = mapper.readValue(
            "[1, \"prefix\", \"uri\"]",
            FullPrefixMessage.class
        );

        Assert.assertEquals(MessageType.PREFIX, message.getMessageType());
        Assert.assertEquals("prefix", message.getPrefix());
        Assert.assertEquals("uri", message.getUri());
    }
}
