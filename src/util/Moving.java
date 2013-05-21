package util;

import java.util.ArrayList;
import java.util.List;

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
	 */
	public static Move movingSingleExam(int examId, Solution s, int targetPeriodId, int targetRoomId){
		//int firstPeriodId = -1;
		//int firstRoomId = -1;
		Exam exam = s.getExamSession().getExams().get(examId);
		ResultCouple target = null;
		ResultCouple origin = null;
		
		//remove the exam
		for (int i = 0; i < s.getResult().size();i++){
			for (int j = 0; j < s.getResult().get(i).getExamList().size();j++){
				if (s.getResult().get(i).getExamList().get(j).getId() == examId){
					//firstPeriodId = s.getResult().get(i).getPeriod().getId();
					//firstRoomId = s.getResult().get(i).getRoom().getId();
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
		
		return new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
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
				s.getExamCoincidence()[i][targetPeriodId] = 0;
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
	 * @param examId
	 * @param examTargetId
	 * @param solution
	 * @return True if the specified exams can be swapped.
	 */
	public static boolean canSwap(int examId, int examTargetId, Solution solution) {
		Solution s = new Solution(solution);
		ResultCouple targetClone = s.getResultForExam(examId);
		ResultCouple originClone = s.getResultForExam(examTargetId);
		
		originClone.removeExam(examId);
		targetClone.removeExam(examTargetId);
		//derp fuck that shit
		originClone.removeExam(examTargetId);
		targetClone.removeExam(examId);
		Moving.refreshExamPeriod(examId, originClone.getPeriod().getId(), s);
		Moving.refreshExamPeriod(examTargetId, targetClone.getPeriod().getId(), s);
		
		//copied from ILSS.isMoveValid
		if (!Solving.canHost(s, examId, targetClone.getPeriod().getId(), s.getResult())
				|| !Solving.canHost(s, examTargetId, originClone.getPeriod().getId(), s.getResult())) {
			//period can't host
			return false;
		} else if (!Solving.isPeriodAvailable(s, examId, s.getResult(), targetClone.getPeriod().getId())
				|| !Solving.isPeriodAvailable(s, examTargetId, s.getResult(), originClone.getPeriod().getId())
				) {
			//target period is not an available period
			return false;
		} else if (!Solving.findSuitable(s, examId, targetClone.getPeriod().getId(), s.getResult())
				.contains(targetClone.getRoom().getId())
				|| !Solving.findSuitable(s, examTargetId, originClone.getPeriod().getId(), s.getResult())
				.contains(originClone.getRoom().getId())) { 
			//target room isn't suitable
			return false;
		} else {
			//everything OK
			return true;
		}
	}	
}