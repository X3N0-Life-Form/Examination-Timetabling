package solve;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import parse.ExamParsingException;
import parse.ExamSessionParser;
import struct.ExamSession;
import struct.Solution;

/**
 * These tests take a long time to run.
 * @author Adrien Droguet - Sara Tari
 * @see HardConstraintsSolver
 * @see HCV_Tests
 */
public class HCS_Tests_slow {
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
	 * @throws SolvingException 
	 */
	@Test
	public void solve_loop() throws SolvingException {
		Solution res = solver.solve();
		assertFalse(res.getNonPlacedExams().size() > 0);
	}
	
}
