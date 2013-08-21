package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.PublishMessage;
import junit.framework.Assert;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PublishMessageConverterTest extends GeneralMessageTests<PublishMessage>
{
    @Before
    public void setUp()
    {
        this.converter = new PublishMessageConverter();
    }

    @Test
    public void canConvertPublishMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.PUBLISH));
    }

    @Test
    public void canNOtConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.INVALID));
    }

    @Test
    public void serializeSimpleEventMessage(final PublishMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "http://effms.de";
            message.getEvent(); result = "event";
            message.isTargeted(); result = false;
            message.excludeMe(); result = false;
            socket.inflateCURI("http://effms.de"); result = "http://effms.de";
        }};

        Assert.assertEquals(
            "[" + Message.PUBLISH + ",\"http://effms.de\",\"event\"]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeEventMessageWithExcludeMe(final PublishMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "http://effms.de";
            message.getEvent(); result = "event";
            message.isTargeted(); result = true;
            message.getExclude(); result = new ArrayList<String>() {{ add("excludeId"); }};
            message.getEligible(); result = new ArrayList<String>();
            socket.inflateCURI("http://effms.de"); result = "http://effms.de";
        }};

        Assert.assertEquals(
            "[" + Message.PUBLISH + ",\"http://effms.de\",\"event\",[\"excludeId\"],[]]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeTargetedEventMessageWithEligible(final PublishMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "http://effms.de";
            message.getEvent(); result = "event";
            message.isTargeted(); result = true;
            message.getExclude(); result = new ArrayList<String>();
            message.getEligible(); result = new ArrayList<String>() {{ add("eligibleId"); }};
            socket.inflateCURI("http://effms.de"); result = "http://effms.de";
        }};

        Assert.assertEquals(
            "[" + Message.PUBLISH + ",\"http://effms.de\",\"event\",[],[\"eligibleId\"]]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeTargetedEventMessageWithExcludeAndEligible(final PublishMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "http://effms.de";
            message.getEvent(); result = "event";
            message.isTargeted(); result = true;
            message.getExclude(); result = new ArrayList<String>() {{ add("excludeId"); }};
            message.getEligible(); result = new ArrayList<String>() {{ add("eligibleId"); }};
            socket.inflateCURI("http://effms.de"); result = "http://effms.de";
        }};

        Assert.assertEquals(
            "[" + Message.PUBLISH + ",\"http://effms.de\",\"event\",[\"excludeId\"],[\"eligibleId\"]]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeTargetedEventMessageTargetedMessagesPrecedesExcludeMe(final PublishMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "http://effms.de";
            message.getEvent(); result = "event";
            message.isTargeted(); result = true;
            message.excludeMe(); result = true;
            message.getExclude(); result = new ArrayList<String>() {{}};
            message.getEligible(); result = new ArrayList<String>() {{}};
            socket.inflateCURI("http://effms.de"); result = "http://effms.de";
        }};

        Assert.assertEquals(
            "[" + Message.PUBLISH + ",\"http://effms.de\",\"event\",[],[]]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeMessageDelegatesToSocketInflateCURI(final PublishMessage message, final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "topic";
        }};

        this.converter.serialize(message, socket);

        new Verifications() {{
            socket.inflateCURI("topic");
        }};
    }

    @Test
    public void serializeMessageUsesResultOfCURIInflation(final PublishMessage message, final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "topic";
            message.getEvent(); result = "event";
            message.isTargeted(); result = true;
            message.excludeMe(); result = true;
            message.getExclude(); result = new ArrayList<String>() {{}};
            message.getEligible(); result = new ArrayList<String>() {{}};
            socket.inflateCURI("topic"); result = "http://effms.de";
        }};

        Assert.assertEquals(
            "[" + Message.PUBLISH + ",\"http://effms.de\",\"event\",[],[]]",
            this.converter.serialize(message, socket)
        );
    }


    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenTopicFieldIsNoString(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", null, null, true]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenExcludeMeFieldIsNoBoolean(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, null]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenExcludeFieldIsNoArray(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, null, []]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenEligibleFieldIsNoArray(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [], null]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenExcludeMeHasNonStrings(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [null], []]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenExcludeMeHasEmptyStrings(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [\"\"], []]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenEligibleMeHasNonStrings(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [], [null]]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenEligibleMeHasEmptyStrings(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [], [\"\"]]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenMessageHasTooManyFields(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [], [], \"additional\"]", socket);
    }

    @Test
    public void deserializeAssumesIncludeMeForSimpleForm(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        PublishMessage message = this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null]", socket);

        Assert.assertFalse(message.excludeMe());
    }

    @Test
    public void deserializeMessageWithExcludeMeFalse(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        PublishMessage message = this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, false]", socket);

        Assert.assertFalse(message.excludeMe());
    }

    @Test
    public void deserializeMessageWithExcludeMeTrue(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        PublishMessage message = this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, true]", socket);

        Assert.assertTrue(message.excludeMe());
    }

    @Test
    public void deserializeTargetedMessageWithExclude(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        PublishMessage message = this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [\"exclude\"], []]", socket);

        Assert.assertEquals(new ArrayList<String>() {{ add("exclude"); }}, message.getExclude());
    }

    @Test
    public void deserializeTargetedMessageWithEligible(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        PublishMessage message = this.converter.deserialize("[" + Message.PUBLISH + ", \"topic\", null, [], [\"eligible\"]]", socket);

        Assert.assertEquals(new ArrayList<String>() {{ add("eligible"); }}, message.getEligible());
    }
}
