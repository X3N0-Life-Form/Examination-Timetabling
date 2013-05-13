package struct;

import java.io.Serializable;
import java.util.ArrayList;

public class Exam implements Comparable<Exam>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Starts at 0.
	 */
	private int id;
	

	@Override
	public Exam clone() {
		Exam clone = new Exam(id, duration, size, students);
		clone.roomHardConstraint = roomHardConstraint;
		clone.constraints = constraints;
		return clone;
	}

	private int duration;
	private int size;
	private ArrayList<Integer> students;
	private ArrayList<PeriodHardConstraint> constraints;
	private RoomHardConstraint roomHardConstraint = null;
	//TODO: make comparable according to number of constraints
	
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

	/**
	 * Compares the number of constraints.
	 */
	@Override
	public int compareTo(Exam exam) {
		int res = this.getNumberOfConstraints() - exam.getNumberOfConstraints();
		return res;
	}
	
	public int getNumberOfConstraints() {
		int res = constraints.size();
		if (roomHardConstraint != null)
			res++;
		return res;
	}
	
	/**
	 * 
	 * @param constraint
	 * @return true if this Exam has at least one constraint
	 * of the specified type.
	 */
	public boolean hasPeriodHardConstraint(
			EPeriodHardConstraint constraint) {
		for (PeriodHardConstraint current : constraints) {
			if (current.getConstraint() == constraint)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constraints == null) ? 0 : constraints.hashCode());
		result = prime * result + duration;
		result = prime * result + id;
		result = prime
				* result
				+ ((roomHardConstraint == null) ? 0 : roomHardConstraint
						.hashCode());
		result = prime * result + size;
		result = prime * result
				+ ((students == null) ? 0 : students.hashCode());
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
		Exam other = (Exam) obj;
		if (constraints == null) {
			if (other.constraints != null)
				return false;
		} else if (!constraints.equals(other.constraints))
			return false;
		if (duration != other.duration)
			return false;
		if (id != other.id)
			return false;
		if (roomHardConstraint == null) {
			if (other.roomHardConstraint != null)
				return false;
		} else if (!roomHardConstraint.equals(other.roomHardConstraint))
			return false;
		if (size != other.size)
			return false;
		if (students == null) {
			if (other.students != null)
				return false;
		} else if (!students.equals(other.students))
			return false;
		return true;
	}

}
