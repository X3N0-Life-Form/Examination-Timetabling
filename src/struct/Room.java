package struct;

public class Room {
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private int size;
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	private int cost;
	//TODO: make comparable according to size &/or cost?
	
	public Room(int id, int size, int cost) {
		this.id = id;
		this.size = size;
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Room [size=" + size + ", cost=" + cost + "]";
	}

	

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}
