# Maintenance Module Glossary

This glossary provides definitions for key terms and concepts used in the Cumulocity Maintenance Module project.

## Core Maintenance Concepts

### Maintenance Plan
A complete maintenance configuration that defines when, how, and under what conditions maintenance activities should be performed. Contains metadata (id, name, description), timing information (start/end dates), status (active/inactive), and one or more maintenance triggers.

### Maintenance Trigger
A condition or criteria that determines when a maintenance plan should be activated. The system supports three types of triggers that can be combined using OR logic (any trigger can activate the plan).

### Active Status
A boolean flag indicating whether a maintenance plan is currently enabled and monitoring for trigger conditions. Inactive plans are ignored by the system.

## Trigger Types

### Time-Based Trigger
A maintenance trigger that activates based on recurring schedules defined using cron expressions.
- **Example**: `0 9 * * 1` (every Monday at 9:00 AM), `0 0 0 1 * *` (first day of every month at midnight), `0 0 12 * * *` (every day at noon)
- **Use Case**: Regular preventive maintenance like weekly inspections, monthly checks, or scheduled maintenance windows

### Usage-Based Trigger
A maintenance trigger that activates when a monitored counter reaches a specific threshold or cycle value.
- **Components**: Subscription (data source), value fragment (data path), and either cycle value or threshold value
- **Use Case**: Maintenance based on equipment usage like operating hours, production cycles, or alarm counts

### Condition-Based Trigger
A maintenance trigger that activates when multiple specified conditions are simultaneously met (AND logic between conditions).
- **Components**: Multiple maintenance conditions with subscription, value fragment, operator, and comparison value
- **Use Case**: Complex maintenance scenarios requiring multiple sensor readings or status checks

## Data Access Components

### Subscription
Defines the data source and filtering criteria for accessing Cumulocity platform data.
- **API Types**: `alarms`, `events`, `measurements`, `managedobjects`, `operations`
- **Type Filter**: Specific data type to monitor (e.g., `c8y_Temperature`, `restartAlarm`)

### Value Fragment
A JSONPath expression used to extract specific values from the subscribed data structure.
- **Examples**: 
  - `$.operatingHours.t` - extracts temperature value from operating hours measurement
  - `$.c8y_Temperature.t` - extracts temperature value from Cumulocity temperature measurement
  - `count` - simple counter value

## Counter Types

### Cycle Value
A recurring value threshold for continuously incrementing counters. When reached, the counter resets and the cycle repeats.
- **Example**: Maintenance every 1000 operating hours
- **Behavior**: Counter resets to 0 after reaching the cycle value

### Threshold Value
A limit value for counters that accumulate until maintenance is performed. Used for non-resetting counters.
- **Example**: Maintenance after 200 restart alarms
- **Behavior**: Counter continues accumulating until manually reset

## Condition Operators

### Comparison Operators
Used in condition-based triggers to compare monitored values against reference values:
- **gt** (greater than): Triggers when monitored value > reference value
- **lt** (less than): Triggers when monitored value < reference value  
- **eq** (equal): Triggers when monitored value = reference value

## API and Data Formats

### ISO 8601 Duration Format
International standard for representing time intervals in time-based triggers.
- **Pattern**: `P[n]Y[n]M[n]W[n]DT[n]H[n]M[n]S`
- **Examples**:
  - `P30D` - 30 days
  - `P3M` - 3 months
  - `PT2H` - 2 hours
  - `P1Y6M` - 1 year and 6 months

### JSONPath Expression
A query language for JSON that allows extraction of specific values from complex data structures.
- **Purpose**: Defines which specific data field to monitor within subscription data
- **Format**: Uses dot notation and array indexing to navigate JSON structure

## Logical Operations

### OR Logic (Between Triggers)
When multiple triggers are defined in a maintenance plan, ANY trigger condition can activate the maintenance (logical OR).
- **Example**: Plan activates on either 30-day interval OR temperature > 80°C

### AND Logic (Within Conditions)
When multiple conditions are defined in a condition-based trigger, ALL conditions must be met simultaneously (logical AND).
- **Example**: Temperature > 80°C AND pressure < 50 PSI AND operating hours > 1000

## Cumulocity Platform Integration

### Cumulocity APIs
The platform APIs that the maintenance module can subscribe to for monitoring data:
- **Alarms**: Error conditions, warnings, and system alerts
- **Events**: Discrete occurrences and state changes
- **Measurements**: Sensor readings and telemetry data
- **Managed Objects**: Device and asset information
- **Operations**: Commands and control instructions

### Microservice Architecture
The maintenance module is implemented as a Cumulocity microservice, providing:
- **Isolation**: Independent deployment and scaling
- **Integration**: Native access to Cumulocity platform APIs
- **Authentication**: Automatic handling of platform security
- **Multi-tenancy**: Support for multiple customers/organizations

## Example Scenarios

### Preventive Maintenance
Regular maintenance performed at fixed intervals regardless of equipment condition.
- **Trigger Type**: Time-based
- **Example**: Monthly equipment inspection every 30 days

### Predictive Maintenance  
Maintenance triggered by equipment usage patterns or performance metrics.
- **Trigger Type**: Usage-based or Condition-based
- **Example**: Maintenance after 1000 operating hours or when temperature exceeds threshold

### Reactive Maintenance
Maintenance triggered by specific alarm conditions or system states.
- **Trigger Type**: Condition-based
- **Example**: Maintenance when critical alarms occur and system performance degrades