package solve;

import java.util.ArrayList;
import java.util.Date;

import struct.EPeriodHardConstraint;
import struct.Exam;
import struct.PeriodHardConstraint;
import struct.ResultCouple;
import struct.Solution;

public class HardConstraintsValidator implements Validator {

	@Override
	public boolean isSolutionValid(Solution s, Feedback feedback) {
		boolean res = true;

		// id : exam id, used for coincidence & exclusion
		int id = 0;
		boolean notAfter;
		Date periodAfter = new Date();
		Date periodBefore = new Date();
		ArrayList<PeriodHardConstraint> constraintList = new ArrayList<PeriodHardConstraint>();

		//check AFTER

		for (ResultCouple currentRC : s.getResult()) {

			for (int i = 0 ; i <currentRC.getExamList().size(); i++){
				// get constraints list
				Exam currentExam = currentRC.getExamList().get(i);
				constraintList = currentExam.getConstraints();

				for (int j = 0; j < constraintList.size(); j++){
					notAfter = false;
					// if AFTER
					
					if (constraintList.get(j).getConstraint() == EPeriodHardConstraint.AFTER) {
						int afterId = constraintList.get(j).getE1Id();
						int beforeId = constraintList.get(j).getE2Id();
						Date dateBefore = null;
						Date dateAfter = null;
						boolean found = true;
						
						for (ResultCouple rc : s.getResult()) {
							for (int k = 0; k < rc.getExamList().size(); k++) {
								if (rc.getExamList().get(k).getId() == beforeId) {
									dateBefore = rc.getPeriod().getDate_hour();
								} else if (rc.getExamList().get(k).getId() == afterId) {
									dateAfter = rc.getPeriod().getDate_hour();
								}
								if (dateBefore != null && dateAfter != null) {
									found = true;
									break;
								}
							}
							if (found)
								break;
						}
						if(periodAfter.compareTo(periodBefore) <= 0)
							notAfter = true;	
					}
					
					if (notAfter) {
						res = false;
						feedback.addItem(currentRC, Feedback.AFTER_VIOLATION
								+ constraintList.get(j).getE1Id() + " should be after "
								+ constraintList.get(j).getE2Id());
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
						boolean present = false;
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
								feedback.addItem(currentBis, Feedback.EXAM_COINCIDENCE_VIOLATION
										+ constraintList.get(i).getE1Id() + " - "
										+ constraintList.get(i).getE2Id());
							}
						}

					}
				}
			}			

			//check EXCLUSION
			// coincidence matrix
			int [][] coincidence = s.getExamCoincidence();

			for (int i = 0; i< currentRC.getExamList().size(); i++){
				// matrix
				for (int j =0; j < s.getExamSession().getExams().size();j++ ){
					boolean present = false;
					// if exam i & j cannot take place at the same time
					if (coincidence[currentRC.getExamList().get(i).getId()][j] == 0) 
						// check if exam j is present 
						for(int k = 0; k < currentRC.getExamList().size();k++){
							// if id is found : present = true
							if (currentRC.getExamList().get(k).getId() == j) {
								present = true;
								/*
								System.out.println("present! --> id=" + j +" " + current.getExamList().get(i).getId());
								for (Integer kStudent: current.getExamList().get(k).getStudents()) {
									if (s.getExamSession().getExams().get(j).getStudents().contains(kStudent)) {
										System.out.println("  Found one! --> " + kStudent);
									}
								}
								 */
							}
						}
					// if present, id's found => false because of the exclusion
					if (present) {
						res = false;
						feedback.addItem(currentRC, Feedback.EXCLUSION_VIOLATION);
					}
				}
			}
			int sizeSum = 0;
			for (Exam e : currentRC.getExamList()) {
				//check ROOM_EXCLUSIVE
				if (e.getRoomHardConstraint() != null) {
					if (currentRC.getExamList().size() > 1) {
						feedback.addItem(currentRC, Feedback.ROOM_EXLUSIVE_VIOLATION);
						return false;
					}
				}
				//check duration
				if (e.getDuration() > currentRC.getPeriod().getDuration()) {
					feedback.addItem(currentRC, Feedback.DURATION_VIOLATION);
					return false;
				}
				sizeSum += e.getSize();
			}
			//check size
			if (sizeSum > currentRC.getRoom().getSize()) {
				feedback.addItem(currentRC, Feedback.ROOM_SIZE_VIOLATION);
				return false;
			}
		}
		return res;
	}

}