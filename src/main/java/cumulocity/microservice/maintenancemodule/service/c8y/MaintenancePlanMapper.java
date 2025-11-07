package cumulocity.microservice.maintenancemodule.service.c8y;

import java.util.List;

import org.joda.time.DateTime;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import cumulocity.microservice.maintenancemodule.model.MaintenancePlan;
import cumulocity.microservice.maintenancemodule.model.MaintenancePlanCreate;
import cumulocity.microservice.maintenancemodule.model.MaintenanceTrigger;

/**
 * Mapper class for converting between MaintenancePlan domain objects and Cumulocity ManagedObjectRepresentation.
 * Provides functionality to map maintenance plan data to and from Cumulocity IoT Platform format.
 * 
 * @author APES
 * @since 1.0.0
 */
public class MaintenancePlanMapper {
    public static final String MANAGED_OBJECT_TYPE = "c8y_MaintenancePlan";
    
    public static final String MP_NAME = "mp_Name";
    public static final String MP_DESCRIPTION = "mp_Description";
    public static final String MP_NOTIFICATION_TEXT = "mp_NotificationText";
    public static final String MP_NOTIFICATION_TYPE = "mp_NotificationType";
    public static final String MP_START_DATE = "mp_StartDate";
    public static final String MP_END_DATE = "mp_EndDate";
    public static final String MP_ACTIVE = "mp_Active";
    public static final String MP_ON = "mp_On";
    
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
        mapper.setTriggers(maintenancePlanCreate.getOn());
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
        mapper.setTriggers(maintenancePlan.getOn());
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
        maintenancePlan.setStartDate(mapper.getStartDate());
        maintenancePlan.setEndDate(mapper.getEndDate());
        maintenancePlan.setActive(mapper.getActive());
        maintenancePlan.setOn(mapper.getTriggers());
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
    
    public void setId(Integer id) {
        if (id == null) {
            return;
        }
        managedObject.setId(GId.asGId(id));
    }

    public Integer getId() {
        if (managedObject.getId() != null) {
            return Integer.valueOf(managedObject.getId().getValue());
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
    
    @SuppressWarnings("unchecked")
    public List<MaintenanceTrigger> getTriggers() {
        Object triggers = managedObject.get(MP_ON);
        if (triggers instanceof List) {
            return (List<MaintenanceTrigger>) triggers;
        }
        return parseTriggers(triggers);
    }
    
    public void setTriggers(List<MaintenanceTrigger> triggers) {
        if (triggers == null) {
            return;
        }
        managedObject.set(triggers, MP_ON);
    }
    
    public ManagedObjectRepresentation getManagedObject() {
        return managedObject;
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
    
    private List<MaintenanceTrigger> parseTriggers(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(obj, mapper.getTypeFactory().constructCollectionType(List.class, MaintenanceTrigger.class));
        } catch (Exception e) {
            return null;
        }
    }
}
