package util;

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

public class SolvingTests {

	private Solution s;
	private ExamSession es;
	private ExamSessionParser esp;
	private String fileName = "res/simple_set.exam";
	
	
	@Before
	public void setUp() throws ExamParsingException, IOException {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
		s = new Solution(es);
	}
	
	@Test
	public void checkCoincidence() {
		List<Integer> res = Solving.checkCoincidence(s, 3);
		assertNotNull(res);
		assertTrue(res.size() == 2);
		assertTrue(res.contains(3));
		assertTrue(res.contains(4));
	}
	
	@Test
	public void canHost_singleTrue() {
		boolean res = Solving.canHost(s, 1, 1, s.getResult());
		assertTrue(res);
	}
	
	@Test
	public void canHost_singleFalse() {
		List<ResultCouple> results = s.getResult();
		for (ResultCouple current : results) {
			for (int i = 0; i < 7; i++) {
				current.addExam(es.getExams().get(i));
			}
		}
		boolean res = Solving.canHost(s, 0, 0, s.getResult());
		assertFalse(res);
	}
	
	@Test
	public void canHost_singleFalse_roomExclusive() {
		List<ResultCouple> results = s.getResult();
		for (ResultCouple current : results) {
			for (int i = 0; i < 7; i++) {
				current.addExam(es.getExams().get(i));
			}
		}
		ResultCouple first = results.get(0);
		first.getExamList().removeAll(first.getExamList());
		Exam e_normal = es.getExams().get(0);
		first.addExam(e_normal);
		boolean res = Solving.canHost(s, 1, 0, s.getResult());
		assertFalse(res);
	}
	
	@Test
	public void canHost_multiple_OK() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(3);
		boolean res = Solving.canHost(s, exams, 0, s.getResult());
		assertTrue(res);
	}
	
	@Test
	public void canHost_multiple_allRoomFull() {
		List<Integer> exams = new ArrayList<Integer>();
		List<ResultCouple> results = s.getResult();
		for (ResultCouple currentRC : results) {
			currentRC.addExam(es.getExams().get(0));
		}
		exams.add(0);
		boolean res = Solving.canHost(s, exams, 0, (ArrayList<ResultCouple>) s.getResult());
		assertFalse(res);
	}
	
	@Test
	public void findSuitable_single_normal() {
		int res = Solving.findSuitable(s, 0, 0, s.getResult()).get(0);
		assertTrue(res != -1);
		assertTrue(res == 0);
	}
	
	@Test @Ignore
	public void findSuitable_single_roomNotFull() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		int roomId = first.getRoom().getId();
		int periodId = first.getPeriod().getId();
		first.addExam(es.getExams().get(0));
		//exam 13's size = rather small
		fail("TOREDO");
		int res = Solving.findSuitable(s, 3, periodId, s.getResult()).get(0);
		assertTrue(res != -1);
		assertTrue(res == roomId);
	}
	
	@Test @Ignore
	public void findSuitable_multiple_sameRoom() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(3);
		exams.add(4);
		fail("TOREDO");
		List<Integer> res = Solving.findSuitable(s, exams, 0, s.getResult());
		assertTrue(res.size() == 2);
		assertTrue(res.get(0) == 0);
		assertTrue(res.get(1) == res.get(0));
	}
	
	@Test @Ignore
	public void findSuitable_multiple_differentRooms() {
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		fail("TOREDO");
		List<Integer> res = Solving.findSuitable(s, exams, 0, s.getResult());
		assertTrue(res.get(0) != res.get(1));
		assertTrue(res.get(0) == 0);
		assertTrue(res.get(1) == 1);
	}
	
	@Test @Ignore
	public void findSuitable_multiple_oneAndTwoOccupied() {
		List<ResultCouple> results = s.getResult();
		Exam e0 = es.getExams().get(0);
		Exam e1 = es.getExams().get(1);
		results.get(0).addExam(e0);
		results.get(1).addExam(e1);
		
		List<Integer> exams = new ArrayList<Integer>();
		exams.add(0);
		exams.add(1);
		fail("TOREDO");
		List<Integer> res = Solving.findSuitable(s, exams, 0, s.getResult());
		assertFalse(res.contains(0));
		assertFalse(res.contains(1));
	}
	
	@Test @Ignore
	public void getAvailablePeriod_single_normal() {
		fail("TODO");
		//assertTrue(solver.getAvailablePeriod(0, s.getResult()) == 0);
	}
	
	@Test @Ignore
	public void getAvailablePeriod_single_mutuallyExclusiveExams() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		first.addExam(es.getExams().get(4));
		fail("TODO");
		//int res = solver.getAvailablePeriod(6, results, s.getResult());
		//assertTrue(res != 0);
		//assertTrue(res == 1);
	}
	
	@Test @Ignore
	public void getAvailablePeriod_multiple() {
		List<Integer> coincidingExams = new ArrayList<Integer>();
		coincidingExams.add(0);
		coincidingExams.add(1);
		fail("TODO");
		//int res = solver.getAvailablePeriod(coincidingExams, s.getResult(), s.getResult());
		//assertTrue(res == 0);
	}
	
	@Test @Ignore
	public void getAvailablePeriod_multiple_mutuallyExclusiveExams() {
		List<ResultCouple> results = s.getResult();
		ResultCouple first = results.get(0);
		first.addExam(es.getExams().get(4));
		List<Integer> list = new ArrayList<Integer>();
		list.add(6);
		list.add(3);
		fail("TODO");
		//int res = solver.getAvailablePeriod(list, results, s.getResult());
		//assertTrue(res != 0);
		//assertTrue(res == 1);
	}
	
	/**
	 * 3 can be placed in period 0 room 4, but 4 can't.
	 */
	@Test
	public void getAvailablePeriod_multiple_tooSmallForBoth() {
		List<ResultCouple> res = s.getResult();
		ResultCouple target = null;
		//fill up period 0's rooms
		for (ResultCouple rc : res) {
			if (rc.getPeriod().getId() == 0) {
				rc.addExam(0);
				rc.addExam(1);
				//target = room 4 (size=40)
				if (rc.getRoom().getId() == 4)
					target = rc;
			}
		}
		target.getExamList().clear();
		fail("TODO");
		//int available = Solving.getAvailablePeriod(s, 3, res);
		//System.out.println(available);
		//assertFalse(available.contains(0));
	}
}
