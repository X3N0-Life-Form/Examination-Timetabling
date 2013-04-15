package solve;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
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
		boolean res = solver.canHost(1, 1);
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
		boolean res = solver.canHost(800, 0);
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
		boolean res = solver.canHost(78, 0);
		assertFalse(res);
	}
	
	//TODO:canHost_multiple
	
	@Test @Ignore
	public void findSuitable_singleNormal() {
		int res = solver.findSuitable(0, 0);
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
		int res = solver.findSuitable(1, periodId);
		System.out.println("roomId=" + roomId + ";res=" + res);
		assertTrue(res != -1);
		assertTrue(res == roomId);
	}
	
	//TODO:
	//isSolutionValid()
	//getAvailablePeriod (les 2)

	//findSuitable (les deux)

}
