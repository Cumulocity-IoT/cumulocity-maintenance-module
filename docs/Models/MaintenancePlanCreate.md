# MaintenancePlanCreate
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **name** | **String** | The name of the maintenance plan | [default to null] |
| **description** | **String** | Detailed description of the maintenance plan | [optional] [default to null] |
| **notificationClass** | **String** | Maintenance notification class which initializes the maintenance notification class | [default to null] |
| **notificationText** | **String** | Maintenance text which can be used to initialize the maintenance alarm text | [optional] [default to null] |
| **notificationType** | **String** | Maintenance type which can be used to initialize the maintenance alarm type | [optional] [default to null] |
| **notificationSeverity** | **String** | Maintenance severity which can be used to initialize the maintenance notification severity | [optional] [default to null] |
| **startDate** | **Date** | Start date and time for the maintenance | [default to null] |
| **endDate** | **Date** | End date and time for the maintenance | [default to null] |
| **active** | **Boolean** | Indicates whether the maintenance plan is active | [optional] [default to null] |
| **on** | [**List**](MaintenanceTrigger.md) | List of triggers (combined view) | [optional] [default to null] |
| **apply** | [**DeviceAssignmentCriteria**](DeviceAssignmentCriteria.md) | Device assignment filter criteria | [optional] [default to null] |
| **frequency** | **String** | Frequency of the maintenance | [optional] [default to null] |
| **equipmentId** | **String** | ID of the equipment | [optional] [default to null] |
| **requiredSkills** | **List** | List of required skills | [optional] [default to null] |
| **tasks** | **List** | List of specific maintenance tasks | [optional] [default to null] |
| **onTime** | [**TimeBasedTrigger**](TimeBasedTrigger.md) | Time-based maintenance trigger definition | [optional] [default to null] |
| **onUsage** | [**UsageBasedTrigger**](UsageBasedTrigger.md) | Usage-based maintenance trigger definition | [optional] [default to null] |
| **onConditions** | [**List**](ConditionBasedTrigger.md) | Condition-based maintenance trigger definitions. Conditions are combined using logical AND. | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

