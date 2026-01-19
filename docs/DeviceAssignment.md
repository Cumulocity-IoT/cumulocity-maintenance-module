# Brainstorming Ideas for Device Assignment

The MaintenancePlan should include a fragment `apply` that contains the filter criteria for devices. Each filter criteria is optional, however at least one must be provided. If more than one filter criteria is provided, the criterias are combined with OR logic. This means each filter criteria will be evaluated independently and devices matching any of the criteria will be included in the result set. If the device is in the result set of more than one filter criteria, the result set contains only one instance of the device. The filter criteria `query` allows for Cumulocity Query Language (CQL) expressions to be used for more complex filtering.

```json
"apply": {
  "types": ["c8y_Device", "c8y_Sensor"],
  "idsInternal": [ "12345", "67890" ],
  "idsSerial": ["SN123456", "356789012345678"],
  "query": "ec_Service.ec_WindFarmId eq 100117"
}
```

The above example will include all devices of type `c8y_Device` or `c8y_Sensor`, as well as devices with internal IDs `12345` or `67890`, devices with serial numbers `SN123456` or `356789012345678`, and devices that belong to the wind farm with ID `100117`. Any device that matches at least one of these criteria will be included in the final result set for the MaintenancePlan.