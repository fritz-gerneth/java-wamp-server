package de.innoaccel.wamp.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.CallResultMessage;
import de.innoaccel.wamp.server.message.Message;

public class CallResultMessageConverter extends JsonParsingConverter<CallResultMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.CALL_RESULT == messageCode;
    }

    @Override
    public String serialize(CallResultMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(new Object[] {
                Message.CALL_RESULT,
                message.getCallId(),
                message.getResult()
            });
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.CALL_RESULT);
        }
    }

    @Override
    protected CallResultMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.CALL_RESULT);

        return new CallResultMessage(
            this.readStringAt(message, 1),
            this.readAnyAt(message, 2)
        );
    }
}
