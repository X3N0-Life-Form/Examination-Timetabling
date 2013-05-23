package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.PeriodHardConstraint;
import struct.ResultCouple;
import struct.Solution;

/**
 * Contains various methods dedicated to solving hard constraints.
 * @author Adrien Droguet - Sara Tari
 *
 */
public class Solving {
	
	/**
	 * Number of available periods the getAvailablePeriod() method looks for.
	 */
	protected static int MAX_GET_AVAILABLE_PERIOD = 9;

	public static boolean canHost(Solution s, int examId, int periodId, List<ResultCouple> resIn) {
		ArrayList<ResultCouple> res = OurCollections.manualClone(resIn);
		boolean canHost = false;
		boolean isExclusive;
		
		for (int i = 0; i< res.size();i++){
			// where the result period id = periodId
			if (res.get(i).getPeriod().getId() == periodId){
				
				// if there's no other exams
				if (res.get(i).getExamList().size() == 0){
					int sizeExam = 0;
					for (int k = 0; k < s.getExamSession().getExams().size(); k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeExam = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					//exam size < room size --> good to go
					if (sizeExam <= res.get(i).getRoom().getSize()){
						canHost = true;
						break;
					}
				}
				// if there's 1 or more exams
				else {
					isExclusive = false;
					// get the sum of all the exams size for this room & this period
					int examSizeSum = 0;
					for (int j = 0; j< res.get(i).getExamList().size(); j++){
						examSizeSum += res.get(i).getExamList().get(j).getSize();
					}
					//get the capacity of the room
					int sizeExam = 0;
					for (int k = 0; k < s.getExamSession().getExams().size(); k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeExam = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					//check if room exclusive or not 
					for (int l = 0; l< s.getExamSession().getRoomHardConstraints().size(); l++){
						if (s.getExamSession().getRoomHardConstraints().get(l).getId() == examId)
							isExclusive = true;
					}
					//if the size of all the exams + size of our exam <= room capacity --=> good to go
					if (examSizeSum + sizeExam <= res.get(i).getRoom().getSize() && !isExclusive ) {
						canHost = true;						
					}
				}
			}
		}
		return canHost;
	}
	
	/**
	 * Can this period host the specified list of exams.
	 * @param exams
	 * @param periodId
	 * @return True is the period can host the exams.
	 */
	public static boolean canHost(Solution s, List<Integer> exams, int periodId, List<ResultCouple> resIn) {
		boolean tmp = false;
		@SuppressWarnings("unchecked")
		ArrayList<Integer> e = (ArrayList<Integer>) ((ArrayList<Integer>) exams).clone();
		ArrayList<ResultCouple> res = OurCollections.manualClone(resIn);
		
		int numberOfExams = exams.size();
		int index;
		Exam ex;				
		
		int suitablesFound = 0;
		
		for (int i = 0; i< numberOfExams; i++){
			if (!canHost(s, exams.get(i), periodId, res)){
				return false;
			}
		}
		
		for (int i = 0; i< numberOfExams; i++){
			if (!canHost(s, e.get(i), periodId, res)){
				return false;
			}
			if (canHost(s, e.get(i), periodId, res)){
				for(int j =0; j<res.size(); j++) {
					List<Integer> suitables = findSuitable(s, e.get(i), periodId, res);
					for (int k=0; k < suitables.size(); k++) {
						if(res.get(j).getPeriod().getId() == periodId
						&& res.get(j).getRoom().getId() == suitables.get(k)){
							index = j;
							//get the exam 
							ex = s.getExamSession().getExams().get(e.get(i));
							res.get(index).getExamList().add(ex);
							suitablesFound++;
							
							if(e.size() == suitablesFound) {
								return true;
							}
							break;
						}
					}
				}
			}
		}
		
		return tmp;
	}
	
	public static boolean checkSizeExam( Solution s, int examId, List<ResultCouple> res){
		int examSize = -1;
		
			int count = 0;
			for (int i = 0 ; i< s.getExamSession().getExams().size(); i++){
				if (s.getExamSession().getExams().get(i).getId() == examId){
					Exam currentExam = s.getExamSession().getExams().get(i);
					examSize = currentExam.getSize();
					boolean isAfter = false;
					for (int j = 0 ; j < currentExam.getConstraints().size() ; j++){
						if (currentExam.getConstraints().get(j).getConstraint() == EPeriodHardConstraint.AFTER
								&& currentExam.getConstraints().get(j).getE1Id() == currentExam.getId()){							
								isAfter = true;
						}
					}
					
					if (checkCoincidence(s, examId).size() != 1 || isAfter ){
						return false;
					}
					break;
				}
			}
			
			for (int j = 0 ; j < s.getExamSession().getRooms().size() ; j++){
				int roomSize = s.getExamSession().getRooms().get(j).getSize();
				if (examSize <= roomSize){
					count++;
				}			
			}
			
			if (count == 1){
				return true;
			}
		
		return false;
	}
	
	/**
	 * 
	 * @param s
	 * @param examId
	 * @param resIn
	 * @param returnCouple A list of examId returned by the method
	 * @param returnList 
	 * @return True if a swap operation can be done, as well as a
	 *  List of exams satisfying the swap prerequisites.
	 */
	public static boolean canSwap(Solution s, int examId, List<ResultCouple> resIn, ResultCouple returnCouple, List<Integer> returnList){
		List<ResultCouple> res = OurCollections.manualClone(resIn);
		int sizeExam = -1;
		List<Exam> listExams = new ArrayList<Exam>(); 
		List<Integer> currentList = new ArrayList<Integer>();
		
		// get the size of the current exam
		for (int i = 0; i < s.getExamSession().getExams().size(); i++){
			if ( s.getExamSession().getExams().get(i).getId() == examId){
				sizeExam = s.getExamSession().getExams().get(i).getSize();
				break;
			}
		}

		// loop
		for (int i = 0 ; i < res.size(); i++){
			// size of the current room
			int roomSize = res.get(i).getRoom().getSize();
			int currentRoomOccupation = 0;
			
			for (int r = 0 ; r < res.get(i).getExamList().size(); r++){
				currentRoomOccupation += res.get(i).getExamList().get(r).getSize();
			}
			
			int periodId = res.get(i).getPeriod().getId();
			System.out.println("period : " + periodId + "roomSize :" +roomSize);
			// if period is OK & size is OK
			if (sizeExam <= roomSize && s.getExamPeriodModif()[examId][periodId]!=0){
				listExams = OurCollections.manualCloneExam(res.get(i).getExamList());
				int currentSum = 0;
				currentList = new ArrayList<Integer>();
				for (int j = 0; j< listExams.size() ; j++){
					currentSum += listExams.get(j).getSize();
					currentList.add(listExams.get(j).getId());
					System.out.println(" currentSum " + currentSum);
					System.out.println(" exams : " + currentList);
					
					if (currentRoomOccupation - currentSum + sizeExam <= roomSize) {
						res.removeAll(currentList);
						System.out.println(" canHost " + canHost(s, currentList, periodId, res));
						if (canHost (s, currentList, periodId, res)) {
							returnCouple = resIn.get(i);
							returnList = currentList;
							return true;
						}
					}
				}
			}
		}
		
		
		return false;
	}
	
	/**
	 * Finds a suitable room for the specified exam in the specified period.
	 * @param examId
	 * @param periodId
	 * @return A List of suitable room id.
	 */
	public static List<Integer> findSuitable(Solution s, int examId, int periodId, List<ResultCouple> res) {
		//note: prioritize rooms that are already in use.
		//List<ResultCouple> res = s.getResult();
		boolean exclusive;
		int sizeE = 0;
		List<Integer> tempList = new ArrayList<Integer>();

		// loop for res
		for(int i = 0; i< res.size() ; i++){
			// check the period
			if (res.get(i).getPeriod().getId() == periodId){
				int sizeExamList = res.get(i).getExamList().size();
				// if there's more than a single exam
				exclusive = false;
				
				//if the exam already present is room exclusive --> look for another
				if (sizeExamList == 1) {
					Exam loneExam = res.get(i).getExamList().get(0);
					if (loneExam.getRoomHardConstraint() != null) {
						continue;
					}
				}
				
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
					
					//does that exam even fit in that room?
					if (sizeE > res.get(i).getRoom().getSize())
						continue;

					// is our exam room exclusive? 
					for (int k = 0; k< s.getExamSession().getRoomHardConstraints().size(); k++){
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
	
	
	public static List<Integer> findSuitable(
			Solution s, List<Integer> exams, int periodId, List<ResultCouple> resIn) {
		List<Integer> list = new ArrayList<Integer>();
		@SuppressWarnings("unchecked")
		ArrayList<Integer> examsClone = (ArrayList<Integer>) ((ArrayList<Integer>) exams).clone();
		ArrayList<ResultCouple> res = OurCollections.manualClone(resIn);
		
		ArrayList<Integer> isAdd = new ArrayList<Integer>();
		
		//int numberOfExams = exams.size();
		int index = -1;
		//int count = 0;
		Exam ex;
		Map<Integer, Integer> mapOfDoom = new HashMap<Integer, Integer>(); //id exam, id room
		
		// exams
		for (int i = 0 ; i < exams.size();i++){
			ArrayList<Integer> suitable = (ArrayList<Integer>) findSuitable (s, exams.get(i), periodId, res);
		
			//if the current exam has only one room
			if (findSuitable (s, exams.get(i), periodId, res).size() == 1){
				for ( int j = 0; j< res.size(); j++){
					if (res.get(j).getPeriod().getId() == periodId 
					&& res.get(j).getRoom().getId() == suitable.get(0) ){
						index = j;						
					
						// get variables for size's verification
						ex = s.getExamSession().getExams().get(examsClone.get(i));
						int totalSize = 0;
						
						for (Exam exam : res.get(index).getExamList()) {
							totalSize += exam.getSize();
						}
						// verification
						if (res.get(index).getRoom().getSize() >= totalSize + ex.getSize()) {
							res.get(index).getExamList().add(ex);
							mapOfDoom.put(exams.get(i), suitable.get(0));
							//list.add(suitable.get(0));
							isAdd.add(exams.get(i));
						}
					}	
				}				
			}
		}
		boolean alreadyAdded = false;
		
		for (int i = 0; i< exams.size();i++){
			alreadyAdded = false;
			for (int j = 0; j< isAdd.size();j++){
				if (isAdd.get(j) == exams.get(i)){
					alreadyAdded = true;
				}
			}
			if (alreadyAdded){
				//System.out.println(mapOfDoom.);
				list.add(mapOfDoom.get(exams.get(i)));
			}
			else if (!alreadyAdded){
				ArrayList<Integer> suitable = (ArrayList<Integer>) findSuitable(s, exams.get(i), periodId, res);
				boolean exitK = false;
				for (int k = 0; k< suitable.size();k++){
					for ( int j = 0; j< res.size(); j++){
						if (res.get(j).getPeriod().getId() == periodId 
						&& res.get(j).getRoom().getId() == suitable.get(0/*k*/) ){
							index = j;						
						
							// get variables for size's verification
							ex = s.getExamSession().getExams().get(examsClone.get(i));
							int totalSize = 0;
							
							for (Exam exam : res.get(index).getExamList()) {
								totalSize += exam.getSize();
							}
							// verification
							if (res.get(index).getRoom().getSize() >= totalSize + ex.getSize()) {
								res.get(index).getExamList().add(ex);
								list.add(suitable.get(0));
								exitK = true;
							}
						}
					}
					if (exitK)
						break;
				}		
			}
		}
		return list;
	}

	/**
	 * Check for exams coinciding with the specified id.
	 * @param examId
	 * @return A list containing examId and whatever other exam coinciding with it.
	 */
	public static List<Integer> checkCoincidence(Solution s, int examId) {
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
					if (examId == currentConstraint.getE1Id()){
						if (!res.contains(currentConstraint.getE2Id()))
							res.add(currentConstraint.getE2Id());
					}
					else if (examId == currentConstraint.getE2Id()){
						if (!res.contains(currentConstraint.getE1Id()))
							res.add(currentConstraint.getE1Id());
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param s
	 * @param examId
	 * @param constraint
	 * @return True if the exam has the specified constraint.
	 */
	public static boolean hasConstraint(Solution s, int examId, EPeriodHardConstraint constraint) {
		Exam exam = s.getExamSession().getExams().get(examId);
		List<PeriodHardConstraint> constraints = exam.getConstraints();
		
		for (PeriodHardConstraint currentConstraint : constraints) {
			if (currentConstraint.getConstraint() == constraint) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param s
	 * @param examId
	 * @param res
	 * @param periodId
	 * @return True if the period is deemed available for the specified exam.
	 */
	public static boolean isPeriodAvailable(Solution s, int examId, List<ResultCouple> res, int periodId) {
		//copied from getAvailablePeriod
		int[][] eP = s.getExamPeriodModif();
		if (hasConstraint(s, examId, EPeriodHardConstraint.AFTER) && !checkBeforeAfter(s, examId, periodId, res)) {
			return false;
		}
		
		if (canHost(s, examId, periodId, res) && (eP[examId][periodId] != 0)){
				return true;
		}
		return false;
	}
	
	/**
	 * Finds periods capable of hosting the specified exam.
	 * @param s
	 * @param examId
	 * @param res
	 * @return A List of periods (empty if no period was found).
	 */
	public static List<Integer> getAvailablePeriod(Solution s, int examId, List<ResultCouple> res) {
		ArrayList<Integer> availablePeriods = new ArrayList<Integer>();
		int[][] eP = s.getExamPeriodModif();
		boolean hasAfter = hasConstraint(s, examId, EPeriodHardConstraint.AFTER);
		
		for (int i = 0; i < s.getExamSession().getPeriods().size(); i++){
			int periodId = s.getExamSession().getPeriods().get(i).getId();
	
			if (hasAfter && !checkBeforeAfter(s, examId, periodId, res)) {
				continue;
			}
			
			if (canHost(s, examId, periodId, res) && (eP[examId][periodId] != 0)){
					availablePeriods.add(periodId);
			}
			
			if (availablePeriods.size() >= MAX_GET_AVAILABLE_PERIOD){
				break;
			}
		}
		
		return availablePeriods;
	}

	public static int getAvailablePeriod(Solution s, List<Integer> coincidingExams, List<ResultCouple> res) {
		int availablePeriods = -1;
		//int [][] eP = s.getExamPeriodModif();
		boolean isHereForExam = false;
		boolean isHere;
		
		ArrayList<Integer> firstExamPeriods = (ArrayList<Integer>) getAvailablePeriod(s, coincidingExams.get(0), res);
		
		for (int i = 0 ; i < firstExamPeriods.size(); i++){
			int currentFirstExamPeriod = firstExamPeriods.get(i);
			isHere = true;
			for (int j = 1 ; j < coincidingExams.size(); j++){
				isHereForExam = false;
				ArrayList<Integer> currentExamPeriods = (ArrayList<Integer>) getAvailablePeriod(s, coincidingExams.get(j), res);
				for (int k = 0; k< currentExamPeriods.size(); k++){
					if (currentExamPeriods.get(k) == currentFirstExamPeriod){
						isHereForExam = true;
					}
				}
				if (isHereForExam == false){
					isHere = false;
					break;
				}
			}
			if (isHere){
				if (canHost(s, coincidingExams, currentFirstExamPeriod, res)){
					availablePeriods = currentFirstExamPeriod;
				}
			}
		}	
		return availablePeriods;
	}
	
	/**
	 * Checks whether an exam can be placed into a specific period according to
	 * AFTER constraints.
	 * @param s
	 * @param examId
	 * @param periodId
	 * @param resIn Remains unchanged.
	 * @return True if the exam can be placed into the period, false if it
	 * must be before or after at least one other exam.
	 */
	public static boolean checkBeforeAfter(Solution s, int examId,
			int periodId, List<ResultCouple> resIn) {
		List<ResultCouple> res = s.getResultsForPeriod(periodId, resIn);
		Exam exam = s.getExamSession().getExams().get(examId);
		List<PeriodHardConstraint> afterList = exam.getConstraints();
		
		for (ResultCouple rc : res) {
			List<Exam> examList = rc.getExamList();
			for (int i = 0; i < examList.size(); i++) {
				Exam currentExam = examList.get(i);
				for (PeriodHardConstraint after : afterList) {
					if (after.getConstraint() == EPeriodHardConstraint.AFTER) {
						if (currentExam.getId() == after.getE1Id()
								|| currentExam.getId() == after.getE2Id()) {
							return false;
						}
					}
				}
			}
		}
		
		//didn't find anything wrong during the loop --> good to go
		return true;
	}
}
