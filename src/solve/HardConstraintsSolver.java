package solve;

import java.util.Collections;
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
		List <ResultCouple> res = s.getResult();
		List <Exam> NPE = (List<Exam>) ((ArrayList<Exam>) s.getNonPlacedExams()).clone(); 
		Exam e = null;
		
		for( int i = 0; i< s.getNonPlacedExams().size();i++){
			
			int examId = s.getNonPlacedExams().get(i).getId();
			List<Integer> listE = checkCoincidence(examId);
			
			// if the list has got more than 1 exam
			if (listE.size() > 1){
				// find a period for the list
				int currentPeriod = getAvailablePeriod(listE);
				// find rooms for the list and currentPeriod
				List<Integer> currentRoomsList = findSuitable(listE, currentPeriod);
			
				// list
				for(int j = 0; j < listE.size();j++){
					//get the exam with its id into the ExamSession
					for(int k = 0; k < s.getExamSession().getExams().size();k++){
						if (s.getExamSession().getExams().get(k).getId() == listE.get(j))
							e = s.getExamSession().getExams().get(k);
					}
					// res
					for (int k = 0; k< res.size(); k++){
						// if in res.get(k) period & room corresponds
						if (res.get(k).getPeriod().getId() == currentPeriod
						&& res.get(k).getRoom().getId() == currentRoomsList.get(j))
							//add the exam j
							res.get(k).getExamList().add(e);
						
						updateValidPeriods(e.getId(), currentPeriod );
					}
					NPE.remove(j);
				}
			}
			List<Exam> sNPE = s.getNonPlacedExams();
			sNPE = NPE;
		}
		for( int i = 0; i< s.getNonPlacedExams().size();i++){
			
			int examId = s.getNonPlacedExams().get(i).getId();
			List<Integer> listE = checkCoincidence(examId);
			
			// if the list has got more than 1 exam
			if (listE.size() == 1){
				// find a period for the list
				int currentPeriod = getAvailablePeriod(listE);
				// find rooms for the list and currentPeriod
				List<Integer> currentRoomsList = findSuitable(listE, currentPeriod);
			
					int j=1;
					for(int k = 0; k < s.getExamSession().getExams().size();k++){
						if (s.getExamSession().getExams().get(k).getId() == listE.get(j))
							e = s.getExamSession().getExams().get(k);
					}
					// res
					for (int k = 0; k< res.size(); k++){
						// if in res.get(k) period & room corresponds
						if (res.get(k).getPeriod().getId() == currentPeriod
						&& res.get(k).getRoom().getId() == currentRoomsList.get(j))
							//add the exam j
							res.get(k).getExamList().add(e);
						
						updateValidPeriods(e.getId(), currentPeriod );
					}
					NPE.remove(j);
				}
			}
		return s;
	}
	
		public void updateValidPeriods(int examId, int periodId)
		{
			int [][] eP = s.getExamPeriodModif();
			int [][] coincidence = s.getExamCoincidence();
			for (int i = 1; i < s.getExamSession().getPeriods().size(); i++)
				if( coincidence [examId][i] == 0)
					eP [i][periodId] = 0;
		}
	
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
						if (notAfter) {
							res = false;
							feedback.addItem(current, Feedback.AFTER_VIOLATION);
						}
				}
				
			}
			//check EXAM_COINCIDENCE
			// exam
			for (ResultCouple currentBis : s.getResult()) {
				for (int j = 0; j< currentBis.getExamList().size(); j++){
					constraintList = currentBis.getExamList().get(j).getConstraints();
					// constraint
					for (int i = 0; i< constraintList.size(); i++){
						// if constraint is EXAM_COINCIDENCE
						int periodId = -1;
						if (constraintList.get(i).getConstraint() == 
								EPeriodHardConstraint.EXAM_COINCIDENCE) {
							//get id of the 2nd exam (EXAM_COINCIDENCE)
							//if e1Id = current exam id, var id = e2ID
							periodId = currentBis.getPeriod().getId();
							if (constraintList.get(i).getE1Id() == 
									currentBis.getExamList().get(j).getId())
								id = constraintList.get(i).getE2Id();
							//else var id = e1ID
							else
								id = constraintList.get(i).getE1Id();
						}
						boolean present = false;
						// check if id is present into list exam
						for(int k = 0; k < currentBis.getExamList().size();k++){
							// if id is found : present = true
							if (currentBis.getExamList().get(k).getId() == id) {
								present = true; 
							} else {
								for(ResultCouple c : s.getResult()){
									if (c.getPeriod().getId() == periodId) {
										for (Exam e : c.getExamList()) {
											if (e.getId() == id)
												present = true;
										}
									}
								}
							}
						}
						// if !present, id's not found => false
						if (!present) {
							res = false;
							feedback.addItem(currentBis, Feedback.EXAM_COINCIDENCE_VIOLATION);
						}
					}
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
						if (present) {
							res = false;
							feedback.addItem(current, Feedback.EXCLUSION_VIOLATION);
						}
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
			res.add(examId);
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
		int[][] eP = s.getExamPeriodModif();
		
		for (int i = 0; i< s.getResult().size();i++){
			int periodId = s.getResult().get(i).getPeriod().getId();
			if (canHost(examId, periodId, s.getResult()) && (eP[examId][periodId] != 0)){
					tmp = periodId;
					break;
			}
		}
		return tmp;
	}
	
	public int getAvailablePeriod(List<Integer> coincidingExams) {
		int tmp = -1;
		int [][] eP = s.getExamPeriodModif();
		boolean periodOk = true;
		
		for (int i = 0; i< s.getResult().size();i++){			
			int currentPeriodId = s.getResult().get(i).getPeriod().getId();
			periodOk = true;
			for (int j = 0; j<coincidingExams.size();j++){
				int currentExamId = coincidingExams.get(j);
				if (eP[currentExamId][currentPeriodId] == 0) {
					periodOk = false;
					break;
				}
			}
			if (canHost (coincidingExams, currentPeriodId) && periodOk){
				tmp = currentPeriodId;
				break;
			}
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
		ArrayList<Integer> e = (ArrayList<Integer>) ((ArrayList<Integer>) exams).clone();
		//Collections.copy(e, exams);
		ArrayList<ResultCouple> res = (ArrayList<ResultCouple>) ((ArrayList<ResultCouple>) s.getResult()).clone();
		//Collections.copy(res, s.getResult());
		
		int numberOfExams = exams.size();
		int index;
		int count = 0;
		Exam ex;				
		
		int suitablesFound = 0;
		while(count < numberOfExams){
			// if canHost the 1st exam from the list
			if (canHost(e.get(count), periodId, res)){
				for(int j =0; j<res.size(); j++) {
					if(res.get(j).getPeriod().getId() == periodId
					&& res.get(j).getRoom().getId() == findSuitable(e.get(count), periodId, res)){
						index = j;
						//get the exam 
						ex = s.getExamSession().getExams().get(e.get(count));
						//set the exam
						res.get(index).getExamList().add(ex);
						suitablesFound++;
						if(e.size() == suitablesFound)
							return true;
					}
				}
			}
			count++;
		}
		return tmp;
	}
	
	public boolean canHost(int examId, int periodId, List<ResultCouple> resIn) {
		ArrayList<ResultCouple> res = (ArrayList<ResultCouple>) ((ArrayList<ResultCouple>) resIn).clone();
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
	public int findSuitable(int examId, int periodId, List<ResultCouple> res) {
		//note: prioritize rooms that are already in use.
		//List<ResultCouple> res = s.getResult();
		int tmp = -1;
		int i =0;
		boolean exclusive;
		int sizeE = 0;
		
		//for (int i = 0; i< res.size();i++){
			while( i < res.size() && tmp == -1)
			{
			exclusive = false;
			// where the result period id = periodId
			if (res.get(i).getPeriod().getId() == periodId){
				// if there's 1 or more exams (priority)
				if (res.get(i).getExamList().size() > 0){
					// get the sum of all the exams size for this room & this period
					int sizeCounter = 0;
					for (int j = 0; j< res.get(i).getExamList().size(); j++){
						sizeCounter += res.get(i).getExamList().get(j).getSize();
					}
					//get the capacity of the room

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
					if (sizeCounter+sizeE <= res.get(i).getRoom().getSize() 
							/*&& !exclusive*/) {
						tmp = res.get(i).getRoom().getId();
					}
						
				}
			}
		i++;
	}
	if (tmp == -1){
		i = 0;
		while( i < res.size() && tmp == -1){
			if (res.get(i).getExamList().size() == 0) {
				//check size >< 
				tmp = res.get(i).getRoom().getId();
			}
			i++;	
		}
	}
	return tmp;
	}	
	
	public List<Integer> findSuitable(List<Integer> exams, int periodId) {
		List<Integer> list = new ArrayList<Integer>();
		
		ArrayList<Integer> e = (ArrayList<Integer>) ((ArrayList<Integer>) exams).clone();
		//Collections.copy(e, exams);
		ArrayList<ResultCouple> res = (ArrayList<ResultCouple>) ((ArrayList<ResultCouple>) s.getResult()).clone();
		//Collections.copy(res, s.getResult());
		int numberOfExams = exams.size();
		int index;
		int count = 0;
		Exam ex;
				
			//while exams != null
			while(count < numberOfExams){
				for(int j =0; j<res.size(); j++) {
					if (list.size() == e.size())
						break;
					int pId = res.get(j).getPeriod().getId();
					int rId = res.get(j).getRoom().getId();
					int suitableId = findSuitable(e.get(count), periodId, res);
					
					if(pId == periodId && rId == suitableId){
						index = j;
						//get the exam 
						ex = s.getExamSession().getExams().get(e.get(count));
						//set the exam
						res.get(index).getExamList().add(ex);
						
						list.add(suitableId);
						//remove from the list
					//	e.remove(0);
					}
				}
				count++;
			}
		return list;
	}	
	
}