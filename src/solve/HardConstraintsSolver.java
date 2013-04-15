package solve;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import parse.ExamSessionParser;
import struct.Exam;
import struct.ExamSession;
import struct.Period;
import struct.PeriodHardConstraint;
import struct.ResultCouple;
import struct.Room;
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
		while (nonPlacedExams.size() > 0) {
			int lastIndex = nonPlacedExams.size() - 1;
			Exam currentExam = nonPlacedExams.get(lastIndex);
			if (phase == ESolvingPhase.HARD_CONSTRAINT) {
				if (currentExam.getNumberOfConstraints() == 0) {
					phase = ESolvingPhase.LEFTOVER;
				} else {
					//test constraint
					//loop through periods
						//loop through rooms
				}
			}
			if (phase == ESolvingPhase.LEFTOVER) {
				
			}
			nonPlacedExams.remove(currentExam);//TODO:remove only if solved?
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
	
	/**
	 * Check for exams coinciding with the specified id.
	 * @param examId
	 * @return List of exams or null.
	 */
	public List<Integer> checkCoincidence(int examId) {
		Exam exam = s.getExamSession().getExams().get(examId);
		if (exam.hasPeriodHardConstraint(
				EPeriodHardConstraint.EXAM_COINCIDENCE)) {
			List<Integer> res = new LinkedList<Integer>();
			// loop through the specified exam's constraints
			for (PeriodHardConstraint currentConstraint :
				exam.getConstraints()) {
				if (currentConstraint.getConstraint() ==
						EPeriodHardConstraint.EXAM_COINCIDENCE) {
					res.add(currentConstraint.getE2Id());
				}
			}
			return res;
		} else {
			return null;
		}
	}
	
	public int getAvailablePeriod(int examId) {
		int tmp = -1;
		int[][] eP = s.getExamPeriod();
		
		for (int i = 0; i< s.getResult().size();i++){
			int periodId = s.getResult().get(i).getPeriod().getId();
			if (canHost(examId, periodId) && (eP[examId][periodId] != 0)){
					tmp = periodId;
					break;
			}
		}
		return tmp;
	}	
	public int getAvailablePeriod(List<Integer> coincidingExams) {
		int tmp = -1;
		int [][] eP = s.getExamPeriod();
		boolean periodOk = true;
		
		for (int i = 0; i< s.getResult().size();i++){			
			int periodId = s.getResult().get(i).getPeriod().getId();
			for (int j = 0; j<coincidingExams.size();j++){
				if (eP[j][periodId] == 0)
					periodOk = false;
			}
			if (canHost (coincidingExams, periodId) && periodOk)
				tmp = periodId;
				break;
		}
		
		return tmp;
	}
	
	/**
	 * Can this period host the specified list of exams.
	 * @param exams
	 * @param periodId
	 * @return
	 */
	public boolean canHost(List<Integer> exams, int periodId) {
		boolean tmp = false;
		List<Integer> e = exams; // TODO : clone
		List<ResultCouple> res = s.getResult(); //TODO : clone
		int size = exams.size();
		int index;
		int count = 0;
		Exam ex;				
			//while exams != null
			while(e!= null && count < size){
				// if canHost the 1st exam from the list
				if (canHost(e.get(0), periodId)){
					for(int j =0; j<res.size(); j++)
						if(res.get(j).getPeriod().getId() == 
								periodId && res.get(j).getRoom().getId() == findSuitable(e.get(0), periodId)){
							index = j;
					//get the exam 
					ex = s.getExamSession().getExams().get(e.get(0));
					//set the exam
					res.get(index).getExamList().add(ex);
					//remove from the list
					e.remove(0);
					}
				count++;
			}
			if(e.size() == 0)
				tmp = true;
		}
		return tmp;
	}
	
	public boolean canHost(int examId, int periodId) {
		List<ResultCouple> res = s.getResult();
		boolean tmp = false;
		boolean exclusive;
		
		for (int i = 0; i< res.size();i++){
			// where the result period id = periodId
			if (res.get(i).getPeriod().getId() == periodId){
				// if there's no other exams
				if (res.get(i).getExamList() == null)
					tmp = true;
				// if there's 1 or more exams
				else {
					exclusive = false;
					// get the sum of all the exams size for this room & this period
					int cmp = 0;
					for (int j = 0; j< res.get(i).getExamList().size(); j++){
						cmp += res.get(i).getExamList().get(j).getSize();
					}
					//get the capacity of the room
					int sizeE = 0;
					for (int k = 0; k < s.getExamSession().getExams().size(); k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeE = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					//check if room exclusive or not 
					for (int l = 0; l< s.getExamSession().getRoomHardConstraints().size(); l++){
						if (s.getExamSession().getRoomHardConstraints().get(l).getId() == examId)
							exclusive = true;
					}
					//if the size of all the exams + size of our exam <= room capacity => true
					if (cmp+sizeE <= res.get(i).getRoom().getSize() && !exclusive )
						tmp = true;
						
			}
		}
	}
		return tmp;
	}
	
	/**
	 * Find a suitable room for the specified exam in the specified period.
	 * @param examId
	 * @param periodId
	 * @return
	 */
	public int findSuitable(int examId, int periodId) {
		//note: prioritize rooms that are already in use.
		List<ResultCouple> res = s.getResult();
		int tmp = -1;
		int i =0;
		boolean exclusive;
		
		//for (int i = 0; i< res.size();i++){
			while( i < res.size() && tmp == -1)
			{
			exclusive = false;
			// where the result period id = periodId
			if (res.get(i).getPeriod().getId() == periodId){
				// if there's 1 or more exams (priority)
				if (res.get(i).getExamList() != null){
					// get the sum of all the exams size for this room & this period
					int cmp = 0;
					for (int j = 0; j< res.get(i).getExamList().size(); j++){
						cmp += res.get(i).getExamList().get(j).getSize();
					}
					//get the capacity of the room
					int sizeE = 0;
					for (int k = 0; k < s.getExamSession().getExams().size(); k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeE = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					//check if room exclusive or not 
					for (int l = 0; l< s.getExamSession().getRoomHardConstraints().size(); l++){
						if (s.getExamSession().getRoomHardConstraints().get(l).getId() == examId)
							exclusive = true;
					}
					//if the size of all the exams + size of our exam <= room capacity, get this id
					if (cmp+sizeE <= res.get(i).getRoom().getSize() && !exclusive)
						tmp = res.get(i).getRoom().getId();
						
				}
			}
		i++;
	}
	if (tmp == -1){
		i = 0;
		while( i < res.size() && tmp == -1){
			if (res.get(i).getExamList() == null)
				//check size >< 
				tmp = res.get(i).getRoom().getId();
			i++;	
		}
	}
	return tmp;
	}	
	
	public List<Integer> findSuitable(List<Integer> exams, int periodId) {
		List<Integer> list = new ArrayList<Integer>();
		
		List<Integer> e = exams; // TODO : clone
		List<ResultCouple> res = s.getResult(); //TODO : clone
		int size = exams.size();
		int index;
		int count = 0;
		Exam ex;
				
			//while exams != null
			while(e!= null && count < size){
					for(int j =0; j<res.size(); j++)
						if(res.get(j).getPeriod().getId() == 
								periodId && res.get(j).getRoom().getId() == findSuitable(e.get(0), periodId)){
							index = j;
					//get the exam 
					ex = s.getExamSession().getExams().get(e.get(0));
					//set the exam
					res.get(index).getExamList().add(ex);
					//remove from the list
					e.remove(0);
					list.add(findSuitable(e.get(0),periodId));
					}
				count++;
			}
		return list;
	}	
}