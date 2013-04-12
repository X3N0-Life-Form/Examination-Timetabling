package solve;

import parse.ExamSessionParser;
import struct.ExamSession;
import struct.ResultCouple;
import struct.Solution;

/**
 * Finds an initial Timetabling solution, which solves every hard constraints.
 * Can also check if a Solution is correct.
 * @author Adrien Droguet - Sara Tari
 * @see Solution
 * @see ExamSession
 * @see ExamSessionParser
 */
public class HardConstraintsSolver {
	
	public boolean isSolutionValid(Solution s) {
		boolean res = true;
		for (ResultCouple current : s.getResult()) {
			//TODO: check AFTER
			//TODO: check EXAM_COINCIDENCE
			//TODO: check EXCLUSION
			//TODO: check exam duration <= period duration
			//TODO: room size <= sum(exam size)
			//TODO: check ROOM_EXCLUSIVE
		}
		return res;
	}
}
