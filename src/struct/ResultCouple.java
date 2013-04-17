package struct;

import java.util.ArrayList;

public class ResultCouple {
	
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
		return "ResultCouple [room=" + room + ", period=" + period
				+ ", examList=" + examList + "]";
	}
	
}
