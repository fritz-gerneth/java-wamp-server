package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.WelcomeMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WelcomeMessageConverter implements Converter<WelcomeMessage>
{
    private static final Pattern messagePattern = Pattern.compile(
          "\\[\\s*(?<messageCode>\\d+)\\s*,"
        + "\\s*\"(?<sessionId>.*?)\"\\s*,"
        + "\\s*(?<protocolVersion>\\d+)\\s*,"
        + "\\s*\"(?<serverIdent>.*?)\"\\s*\\]"
    );

    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.WELCOME == messageCode;
    }

    public String serialize(WelcomeMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        return "[" + Message.WELCOME + ", \"" + socket.getSessionId() + "\", 1, \"\"]";
    }

    @Override
    public WelcomeMessage deserialize(String message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        Matcher matcher = WelcomeMessageConverter.messagePattern.matcher(message);

        if (!matcher.matches()) {
            throw new MessageParseException(message);
        }

        int messageCode = Integer.parseInt(matcher.group("messageCode"));
        if (Message.WELCOME != messageCode) {
            throw new InvalidMessageCodeException(messageCode);
        }

        WelcomeMessage rebuiltMessage = new WelcomeMessage(
            matcher.group("sessionId"),
            Integer.parseInt(matcher.group("protocolVersion")),
            matcher.group("serverIdent")
        );
        return rebuiltMessage;
    }
}
