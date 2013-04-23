package solve;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import parse.ExamParsingException;
import parse.ExamSessionParser;

import struct.Exam;
import struct.ExamSession;
import struct.ResultCouple;
import struct.Solution;

/**
 * These tests take a long time to run.
 * @author Adrien Droguet - Sara Tari
 * @see HardConstraintsSolver
 * @see HardConstraintsValidatorTests
 */
public class HardConstraintsSolverTests_slow {
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
	
	@Test
	public void checkCoincidence() {
		List<Integer> res = solver.checkCoincidence(306);
		assertNotNull(res);
		assertTrue(res.size() == 2);
		assertTrue(res.get(0) == 307);
		assertTrue(res.get(1) == 306);
	}

	@Test
	public void canHost_singleTrue() {
		boolean res = solver.canHost(1, 1, s.getResult());
		assertTrue(res);
	}
	
	@Test
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
	
	@Test
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
	
	@Test
	public void canHost_multipleOK() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		boolean res = solver.canHost(exams, 0, (ArrayList<ResultCouple>) s.getResult());
		assertTrue(res);
	}
	
	@Test
	public void canHost_multipleAllRoomFull() {
		List<Integer> exams = new ArrayList<Integer>();
		List<ResultCouple> results = s.getResult();
		for (ResultCouple currentRC : results) {
			currentRC.addExam(es.getExams().get(0));
		}
		exams.add(0);
		boolean res = solver.canHost(exams, 0, (ArrayList<ResultCouple>) s.getResult());
		assertFalse(res);
	}
	
	@Test
	public void findSuitable_singleNormal() {
		int res = solver.findSuitable(0, 0, s.getResult()).get(0);
		assertTrue(res != -1);
		assertTrue(res == 0);
	}
	
	@Test
	public void findSuitable_singleRoomNotFull() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		int roomId = first.getRoom().getId();
		int periodId = first.getPeriod().getId();
		first.addExam(es.getExams().get(0));
		//exam 13's size = 4
		int res = solver.findSuitable(13, periodId, s.getResult()).get(0);
		assertTrue(res != -1);
		assertTrue(res == roomId);
	}
	
	@Test
	public void findSuitable_multipleSameRoom() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(306);
		exams.add(307);
		List<Integer> res = solver.findSuitable(exams, 0, s.getResult());
		assertTrue(res.size() == 2);
		assertTrue(res.get(0) == 0);
		assertTrue(res.get(1) == res.get(0));
	}
	
	@Test
	public void findSuitable_multipleDifferentRooms() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		List<Integer> res = solver.findSuitable(exams, 0, s.getResult());
		assertTrue(res.get(0) != res.get(1));
		assertTrue(res.get(0) == 0);
		assertTrue(res.get(1) == 1);
	}
	
	@Test
	public void findSuitable_multipleOneAndTwoOccupied() {
		List<ResultCouple> results = s.getResult();
		Exam e0 = es.getExams().get(0);
		Exam e1 = es.getExams().get(1);
		results.get(0).addExam(e0);
		results.get(1).addExam(e1);
		
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		List<Integer> res = solver.findSuitable(exams, 0, s.getResult());
		assertFalse(res.contains(0));
		assertFalse(res.contains(1));
	}
	
	@Test
	public void getAvailablePeriod_singleNormal() {
		fail("TODO");
		//assertTrue(solver.getAvailablePeriod(0, s.getResult()) == 0);
	}
	
	@Test
	public void getAvailablePeriod_singleMutuallyExclusiveExams() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		first.addExam(es.getExams().get(856));
		fail("TODO");
		/*int res = solver.getAvailablePeriod(246, results, s.getResult());
		assertTrue(res != 0);
		assertTrue(res == 1);
		/**/
	}
	
	@Test
	public void getAvailablePeriod_multiple() {
		List<Integer> coincidingExams = new ArrayList<Integer>();
		coincidingExams.add(0);
		coincidingExams.add(1);
		fail("TODO");
		//int res = solver.getAvailablePeriod(coincidingExams, s.getResult(), s.getResult());
		//assertTrue(res == 0);
	}
	
	@Test
	public void getAvailablePeriod_multipleMutuallyExclusiveExams() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		first.addExam(es.getExams().get(856));
		List<Integer> list = new ArrayList<Integer>();
		list.add(246);
		list.add(13);
		fail("TODO");
		/*int res = solver.getAvailablePeriod(list, results, s.getResult());
		assertTrue(res != 0);
		assertTrue(res == 1);
		/**/
	}
	
	

}
