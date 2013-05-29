package solve;

import static org.junit.Assert.*;

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
		Solution res = solver.doRandomMove(solutions, s, moves);
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
	
}
