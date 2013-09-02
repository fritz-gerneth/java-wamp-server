package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.effms.wamp.core.message.MessageType;
import org.springframework.beans.BeanWrapper;

import java.io.IOException;

public class CallResultMessageDeserializer<T> extends AbstractMessageDeserializer<T>
{
    private final Class<T> typeClass;

    public CallResultMessageDeserializer(Class<T> typeClass)
    {
        this.typeClass = typeClass;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        BeanWrapper wrapper = this.getWrapperInstance(this.typeClass);

        this.assertMessageStart(parser);
        this.assertNextIsMessageCode(parser, MessageType.CALL_RESULT);

        this.trySetProperty(wrapper, "messageType", MessageType.CALL_RESULT);
        this.trySetProperty(wrapper, "callId", this.readNextAsString(parser));
        parser.nextToken();
        this.trySetProperty(wrapper, "result",   parser.readValueAs(wrapper.getPropertyType("result")));

        this.assertNextIsMessageEnd(parser);

        return (T) wrapper.getWrappedInstance();
    }
}
