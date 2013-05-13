package struct;

import java.io.Serializable;

public class InstitutionalWeightings implements Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + frontLoad_1;
		result = prime * result + frontLoad_2;
		result = prime * result + frontLoad_3;
		result = prime * result + nonMixedDurations;
		result = prime * result + periodSpread;
		result = prime * result + twoInADay;
		result = prime * result + twoInARow;
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
		InstitutionalWeightings other = (InstitutionalWeightings) obj;
		if (frontLoad_1 != other.frontLoad_1)
			return false;
		if (frontLoad_2 != other.frontLoad_2)
			return false;
		if (frontLoad_3 != other.frontLoad_3)
			return false;
		if (nonMixedDurations != other.nonMixedDurations)
			return false;
		if (periodSpread != other.periodSpread)
			return false;
		if (twoInADay != other.twoInADay)
			return false;
		if (twoInARow != other.twoInARow)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int twoInARow;
	private int twoInADay;
	private int periodSpread;
	private int nonMixedDurations;
	private int frontLoad_1;
	private int frontLoad_2;
	private int frontLoad_3;
	
	public InstitutionalWeightings(int twoInARow, int twoInADay,
			int periodSpread, int nonMixedDurations, int frontLoad_1,
			int frontLoad_2, int frontLoad_3) {
		super();
		this.twoInARow = twoInARow;
		this.twoInADay = twoInADay;
		this.periodSpread = periodSpread;
		this.nonMixedDurations = nonMixedDurations;
		this.frontLoad_1 = frontLoad_1;
		this.frontLoad_2 = frontLoad_2;
		this.frontLoad_3 = frontLoad_3;
	}

	@Override
	public String toString() {
		return "InstitutionalWeightings [twoInARow=" + twoInARow
				+ ", twoInADay=" + twoInADay + ", periodSpread=" + periodSpread
				+ ", nonMixedDurations=" + nonMixedDurations + ", frontLoad_1="
				+ frontLoad_1 + ", frontLoad_2=" + frontLoad_2
				+ ", frontLoad_3=" + frontLoad_3 + "]";
	}

	public int getTwoInARow() {
		return twoInARow;
	}

	public void setTwoInARow(int twoInARow) {
		this.twoInARow = twoInARow;
	}

	public int getTwoInADay() {
		return twoInADay;
	}

	public void setTwoInADay(int twoInADay) {
		this.twoInADay = twoInADay;
	}

	public int getPeriodSpread() {
		return periodSpread;
	}

	public void setPeriodSpread(int periodSpread) {
		this.periodSpread = periodSpread;
	}

	public int getNonMixedDurations() {
		return nonMixedDurations;
	}

	public void setNonMixedDurations(int nonMixedDurations) {
		this.nonMixedDurations = nonMixedDurations;
	}

	public int getFrontLoad_1() {
		return frontLoad_1;
	}

	public void setFrontLoad_1(int frontLoad_1) {
		this.frontLoad_1 = frontLoad_1;
	}

	public int getFrontLoad_2() {
		return frontLoad_2;
	}

	public void setFrontLoad_2(int frontLoad_2) {
		this.frontLoad_2 = frontLoad_2;
	}

	public int getFrontLoad_3() {
		return frontLoad_3;
	}

	public void setFrontLoad_3(int frontLoad_3) {
		this.frontLoad_3 = frontLoad_3;
	}
	
	
}
