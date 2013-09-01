package de.effms.wamp.core._messageMocks;

public class PartialPublishMessage
{
    private Object event;

    private boolean excludeMe;

    public Object getEvent()
    {
        return event;
    }

    public void setEvent(Object event)
    {
        this.event = event;
    }

    public boolean isExcludeMe()
    {
        return excludeMe;
    }

    public void setExcludeMe(boolean excludeMe)
    {
        this.excludeMe = excludeMe;
    }
}
