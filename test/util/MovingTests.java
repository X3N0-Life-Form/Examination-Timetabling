package util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import solve.HCS_serializeSolutions;
import struct.ResultCouple;
import struct.Solution;

public class MovingTests {
	
	private Solution s;
	
	@Before
	public void setup() throws FileNotFoundException, ClassNotFoundException, IOException {
		s = Serialization.loadSolution(HCS_serializeSolutions.set4SerializedName);
	}

	@Test
	public void movingSingleExam() {
		int examId = 0;
		ResultCouple rc = s.getResultForExam(examId);
		int targetPeriodId = 5;
		int targetRoomId = 0;
		Moving.movingSingleExam(examId, s, targetPeriodId, targetRoomId);
		
		ResultCouple res = s.getResultForExam(examId);
		boolean examNotInOrigin = true;
		for (int i = 0; i < rc.getExamList().size(); i++) {
			if (rc.getExamList().get(i).getId() == examId) {
				examNotInOrigin = false;
				break;
			}
		}
		boolean examInTarget = false;
		for (int i = 0; i < res.getExamList().size(); i++) {
			if (res.getExamList().get(i).getId() == examId) {
				examInTarget = true;
			}
		}
		
		assertFalse(rc.equals(res));
		assertTrue(res.getPeriod().getId() == targetPeriodId);
		assertTrue(res.getRoom().getId() == targetRoomId);
		assertTrue(examNotInOrigin);
		assertTrue(examInTarget);
	}

	@Test
	public void swapExams() {
		int examId = 0;
		int examTargetId = 1;
		ResultCouple rc = s.getResultForExam(examId);
		ResultCouple hjvo = s.getResultForExam(examTargetId);
		
		Moving.swapExams(examId, examTargetId, s);
		
		ResultCouple resRC = s.getResultForExam(examId);
		ResultCouple resHjvo = s.getResultForExam(examTargetId);
		
		assertTrue(resRC.getPeriod().getId() == hjvo.getPeriod().getId());
		assertTrue(resRC.getRoom().getId() == hjvo.getRoom().getId());
		
		assertTrue(resHjvo.getPeriod().getId() == rc.getPeriod().getId());
		assertTrue(resHjvo.getRoom().getId() == rc.getRoom().getId());
	}
	
	@Test
	public void canSwap() {
		//TODO:todo
	}
}
