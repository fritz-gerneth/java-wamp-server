package de.effms.wamp.core.converter.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.JsonParsingConverter;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.UnsubscribeMessage;

public class UnsubscribeMessageConverter extends JsonParsingConverter<UnsubscribeMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.UNSUBSCRIBE == messageCode;
    }

    @Override
    public String serialize(UnsubscribeMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(new Object[] {
                Message.UNSUBSCRIBE,
                message.getTopicURI()
            });
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.UNSUBSCRIBE);
        }
    }

    @Override
    protected UnsubscribeMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.UNSUBSCRIBE);

        String topic = this.readStringAt(message, 1);
        if (0 == topic.length()) {
            throw new MessageParseException("Topic may not be empty");
        }

        return new UnsubscribeMessage(socket.inflateCURI(topic));
    }
}
