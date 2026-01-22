# Documentation for Cumulocity Maintenance Module API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *MaintenancePlanControllerApi* | [**createMaintenancePlan**](Apis/MaintenancePlanControllerApi.md#createmaintenanceplan) | **POST** /api/maintenance-plans | Create a new maintenance plan |
*MaintenancePlanControllerApi* | [**getAllMaintenancePlans**](Apis/MaintenancePlanControllerApi.md#getallmaintenanceplans) | **GET** /api/maintenance-plans | Get all maintenance plans with optional filtering and pagination |
*MaintenancePlanControllerApi* | [**getMaintenancePlan**](Apis/MaintenancePlanControllerApi.md#getmaintenanceplan) | **GET** /api/maintenance-plans/{id} | GET maintenance plan by Id |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [ConditionBasedTrigger](./Models/ConditionBasedTrigger.md)
 - [DeviceAssignmentCriteria](./Models/DeviceAssignmentCriteria.md)
 - [MaintenanceCondition](./Models/MaintenanceCondition.md)
 - [MaintenancePlan](./Models/MaintenancePlan.md)
 - [MaintenancePlanCreate](./Models/MaintenancePlanCreate.md)
 - [MaintenancePlanListResponse](./Models/MaintenancePlanListResponse.md)
 - [Subscription](./Models/Subscription.md)
 - [TimeBasedTrigger](./Models/TimeBasedTrigger.md)
 - [UsageBasedTrigger](./Models/UsageBasedTrigger.md)
 - [UsageCounter](./Models/UsageCounter.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

<a name="basicAuth"></a>
### basicAuth

- **Type**: HTTP basic authentication

