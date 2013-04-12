package solve;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import parse.ExamSessionParser;
import struct.Exam;
import struct.ExamSession;
import struct.PeriodHardConstraint;
import struct.ResultCouple;
import struct.Solution;
import struct.EPeriodHardConstraint;

/**
 * Finds an initial Timetabling solution, which solves every hard constraints.
 * Can also check if a Solution is correct and provide some feedback if it's
 * not.
 * @author Adrien Droguet - Sara Tari
 * @see Solution
 * @see ExamSession
 * @see ExamSessionParser
 * @see Feedback
 */
public class HardConstraintsSolver {//TODO: interface solver
	
	private Solution s;
	protected enum ESolvingPhase {
		HARD_CONSTRAINT,
		LEFTOVER
	}
	
	public HardConstraintsSolver(Solution solution) {
		this.s = solution;
	}
	
	public Solution solve() {
		ESolvingPhase phase = ESolvingPhase.HARD_CONSTRAINT;
		List<Exam> nonPlacedExams = s.getNonPlacedExams();
		System.out.println("entering solve loop");
		while (nonPlacedExams.size() > 0) {
			int lastIndex = nonPlacedExams.size() - 1;
			Exam current = nonPlacedExams.get(lastIndex);
			if (phase == ESolvingPhase.HARD_CONSTRAINT) {
				if (current.getNumberOfConstraints() == 0) {
					phase = ESolvingPhase.LEFTOVER;
					continue;
				} else {
					//loop through periods
						//loop through rooms
				}
			} else if (phase == ESolvingPhase.LEFTOVER) {
				
			}
			nonPlacedExams.remove(current);//TODO:remove only if solved?
		}
		return s;
	}
	
	//TODO:test all that
	public boolean isSolutionValid(Feedback feedback) {
		boolean res = true;
		
			// id : exam id, used for coincidence & exclusion
			int id = 0;
			boolean notAfter;
			Date periodAfter = new Date();
			Date periodBefore = new Date();
			ArrayList<PeriodHardConstraint> constraintList = new ArrayList<PeriodHardConstraint>();
			
			//check AFTER
			for (ResultCouple current : s.getResult()) {
				
			for (int i = 0 ; i <current.getExamList().size(); i++){
				// get constraints list
				constraintList = current.getExamList().get(i).getConstraints();
				
				for (int j = 0; j < constraintList.size(); j++){
					notAfter = false ;
					// pour chaque exam ayant un after, récupérer id2(before)
					// if AFTER
					if (constraintList.get(j).getConstraint() ==
					EPeriodHardConstraint.AFTER)
						// the second exam of after has to take place before the 1st
						if(current.getExamList().get(i).getId() == 
								constraintList.get(j).getE2Id()){
							id = constraintList.get(j).getE2Id();
							// period of current exam into periodAfter
							periodAfter = current.getPeriod().getDate_hour();
							// tests
							for (ResultCouple currentBefore : s.getResult())
								for(int k = 0; k< currentBefore.getExamList().size();k++)
									if (currentBefore.getExamList().get(k).getId() == id)
										// check date
										periodBefore = currentBefore.getPeriod().getDate_hour();
										if(periodAfter.compareTo(periodBefore) <= 0)
											notAfter = true;							
						}
						if (notAfter)
							res = false;
				}
				
			}
			//check EXAM_COINCIDENCE
			// exam
			for (int j = 0; j< current.getExamList().size(); j++){
				constraintList = current.getExamList().get(j).getConstraints();
				// constraint
				for (int i = 0; i< constraintList.size(); i++){
					// if constraint is EXAM_COINCIDENCE
					if (constraintList.get(i).getConstraint() == 
					EPeriodHardConstraint.EXAM_COINCIDENCE)
						//get id of the 2nd exam (EXAM_COINCIDENCE)
						//if e1Id = current exam id, var id = e2ID
						if (constraintList.get(i).getE1Id() == 
						current.getExamList().get(j).getId())
							id = constraintList.get(i).getE2Id();
						//else var id = e1ID
						else id = constraintList.get(i).getE1Id();
						boolean present = false;
						// check if id is present into list exam
						for(int k = 0; k < current.getExamList().size();k++){
							// if id is found : present = true
							if (current.getExamList().get(k).getId() == id)
								present = true; 
							}
							// if !present, id's not found => false
							if (!present) res = false;
				}
			}			
			
			//check EXCLUSION
			// coincidence matrix
			int [][] coincidence = s.getExamCoincidence();
			
			for (int i = 0; i< current.getExamList().size(); i++){
				// matrix
				for (int j =0; j < s.getExamSession().getExams().size();j++ ){
						boolean present = false;
						// if exam i & j cannot take place at the same time
						if (coincidence[i][j] == 0) 
						// check if exam j is present 
						for(int k = 0; k < current.getExamList().size();k++){
							// if id is found : present = true
							if (current.getExamList().get(k).getId() == j)
								present = true; 
						}
						// if present, id's found => false because of the exclusion
						if (present) 
							res = false;
				}
			}
			int sizeSum = 0;
			for (Exam e : current.getExamList()) {
				//check ROOM_EXCLUSIVE
				if (e.getRoomHardConstraint() != null) {
					if (current.getExamList().size() > 1) {
						feedback.addItem(current, Feedback.ROOM_EXLUSIVE_VIOLATION);
						return false;
					}
				}
				//check duration
				if (e.getDuration() > current.getPeriod().getDuration()) {
					feedback.addItem(current, Feedback.DURATION_VIOLATION);
					return false;
				}
				sizeSum += e.getSize();
			}
			//check size
			if (sizeSum > current.getRoom().getSize()) {
				feedback.addItem(current, Feedback.ROOM_SIZE_VIOLATION);
				return false;
			}
		}
		return res;
	}
}
