package util;

import java.io.IOException;

import org.junit.Test;

import parse.ExamParsingException;
import parse.ExamSessionParser;
import struct.Exam;
import struct.ExamSession;

/**
 * The best class ever.
 * @author ULTRA POWERFUL
 *
 */
public class Zorg {
	
	private ExamSession es;
	private ExamSessionParser esp;
	private String fileName = "res/exam_comp_set1.exam";
	
	/**
	 * powerful
	 * @throws ExamParsingException
	 * @throws IOException
	 */
	@Test
	public void is() throws ExamParsingException, IOException {
		esp = new ExamSessionParser(fileName);
		es = esp.parse();
		
		for (Exam currentExam : es.getExamsAsList()) {
			System.out.println("id=" + currentExam.getId() + ";size=" + currentExam.getSize()
					+ ";period constraints:" + currentExam.getConstraints()
					+ ";The Matrix:");
		}
	}
}
