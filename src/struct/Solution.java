package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * 
 * @author Adrien Droguet - Sara Tari
 *
 */
public class Solution implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1550068663997242407L;
	private int[][] examCoincidence;
	private int[][] examPeriodBase;
	private int[][] examPeriodModif;
	private int[][] examRoom;	
	private List<Exam> nonPlacedExams = null;
	private List<ResultCouple> result = null;
	private List<Exam> coincidingExams;
	private ExamSession examSession;
	private List<Exam> afterExams;
	private TreeMap<Integer, Student> students;
	
	public TreeMap<Integer,Student> getStudentList() {
		return students;
	}
	
	public void setStudentList(TreeMap<Integer, Student> studentList) {
		this.students = studentList;
	}
	
	public int[][] getExamPeriodBase() {
		return examPeriodBase;
	}
	public void setExamPeriodBase(int[][] examPeriod) {
		this.examPeriodBase = examPeriod;
	}
	
	public int[][] getExamPeriodModif() {
		return examPeriodModif;
	}
	
	public List<Exam> getNonPlacedExams() {
		return nonPlacedExams;
	}
	public void setNonPlacedExams(List<Exam> nonPlacedExams) {
		this.nonPlacedExams = nonPlacedExams;
	}
	
	public List<ResultCouple> getResult() {
		return result;
	}	
	
	public void setResult(ArrayList<ResultCouple> result) {
		this.result = result;
	}
	
	public ExamSession getExamSession() {
		return examSession;
	}
	public void setExamSession(ExamSession examSession) {
		this.examSession = examSession;
	}
	
	public int[][] getExamCoincidence() {
		return examCoincidence;
	}

	public void setExamCoincidence(int[][] examCoincidence) {
		this.examCoincidence = examCoincidence;
	}
	
	public List<Exam> getAfterExams() {
		return afterExams;
	}
	

	public List<Exam> getCoincidingExams() {
		return coincidingExams;
	}
	public void setCoincidingExams(List<Exam> coincidingExams) {
		this.coincidingExams = coincidingExams;
	}
	
	public List<ResultCouple> getResultsForPeriod(int periodId) {
		List<ResultCouple> res = new ArrayList<ResultCouple>();
		for (ResultCouple currentRC : result) {
			if (currentRC.getPeriod().getId() == periodId) {
				res.add(currentRC);
			}
		}
		return res;
	}
	/**
	 * 
	 * @param periodId
	 * @param resIn
	 * @return A List of ResultCouple of the specified period out ResultCouple
	 * found in resIn.
	 */
	public List<ResultCouple> getResultsForPeriod(int periodId, List<ResultCouple> resIn) {
		List<ResultCouple> resOut = new ArrayList<ResultCouple>();
		for (ResultCouple currentRC : resIn) {
			if (currentRC.getPeriod().getId() == periodId) {
				resOut.add(currentRC);
			}
		}
		return resOut;
	}

	
	/**
	 * Must at least be passed through a HardConstraintSolver
	 * after being created.
	 * @param examSession
	 */
	public Solution(ExamSession examSession) {
		this.examSession = examSession;
		
		/////////////////////
		// initializations //
		/////////////////////
		int numberOfExams = examSession.getExams().size();
		examCoincidence = new int[numberOfExams][numberOfExams];
		int numberOfPeriods = examSession.getPeriods().size();
		examPeriodBase = new int[numberOfExams][numberOfPeriods];
		int numberOfRooms = examSession.getRooms().size();
		examRoom = new int[numberOfExams][numberOfRooms];
		
		result = new ArrayList<ResultCouple>();
		for (Period currentPeriod : examSession.getPeriods()) {
			for (Room currentRoom : examSession.getRooms()) {
				ResultCouple nuRC = new ResultCouple(currentRoom, currentPeriod);
				nuRC.setSolution(this);
				result.add(nuRC);
			}
		}
		
		/////////////////////////////////////
		// create constraint-related lists //
		/////////////////////////////////////
		initConstraintLists(examSession);
		
		/**
		 * fills examCoincidence 
		 */
		
		/**
		 * fills examCoincidence (full of 1 )
		 */
		System.out.println("--fills examCoincidence (full of 1 )");
		for (int i = 0; i < numberOfExams; i++) 
			for (int j = 0; j < numberOfExams; j++)
				examCoincidence [i][j] = 1;
		/**
		 * if a student takes exam i & exam j then examCoincidence = 0
		 */
		//TODO:something about that loop fest
		System.out.print("--Finding mutually exclusive exams based on student presence");
		int loopCounter = 0;
		for (int i = 0; i < numberOfExams; i++) {
			for (int j = i + 1; j < numberOfExams ; j++) {
				int eiNumberOfStudents = examSession.getExams().get(i).getSize();
				int ejNumberOfStudents = examSession.getExams().get(j).getSize();
				for (int ei = 0 ; ei < eiNumberOfStudents; ei ++)
					for (int ej = 0 ; ej < ejNumberOfStudents; ej++) {
						//if student is present in both exams --> no coincidence 
						if (examSession.getExams().get(i).getStudents().get(ei) == 
							examSession.getExams().get(j).getStudents().get(ej)) {
								examCoincidence [i][j] = 0;
								break;
						}
						loopCounter++;
					}
			}
		}
		System.out.println(" - done\n--Looped " + loopCounter + " times.\n");
		
		/**
		 * if exam i and j can't be on the same period
		 */
		
		for(int i = 0; i< examSession.getExams().size();i++)
			for (int j = 0 ; j < examSession.getExams().get(i).getConstraints().size(); j++)
				if ( examSession.getExams().get(i).getConstraints().get(j).getConstraint() == EPeriodHardConstraint.EXCLUSION)
					examCoincidence[i][examSession.getExams().get(i).getConstraints().get(j).getE2Id()] = 0;
		/**
		 * if exam i and j have to be on the same period
		 */		
		
		for(int i = 0; i< examSession.getExams().size();i++)
			for (int j = 0 ; j < examSession.getExams().get(i).getConstraints().size(); j++)
				if ( examSession.getExams().get(i).getConstraints().get(j).getConstraint() == EPeriodHardConstraint.EXAM_COINCIDENCE)
					examCoincidence[i][examSession.getExams().get(i).getConstraints().get(j).getE2Id()] = 2;	
		
		/**
		 * fills examPeriod
		 */
		
		initFillExamPeriod(examSession, numberOfExams, numberOfPeriods);
		
		/**
		 * fills examRoom
		 */
		initFillExamRoom(examSession);
			
		/**
		 * initialise results 
		 */
		nonPlacedExams = examSession.getExamsAsList();
		Collections.sort(nonPlacedExams);
		
		/////////////////////////
		// create Student list //
		/////////////////////////
		students = new TreeMap<Integer, Student>();
		for (Exam currentExam : examSession.getExamsAsList()) {
			for (Integer currentStudent : currentExam.getStudents()) {
				if (!students.containsKey(currentStudent)) {
					students.put(currentStudent, new Student(currentStudent));
				}
				//note: not checking if exam already present
				students.get(currentStudent).addExamId(currentExam.getId());
			}
		}
	}

	private void initConstraintLists(ExamSession examSession) {
		afterExams = new ArrayList<Exam>();
		coincidingExams = new ArrayList<Exam>();
		for (Exam currentExam : examSession.getExamsAsList()) {
			for (PeriodHardConstraint c : currentExam.getConstraints()) {
				if (c.getConstraint() == EPeriodHardConstraint.AFTER) {
					afterExams.add(currentExam);
				} else if (c.getConstraint() ==
						EPeriodHardConstraint.EXAM_COINCIDENCE) {
					coincidingExams.add(currentExam);
				}
			}
		}
	}

	private void initFillExamPeriod(ExamSession examSession, int numberOfExams,
			int numberOfPeriods) {
		for (int i = 0; i< examSession.getExams().size();i++) {
			for (int j = 0; j< examSession.getPeriods().size(); j++){
				/**
				 * cost = 0 & duration ok
				 */
				if ((examSession.getExams().get(i).getDuration() <= examSession.getPeriods().get(j).getDuration() &&
						examSession.getPeriods().get(j).getCost() == 0))
							examPeriodBase[i][j] = 2;
				/**
				 * cost > 0 & duration ok
				 */
				else if ((examSession.getExams().get(i).getDuration() <= examSession.getPeriods().get(j).getDuration() &&
						examSession.getPeriods().get(j).getCost() > 0))
						examPeriodBase[i][j] = 1;
				/*
				 * duration not ok
				 */
				else 
					examPeriodBase[i][j] = 0;
			}
		}
		examPeriodModif = new int[numberOfExams][numberOfPeriods];
		for (int i = 0; i < numberOfExams; i++) {
			examPeriodModif[i] =
					Arrays.copyOfRange(examPeriodBase[i], 0, numberOfPeriods);
		}
	}

	private void initFillExamRoom(ExamSession examSession) {
		for (int i = 0; i <examSession.getExams().size() ; i++)
			for (int j =0; j< examSession.getRooms().size(); j++)
				if ((examSession.getExams().get(i).getSize() <= examSession.getRooms().get(j).getSize() &&
				examSession.getRooms().get(j).getCost() == 0))
					examRoom[i][j] = 2;
				else if ((examSession.getExams().get(i).getSize() <= examSession.getRooms().get(j).getSize() &&
				examSession.getRooms().get(j).getCost() > 0))
					examRoom[i][j] = 1;
				else 
					examRoom[i][j] = 0;
	}
	
	
	/**
	 * 
	 * @return A sorted list of all exams with an AFTER constraint.
	 */
	public List<Exam> getBeforeExams() {
		List<Exam> beforeExams = new ArrayList<Exam>();
		
		for (Exam currentExam : examSession.getExamsAsList()) {
			if (currentExam.hasPeriodHardConstraint(EPeriodHardConstraint.AFTER)) {
				beforeExams.add(currentExam);
				//add second exam to the list as well
				for (PeriodHardConstraint constraint : currentExam.getConstraints()) {
					if (constraint.getConstraint() == EPeriodHardConstraint.AFTER
							&& !beforeExams.contains(examSession.getExams().get(constraint.getE2Id()))) {
						beforeExams.add(examSession.getExams().get(constraint.getE2Id()));
					}
				}
			}
		}
		
		Collections.sort(beforeExams, new Comparator<Exam>() {

			/**
			 * Compares two exams according to their AFTER constraints
			 * @param o1
			 * @param o2
			 * @return 1 if o1 AFTER o2,
			 * -1 if o2 AFTER o1,
			 * 0 if these exams have nothing in common
			 */
			@Override
			public int compare(Exam o1, Exam o2) {
				int id1 = o1.getId();
				int id2 = o2.getId();
				//check o1
				for (PeriodHardConstraint o1c : o1.getConstraints()) {
					if (o1c.getConstraint() == EPeriodHardConstraint.AFTER) {
						if (o1c.getE1Id() == id1 && o1c.getE2Id() == id2) {
							//if o1 AFTER o2
							return 1;
						} else if (o1c.getE1Id() == id2 && o1c.getE2Id() == id1) {
							//if o2 AFTER o1
							return -1;
						}
					}
				}
				
				//check o2
				for (PeriodHardConstraint o2c : o2.getConstraints()) {
					if (o2c.getConstraint() == EPeriodHardConstraint.AFTER) {
						if (o2c.getE1Id() == id1 && o2c.getE2Id() == id2) {
							//if o1 AFTER o2
							return 1;
						} else if (o2c.getE1Id() == id2 && o2c.getE2Id() == id1) {
							//if o2 AFTER o1
							return -1;
						}
					}
				}				
				//these exams have no AFTER in common, ==
				return 0;
			}
			
		});
		
		return beforeExams;
	}
	
}
