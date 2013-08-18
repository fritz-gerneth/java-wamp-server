package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.UnsubscribeMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnsubscribeMessageConverter implements Converter<UnsubscribeMessage>
{
    private static final Pattern messagePattern = Pattern.compile(
        "\\[\\s*(?<messageCode>\\d+)\\s*,"
      + "\\s*\"(?<uri>.+?)\"\\s*\\]"
    );

    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.UNSUBSCRIBE == messageCode;
    }

    @Override
    public String serialize(UnsubscribeMessage message, Websocket socket) throws InvalidMessageCode
    {
        return "[" + Message.UNSUBSCRIBE + ", \"" + message.getTopicURI() + "\"]";
    }

    @Override
    public UnsubscribeMessage deserialize(String message, Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        Matcher matcher = UnsubscribeMessageConverter.messagePattern.matcher(message);

        if (!matcher.matches()) {
            throw new MessageParseError(message);
        }

        int messageCode = Integer.parseInt(matcher.group("messageCode"));
        if (Message.UNSUBSCRIBE != messageCode) {
            throw new InvalidMessageCode(messageCode);
        }

        return new UnsubscribeMessage(matcher.group("uri"));
    }
}
