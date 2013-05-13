package struct;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultCouple implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Room room;
	private Period period;
	private ArrayList<Exam> examList;
	private Solution s;
	public Solution getSolution() {
		return s;
	}
	public void setSolution(Solution solution) {
		this.s = solution;
	}
	
	public ResultCouple(Room room, Period period) {
		super();
		this.setRoom(room);
		this.setPeriod(period);
		examList = new ArrayList<Exam>();
	}
	
	public void addExam(Exam e){
		//if exam not in the list
		if (!examList.contains(e)) {
			int[][] epm = s.getExamPeriodModif();
			int eId = e.getId();
			int pId = period.getId();
			int[][] ec = s.getExamCoincidence();
			for (int i = 0; i < s.getExamSession().getExams().size(); i++) {
				if (ec[eId][i] == 0)
					epm[i][pId] = 0;
			}
			examList.add(e);
		}
		//else do nothing
	}
	public void addExam(int examId) {
		Exam e = s.getExamSession().getExams().get(examId);
		this.addExam(e);
	}
	
	public void removeExam(Exam e){
		//TODO: update matrixes
		examList.remove(e);
	}
	
	public ArrayList<Exam> getExamList() {
		return examList;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public String toString() {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (Exam current : examList) {
			idList.add(current.getId());
		}
		return "ResultCouple [room=" + room + ", period=" + period
				+ ", examList=" + idList + "]";
	}
	
	@Override
	public ResultCouple clone() {
		ResultCouple clone = new ResultCouple(room, period);
		clone.examList = new ArrayList<Exam>();
		for (Exam current : examList) {
			clone.examList.add(current);
		}
		clone.s = s;
		return clone;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((examList == null) ? 0 : examList.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + ((s == null) ? 0 : s.hashCode());
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
		ResultCouple other = (ResultCouple) obj;
		if (examList == null) {
			if (other.examList != null)
				return false;
		} else if (!examList.equals(other.examList))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		return true;
	}
	
}
