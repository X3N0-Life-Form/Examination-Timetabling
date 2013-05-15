package util;

import java.util.List;

import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.ResultCouple;
import struct.Solution;

/**
 * Contains various methods dedicated to moving exams within a Solution.
 * @author Adrien Droguet - Sara Tari
 *
 */
public class Moving {

// moving an exam without hardConstraints	
	public void movingSingleExam(int examId, Solution s, int targetPeriodId, int targetRoomId){
		//int firstPeriodId = -1;
		//int firstRoomId = -1;
		Exam exam = s.getExamSession().getExams().get(examId);
		
		//remove the exam
		for (int i = 0; i < s.getResult().size();i++){
			for (int j = 0; j < s.getResult().get(i).getExamList().size();j++){
				if (s.getResult().get(i).getExamList().get(j).getId() == examId){
					//firstPeriodId = s.getResult().get(i).getPeriod().getId();
					//firstRoomId = s.getResult().get(i).getRoom().getId();
					s.getResult().get(i).getExamList().remove(j);
				}
			}
		}
		
		refreshExamPeriod(examId,targetPeriodId,s);
		
		//place the exam in another	period
		for (int i = 0 ; i < s.getResult().size();i++){
			if (s.getResult().get(i).getPeriod().getId() == targetPeriodId
					&& s.getResult().get(i).getRoom().getId() == targetRoomId ){
				s.getResult().get(i).getExamList().add(exam);
			}
		}
		//refresh studentTreeMap
		s.updateStudentRCLists();
	}
	
	public void swapExams(int firstExamId, int secondExamId, Solution s){
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
	}
	
	
	public ResultCouple removeAndReturnRes(int examId, Solution s){
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
	

	
	public void refreshExamPeriod(int examId, int targetPeriodId, Solution s){		
		int firstPeriodId = -1;		
		for (int i = 0; i < s.getResult().size();i++){
			for (int j = 0; j < s.getResult().get(i).getExamList().size();j++){
				if (s.getResult().get(i).getExamList().get(j).getId() == examId){
					firstPeriodId = s.getResult().get(i).getPeriod().getId();
				}
			}
		}		
		for (int i = 0 ; i < s.getExamCoincidence().length ; i++){
			if (s.getExamCoincidence()[examId][i] == 0){
				s.getExamPeriodModif()[i][targetPeriodId] = 0;
			}
			if (s.getExamCoincidence()[i][examId] == 0){
				s.getExamCoincidence()[i][targetPeriodId] = 0;
			}
			if (zorg(examId, i, firstPeriodId, s) == true){
				s.getExamPeriodModif()[i][firstPeriodId] = s.getExamPeriodBase()[i][firstPeriodId];
			}
		}
	}
	
	
	// check exclusion, coincidence exclusion, after, before. If true => EPM otherExamId periodId = baseValue
	public boolean zorg(int currentExamId, int otherExamId, int periodId, Solution s){
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
}