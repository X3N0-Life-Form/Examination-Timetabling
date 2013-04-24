package solve;

import java.util.ArrayList;
import java.util.List;

import parse.ExamSessionParser;
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
	
	public Solution solve() throws SolvingException {
		System.out.println("Solving hard constraints:");
		List <ResultCouple> res = Solving.manualClone(s.getResult());
		List <Exam> NPE = s.getNonPlacedExams();
		System.out.println("--Found " + NPE.size() + " non placed exams");
		
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
			System.out.println("----Processing exam " + examId);
			List<Integer> cExams = Solving.checkCoincidence(s, examId);
			System.out.println("----Found " + cExams.size() + " coinciding exams");
			int periodId = Solving.getAvailablePeriod(s, cExams, res);
			///////////////////////////////////////
			if (examId == 411) {
				for (int jj = 0; jj < s.getExamSession().getPeriods().size(); jj++) {
					System.out.println("period " + jj + "; 411: " + s.getExamPeriodModif()[411][jj]
							+ "; 418: " + s.getExamPeriodModif()[418][jj]);
				}
				/*
				for (ResultCouple hjvo : res) {
					if (hjvo.getPeriod().getDuration() >= 180) {
						System.out.println(hjvo);
					}
				}*/
				System.out.println("411:" + Solving.findSuitable(s, 411, periodId, res));
				System.out.println("418:" + Solving.findSuitable(s, 418, periodId, res));
			}
			////////////////////////////////////////
			if (periodId == -1)
				throw new SolvingException("Incorrect period id: " + periodId); //see Solving.MAX_GET_AVAILABLE_PERIOD
			System.out.println("----Found " + periodId + " periods capable of hosting these exams");
		//	for (Integer periodId : periodIds) {
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
	/**/
	
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

	private int findFalseCoinciding(boolean[] boolArray, List<Exam> nPE) {
		for (int i = 0; i < nPE.size(); i++) {
			if (boolArray[i] == false && Solving.checkCoincidence(s, nPE.get(i).getId()).size() > 1) {
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
	
}