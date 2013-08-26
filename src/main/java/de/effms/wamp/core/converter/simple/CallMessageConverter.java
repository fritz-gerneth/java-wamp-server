package de.effms.wamp.core.converter.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.JsonParsingConverter;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.message.CallMessage;
import de.effms.wamp.core.message.Message;

import java.util.ArrayList;
import java.util.List;

public class CallMessageConverter extends JsonParsingConverter<CallMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.CALL == messageCode;
    }

    @Override
    public String serialize(final CallMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(
                new ArrayList<Object>() {{
                    this.add(Message.CALL);
                    this.add(message.getCallId());
                    this.add(message.getProcURI());
                    this.addAll(message.getArgs());
                }}
            );
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.CALL);
        }
    }

    @Override
    protected CallMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.CALL);

        final int fieldCount = message.size();
        if (3 > fieldCount) {
            throw new MessageParseException("Expected at least three message fields");
        }

        List<Object> arguments = new ArrayList<Object>();
        for (int currentArgPosition = 3; currentArgPosition < fieldCount; currentArgPosition++) {
            arguments.add(message.get(currentArgPosition));
        }

        return new CallMessage(
            this.readStringAt(message, 1),
            socket.inflateCURI(this.readStringAt(message, 2)),
            arguments
        );
    }
}
