package cumulocity.microservice.maintenancemodule.controller;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenanceTrigger;

public class MaintenancePlanMapper {

    // Core Fragments
    public static final String MANAGED_OBJECT_TYPE = "c8y_MaintenancePlan";
    public static final String MP_NAME = "mp_Name";
    public static final String MP_DESCRIPTION = "mp_Description";
    public static final String MP_NOTIFICATION_TEXT = "mp_NotificationText";
    public static final String MP_NOTIFICATION_TYPE = "mp_NotificationType";
    public static final String MP_START_DATE = "mp_StartDate";
    public static final String MP_END_DATE = "mp_EndDate";
    public static final String MP_ACTIVE = "mp_Active";

    // Triggers (Used for queries)
    public static final String MP_ON = "mp_On";
    public static final String MP_ON_TIME = "mp_OnTime";
    public static final String MP_ON_USAGE = "mp_OnUsage";
    public static final String MP_ON_CONDITION = "mp_OnCondition";

    // AI Fields
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
        // Ensure type is set even if the source MO didn't have it explicitly
        if (this.managedObject.getType() == null) {
            this.managedObject.setType(MANAGED_OBJECT_TYPE);
        }
    }

    // --- Mapping Logic ---

    public ManagedObjectRepresentation getManagedObject() {
        return this.managedObject;
    }

    public static MaintenancePlanMapper map2(MaintenancePlanCreate create) {
        if (create == null) return null;
        MaintenancePlanMapper mapper = new MaintenancePlanMapper();
        mapper.setName(create.getName());
        mapper.setDescription(create.getDescription());
        mapper.setNotificationText(create.getNotificationText());
        mapper.setNotificationType(create.getNotificationType());
        mapper.setStartDate(create.getStartDate());
        mapper.setEndDate(create.getEndDate());
        mapper.setActive(create.getActive());
        mapper.setTriggers(create.getOn());
        return mapper;
    }

    public static MaintenancePlanMapper map2(MaintenancePlan plan) {
        if (plan == null) return null;

        // Initialize mapper with ID if present
        MaintenancePlanMapper mapper = (plan.getId() != null)
                ? new MaintenancePlanMapper(plan.getId())
                : new MaintenancePlanMapper();

        mapper.setName(plan.getName());
        mapper.setDescription(plan.getDescription());
        mapper.setNotificationText(plan.getNotificationText());
        mapper.setNotificationType(plan.getNotificationType());
        mapper.setStartDate(plan.getStartDate());
        mapper.setEndDate(plan.getEndDate());
        mapper.setActive(plan.getActive());
        mapper.setTriggers(plan.getOn());

        // Map AI Fields
        mapper.setTasks(plan.getTasks());
        mapper.setFrequency(plan.getFrequency());
        mapper.setRequiredSkills(plan.getRequiredSkills());
        mapper.setEquipmentId(plan.getEquipmentId());

        return mapper;
    }

    public static MaintenancePlan map2(ManagedObjectRepresentation mor) {
        if (mor == null) return null;
        MaintenancePlanMapper mapper = new MaintenancePlanMapper(mor);
        MaintenancePlan plan = new MaintenancePlan();

        plan.setId(mapper.getId());
        plan.setName(mapper.getName());
        plan.setDescription(mapper.getDescription());
        plan.setNotificationText(mapper.getNotificationText());
        plan.setNotificationType(mapper.getNotificationType());
        plan.setStartDate(mapper.getStartDate());
        plan.setEndDate(mapper.getEndDate());
        plan.setActive(mapper.getActive());
        plan.setOn(mapper.getTriggers());

        // Map AI Fields Back
        plan.setTasks(mapper.getTasks());
        plan.setFrequency(mapper.getFrequency());
        plan.setRequiredSkills(mapper.getRequiredSkills());
        plan.setEquipmentId(mapper.getEquipmentId());

        return plan;
    }

    // --- GETTERS AND SETTERS ---

    public void setId(String id) {
        if (id != null) managedObject.setId(GId.asGId(id));
    }

    public String getId() {
        return managedObject.getId() != null ? managedObject.getId().getValue() : null;
    }

    public void setName(String name) {
        if (name != null) {
            managedObject.setName(name);
            managedObject.set(name, MP_NAME);
        }
    }
    public String getName() { return managedObject.getName(); }

    public void setDescription(String d) { if (d != null) managedObject.set(d, MP_DESCRIPTION); }
    public String getDescription() { return (String) managedObject.get(MP_DESCRIPTION); }

    public void setNotificationText(String t) { if (t != null) managedObject.set(t, MP_NOTIFICATION_TEXT); }
    public String getNotificationText() { return (String) managedObject.get(MP_NOTIFICATION_TEXT); }

    public void setNotificationType(String t) { if (t != null) managedObject.set(t, MP_NOTIFICATION_TYPE); }
    public String getNotificationType() { return (String) managedObject.get(MP_NOTIFICATION_TYPE); }

    public void setStartDate(DateTime d) { if (d != null) managedObject.set(d, MP_START_DATE); }
    public DateTime getStartDate() { return parseDateTime(managedObject.get(MP_START_DATE)); }

    public void setEndDate(DateTime d) { if (d != null) managedObject.set(d, MP_END_DATE); }
    public DateTime getEndDate() { return parseDateTime(managedObject.get(MP_END_DATE)); }

    public void setActive(Boolean b) { if (b != null) managedObject.set(b, MP_ACTIVE); }
    public Boolean getActive() { return (Boolean) managedObject.get(MP_ACTIVE); }

    public void setTriggers(List<MaintenanceTrigger> t) { if (t != null) managedObject.set(t, MP_ON); }

    @SuppressWarnings("unchecked")
    public List<MaintenanceTrigger> getTriggers() {
        Object o = managedObject.get(MP_ON);
        if (o == null) return Collections.emptyList();

        if (o instanceof List) {
            // Check if list contents need conversion (e.g. from Map to POJO)
            List<?> list = (List<?>) o;
            if (!list.isEmpty() && !(list.get(0) instanceof MaintenanceTrigger)) {
                return parseList(o, MaintenanceTrigger.class);
            }
            return (List<MaintenanceTrigger>) o;
        }
        return parseList(o, MaintenanceTrigger.class);
    }

    // --- AI Field Accessors ---

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

    // --- Helpers ---

    private DateTime parseDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof DateTime) return (DateTime) obj;
        if (obj instanceof String) {
            try {
                return DateTime.parse((String) obj);
            } catch(Exception e) {
                // Log debug if needed, but return null to be safe
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