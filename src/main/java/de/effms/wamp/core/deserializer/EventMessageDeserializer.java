package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.effms.wamp.core.message.MessageType;
import org.springframework.beans.BeanWrapper;

import java.io.IOException;

public class EventMessageDeserializer<T> extends AbstractMessageDeserializer<T>
{
    private final Class<T> typeClass;

    public EventMessageDeserializer(Class<T> typeClass)
    {
        this.typeClass = typeClass;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        BeanWrapper wrapper = this.getWrapperInstance(this.typeClass);

        this.assertMessageStart(parser);
        this.assertNextIsMessageCode(parser, MessageType.EVENT);

        this.trySetProperty(wrapper, "messageType", MessageType.EVENT);
        this.trySetProperty(wrapper, "topicUri", this.readNextAsString(parser));
        parser.nextToken();
        this.trySetProperty(wrapper, "event",   parser.readValueAs(wrapper.getPropertyType("event")));

        this.assertNextIsMessageEnd(parser);

        return (T) wrapper.getWrappedInstance();
    }
}
