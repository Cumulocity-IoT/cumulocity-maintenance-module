# Brainstorming Ideas for Device Assignment

The MaintenancePlan should include a fragment `devices` that contains a list of devices. Each device should have the following properties:

```json
"devices": [
  {
    "id": "12345",
    "name": "Device A",
    "self": "https://cumulocity.de/inventory/managedObjects/12345"
  },
  {
    "id": "67890",
    "name": "Device B",
    "self": "https://cumulocity.de/inventory/managedObjects/67890"
  }
  ]
```

```json
"devices": [
  {
    "managedObject": {
      "id": "54321",
      "name": "Device C",
      "self": "https://cumulocity.de/inventory/managedObjects/54321"
    }
  },
  {
    "managedObject": {
      "id": "09876",
      "name": "Device D",
      "self": "https://cumulocity.de/inventory/managedObjects/09876"
    }
  }
]
```

The `devices` fragment can be used to assign devices to a maintenance plan. The structure allows for flexibility in how devices are represented, either as a simple list of device identifiers or as objects containing more detailed information about each device.

The device assignment can be done through a PUT request to the maintenance plan endpoint, where the `devices` fragment is included in the request body. This allows for updating the list of devices associated with a specific maintenance plan.