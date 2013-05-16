package solve;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import struct.EMoveType;
import struct.Exam;
import struct.Move;
import struct.ResultCouple;
import struct.Solution;
import util.Serialization;

public class ILSS_Tests {
	
	private Solution s;
	private IteratedLocalSearchSolver solver;

	@Before
	public void setUp() throws Exception {
		s = Serialization.loadSolution(HCS_serializeSolutions.set4SerializedName);
		solver = new IteratedLocalSearchSolver(s);
	}

	//////////////////////
	// areConditionsSet //
	//////////////////////
	
	@Test
	public void testAreStopConditionsSet_no() {
		solver.setStopConditions(-1, -1);
		assertFalse(solver.areStopConditionsSet());
	}
	
	@Test
	public void testAreStopConditionsSet_default() {
		assertTrue(solver.areStopConditionsSet());
	}
	
	@Test
	public void testAreStopConditionsSet_time() {
		solver.setStopConditions(-1, 8000000);
		assertTrue(solver.areStopConditionsSet());
	}
	
	@Test
	public void testAreStopConditionsSet_counter() {
		solver.setStopConditions(400, -1);
		assertTrue(solver.areStopConditionsSet());
	}
	
	@Test
	public void testAreStopConditionsSet_both() {
		solver.setStopConditions(400, 8000000);
		assertTrue(solver.areStopConditionsSet());
	}
	
	/////////////////
	// isMoveValid //
	/////////////////
	
	@Test
	public void testIsMoveValid_yes() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	//TODO:the rest
	/**
	 * SingleMove - room size
	 */
	@Test
	public void testIsMoveValid_no() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * Swap OK
	 */
	@Test
	public void testIsMoveValid_maybe() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * Swap not OK - room size
	 */
	@Test
	public void testIsMoveValid_I() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * singleMove not OK - EXAM_COINCIDENCE
	 */
	@Test
	public void testIsMoveValid_dont() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap not - EXAM_COINCIDENCE
	 */
	@Test
	public void testIsMoveValid_know() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * single not - EXCLUSION
	 */
	@Test
	public void testIsMoveValid_can() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap - EXCLUSION
	 */
	@Test
	public void testIsMoveValid_you() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * single - AFTER
	 */
	@Test
	public void testIsMoveValid_repeat() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap - AFTER
	 */
	@Test
	public void testIsMoveValid_the() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * single - ROOM_EXCLUSIVE
	 */
	@Test
	public void testIsMoveValid_question() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap - ROOM_EXCLUSIVE
	 */
	@Test
	public void testIsMoveValid_urNotTheBossOfMeNow() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResult().get(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	///////////////////////
	// lookForMoveTarget //
	///////////////////////
	
	@Test
	public void testLookForMoveTarget() {
		
	}

	///////////////////////
	// lookForSwapTarget //
	///////////////////////
	
	@Test
	public void testLookForSwapTarget() {
		
	}



	



	/////////////////
	// the big one //
	/////////////////
	
	@Test
	public void testSolve() {
		
	}
}
