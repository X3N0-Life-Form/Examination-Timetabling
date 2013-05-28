package solve;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import struct.Exam;
import struct.Solution;
import util.Serialization;

public class ILSS_Tests_Random {
	
	private Solution s;
	private IteratedLocalSearchSolver solver;

	@Before
	public void setUp() throws Exception {
		s = Serialization.loadSolution(Serialization.set4SerializedName);
		solver = new IteratedLocalSearchSolver(s);
	}
	
	@SuppressWarnings("unused")
	private void printSolutionExams(Solution solution) {
		for (Exam exam : solution.getExamSession().getExamsAsList()) {
			System.out.println("id=" + exam.getId() + "; size=" + exam.getSize());
		}
	}
	
	@Test @Ignore
	public void testSolve() {
		
	}
}
