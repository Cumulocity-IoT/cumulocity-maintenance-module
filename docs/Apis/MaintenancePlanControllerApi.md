# MaintenancePlanControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createMaintenancePlan**](MaintenancePlanControllerApi.md#createMaintenancePlan) | **POST** /api/maintenance-plans | Create a new maintenance plan |
| [**getAllMaintenancePlans**](MaintenancePlanControllerApi.md#getAllMaintenancePlans) | **GET** /api/maintenance-plans | Get all maintenance plans with optional filtering and pagination |
| [**getMaintenancePlan**](MaintenancePlanControllerApi.md#getMaintenancePlan) | **GET** /api/maintenance-plans/{id} | GET maintenance plan by Id |


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

<a name="getAllMaintenancePlans"></a>
# **getAllMaintenancePlans**
> MaintenancePlanListResponse getAllMaintenancePlans(active, startDate, endDate, pageSize, pageNumber, arg0, arg1, arg2, arg3, arg4)

Get all maintenance plans with optional filtering and pagination

    Returns a list of all maintenance plans in IoT Platform. Additional query parameters allow to filter that list. The default configuration will return all active maintenance plans!

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **active** | **Boolean**| Filter by active status | [optional] [default to null] |
| **startDate** | **Date**| Filter plans starting after this date (ISO 8601 format) | [optional] [default to null] |
| **endDate** | **Date**| Filter plans ending before this date (ISO 8601 format) | [optional] [default to null] |
| **pageSize** | **Integer**| Maximum number of items to return (default: 20, max: 100) | [optional] [default to 20] |
| **pageNumber** | **Integer**| Number of items to skip (default: 0) | [optional] [default to 0] |
| **arg0** | **Boolean**|  | [optional] [default to null] |
| **arg1** | **Date**|  | [optional] [default to null] |
| **arg2** | **Date**|  | [optional] [default to null] |
| **arg3** | **Integer**|  | [optional] [default to 20] |
| **arg4** | **Integer**|  | [optional] [default to 0] |

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

