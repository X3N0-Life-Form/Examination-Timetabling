package struct;

import java.util.ArrayList;

public class ResultCouple {
	
	private Room room;
	private Period period;
	private ArrayList<Exam> examList;
	
	public ResultCouple(Room room, Period period) {
		super();
		this.room = room;
		this.period = period;
		examList = new ArrayList<Exam>();
	}
	
	public void addExam(Exam e){
		examList.add(e);
	}
	
	public void removeExam(Exam e){
		examList.remove(e);
	}
	
	
	
}
