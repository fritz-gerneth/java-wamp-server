package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.PrefixMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrefixMessageConverter implements Converter<PrefixMessage>
{
    private static final Pattern messagePattern = Pattern.compile(
            "\\[\\s*(?<messageCode>\\d+)\\s*,"
            + "\\s*\"(?<prefix>.+?)\"\\s*,"
            + "\\s*\"(?<uri>.+?)\"\\s*\\]"
    );

    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.PREFIX == messageCode;
    }

    @Override
    public String serialize(PrefixMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        return "[" + Message.PREFIX + ", \"" + message.getPrefix() + "\", \"" + message.getURI() + "\"]";
    }

    @Override
    public PrefixMessage deserialize(String message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        Matcher matcher = PrefixMessageConverter.messagePattern.matcher(message);

        if (!matcher.matches()) {
            throw new MessageParseException(message);
        }

        int messageCode = Integer.parseInt(matcher.group("messageCode"));
        if (Message.PREFIX != messageCode) {
            throw new InvalidMessageCodeException(messageCode);
        }

        PrefixMessage rebuiltMessage = new PrefixMessage(
                matcher.group("prefix"),
                matcher.group("uri")
        );
        return rebuiltMessage;
    }
}
