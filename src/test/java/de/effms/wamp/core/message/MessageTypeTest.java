package de.effms.wamp.core.message;

import org.junit.Assert;
import org.junit.Test;

public class MessageTypeTest
{
    @Test
    public void typeOfCodeReturnsMatchingValueForIntegerCode()
    {
        Assert.assertEquals(MessageType.WELCOME, MessageType.typeOfCode(0));
        Assert.assertEquals(MessageType.PREFIX, MessageType.typeOfCode(1));
        Assert.assertEquals(MessageType.CALL, MessageType.typeOfCode(2));
        Assert.assertEquals(MessageType.CALL_RESULT, MessageType.typeOfCode(3));
        Assert.assertEquals(MessageType.CALL_ERROR, MessageType.typeOfCode(4));
        Assert.assertEquals(MessageType.SUBSCRIBE, MessageType.typeOfCode(5));
        Assert.assertEquals(MessageType.UNSUBSCRIBE, MessageType.typeOfCode(6));
        Assert.assertEquals(MessageType.PUBLISH, MessageType.typeOfCode(7));
        Assert.assertEquals(MessageType.EVENT, MessageType.typeOfCode(8));
    }

    @Test(expected = IllegalArgumentException.class)
    public void typeOfCodeThrowsExceptionOnInvalidIntegerCode()
    {
        MessageType.typeOfCode(33);
    }
}
