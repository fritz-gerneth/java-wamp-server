package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.effms.wamp.core.message.MessageType;
import org.springframework.beans.BeanWrapper;

import java.io.IOException;

public class UnsubscribeMessageDeserializer<T> extends AbstractMessageDeserializer<T>
{
    private Class<T> classType;

    public UnsubscribeMessageDeserializer(Class<T> classType)
    {
        this.classType = classType;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        BeanWrapper wrapper = this.getWrapperInstance(this.classType);

        this.assertMessageStart(parser);
        this.assertNextIsMessageCode(parser, MessageType.UNSUBSCRIBE);

        this.trySetProperty(wrapper, "messageType", MessageType.UNSUBSCRIBE);
        this.trySetProperty(wrapper, "topicUri", this.readNextAsString(parser));

        this.assertNextIsMessageEnd(parser);

        return (T) wrapper.getWrappedInstance();
    }
}
