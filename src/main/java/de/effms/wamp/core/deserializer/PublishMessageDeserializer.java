package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.effms.wamp.core.message.MessageType;
import org.springframework.beans.BeanWrapper;

import java.io.IOException;
import java.util.List;

public class PublishMessageDeserializer<T> extends AbstractMessageDeserializer<T>
{
    private final Class<T> typeClass;

    public PublishMessageDeserializer(Class<T> typeClass)
    {
        this.typeClass = typeClass;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        BeanWrapper wrapper = this.getWrapperInstance(this.typeClass);

        this.assertMessageStart(parser);
        this.assertNextIsMessageCode(parser, MessageType.PUBLISH);

        this.trySetProperty(wrapper, "messageType", MessageType.PUBLISH);
        this.trySetProperty(wrapper, "topicUri", this.readNextAsString(parser));
        this.trySetProperty(wrapper, "event", this.readNextAsNode(parser));

        JsonToken nextToken = parser.nextToken();
        if (JsonToken.VALUE_FALSE == nextToken || JsonToken.VALUE_TRUE == nextToken) {
            // ExcludeMe Form
            this.trySetProperty(wrapper, "excludeMe", parser.getBooleanValue());
            this.assertToken(JsonToken.END_ARRAY, parser.nextToken());
        } else if (JsonToken.START_ARRAY == nextToken) {
            // Exclude / Eligible Form
            this.trySetProperty(wrapper, "exclude", parser.readValueAs(new TypeReference<List<String>>() {}));
            parser.nextToken();
            this.trySetProperty(wrapper, "eligible", parser.readValueAs(new TypeReference<List<String>>() {}));
            this.assertNextIsMessageEnd(parser);
        } else {
            // Minimal Form
            this.assertToken(JsonToken.END_ARRAY, nextToken);
        }

        return (T) wrapper.getWrappedInstance();
    }
}
