package org.semanticweb.owlapi.model;

import java.util.Set;

public interface HasPeriod {

    void addPeriod(TimePeriod period);

    boolean hasPeriods();

    Set<TimePeriod> getPeriods();
    void setPeriods(Set<TimePeriod> periods);
}
