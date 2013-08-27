package de.effms.wamp.core.converter.direct;

import com.fasterxml.jackson.databind.JsonNode;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.JsonParsingConverter;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.message.CallMessage;
import de.effms.wamp.core.message.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

public class CallMessageFactory extends JsonParsingConverter<CallMessage>
{
    private final UriToConstructorResolver resolver;

    public CallMessageFactory(UriToConstructorResolver resolver)
    {
        this.resolver = resolver;
    }

    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.CALL == messageCode;
    }

    @Override
    public String serialize(CallMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected CallMessage deserialize(JsonNode message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.assertMessageCode(message, Message.CALL);

        String procId = this.readStringAt(message, 1);
        String uri = socket.inflateCURI(this.readStringAt(message, 2));

        Type[] argTypes = new Type[message.size() - 3];
        Object[] args = new Object[message.size() - 3];
        for (int i = 3; i < message.size(); i++) {
            JsonNode currentNode = message.get(i);
            Type currentType;
            Object currentArg;
            switch (currentNode.getNodeType()) {
                case BOOLEAN:
                    currentType = boolean.class;
                    currentArg = currentNode.asBoolean();
                    break;
                case STRING:
                    currentType = String.class;
                    currentArg = currentNode.asText();
                    break;
                case NULL:
                    currentType = null;
                    currentArg = null;
                    break;
                case NUMBER:
                    currentType = int.class;
                    currentArg = currentNode.asInt();
                    break;
                default:
                case OBJECT:
                    currentType = Object.class;
                    currentArg = currentNode;
                    break;
            }
            argTypes[i - 3] = currentType;
            args[i - 3] = currentArg;
        }

        try {
            Constructor constructor = this.resolver.getConstructor(uri, argTypes);
            return (CallMessage) constructor.newInstance(args);
        } catch (Exception e) {
            throw new MessageParseException(e.getMessage());
        }


    }
}
