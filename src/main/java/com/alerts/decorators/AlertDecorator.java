package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Decorator base class that wraps an alert and can extend its behavior.
 */
public abstract class AlertDecorator extends Alert {
    private Alert wrappedAlert;

    protected AlertDecorator(Alert wrappedAlert) {
        super(wrappedAlert.getPatientId(), wrappedAlert.getCondition(), wrappedAlert.getTimestamp());
        this.wrappedAlert = wrappedAlert;
    }

    protected Alert getWrappedAlert() {
        return wrappedAlert;
    }

    @Override
    public String getPatientId() {
        return wrappedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return wrappedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return wrappedAlert.getTimestamp();
    }

    @Override
    public String getPriority() {
        return wrappedAlert.getPriority();
    }
}
