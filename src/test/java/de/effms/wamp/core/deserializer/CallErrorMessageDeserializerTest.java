package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class CallErrorMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, CallErrorMessageDeserializer deserializer)
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
        CallErrorMessageDeserializer<CallErrorMessage> deserializer =
            new CallErrorMessageDeserializer<CallErrorMessage>(CallErrorMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallErrorMessage.class, deserializer);

        String[] messages = {
            "[45, \"callId\", \"errorUri\"]",
            "",
            "4, \"callId\", \"errorUri\", \"desc\"]",
            "[4, \"errorUri\"]",
            "[4, 53, \"errorUri\", \"desc\"]",
            "[4, \"callId\", \"errorUri\", \"desc\", \"details\", \"too_much\"]",
            "[4, \"callId\", false, \"desc\", \"details\", \"too_much\"]",
            "[4]",
            "[4",
        };

        for (String m: messages) {
            try {
                mapper.readValue(m, CallErrorMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateMessageTypeAndCallIdAndErrorUri() throws IOException
    {
        CallErrorMessageDeserializer<CallErrorMessage> deserializer =
            new CallErrorMessageDeserializer<CallErrorMessage>(CallErrorMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallErrorMessage.class, deserializer);

        CallErrorMessage message = mapper.readValue(
            "[4, \"callId\", \"errorUri\", \"desc\"]",
            CallErrorMessage.class
        );

        Assert.assertEquals(MessageType.CALL_ERROR, message.getMessageType());
        Assert.assertEquals("callId", message.getCallId());
        Assert.assertEquals("errorUri", message.getErrorUri());
    }

    @Test
    public void deserializeCanPopulateVariableErrorDesc() throws IOException
    {
        CallErrorMessageDeserializer<CallErrorMessage> deserializer =
            new CallErrorMessageDeserializer<CallErrorMessage>(CallErrorMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallErrorMessage.class, deserializer);

        CallErrorMessage message = mapper.readValue(
            "[4, \"callId\", \"errorUri\", \"desc\"]",
            CallErrorMessage.class
        );

        Assert.assertEquals("desc", message.getErrorDesc());
    }

    @Test
    public void deserializeDetailsNullIfNotSet() throws IOException
    {
        CallErrorMessageDeserializer<CallErrorMessage> deserializer =
            new CallErrorMessageDeserializer<CallErrorMessage>(CallErrorMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallErrorMessage.class, deserializer);

        CallErrorMessage message = mapper.readValue(
            "[4, \"callId\", \"errorUri\", \"desc\"]",
            CallErrorMessage.class
        );

        Assert.assertNull(message.getErrorDetails());
    }

    @Test
    public void deserializeCanPopulateVariableErrorDetails() throws IOException
    {
        CallErrorMessageDeserializer<CallErrorMessage> deserializer =
            new CallErrorMessageDeserializer<CallErrorMessage>(CallErrorMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(CallErrorMessage.class, deserializer);

        CallErrorMessage message = mapper.readValue(
            "[4, \"callId\", \"errorUri\", \"desc\", [\"detail\", \"detail\"]]",
            CallErrorMessage.class
        );

        Assert.assertEquals(new ArrayList<Object>() {{
            this.add("detail");
            this.add("detail");
        }}, message.getErrorDetails());
    }

    public static class CallErrorMessage
    {
        private MessageType messageType;

        private String callId;

        private String errorUri;

        private Object errorDesc;

        private Object errorDetails;

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

        public String getErrorUri()
        {
            return errorUri;
        }

        public void setErrorUri(String errorUri)
        {
            this.errorUri = errorUri;
        }

        public Object getErrorDesc()
        {
            return errorDesc;
        }

        public void setErrorDesc(Object errorDesc)
        {
            this.errorDesc = errorDesc;
        }

        public Object getErrorDetails()
        {
            return errorDetails;
        }

        public void setErrorDetails(Object errorDetails)
        {
            this.errorDetails = errorDetails;
        }
    }
}
