package util;

import java.util.ArrayList;
import java.util.List;

import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.PeriodHardConstraint;
import struct.ResultCouple;
import struct.Solution;

public class Solving {
	
	/**
	 * Manually clones a list and its contents.
	 * @param resIn
	 * @return A list containing clones of resIn's elements.
	 */
	public static ArrayList<ResultCouple> manualClone(List<ResultCouple> resIn) {
		ArrayList<ResultCouple> res = new ArrayList<ResultCouple>();
		for (ResultCouple toClone : resIn) {
			res.add(toClone.clone());
		}//manual cloning - lol
		return res;
	}
	
	public static boolean canHost(Solution s, int examId, int periodId, List<ResultCouple> resIn) {
		ArrayList<ResultCouple> res = manualClone(resIn);
		boolean tmp = false;
		boolean exclusive;

		for (int i = 0; i< res.size();i++){
			// where the result period id = periodId
			if (res.get(i).getPeriod().getId() == periodId){

				// if there's no other exams
				if (res.get(i).getExamList().size() == 0){
					int sizeE = 0;
					for (int k = 0; k < s.getExamSession().getExams().size(); k++){
						if (s.getExamSession().getExams().get(k).getId() == examId){
							sizeE = s.getExamSession().getExams().get(k).getSize();
							break;
						}
					}
					if ( sizeE <= res.get(i).getRoom().getSize()){
						tmp = true;
						break;
					}
				}
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
	 * Finds a suitable room for the specified exam in the specified period.
	 * @param examId
	 * @param periodId
	 * @return A suitable room id or -1.
	 */
	public static List<Integer> findSuitable(Solution s, int examId, int periodId, List<ResultCouple> res) {
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
		ArrayList<ResultCouple> res = manualClone(resIn);
		
		int numberOfExams = exams.size();
		int index;
		int count = 0;
		Exam ex;
		
		while(count < numberOfExams){
			for(int j =0; j<res.size(); j++) {
				if (list.size() == examsClone.size())
					break;
				int pId = res.get(j).getPeriod().getId();
				int rId = res.get(j).getRoom().getId();
				List<Integer> suitableId = findSuitable(s, examsClone.get(count), periodId, res);
				if (suitableId.size() == 0){
					//System.out.println("and the motherfuckin' period is : "+ periodId);
					//System.out.println("error for this id " + examsClone.get(count));
					break;
				}
				for (int k = 0; k< suitableId.size();k++){
					if(pId == periodId && rId == suitableId.get(k)){
						index = j;
						//get the exam 
						ex = s.getExamSession().getExams().get(examsClone.get(count));
						//set the exam
						int totalSize = 0;
						for (Exam exam : res.get(j).getExamList()) {
							totalSize += exam.getSize();
						}
						if (res.get(j).getRoom().getSize() >= totalSize + ex.getSize()) {
							res.get(index).getExamList().add(ex);
							list.add(suitableId.get(k));
							count++;
							System.out.println(" add : " + ex.getId());
							break;
						}
					}	
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
					if (!res.contains(currentConstraint.getE2Id()))
						res.add(currentConstraint.getE2Id());
				}
			}
		}
		return res;
	}
	
	public static List<Integer> getAvailablePeriod(Solution s, int examId, List<ResultCouple> res) {
		ArrayList<Integer> availablePeriods = new ArrayList<Integer>();
		int[][] eP = s.getExamPeriodModif();
		
		for (int i = 0; i< res.size();i++){
			int periodId = res.get(i).getPeriod().getId();
			if (canHost(s, examId, periodId, res) && (eP[examId][periodId] != 0)){
					availablePeriods.add(periodId);
			}
		
		}
		return availablePeriods;
	}
	
	public static int getAvailablePeriod(Solution s, List<Integer> coincidingExams, List<ResultCouple> res) {
		int availablePeriods = -1;
		int [][] eP = s.getExamPeriodModif();
		boolean isHereForExam = false;
		boolean isHere;
		
		ArrayList<Integer> firstExamPeriods = (ArrayList<Integer>) getAvailablePeriod(s, coincidingExams.get(0), res);
		for (int i = 0 ; i < firstExamPeriods.size(); i++){
			int currentFirstExamPeriod = firstExamPeriods.get(i);
			isHere = true;
			for (int j = 0 ; j < coincidingExams.size(); j++){
				isHereForExam = false;
				ArrayList<Integer> currentExamPeriods = (ArrayList<Integer>) getAvailablePeriod(s, coincidingExams.get(j), res);
				for (int k = 0; k< currentExamPeriods.size(); k++){
					if (currentExamPeriods.get(k) == currentFirstExamPeriod){
						isHereForExam = true;
					}
				}
				if (isHereForExam == false){
					isHere = false;
				}
			}
			if (isHere){
				// ajouter condition ????
				if (canHost(s, coincidingExams, currentFirstExamPeriod, res)){
					availablePeriods = currentFirstExamPeriod;
				}
			}
		}	
		return availablePeriods;
	}
	
	/**
	 * Can this period host the specified list of exams.
	 * @param exams
	 * @param periodId
	 * @return
	 */
	public static boolean canHost(Solution s, List<Integer> exams, int periodId, List<ResultCouple> resIn) {
		boolean tmp = false;
		@SuppressWarnings("unchecked")
		ArrayList<Integer> e = (ArrayList<Integer>) ((ArrayList<Integer>) exams).clone();
		ArrayList<ResultCouple> res = manualClone(resIn);
		
		int numberOfExams = exams.size();
		int index;
		Exam ex;				
		
		int suitablesFound = 0;
		
		for (int i = 0; i< numberOfExams; i++){
			if (!canHost(s, exams.get(i), periodId, res)){
				System.out.println(" ## CAN HOST " + exams.get(i) + " = FALSE");
				return false;
			}
		}
		
		for (int i = 0; i< numberOfExams; i++){
			if (!canHost(s, e.get(i), periodId, res)){
				System.out.println("RETURN FALSE FOR EXAM "+ e.get(i));
				return false;
			}
			if (canHost(s, e.get(i), periodId, res)){
				System.out.println("CAN HOST "+ e.get(i) + " FOR PERIOD " + periodId);
				for(int j =0; j<res.size(); j++) {
					List<Integer> suitables = findSuitable(s, e.get(i), periodId, res);
					for (int k=0; k < suitables.size(); k++) {
						if(res.get(j).getPeriod().getId() == periodId
						&& res.get(j).getRoom().getId() == suitables.get(k)){
							index = j;
							//get the exam 
							ex = s.getExamSession().getExams().get(e.get(i));
							/*int sizeE = ex.getSize();
							//set the exam
							int cmp = 0;
							for (int l = 0; l< res.get(j).getExamList().size();l++){
								cmp += res.get(j).getExamList().get(l).getSize();
							}
							if ((sizeE + cmp) )*/
							res.get(index).getExamList().add(ex);
							suitablesFound++;
							if(e.size() == suitablesFound) {
								System.out.println(" -------------- RETURN TRUE !");
								return true;
							}
							break;
						}
					}
				}
			}
		}
		
		System.out.println(("---------------------RETURN " + tmp));
		return tmp;
	}
}
