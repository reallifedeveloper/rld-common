package com.reallifedeveloper.common.domain.event;

import java.util.Date;

public class TestEvent extends AbstractDomainEvent {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    public TestEvent(int id, String name, Date occurredOn, int version) {
        super(occurredOn, version);
        this.id = id;
        this.name = name;
    }

    public TestEvent(int id, String name, Date occurredOn) {
        super(occurredOn);
        this.id = id;
        this.name = name;
    }

    public TestEvent(int id, String name) {
        super(new Date());
        this.id = id;
        this.name = name;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestEvent other = (TestEvent) obj;
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TestEvent{id=" + id() + ", name=" + name() + ", occurredOn=" + occurredOn() + "}";
    }
}
