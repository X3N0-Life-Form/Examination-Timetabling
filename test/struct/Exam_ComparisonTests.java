package struct;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import parse.ExamParsingException;
import parse.ExamSessionParser;

public class Exam_ComparisonTests {
	
	private ExamSessionParser normalParser;
	private Solution normalSolution;
	private ExamSession normalExamSession;
	private String normalFileName = "res/exam_comp_set1.exam";
	
	@Before
	public void normalSolutionSetup() throws ExamParsingException, IOException {
		normalParser = new ExamSessionParser(normalFileName);
		normalExamSession = normalParser.parse();
		normalSolution = new Solution(normalExamSession);
	}

	@Test
	public void test_compare() {
		List<Exam> NPE = normalSolution.getNonPlacedExams();
		System.out.println("NPE");
		System.out.println(NPE);
		System.out.println("Exam TreeMap");
		System.out.println(normalExamSession.getExams());
		System.out.println("Exam as List");
		System.out.println(normalExamSession.getExamsAsList());
		assertTrue(true);
	}

}
