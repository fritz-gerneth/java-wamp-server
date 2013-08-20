package de.innoaccel.wamp.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.PrefixMessage;

public class PrefixMessageConverter extends JsonParsingConverter<PrefixMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.PREFIX == messageCode;
    }

    @Override
    public String serialize(PrefixMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(new Object[] {
                Message.PREFIX,
                message.getPrefix(),
                message.getURI()
            });
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.PREFIX);
        }
    }

    @Override
    protected PrefixMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.PREFIX);

        String prefix = this.readStringAt(message, 1);
        if (0 == prefix.length()) {
            throw new MessageParseException("Prefix may not be empty");
        }

        String uri = this.readStringAt(message, 2);
        if (0 == uri.length()) {
            throw new MessageParseException("URI may not be empty");
        }

        return new PrefixMessage(prefix, uri);
    }
}
