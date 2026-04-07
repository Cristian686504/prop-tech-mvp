package co.com.proptech.model.common;

public class PageRequest {
    private final int page;
    private final int size;
    private final String sortBy;
    private final SortDirection direction;

    public PageRequest(int page, int size, String sortBy, SortDirection direction) {
        this.page = Math.max(0, page);
        this.size = Math.max(1, Math.min(size, 1000)); // Max 1000 items per page
        this.sortBy = sortBy;
        this.direction = direction != null ? direction : SortDirection.DESC;
    }

    public PageRequest(int page, int size) {
        this(page, size, "createdAt", SortDirection.DESC);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public enum SortDirection {
        ASC, DESC
    }
}
