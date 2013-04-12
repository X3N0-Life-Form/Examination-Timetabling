package struct;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import parse.ExamSessionParser;

public class SolutionTests {
	private ExamSessionParser esp;
	private ExamSession es;
	private String fileName = "res/exam_comp_set2.exam";

	@Before
	public void setUp() throws Exception {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
	}

	/**
	 * Is the nonPlacedExam list created correctly?
	 */
	@Test
	public void creation_checkNonPlacedExams() {
		Solution s = new Solution(es);
		assertNotNull(s.getNonPlacedExams());
		assertTrue(s.getNonPlacedExams().size() == es.getExams().size());
	}

}
