package struct;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import parse.ExamSessionParser;

public class SolutionTests {
	private ExamSessionParser esp;
	private ExamSession es;
	private String fileName = "res/exam_comp_set2.exam";
	private Solution s;

	@Before
	public void setUp() throws Exception {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
		s = new Solution(es);
	}
	
	/**
	 * Is the nonPlacedExam list created correctly?
	 */
	@Test
	public void creation_checkNonPlacedExams() {
		assertNotNull(s.getNonPlacedExams());
		assertTrue(s.getNonPlacedExams().size() == es.getExams().size());
	}
	
	/**
	 * Check various properties of the initial result list.
	 */
	@Test
	public void creation_result() {
		int numberOfPeriods = s.getExamSession().getPeriods().size();
		int numberOfRooms = s.getExamSession().getRooms().size();
		assertNotNull(s.getResult());
		assertTrue(s.getResult().size() == (numberOfPeriods * numberOfRooms));
	}
}
