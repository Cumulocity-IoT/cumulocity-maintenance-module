{
  name: "Monthly Connection Check - Tuesday 8 PM",
  description: "Monthly maintenance check for connection and device log files of connection errors",
  notificationText: "Scheduled maintenance: Check device connection status and review connection error logs",
  notificationType: "alarm",
  startDate: "2026-01-01T00:00:00Z",
  endDate: "2026-12-31T23:59:59Z",
  active: true,
  onTime: {
      cronExpression: "0 0 20 * * 2"
  },
  apply: {
    idsInternal: ["7606"]
  }
}
