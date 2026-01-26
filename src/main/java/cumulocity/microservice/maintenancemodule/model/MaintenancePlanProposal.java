package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

/**
 * Root object for the AI proposal.
 * Matches the updated JSON schema structure.
 */
public record MaintenancePlanProposal(
        String planId,           // Matches "planId"
        String equipmentId,      // Matches "equipmentId"
        String description,      // Matches "description"
        String frequency,        // Matches "frequency" (Enum values as String)
        List<ProposedTask> tasks, // Matches "tasks" (Array of Objects)
        List<String> requiredSkills // Matches "requiredSkills" (Array of Strings)
) {}

/**
 * Nested record for individual tasks defined in the schema.
 */
record ProposedTask(
        String taskName,
        String instructions,
        Double estimatedDurationHours
) {}