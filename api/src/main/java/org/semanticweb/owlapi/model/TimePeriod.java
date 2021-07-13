package org.semanticweb.owlapi.model;

public class TimePeriod {
    private Long start;
    private Long end;

    public TimePeriod() {}

    public TimePeriod(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    public TimePeriod(String start, String end) {
        if (start != null) {
            this.start = Long.parseLong(start);
        }
        if (end != null) {
            this.end = Long.parseLong(end);
        }
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }
}
