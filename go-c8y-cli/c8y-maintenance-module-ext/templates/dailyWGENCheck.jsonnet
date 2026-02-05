{
  name: "Daily Wind Generator Check",
  description: "Daily check WGEN device",
  notificationClass: "ALARM",
  notificationSeverity: "MINOR",
  notificationText: "Maintenance is due: Please perform wind generator check.",
  notificationType: "c8y_DailyWGENCheck",
  startDate: "2026-01-01T00:00:00Z",
  endDate: "2026-12-31T23:59:59Z",
  active: true,
  onTime: {
      cronExpression: "0 30 9 * * *"
  },
  apply: {
    query: "$filter=name eq 'WGEN*'"
  }
}
