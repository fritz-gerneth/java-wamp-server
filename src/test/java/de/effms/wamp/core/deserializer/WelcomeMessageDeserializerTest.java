package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core._messageMocks.FullWelcomeMessage;
import de.effms.wamp.core._messageMocks.PartialWelcomeMessage;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class WelcomeMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, WelcomeMessageDeserializer deserializer)
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
        WelcomeMessageDeserializer<FullWelcomeMessage> deserializer =
            new WelcomeMessageDeserializer<FullWelcomeMessage>(FullWelcomeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullWelcomeMessage.class, deserializer);

        String[] messages = {
            "[45, \"mySessionId\", 1, \"serverIdent\"]",
            "",
            "0, \"mySessionId\", 1]",
            "[0, \"mySessionId\", 1]",
            "[0, 53, 1]",
            "[0, \"mySessionId\", \"dd\"]",
            "[0, \"mySessionId\", 1, null]",
            "[0, \"mySessionId\"]",
            "[0]",
            "[0",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, FullWelcomeMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateObjectPartially() throws IOException
    {
        WelcomeMessageDeserializer<PartialWelcomeMessage> deserializer =
            new WelcomeMessageDeserializer<PartialWelcomeMessage>(PartialWelcomeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(PartialWelcomeMessage.class, deserializer);

        PartialWelcomeMessage message = mapper.readValue(
            "[0, \"mySessionId\", 1, \"serverIdent\"]",
            PartialWelcomeMessage.class
        );

        Assert.assertEquals("mySessionId", message.getSessionId());
        Assert.assertEquals("serverIdent", message.getServerIdent());
    }

    @Test
    public void deserializeCanPopulateObjectFully() throws IOException
    {
        WelcomeMessageDeserializer<FullWelcomeMessage> deserializer =
            new WelcomeMessageDeserializer<FullWelcomeMessage>(FullWelcomeMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullWelcomeMessage.class, deserializer);

        FullWelcomeMessage message = mapper.readValue(
            "[0, \"mySessionId\", 1, \"serverIdent\"]",
            FullWelcomeMessage.class
        );

        Assert.assertEquals(MessageType.WELCOME, message.getMessageType());
        Assert.assertEquals("mySessionId", message.getSessionId());
        Assert.assertEquals("serverIdent", message.getServerIdent());
        Assert.assertEquals(1, message.getProtocolVersion());
    }
}
