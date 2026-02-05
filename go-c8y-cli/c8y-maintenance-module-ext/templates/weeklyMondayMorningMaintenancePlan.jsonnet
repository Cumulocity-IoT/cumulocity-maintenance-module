{
  name: "Weekly Monday Morning Maintenance",
  description: "Weekly maintenance for device 864216.",
  notificationText: "Maintenance Task: Check internet connection, status LEDs, and device box for damages.",
  notificationType: "alarm",
  startDate: "2026-02-02T07:00:00Z",
  endDate: "2027-02-02T07:00:00Z",
  active: true,
  onTime: {
      cronExpression: "0 0 7 * * 1"
  },
  apply: {
    idsInternal: ["864216"]
  }
}
