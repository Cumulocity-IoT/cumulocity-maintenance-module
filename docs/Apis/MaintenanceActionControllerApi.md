# MaintenanceActionControllerApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createMaintenanceAction**](MaintenanceActionControllerApi.md#createMaintenanceAction) | **POST** /api/maintenance/actions | Create a new maintenance action |


<a name="createMaintenanceAction"></a>
# **createMaintenanceAction**
> MaintenanceAction createMaintenanceAction(MaintenanceAction)

Create a new maintenance action

    Creates a new maintenance action in IoT Platform

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **MaintenanceAction** | [**MaintenanceAction**](../Models/MaintenanceAction.md)|  | |

### Return type

[**MaintenanceAction**](../Models/MaintenanceAction.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

