package de.effms.wamp.core.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;

import java.io.IOException;

abstract public class JsonParsingConverter<T extends Message> implements Converter<T>
{
    protected final ObjectMapper objectMapper;

    public JsonParsingConverter()
    {
        this(new ObjectMapper());
    }

    public JsonParsingConverter(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    abstract protected T deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException;

    public T deserialize(String message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        JsonNode rootNode;
        try {
            rootNode = this.objectMapper.readTree(message);
        } catch (IOException parseException) {
            // TODO: check when this actually can happen, build a test-case for this
            throw new MessageParseException(parseException.getMessage());
        }

        if (JsonNodeType.ARRAY != rootNode.getNodeType()) {
            throw new MessageParseException("Message is no JSON-Array");
        }

        return this.deserialize(rootNode, socket);
    }

    protected void assertMessageCode(JsonNode rootNode, int messageCode) throws MessageParseException, InvalidMessageCodeException
    {
        int actualMessageCode = this.readIntAt(rootNode, 0);
        if (actualMessageCode != messageCode) {
            throw new InvalidMessageCodeException(actualMessageCode);
        }
    }

    protected String readStringAt(JsonNode rootNode, int position) throws MessageParseException
    {
        JsonNode stringNode = rootNode.get(position);
        if (null == stringNode || JsonNodeType.STRING != stringNode.getNodeType()) {
            throw new MessageParseException("Expected string-type at position " + position);
        }

        String text = stringNode.asText();
        if (0 == text.length()) {
            throw new MessageParseException("String-Type at position " + position + " may not be empty");
        }

        return text;
    }

    protected Object readAnyAt(JsonNode rootNode, int position) throws MessageParseException
    {
        JsonNode objectNode = rootNode.get(position);
        if (null == objectNode) {
            throw new MessageParseException("Expected object at position " + position);
        }
        return objectNode;
    }

    protected int readIntAt(JsonNode rootNode, int position) throws MessageParseException
    {
        JsonNode messageCodeNode = rootNode.get(position);
        if (null == messageCodeNode || JsonNodeType.NUMBER != messageCodeNode.getNodeType() || !messageCodeNode.canConvertToInt()) {
            throw new MessageParseException("Cannot read integer at position " + position);
        }

        return messageCodeNode.asInt();
    }
}
