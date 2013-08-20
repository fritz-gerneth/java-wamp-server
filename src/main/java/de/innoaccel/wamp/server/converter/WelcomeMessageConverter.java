package de.innoaccel.wamp.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.WelcomeMessage;

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
