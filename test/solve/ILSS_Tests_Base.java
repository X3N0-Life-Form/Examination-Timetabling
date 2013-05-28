package solve;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
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

public class ILSS_Tests_Base {
	
	private Solution s;
	private IteratedLocalSearchSolver solver;

	@Before
	public void setUp() throws Exception {
		s = Serialization.loadSolution(Serialization.set4SerializedName);
		solver = new IteratedLocalSearchSolver(s);
	}
	
	@SuppressWarnings("unused")
	private void printSolutionExams(Solution solution) {
		for (Exam exam : solution.getExamSession().getExamsAsList()) {
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
		//bug hunt
		assertNotNull(s.getResultForExam(examId));
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
		//bug hunt
		assertNotNull(s.getResultForExam(examId));
		assertNotNull(s.getResultForExam(targetId));
	}
	
	/**
	 * Swap not OK - room size
	 */
	@Test
	public void testIsMoveValid_I() {
		int examId = 209; //size=5
		int targetId = 185; //size=631
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(targetId);
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move move = new Move(EMoveType.SWAP, examIds, origin, target);
		Moving.swapExams(examId, targetId, s);
		assertFalse(new HardConstraintsValidator().isSolutionValid(s, new Feedback()));
		assertFalse(solver.isMoveValid(move, s));
	}
	
	/**
	 * singleMove not OK - EXAM_COINCIDENCE
	 * @throws SolvingException 
	 */
	@Test
	public void testIsMoveValid_dont() throws SolvingException {
		int examId = 108;
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(0);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		Moving.movingSingleExam(examId, s, target.getPeriod().getId(), target.getRoom().getId());
		assertFalse(new HardConstraintsValidator().isSolutionValid(s, new Feedback()));
		assertFalse(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap not OK - EXAM_COINCIDENCE
	 */
	@Test
	public void testIsMoveValid_know() {
		int examId = 108; //size=50 + examCoincidence
		int targetId = 26; //size=49
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(targetId);
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move move = new Move(EMoveType.SWAP, examIds, origin, target);
		Moving.swapExams(examId, targetId, s);
		assertFalse(new HardConstraintsValidator().isSolutionValid(s, new Feedback()));
		assertFalse(solver.isMoveValid(move, s));
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
	 * swap not OK - EXCLUSION
	 */
	@Test
	public void testIsMoveValid_you() {
		int examId = 6; //size=34 --> exclude 122
		int targetId = 98; //size=34 --> with 122
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = s.getResultForExam(targetId);
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move move = new Move(EMoveType.SWAP, examIds, origin, target);
		assertFalse(solver.isMoveValid(move, s));
		Moving.swapExams(examId, targetId, s);
		assertFalse(new HardConstraintsValidator().isSolutionValid(s, new Feedback()));
	}
	
	/**
	 * single - AFTER
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testIsMoveValid_repeat() throws FileNotFoundException, ClassNotFoundException, IOException {
		Solution s1 = Serialization.loadSolution(Serialization.set1SerializedName);
		int examId = 11; //size=? AFTER 10
		int targetId = 10;
		ResultCouple origin = s1.getResultForExam(examId);
		ResultCouple target = s1.getResultForExam(targetId);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertFalse(solver.isMoveValid(move, s1));
	}
	
	/**
	 * swap - AFTER
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testIsMoveValid_the() throws FileNotFoundException, ClassNotFoundException, IOException {
		Solution s1 = Serialization.loadSolution(Serialization.set1SerializedName);
		int examId = 11; //size=? AFTER 10
		int targetId = 10;
		ResultCouple origin = s1.getResultForExam(examId);
		ResultCouple target = s1.getResultForExam(targetId);
		
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move move = new Move(EMoveType.SWAP, examIds, origin, target);
		assertFalse(solver.isMoveValid(move, s1));
	}
	
	/**
	 * single - ROOM_EXCLUSIVE
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testIsMoveValid_question() throws FileNotFoundException, ClassNotFoundException, IOException {
		Solution s2 = Serialization.loadSolution(Serialization.set2SerializedName);
		int examId = 239; //size=1
		int targetId = 78;//size=31 --> Room exclusive
		ResultCouple origin = s2.getResultForExam(examId);
		ResultCouple target = s2.getResultForExam(targetId);
		
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertFalse(solver.isMoveValid(move, s));
	}
	
	/**
	 * swap - ROOM_EXCLUSIVE
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testIsMoveValid_urNotTheBossOfMeNow() throws FileNotFoundException, ClassNotFoundException, IOException {
		Solution s2 = Serialization.loadSolution(Serialization.set2SerializedName);
		int examId = 78; //size=31 --> Room exclusive
		int targetId = 241;//size=31
		ResultCouple origin = s2.getResultForExam(examId);
		ResultCouple target = s2.getResultForExam(targetId);
		
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		Move move = new Move(EMoveType.SWAP, examIds, origin, target);
		assertFalse(solver.isMoveValid(move, s2));
	}
	
	///////////////////////
	// lookForMoveTarget //
	///////////////////////
	
	/**
	 * Normal case.
	 * @throws MovingException 
	 */
	@Test
	public void testLookForMoveTarget_regular() throws MovingException {
		List<Move> previousMoves = new ArrayList<Move>();
		int examId = 0;
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = solver.lookForMoveTarget(examId, s, previousMoves);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * Giving it a null previousMoves list.
	 * @throws MovingException 
	 */
	@Test
	public void testLookForMoveTarget_previousMovesNull() throws MovingException {
		List<Move> previousMoves = null;
		int examId = 0;
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = solver.lookForMoveTarget(examId, s, previousMoves);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		assertTrue(solver.isMoveValid(move, s));
	}
	
	/**
	 * Giving it an invalid exam id.
	 * @throws MovingException 
	 */
	@Test(expected=MovingException.class)
	public void testLookForMoveTarget_invalidExam() throws MovingException {
		List<Move> previousMoves = new ArrayList<Move>();
		int examId = 1000;
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple target = solver.lookForMoveTarget(examId, s, previousMoves);
		Move move = new Move(EMoveType.SINGLE_MOVE, examId, origin, target);
		solver.isMoveValid(move, s);
	}

	///////////////////////
	// lookForSwapTarget //
	///////////////////////
	
	/**
	 * Regular
	 * @throws MovingException 
	 */
	@Test
	public void testLookForSwapTarget_regular() throws MovingException {
		List<Move> previousMoves = new ArrayList<Move>();
		int examId = 239; //size=1
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple targetLocation = null;
		
		int targetId = solver.lookForSwapTarget(examId, s, previousMoves);
		
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		targetLocation = s.getResultForExam(targetId);
		
		Move move = new Move(EMoveType.SWAP, examIds, origin, targetLocation);
		assertTrue(targetId != -1);
		System.out.println(targetId);
		System.out.println(s.getResultForExam(206));
		System.out.println(examIds);
		assertNotNull(targetLocation);
		assertTrue(solver.isMoveValid(move, s));
	}

	/**
	 * Null previousMoves
	 * @throws MovingException 
	 */
	@Test
	public void testLookForSwapTarget_previousMovesNull() throws MovingException {
		List<Move> previousMoves = null;
		int examId = 239; //size=1
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple targetLocation = null;
		
		int targetId = solver.lookForSwapTarget(examId, s, previousMoves);
		
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		targetLocation = s.getResultForExam(targetId);
		
		Move move = new Move(EMoveType.SWAP, examIds, origin, targetLocation);
		assertTrue(targetId != -1);
		assertNotNull(targetLocation);
		assertTrue(solver.isMoveValid(move, s));
	}

	/**
	 * Invalid examId
	 * @throws MovingException 
	 */
	@Test(expected=MovingException.class)
	public void testLookForSwapTarget_invalidExam() throws MovingException {
		List<Move> previousMoves = new ArrayList<Move>();
		int examId = 1000;
		ResultCouple origin = s.getResultForExam(examId);
		ResultCouple targetLocation = null;
		
		int targetId = solver.lookForSwapTarget(examId, s, previousMoves);
		
		List<Integer> examIds = new ArrayList<Integer>();
		examIds.add(examId);
		examIds.add(targetId);
		targetLocation = s.getResultForExam(targetId);
		
		Move move = new Move(EMoveType.SWAP, examIds, origin, targetLocation);
		solver.isMoveValid(move, s);
	}

	/////////////////
	// the big one //
	/////////////////
	
	/**
	 * Play with stop conditions - stopCounter
	 * @throws SolvingException Something fucked up.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testSolve_stopAfterTwoLoops() throws SolvingException, FileNotFoundException, ClassNotFoundException, IOException {
		Solution s1 = Serialization.loadSolution(Serialization.set4SerializedName);
		IteratedLocalSearchSolver solver1 = new IteratedLocalSearchSolver(s1);
		solver1.setStopConditions(2, -1);
		solver1.solve();
		//assert ends really fast
	}
	
	/**
	 * Play with stop conditions - stopTime
	 * @throws SolvingException Idem.
	 */
	@Test @Ignore
	public void testSolve_stopAfterFiveSeconds() throws SolvingException {
		solver.setStopConditions(-1, 5000);
		solver.solve();
		//assert ends after twenty seconds
	}
	
	@Test @Ignore
	public void testSolve() {
		
	}
}
