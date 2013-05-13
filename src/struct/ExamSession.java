package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

public class ExamSession implements Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exams == null) ? 0 : exams.hashCode());
		result = prime
				* result
				+ ((institutionalWeightings == null) ? 0
						: institutionalWeightings.hashCode());
		result = prime
				* result
				+ ((periodHardConstraints == null) ? 0 : periodHardConstraints
						.hashCode());
		result = prime * result + ((periods == null) ? 0 : periods.hashCode());
		result = prime
				* result
				+ ((roomHardConstraints == null) ? 0 : roomHardConstraints
						.hashCode());
		result = prime * result + ((rooms == null) ? 0 : rooms.hashCode());
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
		ExamSession other = (ExamSession) obj;
		if (exams == null) {
			if (other.exams != null)
				return false;
		} else if (!exams.equals(other.exams))
			return false;
		if (institutionalWeightings == null) {
			if (other.institutionalWeightings != null)
				return false;
		} else if (!institutionalWeightings
				.equals(other.institutionalWeightings))
			return false;
		if (periodHardConstraints == null) {
			if (other.periodHardConstraints != null)
				return false;
		} else if (!periodHardConstraints.equals(other.periodHardConstraints))
			return false;
		if (periods == null) {
			if (other.periods != null)
				return false;
		} else if (!periods.equals(other.periods))
			return false;
		if (roomHardConstraints == null) {
			if (other.roomHardConstraints != null)
				return false;
		} else if (!roomHardConstraints.equals(other.roomHardConstraints))
			return false;
		if (rooms == null) {
			if (other.rooms != null)
				return false;
		} else if (!rooms.equals(other.rooms))
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TreeMap<Integer, Exam> exams;
	private ArrayList<Period> periods;
	private ArrayList<Room> rooms;
	private ArrayList<PeriodHardConstraint> periodHardConstraints;
	private ArrayList<RoomHardConstraint> roomHardConstraints;
	private InstitutionalWeightings institutionalWeightings;
	
	public ExamSession(TreeMap<Integer, Exam> exams, ArrayList<Period> periods,
			ArrayList<Room> rooms,
			ArrayList<PeriodHardConstraint> periodHardConstraints,
			ArrayList<RoomHardConstraint> roomHardConstraints,
			InstitutionalWeightings institutionalWeightings) {
		super();
		this.exams = exams;
		this.periods = periods;
		this.rooms = rooms;
		this.periodHardConstraints = periodHardConstraints;
		this.roomHardConstraints = roomHardConstraints;
		this.institutionalWeightings = institutionalWeightings;
	}

	@Override
	public String toString() {
		return "ExamSession [exams=" + exams + ", periods=" + periods
				+ ", rooms=" + rooms + ", periodHardConstraints="
				+ periodHardConstraints + ", roomHardConstraints="
				+ roomHardConstraints + ", institutionalWeightings="
				+ institutionalWeightings + "]";
	}

	public TreeMap<Integer,Exam> getExams() {
		return exams;
	}

	public void setExams(TreeMap<Integer, Exam> exams) {
		this.exams = exams;
	}

	public ArrayList<Period> getPeriods() {
		return periods;
	}

	public void setPeriods(ArrayList<Period> periods) {
		this.periods = periods;
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	public ArrayList<PeriodHardConstraint> getPeriodHardConstraints() {
		return periodHardConstraints;
	}

	public void setPeriodHardConstraints(
			ArrayList<PeriodHardConstraint> periodHardConstraints) {
		this.periodHardConstraints = periodHardConstraints;
	}

	public ArrayList<RoomHardConstraint> getRoomHardConstraints() {
		return roomHardConstraints;
	}

	public void setRoomHardConstraints(
			ArrayList<RoomHardConstraint> roomHardConstraints) {
		this.roomHardConstraints = roomHardConstraints;
	}

	public InstitutionalWeightings getInstitutionalWeightings() {
		return institutionalWeightings;
	}

	public void setInstitutionalWeightings(
			InstitutionalWeightings institutionalWeightings) {
		this.institutionalWeightings = institutionalWeightings;
	}
	
	/**
	 * 
	 * @return The exams TreeMap as a List.
	 */
	public List<Exam> getExamsAsList() {
		NavigableSet<Integer> nset = exams.navigableKeySet();
		ArrayList<Exam> res = new ArrayList<Exam>();
		for (Integer current : nset) {
			res.add(exams.get(current));
		}
		return res;
	}
}
