package struct;

public class RoomHardConstraint {
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
