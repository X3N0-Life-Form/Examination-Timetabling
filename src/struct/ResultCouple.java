package struct;

import java.util.ArrayList;

public class ResultCouple {
	
	private Room room;
	private Period period;
	private ArrayList<Exam> examList;
	
	public ResultCouple(Room room, Period period) {
		super();
		this.setRoom(room);
		this.setPeriod(period);
		examList = new ArrayList<Exam>();
	}
	
	public void addExam(Exam e){
		examList.add(e);
	}
	
	public void removeExam(Exam e){
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
	
}
