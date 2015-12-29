package mmb.cargo.model;

public class ReturnedProductDirectCatalog {
    private Integer id;

    private Integer catalogId;

    private String catalogLevel;

    private Integer directId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogLevel() {
        return catalogLevel;
    }

    public void setCatalogLevel(String catalogLevel) {
        this.catalogLevel = catalogLevel;
    }

    public Integer getDirectId() {
        return directId;
    }

    public void setDirectId(Integer directId) {
        this.directId = directId;
    }
}