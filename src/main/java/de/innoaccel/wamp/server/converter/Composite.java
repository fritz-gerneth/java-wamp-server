package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Composite implements Converter
{
    private List<Converter> converters;

    private Pattern messageCodeMatcher;

    public Composite()
    {
        this(null);
    }

    public Composite(Collection<Converter> converters)
    {
        this.converters = new LinkedList<Converter>(converters);
        this.messageCodeMatcher = Pattern.compile("\\[\\s*(?<messageCode>\\d+)\\s*,(.*?)");
    }

    @Override
    public boolean canConvert(int messageCode)
    {
        for (Converter h: this.converters) {
            if (h.canConvert(messageCode)) {
                return true;
            }
        }
        return false;
    }

    public Converter getConverter(int messageCode)
    {
        for (Converter converter: this.converters) {
            if (converter.canConvert(messageCode)) {
                return converter;
            }
        }
        return null;
    }

    @Override
    public String serialize(Message message, Websocket socket) throws InvalidMessageCodeException
    {
        Converter converter = this.getConverter(message.getMessageCode());
        if (null == converter) {
            throw new InvalidMessageCodeException(message.getMessageCode());
        }

        return converter.serialize(message, socket);
    }

    @Override
    public Message deserialize(String message, Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        Matcher matcher = this.messageCodeMatcher.matcher(message);

        if (!matcher.matches()) {
            throw new MessageParseException(message);
        }

        int messageCode = Integer.parseInt(matcher.group(1));
        Converter messageConverter = this.getConverter(messageCode);
        if (null == messageConverter) {
            throw new InvalidMessageCodeException(messageCode);
        }

        return messageConverter.deserialize(message, socket);
    }
}
