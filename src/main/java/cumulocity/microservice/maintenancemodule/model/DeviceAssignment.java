package cumulocity.microservice.maintenancemodule.model;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Device assignment filter criteria for maintenance plans.
 * Each filter criteria is optional, however at least one must be provided.
 * If more than one filter criteria is provided, the criterias are combined with OR logic.
 * This means each filter criteria will be evaluated independently and devices matching 
 * any of the criteria will be included in the result set.
 * 
 * @author APES
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@Schema(description = "Device assignment filter criteria. Each filter criteria is optional, however at least one must be provided. If more than one filter criteria is provided, the criterias are combined with OR logic.")
@Validated
public class DeviceAssignment {

    @Schema(description = "List of device types to filter by (e.g., c8y_Device, c8y_Sensor)", example = "[\"c8y_Device\", \"c8y_Sensor\"]")
    private List<String> types;

    @Schema(description = "List of internal device IDs to filter by", example = "[\"12345\", \"67890\"]")
    private List<String> idsInternal;

    @Schema(description = "List of serial numbers to filter by", example = "[\"SN123456\", \"356789012345678\"]")
    private List<String> idsSerial;

    @Schema(description = "Cumulocity Query Language (CQL) expression for complex filtering", example = "ec_Service.ec_WindFarmId eq 100117")
    private String query;
}
