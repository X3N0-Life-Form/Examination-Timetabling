package util;

import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.Solution;

public class Updater {
	
	
	public static void updateValidPeriods(int examId, int periodId, Solution s)
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
			for (int j = 0 ; j < currentExam.getConstraints().size();j++){
				if (currentExam.getConstraints().get(j).getConstraint() == EPeriodHardConstraint.AFTER){
					if (currentExam.getConstraints().get(j).getE2Id() == examId){
						idAfterExam = currentExam.getConstraints().get(j).getE1Id();
						for (int k = 0 ; k <= periodId ; k++){
							eP[idAfterExam][k]= 0;
						}
					}
					else if (currentExam.getConstraints().get(j).getE1Id() == examId ){
						int idBeforeExam = currentExam.getConstraints().get(j).getE2Id();
						for (int k = s.getExamSession().getPeriods().size()-1 ; k >= periodId; k--){
							eP[idBeforeExam][k] = 0;
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
