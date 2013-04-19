package struct;

import java.util.ArrayList;
import java.util.Arrays;
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
	private int[][] examPeriodBase;
	public int[][] getExamPeriodBase() {
		return examPeriodBase;
	}
	public void setExamPeriodBase(int[][] examPeriod) {
		this.examPeriodBase = examPeriod;
	}
	private int[][] examPeriodModif;
	public int[][] getExamPeriodModif() {
		return examPeriodModif;
	}
	
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
	private List<Exam> nonPlacedExams = null;
	public List<Exam> getNonPlacedExams() {
		return nonPlacedExams;
	}
	public void setNonPlacedExams(List<Exam> nonPlacedExams) {
		this.nonPlacedExams = nonPlacedExams;
	}
	
	private List<ResultCouple> result = null;
	public List<ResultCouple> getResult() {
		return result;
	}
	public List<ResultCouple> getResultsForPeriod(int periodId) {
		List<ResultCouple> res = new ArrayList<ResultCouple>();
		for (ResultCouple currentRC : result) {
			if (currentRC.getPeriod().getId() == periodId) {
				res.add(currentRC);
			}
		}
		return res;
	}
	/**
	 * 
	 * @param periodId
	 * @param resIn
	 * @return A List of ResultCouple of the specified period out ResultCouple
	 * found in resIn.
	 */
	public List<ResultCouple> getResultsForPeriod(int periodId, List<ResultCouple> resIn) {
		List<ResultCouple> resOut = new ArrayList<ResultCouple>();
		for (ResultCouple currentRC : resIn) {
			if (currentRC.getPeriod().getId() == periodId) {
				resOut.add(currentRC);
			}
		}
		return resOut;
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
		examPeriodBase = new int[numberOfExams][numberOfPeriods];
		int numberOfRooms = examSession.getRooms().size();
		examRoom = new int[numberOfExams][numberOfRooms];
		
		result = new ArrayList<ResultCouple>();
		for (Period currentPeriod : examSession.getPeriods()) {
			for (Room currentRoom : examSession.getRooms()) {
				ResultCouple nuRC = new ResultCouple(currentRoom, currentPeriod);
				nuRC.setSolution(this);
				result.add(nuRC);
			}
		}
		
		/////////////////////////////////////
		// create constraint-related lists //
		/////////////////////////////////////
		afterExams = new ArrayList<Exam>();
		coincidingExams = new ArrayList<Exam>();
		for (Exam currentExam : examSession.getExamsAsList()) {
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
		System.out.println("--fills examCoincidence (full of 1 )");
		for (int i = 0; i < numberOfExams; i++) 
			for (int j = 0; j < numberOfExams; j++)
				examCoincidence [i][j] = 1;
		/**
		 * if a student takes exam i & exam j then examCoincidence = 0
		 */
		//TODO:something about that loop fest
		System.out.print("--Finding mutually exclusive exams based on student presence");
		int loopCounter = 0;
		for (int i = 0; i < numberOfExams; i++) {
			for (int j = i + 1; j < numberOfExams ; j++) {
				int eiNumberOfStudents = examSession.getExams().get(i).getSize();
				int ejNumberOfStudents = examSession.getExams().get(j).getSize();
				for (int ei = 0 ; ei < eiNumberOfStudents; ei ++)
					for (int ej = 0 ; ej < ejNumberOfStudents; ej++) {
						//if student is present in both exams --> no coincidence 
						if (examSession.getExams().get(i).getStudents().get(ei) == 
							examSession.getExams().get(j).getStudents().get(ej)) {
								examCoincidence [i][j] = 0;
								break;
						}
						loopCounter++;
					}
			}
			//System.out.println("i=" + i + ";numberOfExams=" + numberOfExams);
		}//TODO: mirror values to fully initialize the matrix
		System.out.println(" - done\n--Looped " + loopCounter + " times.\n");
		
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
		
		for (int i = 0; i< examSession.getExams().size();i++) {
			for (int j = 0; j< examSession.getPeriods().size(); j++){
				/**
				 * cost = 0 & duration ok
				 */
				if ((examSession.getExams().get(i).getDuration() <= examSession.getPeriods().get(j).getDuration() &&
						examSession.getPeriods().get(j).getCost() == 0))
							examPeriodBase[i][j] = 2;
				/**
				 * cost > 0 & duration ok
				 */
				else if ((examSession.getExams().get(i).getDuration() <= examSession.getPeriods().get(j).getDuration() &&
						examSession.getPeriods().get(j).getCost() > 0))
						examPeriodBase[i][j] = 1;
				/*
				 * duration not ok
				 */
				else 
					examPeriodBase[i][j] = 0;
			}
		}
		examPeriodModif = new int[numberOfExams][numberOfPeriods];
		for (int i = 0; i < numberOfExams; i++) {
			examPeriodModif[i] =
					Arrays.copyOfRange(examPeriodBase[i], 0, numberOfPeriods);
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
		nonPlacedExams = examSession.getExamsAsList();
		Collections.sort(nonPlacedExams);
	}
	
	public int[][] getExamCoincidence() {
		return examCoincidence;
	}

	public void setExamCoincidence(int[][] examCoincidence) {
		this.examCoincidence = examCoincidence;
	}



	public int[][] getExamRoom() {
		return examRoom;
	}

	public void setExamRoom(int[][] examRoom) {
		this.examRoom = examRoom;
	}
	


	
}
