package solve;

import java.util.ArrayList;
import java.util.List;

import parse.ExamSessionParser;
import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.ExamSession;
import struct.ResultCouple;
import struct.Solution;
import util.Solving;

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
			if (boolArray[i] == false && Solving.checkCoincidence(s, NPE.get(i).getId()).size() > 1) {
				return true;
			}
		}
		return false;
	}


	public boolean hasCoincidingExamsBefore(boolean[] boolArray,
			List<Exam> nPE, List<Exam> beforeExams) throws SolvingException {
		if (boolArray.length != nPE.size())
			throw new SolvingException("The specified list and array have different size.");
		for (int i = 0; i < nPE.size(); i++) {
			if (boolArray[i] == false && Solving.checkCoincidence(s, nPE.get(i).getId()).size() > 1
					&& beforeExams.contains(nPE.get(i).getId())) {
				return true;
			}
		}
		return false;
	}

	public Solution solve() throws SolvingException {
		System.out.println("Solving hard constraints:");
		List <ResultCouple> res = Solving.manualClone(s.getResult());
		List <Exam> NPE = s.getNonPlacedExams();
		System.out.println("--Found " + NPE.size() + " non placed exams");

		List<Exam> beforeExams = s.getBeforeExams();
		System.out.println("--Found " + beforeExams.size() + " exams with AFTER constraint(s)");
	
		boolean[] boolArray = new boolean[NPE.size()];
		for (int i = 0; i < NPE.size(); i++) {
			boolArray[i] = false;
		}

		for (int k = 0; k< boolArray.length; k++){
			if (Solving.checkSizeExam( s, NPE.get(k).getId(), res)){

				System.out.println(" Found one : " + NPE.get(k).getId());
				int examId = NPE.get(k).getId();
				List <Integer> examList = Solving.checkCoincidence(s, examId);
				int periodId = Solving.getAvailablePeriod(s, examList, res);

				if (periodId == -1)
					throw new SolvingException("Incorrect period id: " + periodId); //see Solving.MAX_GET_AVAILABLE_PERIOD
				System.out.println("----Found that period " + periodId + " is capable of hosting these exams");
				System.out.println("------Processing period " + periodId);
				List<Integer> rooms = Solving.findSuitable(s, examList, periodId, res);
				System.out.println("------Found " + rooms.size() + " suitable rooms for " + examList.size() + " exams");
				System.out.println("------Assigning exams to result couples");
				for (int i = 0; i < examList.size(); i++) {
					int currentExam = examList.get(i);
					System.out.println("--------Processing exam " + currentExam);
					List<ResultCouple> resForPeriod = s.getResultsForPeriod(periodId, res);
					System.out.println("--------Found " + resForPeriod.size() + " result couples matching this period");
					for (int j = 0; j < resForPeriod.size(); j++) {
						int currentRoomId = resForPeriod.get(j).getRoom().getId();
						System.out.println("----------Processing room " + currentRoomId);
						if (currentRoomId == rooms.get(i)) {
							System.out.println("----------Room " + currentRoomId + " was deemed suitable for exam " + currentExam);
							resForPeriod.get(j).addExam(currentExam);
							boolArray[getIndex(NPE, currentExam)] = true;
							updateValidPeriods(currentExam, periodId);

						} else {
							System.out.println("----------Room " + currentRoomId + " was rejected");
						}
					}
				}
			}
		}
		
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		System.out.println("###############################");
		
		int count = 0;
		//loop through boolean array
		while (hasFalse(boolArray)) {
			int c = -1;

			/*
			 * priority:
			 * AFTER w/ EXAM_COINCIDENCE
			 * regular AFTER
			 * EXAM_COINCIDENCE
			 * the rest
			 */
			boolean isBeforeExam = false;
			if (beforeExams.size() > count) {
				if (hasCoincidingExamsBefore(boolArray, NPE, beforeExams)) {
					c = findFalseCoincidingBefore(boolArray, NPE, beforeExams);
				} else {
					c = findFalseBefore(boolArray, NPE, beforeExams);
					System.out.println("c is : " + NPE.get(c).getId());
				}
				isBeforeExam = true;
			} else if (hasCoincidingExams(boolArray, NPE)) {
				c = findFalseCoinciding(boolArray, NPE);
			} else {
				c = findFalse(boolArray);
			}
			if (boolArray[c]){
				System.out.println("already placed");
				continue;
			}
			int examId = NPE.get(c).getId();
			System.out.println("----Processing exam " + examId);
			List<Integer> cExams = Solving.checkCoincidence(s, examId);
			System.out.println("----Found " + cExams.size() + " coinciding exams");
			int periodId = Solving.getAvailablePeriod(s, cExams, res);
		
			if (periodId == -1)
				throw new SolvingException("Incorrect period id: " + periodId); //see Solving.MAX_GET_AVAILABLE_PERIOD
			System.out.println("----Found that period " + periodId + " is capable of hosting these exams");
			System.out.println("------Processing period " + periodId);
			List<Integer> rooms = Solving.findSuitable(s, cExams, periodId, res);
			System.out.println("------Found " + rooms.size() + " suitable rooms for " + cExams.size() + " exams");
			System.out.println("------Assigning exams to result couples");
			for (int i = 0; i < cExams.size(); i++) {
				int currentExam = cExams.get(i);
				System.out.println("--------Processing exam " + currentExam);
				List<ResultCouple> resForPeriod = s.getResultsForPeriod(periodId, res);
				System.out.println("--------Found " + resForPeriod.size() + " result couples matching this period");
				for (int j = 0; j < resForPeriod.size(); j++) {
					int currentRoomId = resForPeriod.get(j).getRoom().getId();
					System.out.println("----------Processing room " + currentRoomId);
					if (currentRoomId == rooms.get(i)) {
						System.out.println("----------Room " + currentRoomId + " was deemed suitable for exam " + currentExam);
						resForPeriod.get(j).addExam(currentExam);
						boolArray[getIndex(NPE, currentExam)] = true;
						updateValidPeriods(currentExam, periodId);
						count++;
						break;
					} else {
						System.out.println("----------Room " + currentRoomId + " was rejected");
					}
				}
			}
		}

		//place exams (for real)	
		System.out.println("--Final res:" + res);
		s.setResult((ArrayList<ResultCouple>) res);
		return s;
	}



	/**
	 * 
	 * @param nPE
	 * @param currentExam
	 * @return index of currentExam within the supplied List.
	 */
	private int getIndex(List<Exam> nPE, int currentExam) {
		for (int i = 0; i < nPE.size(); i++) {
			if (nPE.get(i).getId() == currentExam) {
				return i;
			}
		}
		return -1;
	}


	private int findFalseCoincidingBefore(boolean[] boolArray, List<Exam> nPE,
			List<Exam> beforeExams) {
		for (int i = 0; i < nPE.size(); i++) {
			//same check as findFalseCoinciding, but return i only if it is in beforeExams
			System.out.println(s.getExamSession().getExams().get(i));
			System.out.println(boolArray[i]);
			System.out.println(beforeExams.contains(s.getExamSession().getExams().get(i)));
			if (boolArray[i] == false && Solving.checkCoincidence(s, nPE.get(i).getId()).size() > 1
					&& beforeExams.contains(s.getExamSession().getExams().get(nPE.get(i).getId()))) {
				return i;
			}
		}
		return -1;
	}

	private int findFalseCoinciding(boolean[] boolArray, List<Exam> nPE) {
		for (int i = 0; i < nPE.size(); i++) {
			if (boolArray[i] == false && Solving.checkCoincidence(s, nPE.get(i).getId()).size() > 1) {
				return i;
			}
		}
		return -1;
	}

	private int findFalseBefore(boolean[] boolArray,List<Exam> nPE,  List<Exam> beforeExams) {
		
		for (int i = 0; i < beforeExams.size(); i++) {
			//same check as findFalse, but return i only if it is in beforeExams
			int currentExam = beforeExams.get(i).getId();
			System.out.println("current exam in beforeExams : " + currentExam);
			int index = getIndex(nPE, currentExam);
			if (boolArray[index] == false
					&& beforeExams.contains(s.getExamSession().getExams().get(nPE.get(index).getId()))) {
				System.out.println("i is returned ");
				return index;
			}
		}
		return -1;
	/*	for (int i = 0; i < boolArray.length; i++) {
			//same check as findFalse, but return i only if it is in beforeExams
			System.out.println("current exam in findFalseBefore : " + nPE.get(i).getId());
			if (boolArray[i] == false
					&& beforeExams.contains(s.getExamSession().getExams().get(nPE.get(i).getId()))) {
				System.out.println("i is returned ");
				return i;
			}
		}
		return -1; */
		
		
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
		Exam currentExam = null;
		int idAfterExam = -1;
		
		// get the exam
		for (int i = 0 ; i< s.getExamSession().getExams().size() ; i++){
			if (s.getExamSession().getExams().get(i).getId() == examId){
				currentExam = s.getExamSession().getExams().get(i);
				break;
			}
		}
		
		// check after & before
		if (currentExam.hasPeriodHardConstraint(EPeriodHardConstraint.AFTER)){
			System.out.println("current exam " + examId);
			for (int j = 0 ; j < currentExam.getConstraints().size();j++){
				if (currentExam.getConstraints().get(j).getConstraint() == EPeriodHardConstraint.AFTER){
					if (currentExam.getConstraints().get(j).getE2Id() == examId){
						idAfterExam = currentExam.getConstraints().get(j).getE1Id();
						for (int k = 0 ; k <= periodId ; k++){
							eP[idAfterExam][k]= 0;
							System.out.println("(after) eP updated for exam "+idAfterExam + " & period " + k);
						}
					}
					else if (currentExam.getConstraints().get(j).getE1Id() == examId ){
						int idBeforeExam = currentExam.getConstraints().get(j).getE2Id();
						for (int k = s.getExamSession().getPeriods().size()-1 ; k >= periodId; k--){
							eP[idBeforeExam][k] = 0;
							System.out.println("(before) eP updated for exam "+idBeforeExam + " & period " + k);
						}
					}
				}
			}
		}
		
		// check exclusion
		for (int i = 0; i < s.getExamSession().getExams().size(); i++){
			if( coincidence [examId][i] == 0 || coincidence [i][examId] == 0){
				eP [i][periodId] = 0;
			}
		}
	}

}