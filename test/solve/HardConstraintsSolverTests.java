package solve;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
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

public class HardConstraintsSolverTests {
	private Solution s;
	private ExamSession es;
	private ExamSessionParser esp;
	private String fileName = "res/exam_comp_set2.exam";
	
	private HardConstraintsSolver solver;
	
	private ExamSessionParser simpleParser;
	private Solution simpleSolution;
	private ExamSession simpleExamSession;
	private String simpleFileName = "res/simple_set.exam";
	private HardConstraintsSolver simpleSolver;
	
	//@Before
	public void setUp() throws ExamParsingException, IOException {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
		s = new Solution(es);
		solver = new HardConstraintsSolver(s);
	}
	
	/**
	 * Does the solver remove all non placed exams?
	 */
	@Test @Ignore
	public void solve_loop() {
		Solution res = solver.solve();
		assertFalse(res.getNonPlacedExams().size() > 0);
	}
	
	@Test @Ignore
	public void checkCoincidence() {
		List<Integer> res = solver.checkCoincidence(306);
		assertNotNull(res);
		assertTrue(res.size() == 2);
		assertTrue(res.get(0) == 307);
		assertTrue(res.get(1) == 306);
	}

	@Test @Ignore
	public void canHost_singleTrue() {
		boolean res = solver.canHost(1, 1, s.getResult());
		assertTrue(res);
	}
	
	@Test @Ignore
	public void canHost_singleFalse() {
		List<ResultCouple> results = s.getResult();
		for (ResultCouple current : results) {
			for (int i = 0; i < 200; i++) {
				current.addExam(es.getExams().get(i));
			}
		}
		boolean res = solver.canHost(800, 0, s.getResult());
		assertFalse(res);
	}
	
	@Test @Ignore
	public void canHost_singleFalse_roomExclusive() {
		List<ResultCouple> results = s.getResult();
		for (ResultCouple current : results) {
			for (int i = 0; i < 200; i++) {
				current.addExam(es.getExams().get(i));
			}
		}
		ResultCouple first = results.get(0);
		first.getExamList().removeAll(first.getExamList());
		Exam e_normal = es.getExams().get(0);
		first.addExam(e_normal);
		boolean res = solver.canHost(78, 0, s.getResult());
		assertFalse(res);
	}
	
	@Test @Ignore
	public void canHost_multipleOK() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		boolean res = solver.canHost(exams, 0);
		assertTrue(res);
	}
	
	@Test @Ignore
	public void canHost_multipleAllRoomFull() {
		List<Integer> exams = new ArrayList<Integer>();
		List<ResultCouple> results = s.getResult();
		for (ResultCouple currentRC : results) {
			currentRC.addExam(es.getExams().get(0));
		}
		exams.add(0);
		boolean res = solver.canHost(exams, 0);
		assertFalse(res);
	}
	
	@Test @Ignore
	public void findSuitable_singleNormal() {
		int res = solver.findSuitable(0, 0, s.getResult());
		assertTrue(res != -1);
		assertTrue(res == 0);
	}
	
	@Test @Ignore
	public void findSuitable_singleRoomNotFull() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		int roomId = first.getRoom().getId();
		int periodId = first.getPeriod().getId();
		first.addExam(es.getExams().get(0));
		//exam 13's size = 4
		int res = solver.findSuitable(13, periodId, s.getResult());
		assertTrue(res != -1);
		assertTrue(res == roomId);
	}
	
	@Test @Ignore
	public void findSuitable_multipleSameRoom() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(306);
		exams.add(307);
		List<Integer> res = solver.findSuitable(exams, 0);
		assertTrue(res.size() == 2);
		assertTrue(res.get(0) == 0);
		assertTrue(res.get(1) == res.get(0));
	}
	
	@Test @Ignore
	public void findSuitable_multipleDifferentRooms() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		List<Integer> res = solver.findSuitable(exams, 0);
		assertTrue(res.get(0) != res.get(1));
		assertTrue(res.get(0) == 0);
		assertTrue(res.get(1) == 1);
	}
	
	@Test @Ignore
	public void findSuitable_multipleOneAndTwoOccupied() {
		List<ResultCouple> results = s.getResult();
		Exam e0 = es.getExams().get(0);
		Exam e1 = es.getExams().get(1);
		results.get(0).addExam(e0);
		results.get(1).addExam(e1);
		
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		List<Integer> res = solver.findSuitable(exams, 0);
		assertFalse(res.contains(0));
		assertFalse(res.contains(1));
	}
	
	@Test @Ignore
	public void getAvailablePeriod_singleNormal() {
		assertTrue(solver.getAvailablePeriod(0) == 0);
	}
	
	@Test @Ignore
	public void getAvailablePeriod_singleMutuallyExclusiveExams() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		first.addExam(es.getExams().get(856));
		int res = solver.getAvailablePeriod(246);
		assertTrue(res != 0);
		assertTrue(res == 1);
	}
	
	@Test @Ignore
	public void getAvailablePeriod_multiple() {
		List<Integer> coincidingExams = new ArrayList<Integer>();
		coincidingExams.add(0);
		coincidingExams.add(1);
		int res = solver.getAvailablePeriod(coincidingExams);
		assertTrue(res == 0);
	}
	
	@Test @Ignore
	public void getAvailablePeriod_multipleMutuallyExclusiveExams() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		first.addExam(es.getExams().get(856));
		List<Integer> list = new ArrayList<Integer>();
		list.add(246);
		list.add(13);
		int res = solver.getAvailablePeriod(list);
		assertTrue(res != 0);
		assertTrue(res == 1);
	}
	
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
	 * Everything is shiny.
	 */
	@Test
	public void isSolutionValid_OK() {
		List<ResultCouple> results = simpleSolution.getResult();
		Feedback feedback = new Feedback();
		//printSimpleSet(results);
		//left: result couple						;right: examId
		prepareValidResults(results);
		boolean res = simpleSolver.isSolutionValid(feedback);
		if (!res) {
			System.out.println(feedback);
		}
		assertTrue(res);
	}

	private void prepareValidResults(List<ResultCouple> results) {
		results.get(0).addExam(simpleExamSession.getExams().get(0));  //put these three
		results.get(12).addExam(simpleExamSession.getExams().get(1)); //in big room 0 (also 1, ROOM_EXCLUSIVE)
		results.get(24).addExam(simpleExamSession.getExams().get(2)); // (cause they're bigger than 220)
		results.get(2).addExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(2).addExam(simpleExamSession.getExams().get(4)); //
		results.get(7).addExam(simpleExamSession.getExams().get(5)); //5, EXAM_COINCIDENCE, 6 + 
		results.get(6).addExam(simpleExamSession.getExams().get(6)); //4, EXCLUSION, 6
	}

	
	/**
	 * Exams with AFTER constraint are in the same period.
	 */
	@Test @Ignore
	public void isSolutionValid_AFTER_samePeriod() {
		
	}
	
	/**
	 * Exams with AFTER constraint are in the wrong order.
	 */
	@Test @Ignore
	public void isSolutionValid_AFTER_wrongOrder() {
		
	}
	
	/**
	 * Coinciding exams in different periods.
	 */
	@Test
	public void isSolutionValid_EXAM_COINCIDENCE() {
		List<ResultCouple> results = simpleSolution.getResult();
		Feedback feedback = new Feedback();
		prepareValidResults(results);
		//left: result couple						;right: examId
		results.get(2).removeExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(2).removeExam(simpleExamSession.getExams().get(4)); //
		
		results.get(0).addExam(simpleExamSession.getExams().get(3));
		results.get(12).addExam(simpleExamSession.getExams().get(4)); 
		boolean res = simpleSolver.isSolutionValid(feedback);
		assertFalse(res);
	}
	
	/**
	 * Mutually exclusive exams in the same period.
	 */
	@Test
	public void isSolutionValid_EXCLUSION() {
		List<ResultCouple> results = simpleSolution.getResult();
		Feedback feedback = new Feedback();
		prepareValidResults(results);
		//left: result couple						;right: examId
		results.get(6).removeExam(simpleExamSession.getExams().get(6));
		
		results.get(0).addExam(simpleExamSession.getExams().get(4));
		results.get(0).addExam(simpleExamSession.getExams().get(6)); 
		boolean res = simpleSolver.isSolutionValid(feedback);
		assertFalse(res);
	}
	
	/**
	 * Multiple exams in the same room while one of them has a ROOM_EXCLUSIVE constraint.
	 */
	@Test
	public void isSolutionValid_ROOM_EXCLUSIVE() {
		List<ResultCouple> results = simpleSolution.getResult();
		Feedback feedback = new Feedback();
		prepareValidResults(results);
		//left: result couple						;right: examId
		results.get(6).removeExam(simpleExamSession.getExams().get(6)); //4, EXCLUSION, 6
		
		results.get(12).addExam(simpleExamSession.getExams().get(6)); //in big room 0 (also 1, ROOM_EXCLUSIVE)
		boolean res = simpleSolver.isSolutionValid(feedback);
		assertFalse(res);
	}
	
	/**
	 * Too many exams in a single room.
	 */
	@Test
	public void isSolutionValid_RoomSize() {
		List<ResultCouple> results = simpleSolution.getResult();
		Feedback feedback = new Feedback();
		prepareValidResults(results);
		results.get(0).removeExam(simpleExamSession.getExams().get(0));  //put these three
		results.get(12).removeExam(simpleExamSession.getExams().get(1)); //in big room 0 (also 1, ROOM_EXCLUSIVE)
		results.get(24).removeExam(simpleExamSession.getExams().get(2)); // (cause they're bigger than 220)
		results.get(2).removeExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(2).removeExam(simpleExamSession.getExams().get(4)); //
		results.get(7).removeExam(simpleExamSession.getExams().get(5)); //5, EXAM_COINCIDENCE, 6 + 
		results.get(6).removeExam(simpleExamSession.getExams().get(6)); //4, EXCLUSION, 6
		
		results.get(0).addExam(simpleExamSession.getExams().get(0));  //put these three
		results.get(12).addExam(simpleExamSession.getExams().get(1)); //in big room 0 (also 1, ROOM_EXCLUSIVE)
		results.get(0).addExam(simpleExamSession.getExams().get(2)); // (cause they're bigger than 220)
		results.get(2).addExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(2).addExam(simpleExamSession.getExams().get(4)); //
		results.get(7).addExam(simpleExamSession.getExams().get(5)); //5, EXAM_COINCIDENCE, 6 + 
		results.get(6).addExam(simpleExamSession.getExams().get(6)); //4, EXCLUSION, 6
		boolean res = simpleSolver.isSolutionValid(feedback);
		assertFalse(res);
	}
	
	/**
	 * A period is too small to host an exam.
	 */
	@Test
	public void isSolutionValid_Duration() {
		List<ResultCouple> results = simpleSolution.getResult();
		Feedback feedback = new Feedback();
		prepareValidResults(results);
		
		results.get(2).removeExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(2).removeExam(simpleExamSession.getExams().get(4)); //
		
		results.get(29).addExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(29).addExam(simpleExamSession.getExams().get(4)); //
		
		boolean res = simpleSolver.isSolutionValid(feedback);
		assertFalse(res);
	}

}
