package struct;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import util.Serialization;

public class ResultCoupleTests {

	private Solution s;
	
	@Before
	public void setUp() throws Exception {
		s = Serialization.loadSolution(Serialization.set2SerializedName);
	}

	/**
	 * Verify that the examPeriod matrix is properly modified.
	 */
	@Test
	public void addExam() {
		ResultCouple rc = s.getResult().get(0);
		Exam exam = s.getExamSession().getExams().get(856);
		rc.addExam(exam);
		int eId = 246;
		int pId = rc.getPeriod().getId();
		assertTrue(s.getExamPeriodModif()[eId][pId] == 0);
	}

	@Test
	public void removeExam() {
		ResultCouple rc = s.getResultForExam(0);
		Exam e = rc.getExam(0);
		rc.removeExam(e);
		assertFalse(rc.getExamList().contains(e));
	}
}
