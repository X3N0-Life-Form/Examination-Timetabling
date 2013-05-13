package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Student implements Serializable {
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
