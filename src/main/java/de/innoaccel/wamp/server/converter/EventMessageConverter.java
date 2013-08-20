package de.innoaccel.wamp.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.EventMessage;
import de.innoaccel.wamp.server.message.Message;

public class EventMessageConverter extends JsonParsingConverter<EventMessage>
{
    public  EventMessageConverter()
    {
        this(new ObjectMapper());
    }

    public EventMessageConverter(ObjectMapper objectMapper)
    {
        super(objectMapper);
    }

    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.EVENT == messageCode;
    }

    @Override
    public String serialize(EventMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        try {
            return this.objectMapper.writeValueAsString(new Object[] {
                    Message.EVENT,
                    message.getTopicURI(),
                    message.getPayload()
            });
        } catch (JsonProcessingException ex) {
            throw new InvalidMessageCodeException(Message.EVENT);
        }
    }

    protected EventMessage deserialize(JsonNode rootNode, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(rootNode, Message.EVENT);

        String topic = this.readStringAt(rootNode, 1);
        if (0 == topic.length()) {
            throw new MessageParseException("Message has invalid markup (topic is not valid)");
        }
        // TODO: can we formulate further contraints for the payload? Provide tests about the content?

        return new EventMessage(socket.inflateCURI(topic), this.readAnyAt(rootNode, 2));
    }
}
