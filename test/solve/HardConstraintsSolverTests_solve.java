package solve;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import parse.ExamParsingException;
import parse.ExamSessionParser;
import struct.Exam;
import struct.ExamSession;
import struct.ResultCouple;
import struct.Solution;

/**
 * Tests concerning the solve() method. Note that this series of tests uses
 * the simplified data set.
 * @author Adrien Droguet - Sara Tari
 * @see HardConstraintsSolver
 * @see HardConstraintsSolverTests_isSolutionValid
 */
public class HardConstraintsSolverTests_solve {

	private ExamSessionParser simpleParser;
	private Solution simpleSolution;
	private ExamSession simpleExamSession;
	private String simpleFileName = "res/simple_set.exam";
	private HardConstraintsSolver simpleSolver;
	
	
	private ExamSessionParser normalParser;
	private Solution normalSolution;
	private ExamSession normalExamSession;
	private String normalFileName = "res/exam_set_noAFTER.exam";
	private HardConstraintsSolver normalSolver;
	
	/**
	 * For the record:
	 * id=0;size=220
	 * id=1;size=224
	 * id=2;size=226
	 * id=3;size=24
	 * id=4;size=67
	 * id=5;size=70
	 * id=6;size=94
	 * @throws ExamParsingException
	 * @throws IOException
	 */
	@Before
	public void simpleSolutionSetup() throws ExamParsingException, IOException {
		simpleParser = new ExamSessionParser(simpleFileName);
		simpleExamSession = simpleParser.parse();
		simpleSolution = new Solution(simpleExamSession);
		simpleSolver = new HardConstraintsSolver(simpleSolution);
	}
	
	public void normalSolutionSetup() throws ExamParsingException, IOException {
		normalParser = new ExamSessionParser(normalFileName);
		normalExamSession = normalParser.parse();
		normalSolution = new Solution(normalExamSession);
		normalSolver = new HardConstraintsSolver(normalSolution);
	}
	
	@SuppressWarnings("unused")
	private void printSimpleSet(List<ResultCouple> results) {
		int i =0;
		for (ResultCouple currentRC : results) {
			System.out.println("i=" + i + "--" + currentRC);
			i++;
		}
		for (Exam currentExam : simpleExamSession.getExamsAsList()) {
			System.out.println("id=" + currentExam.getId() + ";size=" + currentExam.getSize());
		}
	}
	
	/**
	 * A solved solution is valid.
	 * @throws SolvingException 
	 */
	@Test @Ignore
	public void testSolveSimple() throws SolvingException {
		@SuppressWarnings("unused")
		Solution s = simpleSolver.solve();
		Feedback feedback = new Feedback();
		boolean res = simpleSolver.isSolutionValid(feedback);
		if (!res) {
			System.out.println(feedback);
		}
		assertTrue(res);
	}
	
	@Test
	public void testCanHost_periodUnavailable() {
		List<ResultCouple> resIn = simpleSolution.getResult();
		resIn.get(0).addExam(0); //room 0; period 0 --> occupied
		for (ResultCouple c: resIn) {
			if (c.getPeriod().getId() == 0) {
				c.addExam(1);
			}
		}
		assertFalse(simpleSolver.canHost(3, 0, resIn));
	}
	
	@Test
	public void testSolveNormal() throws SolvingException, ExamParsingException, IOException {
		normalSolutionSetup();
		@SuppressWarnings("unused")
		Solution s = normalSolver.solve();
		Feedback feedback = new Feedback();
		boolean res = normalSolver.isSolutionValid(feedback);
		if (!res) {
			System.out.println(feedback);
		}
		assertTrue(res);
	}
}
