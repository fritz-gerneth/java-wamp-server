package de.effms.wamp.core.deserializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.effms.wamp.core._messageMocks.FullPublishMessage;
import de.effms.wamp.core._messageMocks.PartialPublishMessage;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class PublishMessageDeserializerTest
{
    private ObjectMapper getObjectMapperWith(Class classType, PublishMessageDeserializer deserializer)
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("testing", new Version(1, 0, 0, null, null, null))
            .addDeserializer(classType, deserializer);
        mapper.registerModule(module);
        return mapper;
    }

    @Test
    public void deserializeThrowsExceptionOnMissingOrWrongFields() throws IOException
    {
        PublishMessageDeserializer<FullPublishMessage> deserializer =
            new PublishMessageDeserializer<FullPublishMessage>(FullPublishMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullPublishMessage.class, deserializer);

        String[] messages = {
            "[45, \"topic\", \"event\"]",
            "",
            "7, \"topic\", \"event\"]",
            "[7, \"topic\"]",
            "[7, 53, 1]",
            "[7, \"topic\", \"event\", \"notABoolean\"]",
            "[7, \"topic\", \"event\", [], \"NotAList\"]",
            "[7, \"topic\", \"event\", []]",
            "[7]",
            "[7",
        };

        for (String m: messages) {
            try {
                FullPublishMessage x = mapper.readValue(m, FullPublishMessage.class);
                Assert.fail("Expected IOException for " + m);
            } catch (IOException e) {}
        }
    }

    @Test
    public void deserializeCanPopulateObjectPartially() throws IOException
    {
        PublishMessageDeserializer<PartialPublishMessage> deserializer =
            new PublishMessageDeserializer<PartialPublishMessage>(PartialPublishMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(PartialPublishMessage.class, deserializer);

        PartialPublishMessage message = mapper.readValue(
            "[7, \"topic\", \"event\"]",
            PartialPublishMessage.class
        );

        Assert.assertNotNull(message.getEvent());
    }

    @Test
    public void deserializeCanPopulateObjectWithExcludeMeFalse() throws IOException
    {
        PublishMessageDeserializer<FullPublishMessage> deserializer =
            new PublishMessageDeserializer<FullPublishMessage>(FullPublishMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullPublishMessage.class, deserializer);

        FullPublishMessage message = mapper.readValue(
            "[7, \"topic\", \"event\", false]",
            FullPublishMessage.class
        );

        Assert.assertEquals("topic", message.getTopicUri());
        Assert.assertNotNull(message.getEvent());
        Assert.assertFalse(message.isExcludeMe());
    }

    @Test
    public void deserializeCanPopulateObjectWithExcludeMeTrue() throws IOException
    {
        PublishMessageDeserializer<FullPublishMessage> deserializer =
            new PublishMessageDeserializer<FullPublishMessage>(FullPublishMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullPublishMessage.class, deserializer);

        FullPublishMessage message = mapper.readValue(
            "[7, \"topic\", \"event\", true]",
            FullPublishMessage.class
        );

        Assert.assertEquals("topic", message.getTopicUri());
        Assert.assertNotNull(message.getEvent());
        Assert.assertTrue(message.isExcludeMe());
    }

    @Test
    public void deserializeCanPopulateObjectWithExcludeInclude() throws IOException
    {
        PublishMessageDeserializer<FullPublishMessage> deserializer =
            new PublishMessageDeserializer<FullPublishMessage>(FullPublishMessage.class);
        ObjectMapper mapper = this.getObjectMapperWith(FullPublishMessage.class, deserializer);

        FullPublishMessage message = mapper.readValue(
            "[7, \"topic\", \"event\", [\"exclude\", \"exclude2\"], [\"include\"]]",
            FullPublishMessage.class
        );

        Assert.assertEquals(new ArrayList<String>() {{
            this.add("exclude");
            this.add("exclude2");
        }}, message.getExclude());
        Assert.assertEquals(new ArrayList<String>() {{
            this.add("include");
        }}, message.getEligible());
    }
}
