package util;

import java.util.ArrayList;
import java.util.List;

import solve.Feedback;
import solve.HardConstraintsValidator;
import solve.SolvingException;
import struct.EMoveType;
import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.Move;
import struct.ResultCouple;
import struct.Solution;

/**
 * Contains various methods dedicated to moving exams within a Solution.
 * @author Adrien Droguet - Sara Tari
 *
 */
public class Moving {

	/**
	 * Moves an exam without hard constraints.
	 * @param examId
	 * @param s
	 * @param targetPeriodId
	 * @param targetRoomId
	 * @throws SolvingException 
	 */
	public static Move movingSingleExam(int examId, Solution s, int targetPeriodId, int targetRoomId) throws SolvingException{
		Exam exam = s.getExamSession().getExams().get(examId);
		ResultCouple target = null;
		ResultCouple origin = null;
		/////
		Feedback f = new Feedback();
		HardConstraintsValidator HCV = new HardConstraintsValidator();
		if (!HCV .isSolutionValid(s, f)) {
			System.out.println(f);
			//System.out.println(s.getResultForExam(17));
			//System.out.println(s.getResultForExam(4));
			throw new SolvingException("fuck you move");
		}
		/////
		
		//remove the exam
		for (int i = 0; i < s.getResult().size();i++){
			for (int j = 0; j < s.getResult().get(i).getExamList().size();j++){
				if (s.getResult().get(i).getExamList().get(j).getId() == examId){
					s.getResult().get(i).getExamList().remove(j);
					origin = s.getResult().get(i);
					break;
				}
			}
			if (origin != null) {
				break;
			}
		}
		
		//place the exam in another	period
		for (int i = 0 ; i < s.getResult().size();i++){
			if (s.getResult().get(i).getPeriod().getId() == targetPeriodId
					&& s.getResult().get(i).getRoom().getId() == targetRoomId ){
				s.getResult().get(i).getExamList().add(exam);
				target = s.getResult().get(i);
				break;
			}
		}
		
		refreshExamPeriod(examId,targetPeriodId,s);
		

		//refresh studentTreeMap
		s.updateStudentRCLists();
		
		//////
		//System.out.println((s.getExamCoincidence()[8][2]));
		if (!HCV.isSolutionValid(s, f)) {
			System.out.println(f);
			//System.out.println(s.getResultForExam(8));
			//System.out.println(s.getResultForExam(2));
			System.out.println(examId);
			System.out.println(origin);
			System.out.println(target);
			throw new SolvingException("fuck you move");
		}
		//////
		
		return new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
	}
	
	public static Move moveExamList(ArrayList<Integer> examList, int targetPeriodId, Solution s, ArrayList<Integer> suitableRooms){
		
		ArrayList<ResultCouple> origin = new ArrayList<ResultCouple>();
		ArrayList<ResultCouple> target = new ArrayList<ResultCouple>();
		
		//remove the exam && fills origin
		for (int i = 0; i < s.getResult().size();i++){
			for (int j = 0; j < s.getResult().get(i).getExamList().size();j++){
				for (int examId : examList){
					if (s.getResult().get(i).getExamList().get(j).getId() == examId){
						origin.add(s.getResultForExam(examId));
						s.getResult().get(i).getExamList().remove(j);
						break;
					}
				}
			}
			if (origin.size() == examList.size()){
				break;
			}
		}
		
		// fills target && place exams
		for (int i = 0; i < suitableRooms.size(); i++){
			int roomId = suitableRooms.get(i);
			for (int j = 0; j< s.getResult().size(); j++){
				if (s.getResult().get(j).getPeriod().getId() == targetPeriodId 
					&& roomId == s.getResult().get(j).getRoom().getId()){
					
					for (int k = 0 ; k < examList.size(); k++) {
						if (i == k){
							Exam exam = s.getExamSession().getExams().get(examList.get(k));
							s.getResult().get(j).addExam(exam);
							target.add(s.getResult().get(j));
							refreshExamPeriod(examList.get(k), targetPeriodId, s);
						}
					}					
				}
			}
		}		
		
		s.updateStudentRCLists();		
		return new Move(EMoveType.MULTIPLE_MOVES, examList, origin, target);
	}
	
	/**
	 * Swaps two exams.
	 * @param firstExamId
	 * @param secondExamId
	 * @param s
	 */
	public static Move swapExams(int firstExamId, int secondExamId, Solution s){
		ResultCouple resFirst = removeAndReturnRes(firstExamId, s);
		ResultCouple resSecond = removeAndReturnRes(secondExamId, s);
				
		for (int i = 0; i < s.getResult().size(); i++){
			if (s.getResult().get(i) == resFirst){
				s.getResult().get(i).addExam(secondExamId);
			}
			if (s.getResult().get(i) == resSecond){
				s.getResult().get(i).addExam(firstExamId);
			}
		}
		
		//System.out.println(" first exam "+firstExamId+" seconExamId "+secondExamId);
		refreshExamPeriod(firstExamId,resSecond.getPeriod().getId(),s);
		refreshExamPeriod(secondExamId,resFirst.getPeriod().getId(),s);
		
		
		//refresh studentTreeMap
		s.updateStudentRCLists();
		
		List<Integer> idList = new ArrayList<Integer>();
		idList.add(firstExamId);
		idList.add(secondExamId);
		return new Move(EMoveType.SWAP, idList, resFirst, resSecond);
	}
	
	/**
	 * Removes an exam from the result list.
	 * @param examId
	 * @param s
	 * @return The ResultCouple the exam was found in, or null.
	 */
	public static ResultCouple removeAndReturnRes(int examId, Solution s){
		for (int i = 0; i < s.getResult().size();i++){
			for (int j = 0; j < s.getResult().get(i).getExamList().size();j++){
				if (s.getResult().get(i).getExamList().get(j).getId() == examId){
					s.getResult().get(i).getExamList().remove(j);
					return s.getResult().get(i);
				}
			}
		}
		return null;
	}
	
	 //TODO: move this in Solution?
	/**
	 * Updates the examPeriod matrix.
	 * @param examId
	 * @param targetPeriodId
	 * @param s
	 */
	public static void refreshExamPeriod(int examId, int targetPeriodId, Solution s){		
		int firstPeriodId = -1;
		for (int i = 0; i < s.getResult().size();i++){
			for (int j = 0; j < s.getResult().get(i).getExamList().size();j++){
				if (s.getResult().get(i).getExamList().get(j).getId() == examId){
					firstPeriodId = s.getResult().get(i).getPeriod().getId();
					break;
				}
			}
			if (firstPeriodId > 0)
				break;
		}		
		
		for (int i = 0 ; i < s.getExamCoincidence().length ; i++){
			if (s.getExamCoincidence()[examId][i] == 0){
				s.getExamPeriodModif()[i][targetPeriodId] = 0;
			}
			if (s.getExamCoincidence()[i][examId] == 0){
				s.getExamPeriodModif()[i][targetPeriodId] = 0;
			}
			if (firstPeriodId > 0 && checkConstraints(examId, i, firstPeriodId, s) == true){
				s.getExamPeriodModif()[i][firstPeriodId] = s.getExamPeriodBase()[i][firstPeriodId];
			}
		}
	}
	
	
	/**
	 * Check exclusion, coincidence exclusion, after, before. If true => EPM otherExamId periodId = baseValue
	 * @param currentExamId
	 * @param otherExamId
	 * @param periodId
	 * @param s
	 * @return True if it's cool.
	 */
	public static boolean checkConstraints(int currentExamId, int otherExamId, int periodId, Solution s){
		// if otherExam has exclusions on periodId
		List<Integer> coincidingOther = Solving.checkCoincidence(s, otherExamId);
		
		for (int examId : coincidingOther){
			for (int i = 0; i < s.getExamCoincidence().length ; i++){
				if (s.getExamCoincidence()[examId][i] == 0 || s.getExamCoincidence()[i][examId] == 0){
					for (int j =0 ; j < s.getResult().size();j++){
						for (int k = 0; k < s.getResult().get(j).getExamList().size();k++){
							if (s.getResult().get(j).getExamList().get(k).getId() == i 
								&& s.getResult().get(j).getPeriod().getId() == periodId)
								return false;
						}
					}
				}
			}
		}
		// AFTER
		for (int examId : coincidingOther){
			Exam exam = s.getExamSession().getExams().get(examId);
			for (int i = 0 ; i < exam.getConstraints().size(); i++){
				if (exam.getConstraints().get(i).getConstraint() == EPeriodHardConstraint.AFTER){
					int beforeId = exam.getConstraints().get(i).getE2Id();
					int afterId = exam.getConstraints().get(i).getE1Id();
					int periodOther = -1;
					for (int j = 0 ; j < s.getResult().size(); j++){
						for (int k = 0; k < s.getResult().get(j).getExamList().size(); k++){
							if (s.getResult().get(j).getExamList().get(k).getId() == examId){
									periodOther = s.getResult().get(j).getPeriod().getId();
									break;
								}
						}
					}
					if (examId == beforeId && periodOther >= periodId ){
						return false;
					}
					else if (examId == afterId && periodOther <= periodId){
						return false;
					}
				}
			}			
		}
		return true;
	}

	/**
	 * See if these exams can be placed in each other's period/room.
	 * @param firstExamId
	 * @param secondExamId
	 * @param solution
	 * @return True if the specified exams can be swapped.
	 */
	public static boolean canSwap(int firstExamId, int secondExamId, Solution solution) {
		Solution s = new Solution(solution);
		ResultCouple firstExamOrigin = s.getResultForExam(firstExamId);
		ResultCouple secondExamOrigin = s.getResultForExam(secondExamId);
		
		Exam firstExam = s.getExamSession().getExams().get(firstExamId);
		Exam secondExam = s.getExamSession().getExams().get(secondExamId);
		if (firstExamOrigin.getPeriod().getId() != secondExamOrigin.getPeriod().getId()
				&& (firstExam.hasPeriodHardConstraint(EPeriodHardConstraint.EXAM_COINCIDENCE)
						|| secondExam.hasPeriodHardConstraint(EPeriodHardConstraint.EXAM_COINCIDENCE))) {
			return false;
		} else if (firstExam.getRoomHardConstraint() != null
				|| secondExam.getRoomHardConstraint() != null) {
			return false;
		}
		
		secondExamOrigin.removeExam(secondExamId);
		firstExamOrigin.removeExam(firstExamId);
		//refresh the targeted examPeriod
		Moving.refreshExamPeriod(firstExamId, secondExamOrigin.getPeriod().getId(), s);
		Moving.refreshExamPeriod(secondExamId, firstExamOrigin.getPeriod().getId(), s);
		
		//copied from ILSS.isMoveValid
		if (!Solving.canHost(s, firstExamId, firstExamOrigin.getPeriod().getId(), s.getResult())
				|| !Solving.canHost(s, secondExamId, secondExamOrigin.getPeriod().getId(), s.getResult())) {
			//period can't host
			return false;
		} else if (!Solving.isPeriodAvailable(s, firstExamId, s.getResult(), firstExamOrigin.getPeriod().getId())
				|| !Solving.isPeriodAvailable(s, secondExamId, s.getResult(), secondExamOrigin.getPeriod().getId())
				) {
			//target period is not an available period
			return false;
		} else if (!Solving.findSuitable(s, firstExamId, firstExamOrigin.getPeriod().getId(), s.getResult())
				.contains(firstExamOrigin.getRoom().getId())
				|| !Solving.findSuitable(s, secondExamId, secondExamOrigin.getPeriod().getId(), s.getResult())
				.contains(secondExamOrigin.getRoom().getId())) { 
			//target room isn't suitable
			return false;
		} else if ((firstExamOrigin.getTotalSize() + s.getExamSession().getExams().get(firstExamId).getSize()) > firstExamOrigin.getRoom().getSize()
				 || (secondExamOrigin.getTotalSize() + s.getExamSession().getExams().get(secondExamId).getSize()) > secondExamOrigin.getRoom().getSize()) {
			return false;
		} else {
			//everything OK
			return true;
		}
	}	
}