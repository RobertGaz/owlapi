package org.semanticweb.owlapi.model;

import java.util.HashSet;
import java.util.Set;

public class HasPeriodImpl implements HasPeriod {
    private Set<TimePeriod> periods;

    @Override
    public void addPeriod(TimePeriod period) {
        if (periods == null) {
            periods = new HashSet<>();
        }
        periods.add(period);
    }

    @Override
    public boolean hasPeriods() {
        return periods != null && !periods.isEmpty();
    }

    @Override
    public Set<TimePeriod> getPeriods() {
        return periods;
    }

    @Override
    public void setPeriods(Set<TimePeriod> periods) {
        this.periods = periods;
    }
}
