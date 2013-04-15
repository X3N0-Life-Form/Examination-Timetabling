package struct;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import parse.ExamSessionParser;

public class ExamSessionTests {
	
	private ExamSession es;
	private ExamSessionParser esp;
	private String fileName = "res/exam_comp_set2.exam";
	
	@Before
	public void setUp() throws Exception {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
	}
	
	@Test
	public void testGetExamsAsList() {
		List<Exam> list = es.getExamsAsList();
		assertNotNull(list);
		assertTrue(list.size() == 870);
	}

}
