{
  name: "Usage-based Gn1 Count Maintenance",
  description: "Maintenance plan triggered when Gn1 count measurement reaches threshold of 3 or higher.",
  notificationClass: "ALARM",
  notificationText: "Maintenance required: Gn1 count threshold reached.",
  notificationType: "c8y_MaintenanceRequired",
  notificationSeverity: "MAJOR",
  startDate: "2026-02-10T00:00:00Z",
  endDate: "2027-02-10T00:00:00Z",
  active: true,
  apply: {
    idsInternal: ["335328100"]
  },
  onUsage: {
    counter: {
      subscription: {
        api: "measurements",
        typeFilter: "Gn1"
      },
      valueFragment: "Gn1.count.value",
      thresholdValue: 3
    }
  }
}
