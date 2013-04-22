package solve;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.text.StyledEditorKit.BoldAction;

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
public class HardConstraintsSolver {
	
	private Solution s;
	protected enum ESolvingPhase {
		HARD_CONSTRAINT,
		LEFTOVER
	}
	
	public HardConstraintsSolver(Solution solution) {
		this.s = solution;
	}
	
	/**
	 * 
	 * @param boolArray
	 * @param NPE list of non placed exams
	 * @return
	 * @throws SolvingException If boolArray & NPE have different size.
	 */
	public boolean hasCoincidingExams(boolean[] boolArray, List<Exam> NPE) throws SolvingException {
		if (boolArray.length != NPE.size())
			throw new SolvingException("The specified list and array have different size.");
		for (int i = 0; i < NPE.size(); i++) {
			if (boolArray[i] == false && checkCoincidence(NPE.get(i).getId()).size() > 1) {
				return true;
			}
		}
		return false;
	}
	
	public Solution solve() throws SolvingException {
		List <ResultCouple> res = manualClone();
		List <Exam> NPE = s.getNonPlacedExams();
		
		boolean[] boolArray = new boolean[NPE.size()];
		for (int i = 0; i < NPE.size(); i++) {
			boolArray[i] = false;
		}
		
		//loop through boolean array
		while (hasFalse(boolArray) /*&& hasCoincidingExams(boolArray, NPE)*/) {
			int c = -1;
			
			if (hasCoincidingExams(boolArray, NPE)) {
				c = findFalseCoinciding(boolArray, NPE);
			} else {
				c = findFalse(boolArray);
			}
			
			int examId = NPE.get(c).getId();
			List<Integer> cExams = checkCoincidence(examId);

			if (cExams.size() >1){
				int periodId = getAvailablePeriod(cExams, res);
				List<Integer> rooms = findSuitable(cExams, periodId);
				
				for (int i = 0; i < cExams.size(); i++) {
					int currentExam = cExams.get(i);
					List<ResultCouple> resForPeriod = s.getResultsForPeriod(periodId, res);
					for (int j = 0; j < resForPeriod.size(); j++) {
						if (resForPeriod.get(j).getRoom().getId() == rooms.get(i)) {
							System.out.println("currentExam" + currentExam);
							resForPeriod.get(j).addExam(currentExam);
							//int indexNPE = NPE.get(index)
							//System.out.println("index is : " + getIndex(NPE, currentExam)+ "place is : " );
							System.out.println("period is : " + periodId + " room is " + rooms.get(i));
							boolArray[getIndex(NPE, currentExam)] = true;
							updateValidPeriods(currentExam, periodId);
						}
					}
				}
			}
			else{
				int periodId = getAvailablePeriod(cExams.get(0), res);
				ArrayList<Integer> roomList = (ArrayList<Integer>) findSuitable(cExams.get(0), periodId, res);
				
				int room = roomList.get(0);
				for (int i = 0; i < cExams.size(); i++) {
					int currentExam = cExams.get(i);
					List<ResultCouple> resForPeriod = s.getResultsForPeriod(periodId, res);
					for (int j = 0; j < resForPeriod.size(); j++) {
						if (resForPeriod.get(j).getRoom().getId() == room) {
							System.out.println("currentExam" + currentExam);
							resForPeriod.get(j).addExam(currentExam);
							//int indexNPE = NPE.get(index)
							//System.out.println("index is : " + getIndex(NPE, currentExam)+ "place is : " );
							System.out.println("period is : " + periodId + " room is " + room);
							boolArray[getIndex(NPE, currentExam)] = true;
							updateValidPeriods(currentExam, periodId);
						}
					}
				}
									
			}
		}
			//place exams
			
		System.out.println("final res:" + res);
		s.setResult((ArrayList<ResultCouple>) res);
		return s;
	}
	
	

	private int getIndex(List<Exam> nPE, int currentExam) {
		for (int i = 0; i < nPE.size(); i++) {
			//System.out.println("getIndex of " + currentExam + " - checking NPE.get(" + i + ");");
			if (nPE.get(i).getId() == currentExam) {
				return i;
			}
		}
		return -1;
	}

	private int findFalseCoinciding(boolean[] boolArray, List<Exam> nPE) {
		for (int i = 0; i < nPE.size(); i++) {
			if (boolArray[i] == false && checkCoincidence(nPE.get(i).getId()).size() > 1) {
				return i;
			}
		}
		return -1;
	}
	
	private int findFalse(boolean[] boolArray) {
		for (int i = 0; i < boolArray.length; i++) {
			if (boolArray[i] == false)
				return i;
		}
		return -1;
	}

	private boolean hasFalse(boolean[] boolArray) {
		for (int i =0; i < boolArray.length; i++) {
			if (boolArray[i] == false)
				return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private void printNPE_listE_suitableRooms(List<Exam> NPE,
			List<Integer> listE, List<Integer> suitableRooms) {
		System.out.print("\nListE:\n\t");
		for (Integer ee : listE) {
			System.out.print(ee + "; ");
		}
		System.out.print("\nsuitable rooms:\n\t");
		for (Integer sr : suitableRooms) {
			System.out.print(sr + "; ");
		}
		System.out.println("\nnon placed exams:\n\t");
		for (Exam n : NPE) {
			System.out.print(n.getId() + "; ");
		}
	}
	
		public void updateValidPeriods(int examId, int periodId)
		{
			int [][] eP = s.getExamPeriodModif();
			int [][] coincidence = s.getExamCoincidence();
			for (int i = 1; i < s.getExamSession().getExams().size(); i++){
				if( coincidence [examId][i] == 0){
					eP [i][periodId] = 0;
				}
			}
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
			/*	
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
			/**/
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

						System.out.println(" 1st elem " +currentBis.getExamList().get(j).getId());
						System.out.println(" ########## id coin" +id);
						// check if id is present into list exam
						for(int k = 0; k < currentBis.getExamList().size();k++){
							// if id is found : present = true
							if (currentBis.getExamList().get(k).getId() == id) {
								System.out.println(" TROUVEEEEEEEEEEEEEEEEEEEEEEEEEEEEE " +id);
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
						if (coincidence[current.getExamList().get(i).getId()][j] == 0) 
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
	 * @return A list containing examId and whatever other exam coinciding with it.
	 */
	public List<Integer> checkCoincidence(int examId) {
		Exam exam = s.getExamSession().getExams().get(examId);
		List<Integer> res = new ArrayList<Integer>();
		res.add(examId);
		if (exam.hasPeriodHardConstraint(
				EPeriodHardConstraint.EXAM_COINCIDENCE)) {

			// loop through the specified exam's constraints
			for (PeriodHardConstraint currentConstraint :
				exam.getConstraints()) {
				if (currentConstraint.getConstraint() ==
						EPeriodHardConstraint.EXAM_COINCIDENCE) {
					if (!res.contains(currentConstraint.getE2Id()))
						res.add(currentConstraint.getE2Id());
				}
			}
		}
		return res;
	}
	
	public int getAvailablePeriod(int examId, List<ResultCouple> res) {
		int tmp = -1;
		int[][] eP = s.getExamPeriodModif();
		
		for (int i = 0; i< res.size();i++){
			int periodId = res.get(i).getPeriod().getId();
			if (canHost(examId, periodId, res) && (eP[examId][periodId] != 0)){
					tmp = periodId;
					break;
			}
		}
		return tmp;
	}
	
	public int getAvailablePeriod(List<Integer> coincidingExams, List<ResultCouple> res) {
		int tmp = -1;
		int [][] eP = s.getExamPeriodModif();
		boolean periodOk = true;
		
		for (int i = 0; i< res.size();i++){			
			int currentPeriodId = res.get(i).getPeriod().getId();
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
		//ArrayList<ResultCouple> res = (ArrayList<ResultCouple>) ((ArrayList<ResultCouple>) s.getResult()).clone();
		ArrayList<ResultCouple> res = manualClone();
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
					&& res.get(j).getRoom().getId() == findSuitable(e.get(count), periodId, res).get(0)){
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

	private ArrayList<ResultCouple> manualClone() {
		ArrayList<ResultCouple> res = new ArrayList<ResultCouple>();
		for (ResultCouple toClone : s.getResult()) {
			res.add(toClone.clone());
		}//manual cloning - lol
		return res;
	}
	
	private ArrayList<ResultCouple> manualClone(List<ResultCouple> resIn) {
		ArrayList<ResultCouple> res = new ArrayList<ResultCouple>();
		for (ResultCouple toClone : resIn) {
			res.add(toClone.clone());
		}//manual cloning - lol
		return res;
	}
	
	public boolean canHost(int examId, int periodId, List<ResultCouple> resIn) {
		ArrayList<ResultCouple> res = manualClone(resIn);
		boolean tmp = false;
		boolean exclusive;
		
		for (int i = 0; i< res.size();i++){
			// where the result period id = periodId
			if (res.get(i).getPeriod().getId() == periodId){
				
				// if there's no other exams
				if (res.get(i).getExamList().size() <= 1){
					int sizeE = 0;
					for (int k = 0; k < s.getExamSession().getExams().size(); k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeE = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					if ( sizeE <= res.get(i).getRoom().getSize()){
						return true;	
					}
				}
				// if there's 1 or more exams
				else {
					exclusive = false;
					// get the sum of all the exams size for this room & this period
					int cmp = 0;
				//System.out.println("miaaaaou exam id is " + examId);
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
	public List<Integer> findSuitable(int examId, int periodId, List<ResultCouple> res) {
		//note: prioritize rooms that are already in use.
		//List<ResultCouple> res = s.getResult();
		boolean exclusive;
		int sizeE = 0;
		List<Integer> tempList = new ArrayList<Integer>();
		
		// loop for res
		for(int i = 0; i< res.size() ; i++){
			int sizeExamList = res.get(i).getExamList().size();
			// check the period
			if (res.get(i).getPeriod().getId() == periodId){
		
				// if there's more than a single exam
				exclusive = false;
				if (sizeExamList > 0){
					int sizeCounter = 0;
					// get the sum of all the exams size for this room & period
					for (int j = 0 ; j < sizeExamList ; j++){
						sizeCounter += res.get(i).getExamList().get(j).getSize();
					}
					//get the capacity of the exam we want to add
					for (int k = 0; k < s.getExamSession().getExams().size();k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeE = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					// room exclusive or not 
					for (int k = 0; k< s.getExamSession().getRoomHardConstraints().get(k).getId(); k++){
						if (s.getExamSession().getRoomHardConstraints().get(k).getId() == examId)
							exclusive = true;
					}
					//if size of all the exams + size of our exam <= room capacity
					if (sizeCounter + sizeE <= res.get(i).getRoom().getSize() 
						&& !exclusive ){
						tempList.add(res.get(i).getRoom().getId());
					}
				}
			}
		}
		for (int i = 0; i< res.size(); i++){
			int sizeExamList = res.get(i).getExamList().size();
			// check the period
			if (res.get(i).getPeriod().getId() == periodId){
				if (sizeExamList == 0){
					//get capacity of the exam
					for (int k = 0; k < s.getExamSession().getExams().size();k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeE = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					if (sizeE <= res.get(i).getRoom().getSize())
						tempList.add(res.get(i).getRoom().getId());
				}
			}
		}
		
	return tempList;
	}
	
	public List<Integer> findSuitable(List<Integer> exams, int periodId) {
		List<Integer> list = new ArrayList<Integer>();
		
		ArrayList<Integer> e = (ArrayList<Integer>) ((ArrayList<Integer>) exams).clone();
		//Collections.copy(e, exams);
		ArrayList<ResultCouple> res = manualClone();
		//Collections.copy(res, s.getResult());
		int numberOfExams = exams.size();
		int index;
		int count = 0;
		Exam ex;
		
		while(count < numberOfExams){
			for(int j =0; j<res.size(); j++) {
				if (list.size() == e.size())
					break;
				int pId = res.get(j).getPeriod().getId();
				int rId = res.get(j).getRoom().getId();
				List<Integer> suitableId = findSuitable(e.get(count), periodId, res);
				
				for (int k = 0; k< suitableId.size();k++){
					if(pId == periodId && rId == suitableId.get(k)){
						index = j;
						//get the exam 
						ex = s.getExamSession().getExams().get(e.get(count));
						//set the exam
						int totalSize = 0;
						for (Exam exam : res.get(j).getExamList()) {
							totalSize += exam.getSize();
						}
						if (res.get(j).getRoom().getSize() >= totalSize + ex.getSize()) {
							res.get(index).getExamList().add(ex);
							list.add(suitableId.get(k));
							break;
						}
					}	
				}
				count++;
			}
		}
		return list;
	}
	
	
}