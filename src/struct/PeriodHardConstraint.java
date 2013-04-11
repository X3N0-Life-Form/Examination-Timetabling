package struct;

public class PeriodHardConstraint {
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
