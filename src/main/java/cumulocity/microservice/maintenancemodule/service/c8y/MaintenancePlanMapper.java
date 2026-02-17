package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.joda.time.DateTime;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.maintenancemodule.model.ConditionBasedTrigger;
import cumulocity.microservice.maintenancemodule.model.DeviceAssignmentCriteria;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenanceTrigger;
import cumulocity.microservice.maintenancemodule.model.TimeBasedTrigger;
import cumulocity.microservice.maintenancemodule.model.UsageBasedTrigger;

/**
 * Mapper class for converting between MaintenancePlan domain objects and Cumulocity ManagedObjectRepresentation.
 * Provides functionality to map maintenance plan data to and from Cumulocity IoT Platform format.
 * * Combines logic for standard triggers, device assignments, and AI-enhanced fields.
 * * @author APES
 * @since 1.0.0
 */
public class MaintenancePlanMapper {

    // --- Constants ---

    // Core Identity
    public static final String MANAGED_OBJECT_TYPE = "c8y_MaintenancePlan";
    public static final String MAINTENANCE_PLAN_ID = "mp_MaintenancePlanId";

    // Basic Fields
    public static final String MP_NAME = "mp_Name";
    public static final String MP_DESCRIPTION = "mp_Description";
    public static final String MP_ACTIVE = "mp_Active";
    public static final String MP_START_DATE = "mp_StartDate";
    public static final String MP_END_DATE = "mp_EndDate";

    // Notification Fields
    public static final String MP_NOTIFICATION_TEXT = "mp_NotificationText";
    public static final String MP_NOTIFICATION_TYPE = "mp_NotificationType";
    public static final String MP_NOTIFICATION_CLASS = "mp_NotificationClass";
    public static final String MP_NOTIFICATION_SEVERITY = "mp_NotificationSeverity";

    // Trigger Fields (Specific)
    public static final String MP_ON_TIME = "mp_OnTime";
    public static final String MP_ON_USAGE = "mp_OnUsage";
    public static final String MP_ON_CONDITIONS = "mp_OnConditions"; // aka mp_OnCondition
    public static final String MP_ON = "mp_On"; // Generic/List wrapper

    // Target Assignment
    public static final String MP_APPLY = "mp_Apply";

    // Alarm & Event Constants
    public static final String ALARM_TYPE = "mp_MaintenanceAlarm!";
    public static final String ALARM_LAST_MAINTENANCE = "mp_LastMaintenance";
    public static final String ALARM_NEXT_MAINTENANCE = "mp_NextMaintenance";
    public static final String DEVICE_LAST_MAINTENANCE = "mp_LastMaintenance!";
    public static final String DEVICE_NEXT_MAINTENANCE = "mp_NextMaintenance!";
    public static final String EVENT_TYPE_MAINTENANCE = "mp_MaintenanceActionEvent!";
    public static final String FRAGMENT_STATUS_MAINTENANCE = "ma_Status";

    // AI / Enhanced Fields
    public static final String MP_TASKS = "mp_Tasks";
    public static final String MP_FREQUENCY = "mp_Frequency";
    public static final String MP_REQUIRED_SKILLS = "mp_RequiredSkills";
    public static final String MP_EQUIPMENT_ID = "mp_EquipmentId";

    private final ManagedObjectRepresentation managedObject;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // --- Constructors ---

    public MaintenancePlanMapper() {
        managedObject = new ManagedObjectRepresentation();
        managedObject.setType(MANAGED_OBJECT_TYPE);
    }

    public MaintenancePlanMapper(String id) {
        managedObject = new ManagedObjectRepresentation();
        managedObject.setId(GId.asGId(id));
        managedObject.setType(MANAGED_OBJECT_TYPE);
    }

    public MaintenancePlanMapper(ManagedObjectRepresentation managedObject) {
        this.managedObject = managedObject;
        // Ensure type is set
        if (this.managedObject.getType() == null) {
            this.managedObject.setType(MANAGED_OBJECT_TYPE);
        }
    }

    public ManagedObjectRepresentation getManagedObject() {
        return managedObject;
    }

    // --- Static Mapping Methods ---

    /**
     * Creates a MaintenancePlanMapper from a MaintenancePlanCreate object.
     */
    public static MaintenancePlanMapper map2(MaintenancePlanCreate maintenancePlanCreate) {
        if (maintenancePlanCreate == null) {
            return null;
        }

        MaintenancePlanMapper mapper = new MaintenancePlanMapper();
        mapper.setName(maintenancePlanCreate.getName());
        mapper.setDescription(maintenancePlanCreate.getDescription());
        mapper.setNotificationText(maintenancePlanCreate.getNotificationText());
        mapper.setNotificationType(maintenancePlanCreate.getNotificationType());
        mapper.setStartDate(maintenancePlanCreate.getStartDate());
        mapper.setEndDate(maintenancePlanCreate.getEndDate());
        mapper.setActive(maintenancePlanCreate.getActive());

        // Map Notification Details
        mapper.setNotificationClass(maintenancePlanCreate.getNotificationClass());
        mapper.setNotificationSeverity(maintenancePlanCreate.getNotificationSeverity());

        // Map Specific Triggers (Legacy/Standard)
        mapper.setConditionBasedTriggers(maintenancePlanCreate.getOnConditions());
        mapper.setUsageBasedTrigger(maintenancePlanCreate.getOnUsage());
        mapper.setTimeBasedTrigger(maintenancePlanCreate.getOnTime());

        // Map Generic/AI Triggers
        // Note: Assuming MaintenancePlanCreate has .getOn() based on file 2
        // If not available in your model, you can comment this line out.
        try {
            // reflection or standard getter if exists in your version of MaintenancePlanCreate
            // mapper.setTriggers(maintenancePlanCreate.getOn());
        } catch (Exception e) { /* ignore if method doesn't exist */ }

        // Map Device Assignment
        mapper.setDeviceAssignment(maintenancePlanCreate.getApply());

        return mapper;
    }

    /**
     * Creates a MaintenancePlanMapper from a MaintenancePlan object.
     */
    public static MaintenancePlanMapper map2(MaintenancePlan maintenancePlan) {
        if (maintenancePlan == null) {
            return null;
        }

        MaintenancePlanMapper mapper = (maintenancePlan.getId() != null)
                ? new MaintenancePlanMapper(maintenancePlan.getId())
                : new MaintenancePlanMapper();

        // Basic Info
        mapper.setName(maintenancePlan.getName());
        mapper.setDescription(maintenancePlan.getDescription());
        mapper.setNotificationText(maintenancePlan.getNotificationText());
        mapper.setNotificationType(maintenancePlan.getNotificationType());
        mapper.setStartDate(maintenancePlan.getStartDate());
        mapper.setEndDate(maintenancePlan.getEndDate());
        mapper.setActive(maintenancePlan.getActive());

        // Notifications
        mapper.setNotificationClass(maintenancePlan.getNotificationClass());
        mapper.setNotificationSeverity(maintenancePlan.getNotificationSeverity());

        // Specific Triggers
        mapper.setConditionBasedTriggers(maintenancePlan.getOnConditions());
        mapper.setUsageBasedTrigger(maintenancePlan.getOnUsage());
        mapper.setTimeBasedTrigger(maintenancePlan.getOnTime());

        // Generic Triggers (File 2)
        mapper.setTriggers(maintenancePlan.getOn());

        // Device Assignment
        mapper.setDeviceAssignment(maintenancePlan.getApply());

        // AI Fields (File 2)
        mapper.setTasks(maintenancePlan.getTasks());
        mapper.setFrequency(maintenancePlan.getFrequency());
        mapper.setRequiredSkills(maintenancePlan.getRequiredSkills());
        mapper.setEquipmentId(maintenancePlan.getEquipmentId());

        return mapper;
    }

    /**
     * Creates a MaintenancePlan domain object from a ManagedObjectRepresentation.
     */
    public static MaintenancePlan map2(ManagedObjectRepresentation managedObject) {
        if (managedObject == null) {
            return null;
        }

        MaintenancePlanMapper mapper = new MaintenancePlanMapper(managedObject);
        MaintenancePlan maintenancePlan = new MaintenancePlan();

        // Basic Info
        maintenancePlan.setId(mapper.getId());
        maintenancePlan.setName(mapper.getName());
        maintenancePlan.setDescription(mapper.getDescription());
        maintenancePlan.setNotificationText(mapper.getNotificationText());
        maintenancePlan.setNotificationType(mapper.getNotificationType());
        maintenancePlan.setStartDate(mapper.getStartDate());
        maintenancePlan.setEndDate(mapper.getEndDate());
        maintenancePlan.setActive(mapper.getActive());

        // Notifications
        maintenancePlan.setNotificationClass(mapper.getNotificationClass());
        maintenancePlan.setNotificationSeverity(mapper.getNotificationSeverity());

        // Specific Triggers
        maintenancePlan.setOnConditions(mapper.getConditionBasedTriggers());
        maintenancePlan.setOnUsage(mapper.getUsageBasedTrigger());
        maintenancePlan.setOnTime(mapper.getTimeBasedTrigger());

        // Generic Triggers
        maintenancePlan.setOn(mapper.getTriggers());

        // Device Assignment
        maintenancePlan.setApply(mapper.getDeviceAssignment());

        // AI Fields
        maintenancePlan.setTasks(mapper.getTasks());
        maintenancePlan.setFrequency(mapper.getFrequency());
        maintenancePlan.setRequiredSkills(mapper.getRequiredSkills());
        maintenancePlan.setEquipmentId(mapper.getEquipmentId());

        return maintenancePlan;
    }

    // --- Getters and Setters ---

    public void setId(String id) {
        if (id == null) return;
        managedObject.setId(GId.asGId(id));
    }

    public String getId() {
        if (managedObject.getId() != null) {
            return managedObject.getId().getValue();
        }
        return null;
    }

    public String getName() {
        return managedObject.getName();
    }

    public void setName(String name) {
        if (name == null) return;
        managedObject.setName(name);
        managedObject.set(name, MP_NAME);
    }

    public String getDescription() {
        return (String) managedObject.get(MP_DESCRIPTION);
    }

    public void setDescription(String description) {
        if (description == null) return;
        managedObject.set(description, MP_DESCRIPTION);
    }

    public String getNotificationText() {
        return (String) managedObject.get(MP_NOTIFICATION_TEXT);
    }

    public void setNotificationText(String notificationText) {
        if (notificationText == null) return;
        managedObject.set(notificationText, MP_NOTIFICATION_TEXT);
    }

    public String getNotificationType() {
        return (String) managedObject.get(MP_NOTIFICATION_TYPE);
    }

    public void setNotificationType(String notificationType) {
        if (notificationType == null) return;
        managedObject.set(notificationType, MP_NOTIFICATION_TYPE);
    }

    public MaintenancePlan.MaintenanceNotificationClass getNotificationClass() {
        Object notificationClass = managedObject.get(MP_NOTIFICATION_CLASS);
        if (notificationClass instanceof String) {
            try {
                return MaintenancePlan.MaintenanceNotificationClass.valueOf((String) notificationClass);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public void setNotificationClass(MaintenancePlan.MaintenanceNotificationClass notificationClass) {
        if (notificationClass == null) return;
        managedObject.set(notificationClass.name(), MP_NOTIFICATION_CLASS);
    }

    public MaintenancePlan.MaintenanceNotificationSeverity getNotificationSeverity() {
        Object notificationSeverity = managedObject.get(MP_NOTIFICATION_SEVERITY);
        if (notificationSeverity instanceof String) {
            try {
                return MaintenancePlan.MaintenanceNotificationSeverity.valueOf((String) notificationSeverity);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public void setNotificationSeverity(MaintenancePlan.MaintenanceNotificationSeverity notificationSeverity) {
        if (notificationSeverity == null) return;
        managedObject.set(notificationSeverity.name(), MP_NOTIFICATION_SEVERITY);
    }

    public DateTime getStartDate() {
        return parseDateTime(managedObject.get(MP_START_DATE));
    }

    public void setStartDate(DateTime startDate) {
        if (startDate == null) return;
        managedObject.set(startDate, MP_START_DATE);
    }

    public DateTime getEndDate() {
        return parseDateTime(managedObject.get(MP_END_DATE));
    }

    public void setEndDate(DateTime endDate) {
        if (endDate == null) return;
        managedObject.set(endDate, MP_END_DATE);
    }

    public Boolean getActive() {
        return (Boolean) managedObject.get(MP_ACTIVE);
    }

    public void setActive(Boolean active) {
        if (active == null) return;
        managedObject.set(active, MP_ACTIVE);
    }

    // --- Specific Trigger Accessors ---

    public List<ConditionBasedTrigger> getConditionBasedTriggers() {
        Object onConditions = managedObject.get(MP_ON_CONDITIONS);
        if (onConditions instanceof List) {
            return parseList(onConditions, ConditionBasedTrigger.class);
        }
        return new ArrayList<>();
    }

    public void setConditionBasedTriggers(List<ConditionBasedTrigger> conditionBasedTriggers) {
        if (conditionBasedTriggers == null) return;
        managedObject.set(conditionBasedTriggers, MP_ON_CONDITIONS);
    }

    public UsageBasedTrigger getUsageBasedTrigger() {
        Object onUsage = managedObject.get(MP_ON_USAGE);
        if (onUsage instanceof UsageBasedTrigger) {
            return (UsageBasedTrigger) onUsage;
        }
        try {
            return objectMapper.convertValue(onUsage, UsageBasedTrigger.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setUsageBasedTrigger(UsageBasedTrigger usageBasedTrigger) {
        if (usageBasedTrigger == null) return;
        managedObject.set(usageBasedTrigger, MP_ON_USAGE);
    }

    public TimeBasedTrigger getTimeBasedTrigger() {
        Object onTime = managedObject.get(MP_ON_TIME);
        if (onTime instanceof TimeBasedTrigger) {
            return (TimeBasedTrigger) onTime;
        }
        try {
            return objectMapper.convertValue(onTime, TimeBasedTrigger.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setTimeBasedTrigger(TimeBasedTrigger timeBasedTrigger) {
        if (timeBasedTrigger == null) return;
        managedObject.set(timeBasedTrigger, MP_ON_TIME);
    }

    // --- Device Assignment Accessors ---

    public DeviceAssignmentCriteria getDeviceAssignment() {
        Object deviceAssignment = managedObject.get(MP_APPLY);
        if (deviceAssignment instanceof DeviceAssignmentCriteria) {
            return (DeviceAssignmentCriteria) deviceAssignment;
        }
        try {
            return objectMapper.convertValue(deviceAssignment, DeviceAssignmentCriteria.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setDeviceAssignment(DeviceAssignmentCriteria deviceAssignment) {
        if (deviceAssignment == null) return;
        managedObject.set(deviceAssignment, MP_APPLY);
    }

    // --- Generic / AI Field Accessors ---

    public void setTriggers(List<MaintenanceTrigger> t) {
        if (t != null) managedObject.set(t, MP_ON);
    }

    @SuppressWarnings("unchecked")
    public List<MaintenanceTrigger> getTriggers() {
        Object o = managedObject.get(MP_ON);
        if (o == null) return Collections.emptyList();
        if (o instanceof List) {
            List<?> list = (List<?>) o;
            if (!list.isEmpty() && !(list.get(0) instanceof MaintenanceTrigger)) {
                return parseList(o, MaintenanceTrigger.class);
            }
            return (List<MaintenanceTrigger>) o;
        }
        return parseList(o, MaintenanceTrigger.class);
    }

    public void setFrequency(String f) { if (f != null) managedObject.set(f, MP_FREQUENCY); }
    public String getFrequency() { return (String) managedObject.get(MP_FREQUENCY); }

    public void setEquipmentId(String e) { if (e != null) managedObject.set(e, MP_EQUIPMENT_ID); }
    public String getEquipmentId() { return (String) managedObject.get(MP_EQUIPMENT_ID); }

    public void setRequiredSkills(List<String> s) { if (s != null) managedObject.set(s, MP_REQUIRED_SKILLS); }

    @SuppressWarnings("unchecked")
    public List<String> getRequiredSkills() {
        return (List<String>) managedObject.get(MP_REQUIRED_SKILLS);
    }

    public void setTasks(List<?> t) { if (t != null) managedObject.set(t, MP_TASKS); }

    @SuppressWarnings("unchecked")
    public List<Object> getTasks() {
        return (List<Object>) managedObject.get(MP_TASKS);
    }

    // --- Helper Methods ---

    private DateTime parseDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof DateTime) return (DateTime) obj;
        if (obj instanceof String) {
            try {
                return DateTime.parse((String) obj);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private <T> List<T> parseList(Object obj, Class<T> clazz) {
        if (obj == null) return Collections.emptyList();
        try {
            return objectMapper.convertValue(obj,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch(Exception e) {
            return Collections.emptyList();
        }
    }
}