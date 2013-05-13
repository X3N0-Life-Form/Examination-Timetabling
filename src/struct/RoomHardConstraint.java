package struct;

import java.io.Serializable;

public class RoomHardConstraint implements Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constraint == null) ? 0 : constraint.hashCode());
		result = prime * result + id;
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
		RoomHardConstraint other = (RoomHardConstraint) obj;
		if (constraint != other.constraint)
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private ERoomHardConstraint constraint;
	
	
	public RoomHardConstraint(int id, ERoomHardConstraint constraint) {
		super();
		this.id = id;
		this.constraint = constraint;
	}

	@Override
	public String toString() {
		return "RoomHardConstraint [id=" + id + ", constraint=" + constraint
				+ "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ERoomHardConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(ERoomHardConstraint constraint) {
		this.constraint = constraint;
	}
	
	
}
