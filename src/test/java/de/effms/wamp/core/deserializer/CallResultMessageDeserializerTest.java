package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core._messageMocks.FullPublishMessage;
import de.effms.wamp.core._messageMocks.PartialPublishMessage;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class CallResultMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, CallResultMessageDeserializer deserializer)
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
        CallResultMessageDeserializer<CallResultMessage> deserializer =
            new CallResultMessageDeserializer<CallResultMessage>(CallResultMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallResultMessage.class, deserializer);

        String[] messages = {
            "[45, \"callId\", \"result\"]",
            "",
            "3, \"callId\", \"result\"]",
            "[3, \"callId\"]",
            "[3, 53, \"result\"]",
            "[3, \"callId\", \"result\", \"additionalValue\"]",
            "[3]",
            "[3",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, CallResultMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateMessageTypeAndCallId() throws IOException
    {
        CallResultMessageDeserializer<CallResultMessage> deserializer =
            new CallResultMessageDeserializer<CallResultMessage>(CallResultMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallResultMessage.class, deserializer);

        CallResultMessage message = mapper.readValue(
            "[3, \"callId\", null]",
            CallResultMessage.class
        );

        Assert.assertEquals(MessageType.CALL_RESULT, message.getMessageType());
        Assert.assertEquals("callId", message.getCallId());
    }

    @Test
    public void deserializeCanPopulateResult() throws IOException
    {
        CallResultMessageDeserializer<CallResultMessage> deserializer =
            new CallResultMessageDeserializer<CallResultMessage>(CallResultMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallResultMessage.class, deserializer);

        CallResultMessage message = mapper.readValue(
            "[3, \"callId\", [\"result\", \"result\"]]",
            CallResultMessage.class
        );

        Assert.assertEquals(new ArrayList<String>() {{
            this.add("result");
            this.add("result");
        }}, message.getResult());
    }

    public static class CallResultMessage
    {
        private MessageType messageType;

        private String callId;

        private Object result;

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

        public Object getResult()
        {
            return result;
        }

        public void setResult(Object result)
        {
            this.result = result;
        }
    }
}
