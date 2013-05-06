package solve;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
 * Contains all tests related to the isSolutionValid method.
 * @author Adrien Droguet - Sara Tari
 * @see HardConstraintsSolver
 * @see HCS_Tests_slow
 */
public class HCV_Tests {
	
	private ExamSessionParser simpleParser;
	private Solution simpleSolution;
	private ExamSession simpleExamSession;
	private String simpleFileName = "res/simple_set.exam";
	private HardConstraintsValidator HCV;
	
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
		new HardConstraintsSolver(simpleSolution);
		
		HCV = new HardConstraintsValidator();
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
		boolean res = HCV.isSolutionValid(simpleSolution, feedback);
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
	@Test
	public void isSolutionValid_AFTER_wrongOrder() {
		List<ResultCouple> results = simpleSolution.getResult();
		Feedback feedback = new Feedback();
		prepareValidResults(results);
		
		results.get(2).removeExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(2).removeExam(simpleExamSession.getExams().get(4)); //
		results.get(7).removeExam(simpleExamSession.getExams().get(5)); //5, EXAM_COINCIDENCE, 6 + 
		results.get(6).removeExam(simpleExamSession.getExams().get(6)); //4, EXCLUSION, 6
		
		results.get(6).addExam(simpleExamSession.getExams().get(3)); //3, EXAM_COINCIDENCE, 4
		results.get(6).addExam(simpleExamSession.getExams().get(4)); //
		results.get(3).addExam(simpleExamSession.getExams().get(5)); //5, EXAM_COINCIDENCE, 6 + 
		results.get(2).addExam(simpleExamSession.getExams().get(6)); //4, EXCLUSION, 6
		
		boolean res = HCV.isSolutionValid(simpleSolution, feedback);
		printSimpleSet(simpleSolution.getResult());
		assertFalse(res);
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
		boolean res = HCV.isSolutionValid(simpleSolution, feedback);
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
		boolean res = HCV.isSolutionValid(simpleSolution, feedback);
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
		boolean res = HCV.isSolutionValid(simpleSolution, feedback);
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
		boolean res = HCV.isSolutionValid(simpleSolution, feedback);
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
		
		boolean res = HCV.isSolutionValid(simpleSolution, feedback);
		assertFalse(res);
	}
}
