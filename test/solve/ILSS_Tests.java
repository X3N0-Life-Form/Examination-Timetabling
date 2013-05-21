package solve;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import struct.EMoveType;
import struct.Exam;
import struct.Move;
import struct.ResultCouple;
import struct.Solution;
import util.Moving;
import util.Serialization;

public class ILSS_Tests {
	
	private Solution s;
	private IteratedLocalSearchSolver solver;

	@Before
	public void setUp() throws Exception {
		s = Serialization.loadSolution(Serialization.set4SerializedName);
		solver = new IteratedLocalSearchSolver(s);
	}
	
	@SuppressWarnings("unused")
	private void printSolutionExams() {
		for (Exam exam : s.getExamSession().getExamsAsList()) {
			System.out.println("id=" + exam.getId() + "; size=" + exam.getSize());
		}
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
	
	/**
	 * SingleMove not OK - room size
	 */
	@Test
	public void testIsMoveValid_no() {
		int examId = 261; //size=1042
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(223);//size=455
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertFalse(solver.isMoveValid(move, s));
	}

	/**
	 * Swap OK
	 */
	@Test
	public void testIsMoveValid_maybe() {
		int examId = 257; //size=1
		int targetId = 258; //size=1
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(targetId);
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move move = new Move(EMoveType.SWAP, examIds, origin, target);
		Moving.swapExams(examId, targetId, s);
		assertTrue(new HardConstraintsValidator().isSolutionValid(s, new Feedback()));
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * Swap not OK - room size
	 */
	@Test
	public void testIsMoveValid_I() {
		int examId = 257; //size=1
		int targetId = 258; //size=
		printSolutionExams();
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(targetId);
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move move = new Move(EMoveType.SWAP, examIds, origin, target);
		//Moving.swapExams(examId, targetId, s);
		//assertTrue(new HardConstraintsValidator().isSolutionValid(s, new Feedback()));
		//assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * singleMove not OK - EXAM_COINCIDENCE
	 */
	@Test @Ignore
	public void testIsMoveValid_dont() {
		int examId = 3;
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertFalse(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap not - EXAM_COINCIDENCE
	 */
	@Test @Ignore
	public void testIsMoveValid_know() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * single not OK - EXCLUSION
	 */
	@Test
	public void testIsMoveValid_can() {
		int examId = 6;
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(69);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertFalse(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap - EXCLUSION
	 */
	@Test @Ignore
	public void testIsMoveValid_you() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * single - AFTER
	 */
	@Test @Ignore
	public void testIsMoveValid_repeat() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap - AFTER
	 */
	@Test @Ignore
	public void testIsMoveValid_the() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * single - ROOM_EXCLUSIVE
	 */
	@Test @Ignore
	public void testIsMoveValid_question() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap - ROOM_EXCLUSIVE
	 */
	@Test @Ignore
	public void testIsMoveValid_urNotTheBossOfMeNow() {
		int examId = 272; //size=3
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	///////////////////////
	// lookForMoveTarget //
	///////////////////////
	
	@Test @Ignore
	public void testLookForMoveTarget() {
		
	}

	///////////////////////
	// lookForSwapTarget //
	///////////////////////
	
	@Test @Ignore
	public void testLookForSwapTarget() {
		
	}



	



	/////////////////
	// the big one //
	/////////////////
	
	@Test @Ignore
	public void testSolve() {
		
	}
}
