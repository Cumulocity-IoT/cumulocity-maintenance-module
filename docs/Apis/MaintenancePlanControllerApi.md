# MaintenancePlanControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createMaintenanceAction**](MaintenancePlanControllerApi.md#createMaintenanceAction) | **POST** /api/maintenance/plans/{id}/actions | Create a new maintenance action for maintenance plan |
| [**createMaintenancePlan**](MaintenancePlanControllerApi.md#createMaintenancePlan) | **POST** /api/maintenance/plans | Create a new maintenance plan |
| [**deleteMaintenancePlan**](MaintenancePlanControllerApi.md#deleteMaintenancePlan) | **DELETE** /api/maintenance/plans/{id} |  |
| [**getAllMaintenancePlans**](MaintenancePlanControllerApi.md#getAllMaintenancePlans) | **GET** /api/maintenance/plans |  |
| [**getMaintenancePlan**](MaintenancePlanControllerApi.md#getMaintenancePlan) | **GET** /api/maintenance/plans/{id} | GET maintenance plan by Id |
| [**proposeMaintenancePlan**](MaintenancePlanControllerApi.md#proposeMaintenancePlan) | **POST** /api/maintenance/plans/ai |  |
| [**updateMaintenancePlan**](MaintenancePlanControllerApi.md#updateMaintenancePlan) | **PUT** /api/maintenance/plans/{id} |  |


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

<a name="deleteMaintenancePlan"></a>
# **deleteMaintenancePlan**
> deleteMaintenancePlan(id)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | [default to null] |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="getAllMaintenancePlans"></a>
# **getAllMaintenancePlans**
> MaintenancePlanListResponse getAllMaintenancePlans(active, startDate, endDate, pageSize, pageNumber)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **active** | **Boolean**|  | [optional] [default to null] |
| **startDate** | **Date**|  | [optional] [default to null] |
| **endDate** | **Date**|  | [optional] [default to null] |
| **pageSize** | **Integer**|  | [optional] [default to 20] |
| **pageNumber** | **Integer**|  | [optional] [default to 0] |

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

<a name="proposeMaintenancePlan"></a>
# **proposeMaintenancePlan**
> MaintenancePlan proposeMaintenancePlan(request\_body)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **request\_body** | [**Map**](../Models/string.md)|  | |

### Return type

[**MaintenancePlan**](../Models/MaintenancePlan.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="updateMaintenancePlan"></a>
# **updateMaintenancePlan**
> MaintenancePlan updateMaintenancePlan(id, MaintenancePlan)



### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | [default to null] |
| **MaintenancePlan** | [**MaintenancePlan**](../Models/MaintenancePlan.md)|  | |

### Return type

[**MaintenancePlan**](../Models/MaintenancePlan.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

