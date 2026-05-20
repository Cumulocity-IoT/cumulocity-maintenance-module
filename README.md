# cumulocity-maintenance-module
This module extends Cumulocity with maintenance plans for devices and assets. It gets configured by a declarative JSON file and provides a REST API to manage maintenance plans. In comparison to Analytics Builder which offers an imperative approach, this module focuses on declarative definitions and focus only on this maintenance aspect. Each maintenance plan can be associated with one or more devices and assets, and it can be scheduled based on time, usage, or condition.

## Maintenance Types

### Time-based Maintenance (TBM)

• Maintenance performed at regular time intervals (e.g., every 6 months).

• Often used for components with predictable wear patterns.

Example:

```json

{
  "name": "Time-based Maintenance",
  "description": "This is a simple time-based maintenance task.",
  "text": "Please contact your service for planned maintenance, the 6 month maintenance plan is reached!",
  "startDate": "2025-10-01T00:00:00Z",
  "endDate": "2030-10-02T00:00:00Z",
  "active": true,
  "on": [
    {
      "type": "Time-based",
      "interval": "P6M"
    }
  ]
}

```


### Usage-based Maintenance

• Based on operational metrics like hours run, cycles, or kilometers.

• Example: Engine overhaul after 10,000 operating hours.

Example:

```json

{
  "name": "Operating Hours Based Maintenance",
  "description": "Triggers maintenance when operating hours for a device exceed 10000 hours, based on the 'operatingHours' measurement type.",
  "text": "Please schedule an engine overhaul, operating hours have exceeded the threshold of 10000 hours.",
  "startDate": "2025-05-22T00:00:00Z",
  "endDate": "2030-05-22T00:00:00Z",
  "active": true,
  "on": [
    {
      "type": "Usage-based",
      "counter": {
          "subscription": {
            "api": "measurements",
            "typeFilter": "operatingHours"
          },
          "valueFragment": "operatingHours.t",
          "operator": "gt",
          "value": 10000.0
        }
    }
  ]
}
```

### Condition-based Maintenance (CBM)

• Triggered when a specific condition or parameter reaches a threshold (e.g., vibration, temperature).

• Requires monitoring systems and sensors.

Example:

```json

{
  "name": "Condition-based Maintenance",
  "description": "Triggers maintenance when the temperature exceeds 75 degrees Celsius based on the 'temperature' measurement type.",
  "text": "Please inspect the device, temperature has exceeded the threshold of 75°C.",
  "startDate": "2025-06-15T00:00:00Z",
  "endDate": "2030-06-15T00:00:00Z",
  "active": true,
  "on": [
    {
      "type": "Condition-based",
      "condition": {
          "subscription": {
            "api": "measurements",
            "typeFilter": "temperature"
          },
          "valueFragment": "temperature.t",
          "operator": "gt",
          "value": 75.0
        }
    }
  ]
}
```
