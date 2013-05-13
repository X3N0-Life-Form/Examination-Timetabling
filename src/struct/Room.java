package struct;

import java.io.Serializable;

public class Room implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
	
	public Room(int id, int size, int cost) {
		this.id = id;
		this.size = size;
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", size=" + size + ", cost=" + cost + "]";
	}

	

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}
