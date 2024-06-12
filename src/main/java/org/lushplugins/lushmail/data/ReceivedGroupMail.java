package org.lushplugins.lushmail.data;

public class ReceivedGroupMail {
    private final String group;
    private final String id;
    private final long timeSent;
    private final long timeout;

    public ReceivedGroupMail(String id, String group, long timeSent, long timeout) {
        this.id = id;
        this.group = group;
        this.timeSent = timeSent;
        this.timeout = timeout;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public long getTimeout() {
        return timeout;
    }
}
