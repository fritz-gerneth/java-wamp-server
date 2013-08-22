package de.innoaccel.wamp.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.CallErrorMessage;
import de.innoaccel.wamp.server.message.Message;

import java.util.ArrayList;
import java.util.List;

public class CallErrorMessageConverter extends JsonParsingConverter<CallErrorMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.CALL_ERROR == messageCode;
    }

    @Override
    public String serialize(final CallErrorMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        List<Object> elements = new ArrayList<Object>() {{
            this.add(Message.CALL_ERROR);
            this.add(message.getCallId());
            this.add(message.getErrorURI());
            this.add(message.getErrorDesc());
        }};

        Object details = message.getErrorDetails();
        if (null != details) {
            elements.add(details);
        }
        try {
            return this.objectMapper.writeValueAsString(elements);
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.CALL);
        }
    }

    @Override
    protected CallErrorMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.CALL_ERROR);

        final int elementCount = message.size();
        if (elementCount > 5 || elementCount < 4) {
            throw new MessageParseException("Expected [4,5] elements in  message");
        }

        final JsonNode detailsNode = message.get(4);
        if (detailsNode instanceof NullNode) {
            throw new MessageParseException("Error-Details may not be set but null");
        }

        return new CallErrorMessage(
            this.readStringAt(message, 1),
            socket.inflateCURI(this.readStringAt(message, 2)),
            this.readStringAt(message, 3),
            detailsNode
        );
    }
}
