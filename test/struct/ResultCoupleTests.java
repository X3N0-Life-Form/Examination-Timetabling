package struct;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import parse.ExamSessionParser;

public class ResultCoupleTests {

	private String fileName = "res/exam_comp_set2.exam";
	private ExamSessionParser esp;
	private ExamSession es;
	private Solution s;
	
	@Before
	public void setUp() throws Exception {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
		s = new Solution(es);
	}

	/**
	 * Verify that the examPeriod matrix is properly modified.
	 */
	@Test
	public void addExam() {
		ResultCouple rc = s.getResult().get(0);
		Exam exam = es.getExams().get(856);
		rc.addExam(exam);
		int eId = 246;
		int pId = rc.getPeriod().getId();
		assertTrue(s.getExamPeriodModif()[eId][pId] == 0);
	}

}
