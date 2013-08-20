package de.innoaccel.wamp.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.EventMessage;
import de.innoaccel.wamp.server.message.Message;
import java.io.IOException;

public class EventMessageConverter extends JsonParsingConverter<EventMessage>
{
    public  EventMessageConverter()
    {
        this(new ObjectMapper());
    }

    public EventMessageConverter(ObjectMapper objectMapper)
    {
        super(objectMapper);
    }

    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.EVENT == messageCode;
    }

    @Override
    public String serialize(EventMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(new Object[] {
                    Message.EVENT,
                    message.getTopicURI(),
                    message.getPayload()
            });
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.EVENT);
        }
    }

    @Override
    public EventMessage deserialize(String message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        JsonNode rootNode;
        try {
            rootNode = this.objectMapper.readTree(message);
        } catch (IOException parseException) {
            // TODO: check when this actually can happen, build a test-case for this
            throw new MessageParseException(parseException.getMessage());
        }

        if (JsonNodeType.ARRAY != rootNode.getNodeType()) {
            throw new MessageParseException("Message has invalid markup (not an array)");
        }

        JsonNode messageCodeNode = rootNode.get(0);
        if (null == messageCodeNode || JsonNodeType.NUMBER != messageCodeNode.getNodeType() || !messageCodeNode.canConvertToInt()) {
            throw new MessageParseException("Message has invalid markup (message code not a nunber)");
        }

        int messageCode = messageCodeNode.asInt();
        if (Message.EVENT != messageCode) {
            throw new InvalidMessageCodeException(messageCode);
        }

        JsonNode topicNode = rootNode.get(1);
        if (null == topicNode || JsonNodeType.STRING != topicNode.getNodeType()) {
            throw new MessageParseException("Message has invalid markup (topic is no string)");
        }

        String topic = topicNode.asText();
        if (0 == topic.length()) {
            throw new MessageParseException("Message has invalid markup (topic is not valid)");
        }

        JsonNode payloadNode = rootNode.get(2);
        if (null == payloadNode) {
            throw new MessageParseException("Message has invalid markup (no payload)");
        }

        // TODO: can we formulate further contraints for the payload? Provide tests about the content?

        return new EventMessage(socket.inflateCURI(topic), payloadNode);
    }
}
