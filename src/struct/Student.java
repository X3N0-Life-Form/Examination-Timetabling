package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Student implements Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((examIds == null) ? 0 : examIds.hashCode());
		result = prime * result + ((examRes == null) ? 0 : examRes.hashCode());
		result = prime * result + id;
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
		Student other = (Student) obj;
		if (examIds == null) {
			if (other.examIds != null)
				return false;
		} else if (!examIds.equals(other.examIds))
			return false;
		if (examRes == null) {
			if (other.examRes != null)
				return false;
		} else if (!examRes.equals(other.examRes))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private List<Integer> examIds ;
	private List<ResultCouple> examRes;
	
	public Student(int id){
		this.id = id;
		this.examIds = new ArrayList<Integer>();
		this.examRes = new ArrayList<ResultCouple>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Integer> getExamIds() {
		return examIds;
	}
	public void setExamIds(List<Integer> examIds) {
		this.examIds = examIds;
	}
	public List<ResultCouple> getExamRes() {
		return examRes;
	}
	public void setExamRes(List<ResultCouple> examRes) {
		this.examRes = examRes;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", examIds=" + examIds + ", examRes="
				+ examRes + "]";
	}

	public void addExamId(int examId) {
		examIds.add(examId);
	}

	public void addResultCouple(ResultCouple rc) {
		examRes.add(rc);
	}
	

}
