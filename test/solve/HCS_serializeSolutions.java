package solve;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import parse.ExamParsingException;
import parse.ExamSessionParser;
import struct.ExamSession;
import struct.Solution;

public class HCS_serializeSolutions {

	///////////////////////////////////////////////////////////
	// res/simple_set.exam - res/solutions/simple_set.object //
	///////////////////////////////////////////////////////////
	private ExamSessionParser simpleParser;
	private Solution simpleSolution;
	private ExamSession simpleExamSession;
	private String simpleFileName = "res/simple_set.exam";
	private HardConstraintsSolver simpleSolver;
	private String simpleSerializedName = "res/solutions/simple_set.object";
	
	///////////////////////////////////////////////////////////////////
	// res/exam_comp_set4.exam - res/solutions/exam_comp_set4.object //
	///////////////////////////////////////////////////////////////////
	private ExamSessionParser set4Parser;
	private Solution set4Solution;
	private ExamSession set4ExamSession;
	private String set4FileName = "res/exam_comp_set4.exam";
	private HardConstraintsSolver set4Solver;
	private String set4SerializedName = "res/solutions/exam_comp_set4.object";
	
	/**
	 * Remove previously serialized objects.
	 */
	@Before
	public void tearDown() {
		new File(simpleSerializedName).delete();
		new File(set4SerializedName).delete();
	}
	//TODO: time stamp the files
	
	@Test @Ignore
	public void simpleSet() throws ExamParsingException, IOException, SolvingException {
		File file = new File(simpleSerializedName);
		//safety
		assertFalse(file.exists());
		
		simpleParser = new ExamSessionParser(simpleFileName);
		simpleExamSession = simpleParser.parse();
		simpleSolution = new Solution(simpleExamSession);
		simpleSolver = new HardConstraintsSolver(simpleSolution);
		Solution toSave = simpleSolver.solve();
		
		FileOutputStream fos = new FileOutputStream(simpleSerializedName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(toSave);
		oos.close();
		fos.close();
		
		assertTrue(file.exists());
		
		//TODO: make it work
	}
	
	@Test
	public void set4() throws IOException, ExamParsingException, SolvingException, ClassNotFoundException {
		File file = new File(set4SerializedName);
		//safety
		//assertFalse(file.exists());
		
		set4Parser = new ExamSessionParser(set4FileName);
		set4ExamSession = set4Parser.parse();
		set4Solution = new Solution(set4ExamSession);
		set4Solver = new HardConstraintsSolver(set4Solution);
		Solution toSave = set4Solver.solve();
		
		FileOutputStream fos = new FileOutputStream(set4SerializedName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(toSave);
		oos.close();
		fos.close();
		
		assertTrue(file.exists());
		
		FileInputStream fis = new FileInputStream(set4SerializedName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Solution toLoad = (Solution) ois.readObject();
		ois.close();
		fis.close();
		
		assertNotNull(toLoad);
		assertEquals(toSave, toLoad);//TODO: Solution.equals - lol
	}

}
