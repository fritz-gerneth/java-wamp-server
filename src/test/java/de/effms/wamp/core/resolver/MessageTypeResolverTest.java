package de.effms.wamp.core.resolver;

import de.effms.wamp.core.MessageParseException;
import de.effms.wamp.core.message.MessageType;
import org.junit.Assert;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.VerificationsInOrder;
import org.junit.Before;
import org.junit.Test;

public class MessageTypeResolverTest
{
    private MessageTypeResolver resolver;

    @Before
    public void setUp()
    {
        this.resolver = new MessageTypeResolver();
    }

    @Test(expected = MessageParseException.class)
    public void resolveThrowsExceptionOnNonIntegerMessageCode() throws MessageParseException
    {
        this.resolver.resolve("[false, ");
    }

    @Test(expected = MessageParseException.class)
    public void resolveThrowsExceptionForMessageTypesWithNoRegisteredTypeResolver() throws MessageParseException
    {
        this.resolver.resolve("[1, ");
    }

    @Test
    public void resolveInvokesTypeResolverForSpecificMessageCode(final StandardClassTypeResolver typeResolver)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);

        final String message = "[2, \"\"]";
        this.resolver.resolve(message);

        new Verifications() {{
            typeResolver.tryResolve(message);
        }};
    }

    @Test
    public void resolveInvokesResolverInOrderOfBeingAdded(final StandardClassTypeResolver typeResolver, final StandardClassTypeResolver typeResolver2)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString); result = null;
            typeResolver2.tryResolve(anyString); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);
        this.resolver.addResolver(MessageType.CALL, typeResolver2);

        final String message = "[2, \"\"]";
        this.resolver.resolve(message);

        new VerificationsInOrder() {{
            typeResolver.tryResolve(message);
            typeResolver2.tryResolve(message);
        }};
    }



    @Test
    public void resolveInvokesReturnsAfterFirstSuccessfulResolve(final StandardClassTypeResolver typeResolver, final StandardClassTypeResolver typeResolver2)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString); result = TestType.class;
            typeResolver2.tryResolve(anyString); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);
        this.resolver.addResolver(MessageType.CALL, typeResolver2);

        final String message = "[2, \"\"]";
        this.resolver.resolve(message);

        new Verifications() {{
            typeResolver.tryResolve(message); times = 1;
            typeResolver2.tryResolve(message); times = 0;
        }};
    }

    @Test(expected = MessageParseException.class)
    public void resolveThrowsExceptionIfNoResolverReturnsType(final StandardClassTypeResolver typeResolver)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString); result = null;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);

        this.resolver.resolve("[2, \"\"]");
    }

    @Test
    public void resolveReturnsTypeOfResolver(final StandardClassTypeResolver typeResolver)
        throws MessageParseException
    {
        new NonStrictExpectations() {{
            typeResolver.tryResolve(anyString); result = TestType.class;
        }};
        this.resolver.addResolver(MessageType.CALL, typeResolver);

        Assert.assertEquals(TestType.class, this.resolver.resolve("[2, \"\"]"));
    }

    @Test
    public void resolveCanResolveTypesAddedByTypeWithoutResolver() throws MessageParseException
    {
        this.resolver.addResolver(MessageType.CALL, TestType.class);

        Assert.assertEquals(TestType.class, this.resolver.resolve("[2, \"\"]"));
    }

    public static class TestType {}
}
