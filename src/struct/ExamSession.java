package struct;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

public class ExamSession {
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
