package struct;

import java.util.ArrayList;

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
	
	private ArrayList<Exam> nonPlacedExams = null;
	
	private ArrayList<ResultCouple> result = null;//TODO:optimisation réfléchir au type de liste
	public ArrayList<ResultCouple> getResult() {
		return result;
	}
	
	private ExamSession examSession;
	
	/**
	 * Must at least be passed through a HardConstraintSolver
	 * after being created.
	 * @param examSession
	 */
	public Solution(ExamSession examSession) {
		this.examSession = examSession;
		
		/**
		 * fills examCoincidence 
		 */
		
		/**
		 * fills examCoincidence (full of 1 )
		 */
		for (int i = 0; i < examSession.getExams().size(); i++) 
			for (int j = 0; j < examSession.getExams().size(); j++)
				examCoincidence [i][j] = 1;
		/**
		 * if a student takes exam i & exam j then examCoincidence = 0
		 */
		for (int i = 0; i < examSession.getExams().size(); i++)
			for (int j = 0; j < examSession.getExams().size() ; j++)
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
	}
}
