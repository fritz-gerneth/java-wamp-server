package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.effms.wamp.core.message.MessageType;
import org.springframework.beans.BeanWrapper;

import java.io.IOException;

public class CallErrorMessageDeserializer<T> extends AbstractMessageDeserializer<T>
{
    private final Class<T> typeClass;

    public CallErrorMessageDeserializer(Class<T> typeClass)
    {
        this.typeClass = typeClass;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        BeanWrapper wrapper = this.getWrapperInstance(this.typeClass);

        this.assertMessageStart(parser);
        this.assertNextIsMessageCode(parser, MessageType.CALL_ERROR);

        this.trySetProperty(wrapper, "messageType", MessageType.CALL_ERROR);
        this.trySetProperty(wrapper, "callId", this.readNextAsString(parser));
        this.trySetProperty(wrapper, "errorUri", this.readNextAsString(parser));
        parser.nextToken();
        this.trySetProperty(wrapper, "errorDesc",   parser.readValueAs(wrapper.getPropertyType("errorDesc")));
        if (JsonToken.END_ARRAY != parser.nextToken()) {
            this.trySetProperty(wrapper, "errorDetails",   parser.readValueAs(wrapper.getPropertyType("errorDetails")));
            this.assertNextIsMessageEnd(parser);
        }

        return (T) wrapper.getWrappedInstance();
    }
}
