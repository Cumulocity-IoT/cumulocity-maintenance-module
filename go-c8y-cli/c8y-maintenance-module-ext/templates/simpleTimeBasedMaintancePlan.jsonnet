{
  name: "Maintenance Task Test 1",
  description: "This is a simple maintenance task.",
  notificationText: "Maintenance Task: This is a simple maintenance task that requires periodic attention.",
  notificationType: "alarm",
  startDate: "2025-10-01T00:00:00Z",
  endDate: "2026-10-02T00:00:00Z",
  active: true,
  onTime: {
      cronExpression: "0 0 0 */30 * *"
  },
  apply: {
    idsInternal: ["7606", "383902"]
  }
}