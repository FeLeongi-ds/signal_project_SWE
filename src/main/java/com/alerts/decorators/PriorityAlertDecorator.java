package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Adds a dynamic priority label to an alert.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    private String priority;

    public PriorityAlertDecorator(Alert wrappedAlert, String priority) {
        super(wrappedAlert);
        this.priority = priority;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    @Override
    public String getCondition() {
        return "[" + priority + "] " + getWrappedAlert().getCondition();
    }
}
