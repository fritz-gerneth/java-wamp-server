package de.effms.wamp.core.resolver;

import de.effms.wamp.core.MessageParseException;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTypeResolver
{
    private final Map<MessageType, List<ClassTypeResolverInterface>> resolverMap;

    private final Pattern messageCodeMatcher;

    public MessageTypeResolver()
    {
        this.resolverMap = new HashMap<MessageType, List<ClassTypeResolverInterface>>();
        this.messageCodeMatcher = Pattern.compile("\\[\\s*(?<messageCode>\\d+)\\s*,(.*?)");
    }

    public void addResolver(MessageType messageType, Class resolvedType)
    {
        this.addResolver(messageType, new StandardClassTypeResolver(resolvedType));
    }

    public void addResolver(MessageType messageType, ClassTypeResolverInterface resolver)
    {
        List<ClassTypeResolverInterface> codeSpecificResolvers;
        if (!this.resolverMap.containsKey(messageType)) {
            codeSpecificResolvers = new ArrayList<ClassTypeResolverInterface>();
            this.resolverMap.put(messageType, codeSpecificResolvers);
        } else {
            codeSpecificResolvers = this.resolverMap.get(messageType);
        }
        codeSpecificResolvers.add(resolver);
    }

    public Class resolve(String message, Websocket socket) throws MessageParseException
    {
        Matcher seg = this.messageCodeMatcher.matcher(message);
        if (!seg.matches()) {
            throw new MessageParseException("Unable to determine message code for " + message);
        }

        try {
            return this.resolve(
                MessageType.typeOfCode(Integer.parseInt(seg.group("messageCode"))),
                message,
                socket
            );
        } catch (IllegalArgumentException ex) {
            throw new MessageParseException(ex);
        }
    }

    protected Class resolve(MessageType messageType, String message, Websocket socket) throws MessageParseException
    {
        if (!this.resolverMap.containsKey(messageType)) {
            throw new MessageParseException("No class registered for " + messageType);
        }

        List<ClassTypeResolverInterface> codeSpecificResolvers = this.resolverMap.get(messageType);
        for (ClassTypeResolverInterface resolver: codeSpecificResolvers) {
            Class t = resolver.tryResolve(message, socket);
            if (null != t) {
                return t;
            }
        }

        throw new MessageParseException("Could not resolve " + message + " to a class");
    }
}
