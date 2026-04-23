# Alert Generation System

The Alert Generation System monitors patient vitals and triggers alerts when something goes wrong. The main class is `AlertGenerator`, which takes incoming patient data and checks it against thresholds stored in `AlertThreshold` objects. Each threshold defines a metric and its acceptable range for a specific patient, so different patients can have different rules.

When a threshold is exceeded, `AlertGenerator` creates an `Alert` object with the patient ID, the condition that was violated, a timestamp, and a severity level. The alert is then passed to `AlertManager`, which keeps track of active alerts and sends them to medical staff.

I put the threshold logic in its own class because it makes things easier to change later. If you need a new type of threshold you just add a new `AlertThreshold` without touching the generator. `AlertManager` is separate so the generator only worries about detecting problems, not what happens after.

In terms of access, only `AlertGenerator` can create alerts and only `AlertManager` can dispatch them. Medical staff interact with `AlertManager` only, they do not directly access the generator or the thresholds. This keeps sensitive alert logic protected and makes sure alerts always go through the right channel before reaching anyone.
