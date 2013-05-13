package struct;

import java.io.Serializable;

public class Room implements Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cost;
		result = prime * result + id;
		result = prime * result + size;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (cost != other.cost)
			return false;
		if (id != other.id)
			return false;
		if (size != other.size)
			return false;
		return true;
	}
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
