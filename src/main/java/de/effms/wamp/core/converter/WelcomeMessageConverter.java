package de.effms.wamp.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.WelcomeMessage;

public class WelcomeMessageConverter extends JsonParsingConverter<WelcomeMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.WELCOME == messageCode;
    }

    public String serialize(WelcomeMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(new Object[] {
                Message.WELCOME,
                socket.getSessionId(),
                message.getProtocolVersion(),
                message.getServerIdent()
            });
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.WELCOME);
        }
    }

    @Override
    protected WelcomeMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.WELCOME);

        return new WelcomeMessage(
            this.readStringAt(message, 1),
            this.readIntAt(message, 2),
            this.readStringAt(message, 3)
        );
    }
}
