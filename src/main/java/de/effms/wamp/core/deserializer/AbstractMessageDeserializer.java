package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.core.message.MessageType;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.IOException;

abstract public class AbstractMessageDeserializer<T> extends JsonDeserializer<T>
{
    protected BeanWrapper getWrapperInstance(Class<T> typeClass) throws IOException
    {
        BeanWrapper wrapper;
        try {
            wrapper = new BeanWrapperImpl(typeClass.newInstance());
        } catch (IllegalAccessException ex) {
            throw new IOException(ex);
        } catch (InstantiationException ex) {
            throw new IOException(ex);
        }
        return wrapper;
    }

    protected void trySetProperty(BeanWrapper wrapper, String property, Object value)
    {
        if (wrapper.isWritableProperty(property)) {
            wrapper.setPropertyValue(property, value);
        }
    }

    protected int readNextAsInt(JsonParser parser) throws IOException
    {
        this.assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        return parser.getIntValue();
    }

    protected String readNextAsString(JsonParser parser) throws IOException
    {
        this.assertToken(JsonToken.VALUE_STRING, parser.nextToken());
        return parser.getValueAsString();
    }

    protected void assertToken(JsonToken expected, JsonToken actual) throws IOException
    {
        if (actual != expected) {
            throw new IOException("Expected " + expected + "; Got " + actual);
        }
    }

    protected void assertNextIsMessageCode(JsonParser parser, MessageType messageType) throws IOException
    {
        int actualMessageCode = this.readNextAsInt(parser);
        if (messageType.getMessageCode() != actualMessageCode) {
            throw new IOException("Expected " + messageType.getMessageCode() + "; Got " + actualMessageCode);
        }
    }

    protected void assertMessageStart(JsonParser parser) throws IOException
    {
        this.assertToken(JsonToken.START_ARRAY, parser.getCurrentToken());
    }

    protected void assertNextIsMessageEnd(JsonParser parser) throws IOException
    {
        this.assertToken(JsonToken.END_ARRAY, parser.nextToken());
    }

    protected JsonNode readNextAsNode(JsonParser parser) throws IOException
    {
        parser.nextToken();
        return parser.readValueAsTree();
    }
}
