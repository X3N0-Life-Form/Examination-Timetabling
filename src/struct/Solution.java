package struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Adrien Droguet - Sara Tari
 *
 */
public class Solution {
	/**
	 * List of coinciding exams [id1][id2].
	 */
	private int[][] examCoincidence;
	/**
	 * [id_exam][id_period]
	 */
	private int[][] examPeriod;
	
	/**
	 * [id_exam][id_room]
	 */
	private int[][] examRoom;
	/**
	 * [id_exam] True if the exam has been placed on the timetable.
	 */
	/**
	 * List of exams that still need to be placed.
	 */
	private ArrayList<Exam> nonPlacedExams = null;
	public ArrayList<Exam> getNonPlacedExams() {
		return nonPlacedExams;
	}
	public void setNonPlacedExams(ArrayList<Exam> nonPlacedExams) {
		this.nonPlacedExams = nonPlacedExams;
	}
	
	//TODO:optimisation réfléchir au type de liste
	private List<ResultCouple> result = null;
	public List<ResultCouple> getResult() {
		return result;
	}
	public void setResult(ArrayList<ResultCouple> result) {
		this.result = result;
	}
	
	private ExamSession examSession;
	public ExamSession getExamSession() {
		return examSession;
	}
	public void setExamSession(ExamSession examSession) {
		this.examSession = examSession;
	}
	
	private List<Exam> afterExams;
	public List<Exam> getAfterExams() {
		return afterExams;
	}
	
	private List<Exam> coincidingExams;
	public List<Exam> getCoincidingExams() {
		return coincidingExams;
	}
	public void setCoincidingExams(List<Exam> coincidingExams) {
		this.coincidingExams = coincidingExams;
	}
	
	/**
	 * Must at least be passed through a HardConstraintSolver
	 * after being created.
	 * @param examSession
	 */
	public Solution(ExamSession examSession) {
		this.examSession = examSession;
		
		/////////////////////
		// initializations //
		/////////////////////
		int numberOfExams = examSession.getExams().size();
		examCoincidence = new int[numberOfExams][numberOfExams];
		int numberOfPeriods = examSession.getPeriods().size();
		examPeriod = new int[numberOfExams][numberOfPeriods];
		int numberOfRooms = examSession.getRooms().size();
		examRoom = new int[numberOfExams][numberOfRooms];
		
		/////////////////////////////////////
		// create constraint-related lists //
		/////////////////////////////////////
		afterExams = new ArrayList<Exam>();
		coincidingExams = new ArrayList<Exam>();
		for (Exam currentExam : examSession.getExams()) {
			for (PeriodHardConstraint c : currentExam.getConstraints()) {
				if (c.getConstraint() == EPeriodHardConstraint.AFTER) {
					afterExams.add(currentExam);
				} else if (c.getConstraint() ==
						EPeriodHardConstraint.EXAM_COINCIDENCE) {
					coincidingExams.add(currentExam);
				}
			}
		}

		
		
		/**
		 * fills examCoincidence 
		 */
		
		/**
		 * fills examCoincidence (full of 1 )
		 */
		System.out.println("fills examCoincidence (full of 1 )");
		for (int i = 0; i < numberOfExams; i++) 
			for (int j = 0; j < numberOfExams; j++)
				examCoincidence [i][j] = 1;
		/**
		 * if a student takes exam i & exam j then examCoincidence = 0
		 */
		//TODO:something about that loop fest (22s exec time)
		System.out.println("if a student takes exam i & exam j then examCoincidence = 0");
		for (int i = 0; i < numberOfExams; i++)
			for (int j = 0; j < numberOfExams ; j++)
				for (int ei = 0 ; ei < examSession.getExams().get(i).getSize(); ei ++)
					for (int ej = 0 ; ej < examSession.getExams().get(j).getSize(); ej++){
						if (examSession.getExams().get(i).getStudents().get(ei) == 
							examSession.getExams().get(j).getStudents().get(ej)){
								examCoincidence [i][j] = 0;
								break;
						}
					}
		/**
		 * if exam i and j can't be on the same period
		 */
		
		for(int i = 0; i< examSession.getExams().size();i++)
			for (int j = 0 ; j < examSession.getExams().get(i).getConstraints().size(); j++)
				if ( examSession.getExams().get(i).getConstraints().get(j).getConstraint() == EPeriodHardConstraint.EXCLUSION)
					examCoincidence[i][examSession.getExams().get(i).getConstraints().get(j).getE2Id()] = 0;
		/**
		 * if exam i and j have to be on the same period
		 */		
		
		for(int i = 0; i< examSession.getExams().size();i++)
			for (int j = 0 ; j < examSession.getExams().get(i).getConstraints().size(); j++)
				if ( examSession.getExams().get(i).getConstraints().get(j).getConstraint() == EPeriodHardConstraint.EXAM_COINCIDENCE)
					examCoincidence[i][examSession.getExams().get(i).getConstraints().get(j).getE2Id()] = 2;	
		
		/**
		 * fills examPeriod
		 */
		
		for (int i = 0; i< examSession.getExams().size();i++)
			for (int j = 0; j< examSession.getPeriods().size(); j++){
				/**
				 * cost = 0 & duration ok
				 */
				if ((examSession.getExams().get(i).getDuration() <= examSession.getPeriods().get(j).getDuration() &&
						examSession.getPeriods().get(j).getCost() == 0))
							examPeriod[i][j] = 2;
				/**
				 * cost > 0 & duration ok
				 */
				else if ((examSession.getExams().get(i).getDuration() <= examSession.getPeriods().get(j).getDuration() &&
						examSession.getPeriods().get(j).getCost() > 0))
						examPeriod[i][j] = 1;
				/*
				 * duration not ok
				 */
				else 
					examPeriod[i][j] = 0;
			}
		
		/**
		 * fills examRoom
		 */
		for (int i = 0; i <examSession.getExams().size() ; i++)
			for (int j =0; j< examSession.getRooms().size(); j++)
				if ((examSession.getExams().get(i).getSize() <= examSession.getRooms().get(j).getSize() &&
				examSession.getRooms().get(j).getCost() == 0))
					examRoom[i][j] = 2;
				else if ((examSession.getExams().get(i).getSize() <= examSession.getRooms().get(j).getSize() &&
				examSession.getRooms().get(j).getCost() > 0))
					examRoom[i][j] = 1;
				else 
					examRoom[i][j] = 0; 
			
		/**
		 * initialise results 
		 */
		nonPlacedExams = (ArrayList<Exam>) examSession.getExams().clone();
		Collections.sort(nonPlacedExams);
	}
	
	public int[][] getExamCoincidence() {
		return examCoincidence;
	}

	public void setExamCoincidence(int[][] examCoincidence) {
		this.examCoincidence = examCoincidence;
	}

	public int[][] getExamPeriod() {
		return examPeriod;
	}

	public void setExamPeriod(int[][] examPeriod) {
		this.examPeriod = examPeriod;
	}

	public int[][] getExamRoom() {
		return examRoom;
	}

	public void setExamRoom(int[][] examRoom) {
		this.examRoom = examRoom;
	}
	


	
}
