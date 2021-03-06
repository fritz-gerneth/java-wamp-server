package de.effms.wamp.core.converter.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.JsonParsingConverter;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.SubscribeMessage;

public class SubscribeMessageConverter extends JsonParsingConverter<SubscribeMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.SUBSCRIBE == messageCode;
    }

    @Override
    public String serialize(SubscribeMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(new Object[] {
                Message.SUBSCRIBE,
                message.getTopicURI()
            });
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.SUBSCRIBE);
        }
    }

    @Override
    protected SubscribeMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.SUBSCRIBE);

        return new SubscribeMessage(socket.inflateCURI(this.readStringAt(message, 1)));
    }
}
