package parse;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ExamSessionPerserTests_flexibility {
	
	private String fileName = "res/simple_set_goofy.exam";
	private ExamSessionParser esp;
	
	@Before
	public void setUp() {
		esp = new ExamSessionParser(fileName);
	}
	
	/**
	 * If no exception is thrown, the test passes.
	 * @throws ExamParsingException
	 * @throws IOException
	 */
	@Test
	public void test() throws ExamParsingException, IOException {
		esp.parse();
		assertTrue(true);
	}

}
