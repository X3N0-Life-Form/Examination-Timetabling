package struct;

import java.util.ArrayList;

public class Exam {
	/**
	 * Starts at 0.
	 */
	private int id;
	private int duration;
	private int size;
	private ArrayList<Integer> students;
	private ArrayList<PeriodHardConstraint> constraints;
	private RoomHardConstraint roomHardConstraint = null;
	
	public Exam(int id, int duration, int size, ArrayList<Integer> students) {
		this.id = id;
		this.duration = duration;
		this.size = size;
		this.students = students;
		constraints = new ArrayList<PeriodHardConstraint>();
	}

	public void addConstraint(PeriodHardConstraint constraint) {
		constraints.add(constraint);
	}
	
	public ArrayList<PeriodHardConstraint> getConstraints() {
		return constraints;
	}


	public void setConstraints(ArrayList<PeriodHardConstraint> constraints) {
		this.constraints = constraints;
	}


	@Override
	public String toString() {
		return "Exam [id=" + id + ", duration=" + duration + ", size=" + size
				+ ", students=" + students + "]";
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<Integer> getStudents() {
		return students;
	}

	public void setStudents(ArrayList<Integer> students) {
		this.students = students;
	}

	public RoomHardConstraint getRoomHardConstraint() {
		return roomHardConstraint;
	}

	public void setRoomHardConstraint(RoomHardConstraint roomHardConstraint) {
		this.roomHardConstraint = roomHardConstraint;
	}

}
