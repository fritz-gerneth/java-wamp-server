package de.effms.wamp.core.converter.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.JsonParsingConverter;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.PrefixMessage;

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
