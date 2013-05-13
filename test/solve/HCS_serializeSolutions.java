package solve;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.BeforeClass;
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
	private static final String simpleSerializedName = "res/solutions/simple_set.object";
	
	///////////////////////////////////////////////////////////////////
	// res/exam_comp_set4.exam - res/solutions/exam_comp_set4.object //
	///////////////////////////////////////////////////////////////////
	private ExamSessionParser set4Parser;
	private Solution set4Solution;
	private ExamSession set4ExamSession;
	private String set4FileName = "res/exam_comp_set4.exam";
	private HardConstraintsSolver set4Solver;
	private static final String set4SerializedName = "res/solutions/exam_comp_set4.object";
	
	///////////////////////////////////////////////////////////////////
	// res/exam_comp_set1.exam - res/solutions/exam_comp_set1.object //
	///////////////////////////////////////////////////////////////////
	private ExamSessionParser set1Parser;
	private Solution set1Solution;
	private ExamSession set1ExamSession;
	private String set1FileName = "res/exam_comp_set1.exam";
	private HardConstraintsSolver set1Solver;
	private static final String set1SerializedName = "res/solutions/exam_comp_set1.object";
	
	///////////////////////////////////////////////////////////////////
	// res/exam_comp_set2.exam - res/solutions/exam_comp_set2.object //
	///////////////////////////////////////////////////////////////////
	private ExamSessionParser set2Parser;
	private Solution set2Solution;
	private ExamSession set2ExamSession;
	private String set2FileName = "res/exam_comp_set2.exam";
	private HardConstraintsSolver set2Solver;
	private static final String set2SerializedName = "res/solutions/exam_comp_set2.object";
	
	///////////////////////////////////////////////////////////////////
	// res/exam_comp_set3.exam - res/solutions/exam_comp_set3.object //
	///////////////////////////////////////////////////////////////////
	private ExamSessionParser set3Parser;
	private Solution set3Solution;
	private ExamSession set3ExamSession;
	private String set3FileName = "res/exam_comp_set3.exam";
	private HardConstraintsSolver set3Solver;
	private static final String set3SerializedName = "res/solutions/exam_comp_set3.object";
	
	/**
	 * Remove previously serialized objects.
	 */
	@BeforeClass
	public static void tearDownBeforeClass() {
		new File(simpleSerializedName).delete();
		new File(set4SerializedName).delete();
		new File(set1SerializedName).delete();
		new File(set2SerializedName).delete();
		new File(set3SerializedName).delete();
	}
	//TODO: time stamp the files
	
	@Test @Ignore
	public void simpleSet() throws ExamParsingException, IOException, SolvingException {
		File file = new File(simpleSerializedName);
		//safety
		if (file.exists())
			fail(simpleSerializedName + " exists");
		
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
		
	}
	
	@Test
	public void set4() throws IOException, ExamParsingException, SolvingException, ClassNotFoundException {
		File file = new File(set4SerializedName);
		//safety
		if (file.exists())
			fail(set4SerializedName + " exists");
		
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
		assertTrue(toSave.equals(toLoad));
	}
	
	@Test @Ignore
	public void set1() throws IOException, ExamParsingException, SolvingException, ClassNotFoundException {
		File file = new File(set1SerializedName);
		//safety
		if (file.exists())
			fail(set1SerializedName + " exists");
		
		set1Parser = new ExamSessionParser(set1FileName);
		set1ExamSession = set1Parser.parse();
		set1Solution = new Solution(set1ExamSession);
		set1Solver = new HardConstraintsSolver(set1Solution);
		Solution toSave = set1Solver.solve();
		
		FileOutputStream fos = new FileOutputStream(set1SerializedName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(toSave);
		oos.close();
		fos.close();
		
		assertTrue(file.exists());
		
		FileInputStream fis = new FileInputStream(set1SerializedName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Solution toLoad = (Solution) ois.readObject();
		ois.close();
		fis.close();
		
		assertNotNull(toLoad);
		assertEquals(toSave, toLoad);
	}
	
	@Test @Ignore
	public void set2() throws IOException, ExamParsingException, SolvingException, ClassNotFoundException {
		File file = new File(set2SerializedName);
		//safety
		if (file.exists())
			fail(set2SerializedName + " exists");
		
		set2Parser = new ExamSessionParser(set2FileName);
		set2ExamSession = set2Parser.parse();
		set2Solution = new Solution(set2ExamSession);
		set2Solver = new HardConstraintsSolver(set2Solution);
		Solution toSave = set2Solver.solve();
		
		FileOutputStream fos = new FileOutputStream(set2SerializedName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(toSave);
		oos.close();
		fos.close();
		
		assertTrue(file.exists());
		
		FileInputStream fis = new FileInputStream(set2SerializedName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Solution toLoad = (Solution) ois.readObject();
		ois.close();
		fis.close();
		
		assertNotNull(toLoad);
		assertEquals(toSave, toLoad);
	}
	
	@Test @Ignore
	public void set3() throws IOException, ExamParsingException, SolvingException, ClassNotFoundException {
		File file = new File(set3SerializedName);
		//safety
		if (file.exists())
			fail(set3SerializedName + " exists");
		
		set3Parser = new ExamSessionParser(set3FileName);
		set3ExamSession = set3Parser.parse();
		set3Solution = new Solution(set3ExamSession);
		set3Solver = new HardConstraintsSolver(set3Solution);
		Solution toSave = set3Solver.solve();
		
		FileOutputStream fos = new FileOutputStream(set3SerializedName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(toSave);
		oos.close();
		fos.close();
		
		assertTrue(file.exists());
		
		FileInputStream fis = new FileInputStream(set3SerializedName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Solution toLoad = (Solution) ois.readObject();
		ois.close();
		fis.close();
		
		assertNotNull(toLoad);
		assertEquals(toSave, toLoad);
	}

}
