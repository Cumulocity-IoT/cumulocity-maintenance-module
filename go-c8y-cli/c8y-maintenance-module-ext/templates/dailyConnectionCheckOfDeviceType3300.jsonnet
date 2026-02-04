{
  name: "Daily Device 3300 Check",
  description: "Daily check for connection and device log files of connection errors",
  notificationClass: "ALARM",
  notificationSeverity: "MINOR",
  notificationText: "Maintenance is due: Please perform maintenance check for device type 3300.",
  notificationType: "c8y_DailyMaintenanceDueAlarm",
  startDate: "2026-01-01T00:00:00Z",
  endDate: "2026-12-31T23:59:59Z",
  active: true,
  onTime: {
      cronExpression: "0 0 10 * * *"
  },
  apply: {
    types: ["3300"]
  }
}
