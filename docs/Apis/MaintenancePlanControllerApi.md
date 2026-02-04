# MaintenancePlanControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createMaintenanceAction**](MaintenancePlanControllerApi.md#createMaintenanceAction) | **POST** /api/maintenance/plans/{id}/actions | Create a new maintenance action for maintenance plan |
| [**createMaintenancePlan**](MaintenancePlanControllerApi.md#createMaintenancePlan) | **POST** /api/maintenance/plans | Create a new maintenance plan |
| [**getActiveMaintenancePlansByType**](MaintenancePlanControllerApi.md#getActiveMaintenancePlansByType) | **GET** /api/maintenance/plans | Get all active maintenance plans with optional filtering and pagination |
| [**getMaintenancePlan**](MaintenancePlanControllerApi.md#getMaintenancePlan) | **GET** /api/maintenance/plans/{id} | GET maintenance plan by Id |


<a name="createMaintenanceAction"></a>
# **createMaintenanceAction**
> MaintenanceAction createMaintenanceAction(id, MaintenanceAction)

Create a new maintenance action for maintenance plan

    Creates a new maintenance action in IoT Platform for defined maintenance plan

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Internal maintenance plan Id | [default to null] |
| **MaintenanceAction** | [**MaintenanceAction**](../Models/MaintenanceAction.md)|  | |

### Return type

[**MaintenanceAction**](../Models/MaintenanceAction.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="createMaintenancePlan"></a>
# **createMaintenancePlan**
> MaintenancePlan createMaintenancePlan(MaintenancePlanCreate)

Create a new maintenance plan

    Creates a new maintenance plan in IoT Platform

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **MaintenancePlanCreate** | [**MaintenancePlanCreate**](../Models/MaintenancePlanCreate.md)|  | |

### Return type

[**MaintenancePlan**](../Models/MaintenancePlan.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="getActiveMaintenancePlansByType"></a>
# **getActiveMaintenancePlansByType**
> MaintenancePlanListResponse getActiveMaintenancePlansByType(type, pageSize, pageNumber)

Get all active maintenance plans with optional filtering and pagination

    Returns a list of all maintenance plans in IoT Platform. Additional query parameters allow to filter that list. The default configuration will return all active maintenance plans!

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **type** | **String**| Maintenance plan type | [optional] [default to null] |
| **pageSize** | **Integer**| Maximum number of items to return (default: 20, max: 100) | [optional] [default to 20] |
| **pageNumber** | **Integer**| Number of items to skip (default: 0) | [optional] [default to 0] |

### Return type

[**MaintenancePlanListResponse**](../Models/MaintenancePlanListResponse.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getMaintenancePlan"></a>
# **getMaintenancePlan**
> MaintenancePlan getMaintenancePlan(id)

GET maintenance plan by Id

    Returns maintenance plan by internal Id

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Internal maintenance plan Id | [default to null] |

### Return type

[**MaintenancePlan**](../Models/MaintenancePlan.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

