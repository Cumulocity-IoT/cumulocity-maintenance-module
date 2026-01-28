package cumulocity.microservice.maintenancemodule.service.c8y;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;

import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanListResponse;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanProposal;

@Service
public class MaintenancePlanService {

    private static final Logger log = LoggerFactory.getLogger(MaintenancePlanService.class);

    private final ChatClient chatClient;
    private final String maintenanceSchemaJson;
    private final ObjectMapper objectMapper;

    private static final String JSON_SCHEMA = """
        {
          "type": "object",
          "properties": {
            "name": { "type": "string" },
            "description": { "type": "string" },
            "frequency": { "type": "string", "enum": ["DAILY", "WEEKLY", "MONTHLY", "YEARLY"] },
            "tasks": { "type": "array", "items": { "type": "string" } },
            "requiredSkills": { "type": "array", "items": { "type": "string" } }
          },
          "required": ["name", "frequency", "tasks"]
        }
        """;

    private static final String SYSTEM_PROMPT = """
        You are a generic Maintenance Planning Assistant.
        
        RULES:
        1. You must ONLY generate maintenance plans.
        2. If the user prompt is not related to maintenance, equipment, or machinery, return: {"error": "Irrelevant prompt"}.
        3. Output MUST be valid JSON matching this schema:
        %s
        """.formatted(JSON_SCHEMA);

    @Autowired
    public MaintenancePlanService(
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper,
            @Value("classpath:maintenance_schema.json") Resource schemaResource) throws IOException {

        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;

        if (schemaResource.exists()) {
            this.maintenanceSchemaJson = schemaResource.getContentAsString(StandardCharsets.UTF_8);
        } else {
            this.maintenanceSchemaJson = "{}";
            log.warn("maintenance_schema.json not found in classpath");
        }
    }

    /**
     * Generates a maintenance plan proposal.
     * Uses Single-Shot Prompting: The Schema and Guarding rules are sent in one go.
     */
    public MaintenancePlanProposal proposeMaintenancePlan(String userPrompt) {

        //

        // 1. Construct the System Prompt with Guarding, Data Rules & Schema
        String systemInstructions = """
            You are a maintenance planning assistant.
            
            ### PROMPT GUARDING
            First, evaluate if the USER PROMPT is related to maintenance, repairs, technical equipment, or schedules.
            - IF NO: Return exactly: {"error": "Irrelevant prompt"}
            - IF YES: Proceed to generate the plan.
            
            ### DATA HANDLING RULES
            - If the user does NOT provide an Equipment ID, use "UNKNOWN_EQUIPMENT" as the equipmentId.
            - Generate a random UUID for the "planId".
            - "frequency" must be one of: DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY, AS_NEEDED.
            
            ### OUTPUT FORMAT
            You must return a valid JSON object based strictly on this schema:
            %s
            
            Do not include markdown formatting (like ```json ... ```). Return raw JSON only.
            """.formatted(maintenanceSchemaJson);

        // 2. Call the AI
        String aiResponse = chatClient.prompt(new Prompt(
                new SystemMessage(systemInstructions),
                new UserMessage(userPrompt)
        )).call().content();

        // 3. Clean and Parse Response
        String cleanedJson = cleanMarkdown(aiResponse);

        try {
            // Read as generic node first to check for errors
            JsonNode rootNode = objectMapper.readTree(cleanedJson);

            // Check if AI triggered the guard rail
            if (rootNode.has("error")) {
                log.warn("AI rejected prompt: {}", rootNode.get("error").asText());
                throw new IllegalArgumentException("Irrelevant prompt: " + rootNode.get("error").asText());
            }

            // Convert to actual POJO
            return objectMapper.treeToValue(rootNode, MaintenancePlanProposal.class);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw guarding errors
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", cleanedJson, e);
            throw new RuntimeException("AI generated invalid JSON", e);
        }
    }

    private String cleanMarkdown(String input) {
        if (input == null) return "{}";
        return input.replaceAll("(?s)^```json", "")
                .replaceAll("(?s)^```", "")
                .replaceAll("(?s)```$", "")
                .trim();
    }

    // --- Standard CRUD Methods ---

    public MaintenancePlanListResponse getAllMaintenancePlans(Boolean active, DateTime from, DateTime to, Integer size, Integer page, boolean total) {
        MaintenancePlanListResponse response = new MaintenancePlanListResponse();
        response.setPlans(new ArrayList<>());
        return response;
    }

    public MaintenancePlan createMaintenancePlan(MaintenancePlanCreate request) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setName(request.getName());
        return plan;
    }

    public MaintenancePlan getMaintenancePlan(Integer id) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setId(id);
        return plan;
    }

    public MaintenancePlan updateMaintenancePlan(Integer id, MaintenancePlan request) {
        request.setId(id);
        return request;
    }

    public void deleteMaintenancePlan(Integer id) {
        log.info("Deleting plan {}", id);
    }

    // Kept for backward compatibility if needed, but proposeMaintenancePlan handles this internally now.
    public boolean isPromptRelevant(String userPrompt) {
        return true;
    }
}