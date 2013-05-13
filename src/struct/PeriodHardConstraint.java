package struct;

import java.io.Serializable;

//TODO: constraint interface? + hard constraint interface?
public class PeriodHardConstraint implements Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constraint == null) ? 0 : constraint.hashCode());
		result = prime * result + e1Id;
		result = prime * result + e2Id;
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
		PeriodHardConstraint other = (PeriodHardConstraint) obj;
		if (constraint != other.constraint)
			return false;
		if (e1Id != other.e1Id)
			return false;
		if (e2Id != other.e2Id)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int e1Id;
	private int e2Id;
	private EPeriodHardConstraint constraint;
	
	public PeriodHardConstraint(int e1Id, int e2Id,
			EPeriodHardConstraint constraint) {
		super();
		this.e1Id = e1Id;
		this.e2Id = e2Id;
		this.constraint = constraint;
	}

	@Override
	public String toString() {
		return "PeriodHardConstraint [e1Id=" + e1Id + ", e2Id=" + e2Id
				+ ", constraint=" + constraint + "]";
	}

	public int getE1Id() {
		return e1Id;
	}

	public void setE1Id(int e1Id) {
		this.e1Id = e1Id;
	}

	public int getE2Id() {
		return e2Id;
	}

	public void setE2Id(int e2Id) {
		this.e2Id = e2Id;
	}

	public EPeriodHardConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(EPeriodHardConstraint constraint) {
		this.constraint = constraint;
	}
	
	
}
