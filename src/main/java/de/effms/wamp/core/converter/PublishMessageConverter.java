package de.effms.wamp.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.PublishMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PublishMessageConverter extends JsonParsingConverter<PublishMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.PUBLISH == messageCode;
    }

    @Override
    public String serialize(PublishMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        List<Object> data = new ArrayList<Object>();
        data.add(Message.PUBLISH);
        data.add(socket.inflateCURI(message.getTopicURI()));
        data.add(message.getEvent());

        if (message.isTargeted()) {
            data.add(message.getExclude());
            data.add(message.getEligible());
        } else if (message.excludeMe()) {
            data.add(true);
        }

        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.PUBLISH);
        }
    }

    @Override
    protected PublishMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.PUBLISH);

        int messageElementCount = message.size();
        if (5 == messageElementCount) {
            return new PublishMessage(
                this.readStringAt(message, 1),
                this.readAnyAt(message, 2),
                this.readStringArray(message, 3),
                this.readStringArray(message, 4)
            );
        } else if (3 == messageElementCount || 4 == messageElementCount) {
            return new PublishMessage(
                this.readStringAt(message, 1),
                this.readAnyAt(message, 2),
                this.readOptBoolean(message, 3, false)
            );
        }
        throw new MessageParseException("Expected [3,5] Elements in message, got " + messageElementCount);
    }

    private List<String> readStringArray(JsonNode rootNode, int position) throws MessageParseException
    {
        JsonNode arrayNode = rootNode.get(position);
        if (null == arrayNode || JsonNodeType.ARRAY != arrayNode.getNodeType()) {
            throw new MessageParseException("Expected array at position " + position);
        }

        List<String> stringList = new ArrayList<String>();
        Iterator<JsonNode> elements = arrayNode.elements();
        int iteratorPosition = 0;
        while(elements.hasNext()) {
            JsonNode element = elements.next();
            if (JsonNodeType.STRING != element.getNodeType()) {
                throw new MessageParseException("Array-Item at position " + iteratorPosition + " is no string");
            }
            String stringifiedElement = element.asText();
            if (0 == stringifiedElement.length()) {
                throw new MessageParseException("Array-Item as position " + iteratorPosition + " may not be empty");
            }
            stringList.add(stringifiedElement);
            iteratorPosition++;
        }

        return stringList;
    }

    private boolean readOptBoolean(JsonNode rootNode, int position, boolean nodeNotPresentValue) throws MessageParseException
    {
        JsonNode booleanNode = rootNode.get(position);
        if (null == booleanNode) {
            return nodeNotPresentValue;
        }

        if (JsonNodeType.BOOLEAN != booleanNode.getNodeType()) {
            throw new MessageParseException("Expected boolean type at position " + position);
        }

        return booleanNode.asBoolean();
    }
}
