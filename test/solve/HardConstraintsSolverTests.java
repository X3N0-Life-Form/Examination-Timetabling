package solve;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import parse.ExamParsingException;
import parse.ExamSessionParser;

import struct.ExamSession;
import struct.Solution;

public class HardConstraintsSolverTests {
	private Solution s;
	private ExamSession es;
	private ExamSessionParser esp;
	private String fileName = "res/exam_comp_set2.exam";
	
	private HardConstraintsSolver solver;
	
	@Before
	public void setUp() throws ExamParsingException, IOException {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
		s = new Solution(es);
		solver = new HardConstraintsSolver(s);
	}
	
	/**
	 * Does the solver remove all non placed exams?
	 */
	@Ignore
	@Test
	public void solve_loop() {
		Solution res = solver.solve();
		assertFalse(res.getNonPlacedExams().size() > 0);
	}
	
	@Test
	public void checkCoincidence() {
		List<Integer> res = solver.checkCoincidence(306);
		assertNotNull(res);
		assertTrue(res.size() == 2);
		assertTrue(res.get(0) == 307);
		assertTrue(res.get(1) == 306);
	}

}
