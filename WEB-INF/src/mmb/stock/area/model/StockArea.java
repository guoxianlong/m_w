package mmb.stock.area.model;

public class StockArea {
    private Integer id;

    private String name;

    private Integer type;

    private Integer attribute;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getAttribute() {
		return attribute;
	}

	public void setAttribute(Integer attribute) {
		this.attribute = attribute;
	}

	public void setName(String name) {
		this.name = name;
	}

}