package de.effms.wamp.core.resolver;

import de.effms.wamp.core.MessageParseException;
import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.MessageType;
import mockit.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageTypeResolverTest
{
    private MessageTypeResolver resolver;

    @Mocked
    private Websocket socket;

    @Before
    public void setUp()
    {
        this.resolver = new MessageTypeResolver();
    }

    @Test(expected = MessageParseException.class)
    public void resolveThrowsExceptionOnNonIntegerMessageCode() throws MessageParseException
    {
        this.resolver.resolve("[false, ", this.socket);
    }

    @Test(expected = MessageParseException.class)
    public void resolveThrowsExceptionForMessageTypesWithNoRegisteredTypeResolver() throws MessageParseException
    {
        this.resolver.resolve("[1, ", this.socket);
    }

    @Test
    public void resolveInvokesTypeResolverForSpecificMessageCode(final StandardClassTypeResolver typeResolver)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString, (Websocket) any); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);

        final String message = "[2, \"\"]";
        this.resolver.resolve(message, this.socket);

        new Verifications() {{
            typeResolver.tryResolve(message, (Websocket) any);
        }};
    }

    @Test
    public void resolveInvokesResolverInOrderOfBeingAdded(final StandardClassTypeResolver typeResolver, final StandardClassTypeResolver typeResolver2)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString, (Websocket) any); result = null;
            typeResolver2.tryResolve(anyString, (Websocket) any); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);
        this.resolver.addResolver(MessageType.CALL, typeResolver2);

        final String message = "[2, \"\"]";
        this.resolver.resolve(message, this.socket);

        new VerificationsInOrder() {{
            typeResolver.tryResolve(message, (Websocket) any);
            typeResolver2.tryResolve(message, (Websocket) any);
        }};
    }



    @Test
    public void resolveInvokesReturnsAfterFirstSuccessfulResolve(final StandardClassTypeResolver typeResolver, final StandardClassTypeResolver typeResolver2)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString, (Websocket) any); result = TestType.class;
            typeResolver2.tryResolve(anyString, (Websocket) any); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);
        this.resolver.addResolver(MessageType.CALL, typeResolver2);

        final String message = "[2, \"\"]";
        this.resolver.resolve(message, this.socket);

        new Verifications() {{
            typeResolver.tryResolve(message, (Websocket) any); times = 1;
            typeResolver2.tryResolve(message, (Websocket) any); times = 0;
        }};
    }

    @Test(expected = MessageParseException.class)
    public void resolveThrowsExceptionIfNoResolverReturnsType(final StandardClassTypeResolver typeResolver)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString, (Websocket) any); result = null;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);

        this.resolver.resolve("[2, \"\"]", this.socket);
    }

    @Test
    public void resolveReturnsTypeOfResolver(final StandardClassTypeResolver typeResolver)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString, (Websocket) any); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);

        Assert.assertEquals(TestType.class, this.resolver.resolve("[2, \"\"]", this.socket));
    }

    @Test
    public void resolveCanResolveTypesAddedByTypeWithoutResolver() throws MessageParseException
    {
        this.resolver.addResolver(MessageType.CALL, TestType.class);

        Assert.assertEquals(TestType.class, this.resolver.resolve("[2, \"\"]", this.socket));
    }

    @Test
    public void resolvePassesSocketToResolvers(final StandardClassTypeResolver typeResolver) throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString, (Websocket) any); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);

        this.resolver.resolve("[2, \"\"]", socket);

        new Verifications() {{
            typeResolver.tryResolve(anyString, MessageTypeResolverTest.this.socket);
        }};
    }

    public static class TestType {}
}
