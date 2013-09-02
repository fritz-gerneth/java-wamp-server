package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.effms.wamp.core.message.MessageType;
import org.springframework.beans.BeanWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallMessageDeserializer<T> extends AbstractMessageDeserializer<T>
{
    private final Class<T> typeClass;

    public CallMessageDeserializer(Class<T> typeClass)
    {
        this.typeClass = typeClass;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        BeanWrapper wrapper = this.getWrapperInstance(this.typeClass);

        this.assertMessageStart(parser);
        this.assertNextIsMessageCode(parser, MessageType.CALL);

        this.trySetProperty(wrapper, "messageType", MessageType.CALL);
        this.trySetProperty(wrapper, "callId", this.readNextAsString(parser));
        this.trySetProperty(wrapper, "procUri", this.readNextAsString(parser));

        List<Object> arguments = new ArrayList<Object>();
        if (JsonToken.END_ARRAY != parser.nextToken()) {
            Iterator<Object> currentArgument = parser.readValuesAs(Object.class);
            while(currentArgument.hasNext()) {
                arguments.add(currentArgument.next());
            }
        }
        this.trySetProperty(wrapper, "arguments", arguments);

        return (T) wrapper.getWrappedInstance();
    }
}
