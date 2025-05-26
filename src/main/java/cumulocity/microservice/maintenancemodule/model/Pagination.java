package cumulocity.microservice.maintenancemodule.model;

/**
 * Pagination information for list responses
 * 
 * @author APES
 */
public class Pagination {
    private Integer total;
    private Integer limit;
    private Integer offset;
    private Boolean hasNext;
    private Boolean hasPrevious;

    // Default constructor
    public Pagination() {}

    // Constructor
    public Pagination(Integer total, Integer limit, Integer offset, Boolean hasNext, Boolean hasPrevious) {
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "total=" + total +
                ", limit=" + limit +
                ", offset=" + offset +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
}
