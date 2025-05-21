# cumulocity-maintenance-module
This module extends Cumulocity with maintenance plans for devices and assets. It gets configured by a declarative JSON file and provides a REST API to manage maintenance plans. Each maintenance plan can be associated with one or more devices and assets, and it can be scheduled based on time, usage, or condition.

## Maintenance Types

### Time-based Maintenance (TBM)

• Maintenance performed at regular time intervals (e.g., every 6 months).

• Often used for components with predictable wear patterns.


### Usage-based Maintenance

• Based on operational metrics like hours run, cycles, or kilometers.

• Example: Engine overhaul after 10,000 operating hours.


### Condition-based Maintenance (CBM)

• Triggered when a specific condition or parameter reaches a threshold (e.g., vibration, temperature).

• Requires monitoring systems and sensors.
