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

public class CallMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, CallMessageDeserializer deserializer)
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
        CallMessageDeserializer<CallMessage> deserializer =
            new CallMessageDeserializer<CallMessage>(CallMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallMessage.class, deserializer);

        String[] messages = {
            "[45, \"callId\", \"procUri\"]",
            "",
            "2, \"callId\", \"procUri\"]",
            "[2, \"callId\"]",
            "[2, \"callId\", 1]",
            "[2, false, \"procUri\"]",
            "[2]",
            "[2",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, CallMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateCallIdAndProcUri() throws IOException
    {
        CallMessageDeserializer<CallMessage> deserializer =
            new CallMessageDeserializer<CallMessage>(CallMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallMessage.class, deserializer);

        CallMessage message = mapper.readValue(
            "[2, \"callId\", \"procUri\"]",
            CallMessage.class
        );

        Assert.assertEquals(MessageType.CALL, message.getMessageType());
        Assert.assertEquals("callId", message.getCallId());
        Assert.assertEquals("procUri", message.getProcUri());
    }

    @Test
    public void deserializeCanPopulateMessageNoArguments() throws IOException
    {
        CallMessageDeserializer<CallMessage> deserializer =
            new CallMessageDeserializer<CallMessage>(CallMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallMessage.class, deserializer);

        CallMessage message = mapper.readValue(
            "[2, \"callId\", \"procUri\"]",
            CallMessage.class
        );

        Assert.assertEquals(new ArrayList<Object>(), message.getArguments());
    }

    @Test
    public void deserializeCanPopulateMessageWithArgumentTypeUpcastingAndMixedTypes() throws IOException
    {
        CallMessageDeserializer<CallMessage> deserializer =
            new CallMessageDeserializer<CallMessage>(CallMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallMessage.class, deserializer);

        CallMessage message = mapper.readValue(
            "[2, \"topic\", \"procUri\", [\"arguments\", \"event2\"], 5, \"string\", false]",
            CallMessage.class
        );
        Assert.assertEquals(
            new ArrayList<Object> () {{
                this.add(new ArrayList<String>() {{
                    this.add("arguments");
                    this.add("event2");
                }});
                this.add(5);
                this.add("string");
                this.add(false);
        }},
            message.getArguments()
        );

        Assert.assertTrue(message.getArguments() instanceof ArrayList);
    }

    public static class CallMessage
    {
        private MessageType messageType;

        private String callId;

        private String procUri;

        private List<Object> arguments;

        public MessageType getMessageType()
        {
            return messageType;
        }

        public void setMessageType(MessageType messageType)
        {
            this.messageType = messageType;
        }

        public String getCallId()
        {
            return callId;
        }

        public void setCallId(String callId)
        {
            this.callId = callId;
        }

        public String getProcUri()
        {
            return procUri;
        }

        public void setProcUri(String procUri)
        {
            this.procUri = procUri;
        }

        public List<Object> getArguments()
        {
            return arguments;
        }

        public void setArguments(List<Object> arguments)
        {
            this.arguments = arguments;
        }
    }
}
