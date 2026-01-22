package cumulocity.microservice.maintenancemodule.service.c8y;

import com.cumulocity.sdk.client.Filter;
import com.cumulocity.sdk.client.Param;
import com.cumulocity.sdk.client.QueryParam;

public enum CustomQueryParam implements Param {
	WITH_TOTAL_PAGES("withTotalPages"),
	PAGE_SIZE("pageSize"),
	QUERY("query"),
	DEVICE_QUERY("q"),
	DATE_FROM("dateFrom"),
	STATUS("status"),
	FRAGMENT_TYPE("fragmentType"),
	DEVICE_ID("deviceId"),
	REVERT("revert"),
	;

	private String name;
	
	private String value;
	
	public String getValue() {
		return value;
	}

	private CustomQueryParam(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public CustomQueryParam setValue(final String value) {
		this.value = value;
		return this;
	}

	public QueryParam toQueryParam() {
		return new QueryParam(this, Filter.encode(value));
	}

}  
