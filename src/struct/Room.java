package struct;

public class Room {
	//private int id;
	private int size;
	private int cost;
	//TODO: make comparable according to size &/or cost?
	
	public Room(int size, int cost) {
		this.size = size;
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Room [size=" + size + ", cost=" + cost + "]";
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}
