package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Adds repeat interval metadata to an alert.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    private long repeatIntervalMillis;

    public RepeatedAlertDecorator(Alert wrappedAlert, long repeatIntervalMillis) {
        super(wrappedAlert);
        this.repeatIntervalMillis = repeatIntervalMillis;
    }

    public long getRepeatIntervalMillis() {
        return repeatIntervalMillis;
    }

    public long getNextCheckTime() {
        return getTimestamp() + repeatIntervalMillis;
    }

    @Override
    public String getCondition() {
        return getWrappedAlert().getCondition() + " (recheck every "
                + repeatIntervalMillis + " ms)";
    }
}
