package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, EventMessageDeserializer deserializer)
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
        EventMessageDeserializer<FullStringEventMessage> deserializer =
            new EventMessageDeserializer<FullStringEventMessage>(FullStringEventMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullStringEventMessage.class, deserializer);

        String[] messages = {
            "[45, \"topic\", \"event\"]",
            "",
            "8, \"topic\", \"event\"]",
            "[8, \"topic\"]",
            "[8, 53, 1]",
            "[8, \"topic\", \"event\", \"notABoolean\"]",
            "[8]",
            "[8",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, FullStringEventMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateMessageWithEventString() throws IOException
    {
        EventMessageDeserializer<FullStringEventMessage> deserializer =
            new EventMessageDeserializer<FullStringEventMessage>(FullStringEventMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullStringEventMessage.class, deserializer);

        FullStringEventMessage message = mapper.readValue(
            "[8, \"topic\", \"event\"]",
            FullStringEventMessage.class
        );

        Assert.assertEquals(MessageType.EVENT, message.getMessageType());
        Assert.assertEquals("topic", message.getTopicUri());
        Assert.assertEquals("event", message.getEvent());
    }

    @Test
    public void deserializeCanPopulateMessageWithEventTypeUpcasting() throws IOException
    {
        EventMessageDeserializer<FullStringEventMessage> deserializer =
            new EventMessageDeserializer<FullStringEventMessage>(FullStringEventMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullStringEventMessage.class, deserializer);

        FullStringEventMessage message = mapper.readValue(
            "[8, \"topic\", [\"event\", \"event2\"]]",
            FullStringEventMessage.class
        );
        Assert.assertEquals(new ArrayList<String>() {{
            this.add("event");
            this.add("event2");
        }},
            message.getEvent()
        );
        Assert.assertTrue(message.getEvent() instanceof ArrayList);
    }

    public static class FullStringEventMessage
    {
        private MessageType messageType;

        private String topicUri;

        private Object event;

        public MessageType getMessageType()
        {
            return messageType;
        }

        public void setMessageType(MessageType messageType)
        {
            this.messageType = messageType;
        }

        public String getTopicUri()
        {
            return topicUri;
        }

        public void setTopicUri(String topicUri)
        {
            this.topicUri = topicUri;
        }

        public Object getEvent()
        {
            return event;
        }

        public void setEvent(Object event)
        {
            this.event = event;
        }
    }
}
