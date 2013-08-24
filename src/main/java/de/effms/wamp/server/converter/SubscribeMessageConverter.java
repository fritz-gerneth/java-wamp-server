package de.effms.wamp.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.server.Websocket;
import de.effms.wamp.server.message.Message;
import de.effms.wamp.server.message.SubscribeMessage;

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