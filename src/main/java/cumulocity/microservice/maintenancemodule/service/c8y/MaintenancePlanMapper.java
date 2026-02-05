package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.List;

import org.joda.time.DateTime;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.maintenancemodule.model.ConditionBasedTrigger;
import cumulocity.microservice.maintenancemodule.model.DeviceAssignmentCriteria;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.TimeBasedTrigger;
import cumulocity.microservice.maintenancemodule.model.UsageBasedTrigger;

/**
 * Mapper class for converting between MaintenancePlan domain objects and Cumulocity ManagedObjectRepresentation.
 * Provides functionality to map maintenance plan data to and from Cumulocity IoT Platform format.
 * 
 * 
 * @author APES
 * @since 1.0.0
 */
public class MaintenancePlanMapper {
    public static final String MANAGED_OBJECT_TYPE = "c8y_MaintenancePlan";
    public static final String ALARM_TYPE = "mp_MaintenanceAlarm!";
    public static final String ALARM_LAST_MAINTENANCE = "mp_LastMaintenance";
    public static final String ALARM_NEXT_MAINTENANCE = "mp_NextMaintenance";
    public static final String DEVICE_LAST_MAINTENANCE = "mp_LastMaintenance!";
    public static final String DEVICE_NEXT_MAINTENANCE = "mp_NextMaintenance!";
    public static final String MAINTENANCE_PLAN_ID = "mp_MaintenancePlanId";
    public static final String EVENT_TYPE_MAINTENANCE = "mp_MaintenanceActionEvent!";
    public static final String FRAGMENT_STATUS_MAINTENANCE = "ma_Status";
    
    public static final String MP_NAME = "mp_Name";
    public static final String MP_DESCRIPTION = "mp_Description";
    public static final String MP_NOTIFICATION_TEXT = "mp_NotificationText";
    public static final String MP_NOTIFICATION_TYPE = "mp_NotificationType";
    public static final String MP_NOTIFICATION_CLASS = "mp_NotificationClass";
    public static final String MP_NOTIFICATION_SEVERITY = "mp_NotificationSeverity";
    public static final String MP_START_DATE = "mp_StartDate";
    public static final String MP_END_DATE = "mp_EndDate";
    public static final String MP_ACTIVE = "mp_Active";
    public static final String MP_ON = "mp_On";
    public static final String MP_ON_TIME = "mp_OnTime";
    public static final String MP_ON_USAGE = "mp_OnUsage";
    public static final String MP_ON_CONDITIONS = "mp_OnConditions";
    public static final String MP_APPLY = "mp_Apply";
    
    private final ManagedObjectRepresentation managedObject;
    
    /**
     * Creates a MaintenancePlanMapper from a MaintenancePlanCreate object.
     * 
     * @param maintenancePlanCreate the maintenance plan creation data to map
     * @return the mapped MaintenancePlanMapper instance, or null if input is null
     * @since 1.0.0
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
        mapper.setConditionBasedTriggers(maintenancePlanCreate.getOnConditions());
        mapper.setUsageBasedTrigger(maintenancePlanCreate.getOnUsage());
        mapper.setTimeBasedTrigger(maintenancePlanCreate.getOnTime());
        mapper.setNotificationClass(maintenancePlanCreate.getNotificationClass());
        mapper.setNotificationSeverity(maintenancePlanCreate.getNotificationSeverity());
        mapper.setDeviceAssignment(maintenancePlanCreate.getApply());
        return mapper;
    }
    
    /**
     * Creates a MaintenancePlanMapper from a MaintenancePlan object.
     * 
     * @param maintenancePlan the maintenance plan to map
     * @return the mapped MaintenancePlanMapper instance, or null if input is null
     * @since 1.0.0
     */
    public static MaintenancePlanMapper map2(MaintenancePlan maintenancePlan) {
        if (maintenancePlan == null) {
            return null;
        }
        
        MaintenancePlanMapper mapper = new MaintenancePlanMapper();
        mapper.setId(maintenancePlan.getId());
        mapper.setName(maintenancePlan.getName());
        mapper.setDescription(maintenancePlan.getDescription());
        mapper.setNotificationText(maintenancePlan.getNotificationText());
        mapper.setNotificationType(maintenancePlan.getNotificationType());
        mapper.setStartDate(maintenancePlan.getStartDate());
        mapper.setEndDate(maintenancePlan.getEndDate());
        mapper.setActive(maintenancePlan.getActive());
        mapper.setConditionBasedTriggers(maintenancePlan.getOnConditions());
        mapper.setUsageBasedTrigger(maintenancePlan.getOnUsage());
        mapper.setTimeBasedTrigger(maintenancePlan.getOnTime());
        mapper.setNotificationClass(maintenancePlan.getNotificationClass());
        mapper.setNotificationSeverity(maintenancePlan.getNotificationSeverity());
        mapper.setDeviceAssignment(maintenancePlan.getApply());
        return mapper;
    }
    
    public static MaintenancePlan map2(ManagedObjectRepresentation managedObject) {
        if (managedObject == null) {
            return null;
        }
        
        MaintenancePlanMapper mapper = new MaintenancePlanMapper(managedObject);
        MaintenancePlan maintenancePlan = new MaintenancePlan();
        maintenancePlan.setId(mapper.getId());
        maintenancePlan.setName(mapper.getName());
        maintenancePlan.setDescription(mapper.getDescription());
        maintenancePlan.setNotificationText(mapper.getNotificationText());
        maintenancePlan.setNotificationType(mapper.getNotificationType());
        maintenancePlan.setNotificationClass(mapper.getNotificationClass());
        maintenancePlan.setNotificationSeverity(mapper.getNotificationSeverity());
        maintenancePlan.setStartDate(mapper.getStartDate());
        maintenancePlan.setEndDate(mapper.getEndDate());
        maintenancePlan.setActive(mapper.getActive());
        maintenancePlan.setOnConditions(mapper.getConditionBasedTriggers());
        maintenancePlan.setOnUsage(mapper.getUsageBasedTrigger());
        maintenancePlan.setOnTime(mapper.getTimeBasedTrigger());
        maintenancePlan.setApply(mapper.getDeviceAssignment());
        return maintenancePlan;
    }
    
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
        this.managedObject.setType(MANAGED_OBJECT_TYPE);
    }
    
    public void setId(String id) {
        if (id == null) {
            return;
        }
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
        if (name == null) {
            return;
        }
        managedObject.setName(name);
        managedObject.set(name, MP_NAME);
    }
    
    public String getDescription() {
        return (String) managedObject.get(MP_DESCRIPTION);
    }
    
    public void setDescription(String description) {
        if (description == null) {
            return;
        }
        managedObject.set(description, MP_DESCRIPTION);
    }

    public MaintenancePlan.MaintenanceNotificationClass getNotificationClass() {
        Object notificationClass = managedObject.get(MP_NOTIFICATION_CLASS);
        if (notificationClass instanceof String) {
            return MaintenancePlan.MaintenanceNotificationClass.valueOf((String) notificationClass);
        }
        return null;
    } 

    public void setNotificationClass(MaintenancePlan.MaintenanceNotificationClass notificationClass) {
        if (notificationClass == null) {
            return;
        }
        managedObject.set(notificationClass.name(), MP_NOTIFICATION_CLASS);
    }

    public MaintenancePlan.MaintenanceNotificationSeverity getNotificationSeverity() {
        Object notificationSeverity = managedObject.get(MP_NOTIFICATION_SEVERITY);
        if (notificationSeverity instanceof String) {
            return MaintenancePlan.MaintenanceNotificationSeverity.valueOf((String) notificationSeverity);
        }
        return null;
    }

    public void setNotificationSeverity(MaintenancePlan.MaintenanceNotificationSeverity notificationSeverity) {
        if (notificationSeverity == null) {
            return;
        }
        managedObject.set(notificationSeverity.name(), MP_NOTIFICATION_SEVERITY);
    }
    
    /**
     * Retrieves the maintenance notification text from the managed object.
     * 
     * @return the maintenance notification text, or null if not set
     * @since 1.0.0
     */
    public String getNotificationText() {
        return (String) managedObject.get(MP_NOTIFICATION_TEXT);
    }
    
    /**
     * Sets the maintenance notification text in the managed object.
     * 
     * @param notificationText the maintenance notification text to set, null values are ignored
     * @since 1.0.0
     */
    public void setNotificationText(String notificationText) {
        if (notificationText == null) {
            return;
        }
        managedObject.set(notificationText, MP_NOTIFICATION_TEXT);
    }
    
    /**
     * Retrieves the maintenance notification type from the managed object.
     * 
     * @return the maintenance notification type, or null if not set
     * @since 1.0.0
     */
    public String getNotificationType() {
        return (String) managedObject.get(MP_NOTIFICATION_TYPE);
    }
    
    /**
     * Sets the maintenance notification type in the managed object.
     * 
     * @param notificationType the maintenance notification type to set, null values are ignored
     * @since 1.0.0
     */
    public void setNotificationType(String notificationType) {
        if (notificationType == null) {
            return;
        }
        managedObject.set(notificationType, MP_NOTIFICATION_TYPE);
    }
    
    public DateTime getStartDate() {
        Object startDate = managedObject.get(MP_START_DATE);
        if (startDate instanceof DateTime) {
            return (DateTime) startDate;
        }
        return parseDateTime(startDate);
    }
    
    public void setStartDate(DateTime startDate) {
        if (startDate == null) {
            return;
        }
        managedObject.set(startDate, MP_START_DATE);
    }
    
    public DateTime getEndDate() {
        Object endDate = managedObject.get(MP_END_DATE);
        if (endDate instanceof DateTime) {
            return (DateTime) endDate;
        }
        return parseDateTime(endDate);
    }
    
    public void setEndDate(DateTime endDate) {
        if (endDate == null) {
            return;
        }
        managedObject.set(endDate, MP_END_DATE);
    }
    
    public Boolean getActive() {
        return (Boolean) managedObject.get(MP_ACTIVE);
    }
    
    public void setActive(Boolean active) {
        if (active == null) {
            return;
        }
        managedObject.set(active, MP_ACTIVE);
    }

    public DeviceAssignmentCriteria getDeviceAssignment() {
        Object deviceAssignment = managedObject.get(MP_APPLY);
        if (deviceAssignment instanceof DeviceAssignmentCriteria) {
            return (DeviceAssignmentCriteria) deviceAssignment;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(deviceAssignment, DeviceAssignmentCriteria.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setDeviceAssignment(DeviceAssignmentCriteria deviceAssignment) {
        if (deviceAssignment == null) {
            return;
        }
        managedObject.set(deviceAssignment, MP_APPLY);
    }
    
    public List<ConditionBasedTrigger> getConditionBasedTriggers() {
        Object onConditions = managedObject.get(MP_ON_CONDITIONS);
        if (onConditions instanceof List) {
            return parseConditions(onConditions);
        }
        return new java.util.ArrayList<>();
    }

    public void setConditionBasedTriggers(List<ConditionBasedTrigger> conditionBasedTriggers) {
        if (conditionBasedTriggers == null) {
            return;
        }
        managedObject.set(conditionBasedTriggers, MP_ON_CONDITIONS);
    }


    public UsageBasedTrigger getUsageBasedTrigger() {
        Object onUsage = managedObject.get(MP_ON_USAGE);
        if (onUsage instanceof UsageBasedTrigger) {
            return (UsageBasedTrigger) onUsage;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(onUsage, UsageBasedTrigger.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setUsageBasedTrigger(UsageBasedTrigger usageBasedTrigger) {
        if (usageBasedTrigger == null) {
            return;
        }
        managedObject.set(usageBasedTrigger, MP_ON_USAGE);
    }
    
    public ManagedObjectRepresentation getManagedObject() {
        return managedObject;
    }

    public TimeBasedTrigger getTimeBasedTrigger() {
        Object onTime = managedObject.get(MP_ON_TIME);
        if (onTime instanceof TimeBasedTrigger) {
            return (TimeBasedTrigger) onTime;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(onTime, TimeBasedTrigger.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setTimeBasedTrigger(TimeBasedTrigger timeBasedTrigger) {
        if (timeBasedTrigger == null) {
            return;
        }
        managedObject.set(timeBasedTrigger, MP_ON_TIME);
    }
    
    private DateTime parseDateTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            try {
                return DateTime.parse((String) obj);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    private List<ConditionBasedTrigger> parseConditions(Object obj) {
        if (obj == null) {
            return new java.util.ArrayList<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(obj, mapper.getTypeFactory().constructCollectionType(List.class, ConditionBasedTrigger.class));
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
}
