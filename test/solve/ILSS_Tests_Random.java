package solve;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import struct.EMoveType;
import struct.Move;
import struct.ResultCouple;
import struct.Solution;
import util.Serialization;

public class ILSS_Tests_Random {
	
	private Solution s;
	private IteratedLocalSearchSolver solver;
	private List<Move> moves;
	private HardConstraintsValidator HCV;

	@Before
	public void setUp() throws Exception {
		s = Serialization.loadSolution(Serialization.set4SerializedName);
		solver = new IteratedLocalSearchSolver(s);
		moves = new ArrayList<Move>();
		HCV = new HardConstraintsValidator();
	}
	
	/**
	 * Good to go if: doesn't crash, has found something & that move is valid.
	 * @throws MovingException
	 * @throws SolvingException 
	 */
	@Test
	public void testRandomMoveTarget() throws MovingException, SolvingException {
		int examId = 0;
		ResultCouple rc = solver.lookForRandomMoveTarget(examId, s, moves);
		Move m = new Move(EMoveType.SINGLE_MOVE, examId, s.getResultForExam(examId), rc);
		assertNotNull(rc);
		assertTrue(solver.isMoveValid(m, s));
	}
	
	/**
	 * Same as testRandomMoveTarget().
	 * @throws MovingException
	 */
	@Test
	public void testRandomSwapTarget() throws MovingException {
		int examId = 0;
		int targetId = solver.lookForRandomSwapTarget(examId, s, moves);
		ResultCouple rc = s.getResultForExam(targetId);
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move m = new Move(EMoveType.SWAP, examIds, s.getResultForExam(examId), rc);
		assertNotNull(rc);
		assertTrue(solver.isMoveValid(m, s));
	}
	
	/**
	 * Good if: a new Solution has been produced, it's different from the previous ones,
	 * the move has been saved & the resulting Solution is valid.
	 * @throws SolvingException
	 */
	@Test
	public void testDoRandomMove() throws SolvingException {
		List<Solution> solutions = new ArrayList<Solution>();//TODO: add some other solutions
		Solution res = solver.doRandomMove(solutions, s, moves, true);
		assertNotNull(res);
		assertFalse(res.equals(s));
		assertTrue(moves.size() > 0);
		assertTrue(solver.getAppliedMoves().size() > 0);
		Feedback f = new Feedback();
		boolean isValid = HCV.isSolutionValid(res, f);
		if (!isValid)
			System.out.println(f);
		assertTrue(isValid);
	}
	
	/**
	 * Set ignoreThreshold to 0; enjoy making random moves.
	 * Good to go if it doesn't crash.
	 * @throws SolvingException
	 */
	//@Test
	public void testSolve_randomOnly() throws SolvingException {
		solver.setStopConditions(1, -1);
		solver.setIgnoreThreshold(0);
		solver.solve();
	}
	
	/**
	 * Big test that takes a lot of time, hopefully forcing the use of random moves.
	 * Good to go if it doesn't crash.
	 * @throws SolvingException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	//@Test
	public void testSolve_bigassTest() throws SolvingException, FileNotFoundException, IOException {
		solver.setStopConditions(20, -1);
		solver.setIgnoreThreshold(1000);
		Solution s = solver.solve();
		Serialization.saveSolution(s, Serialization.SET4_GOOD_PATH);
	}
	
	@Test
	public void testSolve_bigass_set1() throws SolvingException, FileNotFoundException, IOException, ClassNotFoundException {
		Solution s1 = Serialization.loadSolution(Serialization.SET1_GOOD_PATH);
		solver = new IteratedLocalSearchSolver(s1);
		solver.setStopConditions(8, -1);
		solver.setIgnoreThreshold(1000);
		Solution s = solver.solve();
		Serialization.saveSolution(s, Serialization.SET1_GOOD_PATH);
	}
	
	//@Test
	public void testSolve_bigass_set2() throws SolvingException, FileNotFoundException, IOException, ClassNotFoundException {
		Solution s1 = Serialization.loadSolution(Serialization.set2SerializedName);
		solver = new IteratedLocalSearchSolver(s1);
		solver.setStopConditions(5, -1);
		solver.setIgnoreThreshold(1000);
		Solution s = solver.solve();
		Serialization.saveSolution(s, Serialization.SET2_GOOD_PATH);
	}
	
	//@Test
	public void testSolve_bigass_set3() throws SolvingException, FileNotFoundException, IOException, ClassNotFoundException {
		Solution s1 = Serialization.loadSolution(Serialization.set3SerializedName);
		solver = new IteratedLocalSearchSolver(s1);
		solver.setStopConditions(2, -1);
		solver.setIgnoreThreshold(1000);
		Solution s = solver.solve();
		Serialization.saveSolution(s, Serialization.SET3_GOOD_PATH);
	}
}
