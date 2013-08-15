package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.WelcomeMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WelcomeMessageConverter implements Converter
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

    @Override
    public String serialize(Message message, Websocket socket) throws InvalidMessageCode
    {
        throw new InvalidMessageCode(message.getMessageCode());
    }

    public String serialize(WelcomeMessage message, Websocket socket) throws InvalidMessageCode
    {
        return "[" + Message.WELCOME + ", \"" + socket.getSessionId() + "\", 1, \"\"]";
    }

    @Override
    public Message deserialize(String message, Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        Matcher matcher = WelcomeMessageConverter.messagePattern.matcher(message);

        if (!matcher.matches()) {
            throw new MessageParseError(message);
        }

        int messageCode = Integer.parseInt(matcher.group("messageCode"));
        if (Message.WELCOME != messageCode) {
            throw new InvalidMessageCode(messageCode);
        }

        WelcomeMessage rebuiltMessage = new WelcomeMessage(
            matcher.group("sessionId"),
            Integer.parseInt(matcher.group("protocolVersion")),
            matcher.group("serverIdent")
        );
        return rebuiltMessage;
    }
}
