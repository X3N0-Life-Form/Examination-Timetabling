package parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import struct.EPeriodHardConstraint;
import struct.ERoomHardConstraint;
import struct.Exam;
import struct.ExamSession;
import struct.InstitutionalWeightings;
import struct.Period;
import struct.PeriodHardConstraint;
import struct.Room;
import struct.RoomHardConstraint;

/**
 * This class is in charge of parsing exam data set files, and generate an
 * ExamSession object matching that file.
 * @author Adrien Droguet
 * @see ExamSession
 */
public class ExamSessionParser {
	public static final String IW_FRONTLOAD = "FRONTLOAD";
	public static final String IW_NONMIXEDDURATIONS = "NONMIXEDDURATIONS";
	public static final String IW_PERIODSPREAD = "PERIODSPREAD";
	public static final String IW_TWOINADAY = "TWOINADAY";
	public static final String IW_TWOINAROW = "TWOINAROW";
	public static final String PHC_AFTER = "AFTER";
	public static final String PHC_EXCLUSION = "EXCLUSION";
	public static final String PHC_EXAM_COINCIDENCE = "EXAM_COINCIDENCE";
	
	public static final String RHC_ROOM_EXCLUSIVE = "ROOM_EXCLUSIVE";
	
	public static final String ENTRY_EXAMS = "Exams:";
	public static final String ENTRY_PERIODS = "Periods:";
	public static final String ENTRY_ROOMS = "Rooms:";
	public static final String ENTRY_PERIOD_HARD_CONSTRAINTS = "PeriodHardConstraints";
	public static final String ENTRY_ROOM_HARD_CONSTRAINTS = "RoomHardConstraints";
	public static final String ENTRY_INSTITUTIONAL_WEIGHTINGS = "InstitutionalWeightings";
	
	private String fileName;
	
	public ExamSessionParser(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * 
	 * @return ExamSession object matching the specified data set file.
	 * @throws ExamParsingException
	 * @throws IOException
	 */
	public ExamSession parse() throws ExamParsingException, IOException {
		File sessionFile = new File(fileName);
		try {
			FileInputStream fis = new FileInputStream(sessionFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			String line = "";
			ArrayList<Exam> exams = null;
			ArrayList<Period> periods = null;
			ArrayList<Room> rooms = null;
			ArrayList<PeriodHardConstraint> periodHardConstraints = null;
			ArrayList<RoomHardConstraint> roomHardConstraints = null;
			InstitutionalWeightings institutionalWeightings = null;
			System.out.println("Parsing file:" + fileName);
			while ((line = reader.readLine()) != null) {
				//TODO: utiliser des regexs, ce sera plus propre
				//line.matches("[Exams:]");
				if (line.contains(ENTRY_EXAMS)) {
					exams = parseExams(reader, line);
				} else if (line.contains(ENTRY_PERIODS)) {
					periods = parsePeriods(reader, line);
				} else if (line.contains(ENTRY_ROOMS)) {
					rooms = parseRooms(reader, line);
				} else if (line.contains(ENTRY_PERIOD_HARD_CONSTRAINTS)) {
					periodHardConstraints =
							parsePeriodHardConstraints(reader, line);
				} else if (line.contains(ENTRY_ROOM_HARD_CONSTRAINTS)) {
					roomHardConstraints =
							parseRoomHardConstraints(reader, line);
				} else if (line.contains(ENTRY_INSTITUTIONAL_WEIGHTINGS)) {
					institutionalWeightings =
							parseInstitutionalWeightings(reader, line);
				} else {
					//System.out.println("Read line:" + line);
				}
			}
			reader.close();
			for (PeriodHardConstraint current : periodHardConstraints) {
				System.out.println(current);
				int index = current.getE1Id();
				exams.get(index).addConstraint(current);
				//TODO: special case: EXAM_COINCIDENCE
			}
			
			ExamSession res = new ExamSession(exams,
					periods,
					rooms,
					periodHardConstraints,
					roomHardConstraints,
					institutionalWeightings);
			return res;
		} catch (FileNotFoundException e) {
			throw new ExamParsingException(
					"File " + fileName + " could not be found");
		} catch (IOException e) {
			throw e;
		}
	}

	private InstitutionalWeightings parseInstitutionalWeightings(
			BufferedReader reader, String line) throws IOException {
		int twoInARow = 0;
		int twoInADay = 0;
		int periodSpread = 0;
		int nonMixedDurations = 0;
		int frontLoad_1 = 0;
		int frontLoad_2 = 0;
		int frontLoad_3 = 0;
		while ((line = reader.readLine()) != null) {
			int comaIndex = line.indexOf(',');
			if (line.contains(IW_TWOINAROW)) {
				twoInARow =
						Integer.parseInt(line.substring(comaIndex + 2));
			} else if (line.contains(IW_TWOINADAY)) {
				twoInADay =
						Integer.parseInt(line.substring(comaIndex + 2));
			} else if (line.contains(IW_PERIODSPREAD)) {
				periodSpread =
						Integer.parseInt(line.substring(comaIndex + 2));
			} else if (line.contains(IW_NONMIXEDDURATIONS)) {
				nonMixedDurations =
						Integer.parseInt(line.substring(comaIndex + 1));
			} else if (line.contains(IW_FRONTLOAD)) {
				line = line.substring(comaIndex + 1);
				comaIndex = line.indexOf(',');
				frontLoad_1 = Integer.parseInt(line.substring(0, comaIndex));
				line = line.substring(comaIndex + 1);
				comaIndex = line.indexOf(',');
				frontLoad_2 = Integer.parseInt(line.substring(0, comaIndex));
				line = line.substring(comaIndex + 1);
				frontLoad_3 = Integer.parseInt(line);
			} else {
				//do-nothing
			}
		}
		return new InstitutionalWeightings(twoInARow,
				twoInADay,
				periodSpread,
				nonMixedDurations,
				frontLoad_1,
				frontLoad_2,
				frontLoad_3);
	}

	private ArrayList<RoomHardConstraint> parseRoomHardConstraints(
			BufferedReader reader, String line)
					throws IOException, ExamParsingException {
		reader.mark(1);
		ArrayList<RoomHardConstraint> roomHardConstraints =
				new ArrayList<RoomHardConstraint>();
		while (!(line = reader.readLine())
				.contains(ENTRY_INSTITUTIONAL_WEIGHTINGS)) {
			int comaIndex = line.indexOf(',');
			int id = Integer.parseInt(line.substring(0, comaIndex));
			line = line.substring(comaIndex + 2);
			ERoomHardConstraint constraint = getERoomHardConstraint(line);
			RoomHardConstraint currentRHC =
					new RoomHardConstraint(id, constraint);
			roomHardConstraints.add(currentRHC);
		}
		reader.reset();
		return roomHardConstraints;
	}

	/**
	 * 
	 * @param string
	 * @return Corresponding ERoomHardConstraint
	 * @throws ExamParsingException if illegal.
	 */
	public ERoomHardConstraint getERoomHardConstraint(String string)
		throws ExamParsingException {
			if (string.contentEquals(RHC_ROOM_EXCLUSIVE)) {
				return ERoomHardConstraint.ROOM_EXCLUSIVE;
			} else {
				throw new ExamParsingException("\"" +string
						+ "\" is not a valid period hard constraint");
			}
	}

	private ArrayList<PeriodHardConstraint> parsePeriodHardConstraints(
			BufferedReader reader, String line)
					throws IOException, ExamParsingException {
		ArrayList<PeriodHardConstraint> periodHardConstraints =
				new ArrayList<PeriodHardConstraint>();
		reader.mark(1);
		while (!(line = reader.readLine())
				.contains(ENTRY_ROOM_HARD_CONSTRAINTS)) {
			int comaIndex = line.indexOf(",");
			int e1Id = Integer.parseInt(line.substring(0, comaIndex));
			line = line.substring(comaIndex + 2);
			comaIndex = line.indexOf(",");
			EPeriodHardConstraint constraint =
					getEPeriodHardConstraint(line.substring(0, comaIndex));
			line = line.substring(comaIndex + 2);
			int e2Id = Integer.parseInt(line);
			PeriodHardConstraint currentPHC =
					new PeriodHardConstraint(e1Id, e2Id, constraint);
			periodHardConstraints.add(currentPHC);
		}
		reader.reset();
		return periodHardConstraints;
	}

	/**
	 * 
	 * @param string AFTER, EXAM_COINCIDENCE or EXCLUSION
	 * @return AFTER, EXAM_COINCIDENCE or EXCLUSION
	 * @throws ExamParsingException if an illegal string is passed.
	 */
	public EPeriodHardConstraint getEPeriodHardConstraint(String string)
			throws ExamParsingException {
		if (string.contentEquals(PHC_AFTER)) {
			return EPeriodHardConstraint.AFTER;
		} else if (string.contentEquals(PHC_EXAM_COINCIDENCE)) {
			return EPeriodHardConstraint.EXAM_COINCIDENCE;
		} else if (string.contentEquals(PHC_EXCLUSION)) {
			return EPeriodHardConstraint.EXCLUSION;
		} else {
			throw new ExamParsingException("\"" +string
					+ "\" is not a valid period hard constraint");
		}
	}

	private ArrayList<Room> parseRooms(BufferedReader reader, String line)
			throws IOException {
		ArrayList<Room> rooms = new ArrayList<Room>();
		int number = getNumberOfEntries(line);
		//////////////////////////
		// read every room line //
		//////////////////////////
		for (int i = 0; i < number; i++) {
			line = reader.readLine();
			int comaIndex = line.indexOf(',');
			int size = Integer.parseInt(line.substring(0, comaIndex));
			line = line.substring(comaIndex + 2);
			int cost = Integer.parseInt(line);
			Room currentRoom = new Room(size, cost);
			rooms.add(currentRoom);
		}
		return rooms;
	}

	private ArrayList<Period> parsePeriods(BufferedReader reader, String line)
			throws IOException {
		ArrayList<Period> periods = new ArrayList<Period>();
		int number = getNumberOfEntries(line);
		////////////////////////////
		// read every period line //
		////////////////////////////
		for (int i = 0; i < number; i++) {
			line = reader.readLine();
			int comaIndex = line.indexOf(',');
			int colonIndex = line.indexOf(':');
			int[] idate = new int[6];
			//////////////////
			// get the date //
			//////////////////
			for (int j = 0; j < 6; j++) {
				String current = null;
				if (comaIndex < colonIndex || colonIndex < 0) {
					current = line.substring(0, comaIndex);
					line = line.substring(comaIndex + 2); //"2005, 09"
				} else {
					current = line.substring(0, colonIndex);
					line = line.substring(colonIndex + 1); //"15:04"
				}
				colonIndex = line.indexOf(':');
				comaIndex = line.indexOf(',');
				
				idate[j] = Integer.parseInt(current);
			}
			Calendar c = Calendar.getInstance();
			c.set(idate[2],
					idate[1] - 1,
					idate[0],
					idate[3],
					idate[4],
					idate[5]);
			Date date = c.getTime();
			///////////////////////////
			// get duration and cost //
			///////////////////////////
			int duration = Integer.parseInt(line.substring(0, comaIndex));
			line = line.substring(comaIndex + 2); //", "
			int cost = Integer.parseInt(line);
			
			Period currentPeriod = new Period(date, duration, cost);
			periods.add(currentPeriod);
		}
		return periods;
	}

	private ArrayList<Exam> parseExams(BufferedReader reader, String line)
			throws IOException {
		ArrayList<Exam> exams = new ArrayList<Exam>();
		int numberOfExams = getNumberOfEntries(line);
		//////////////////////////
		// read every exam line //
		//////////////////////////
		for (int i = 0; i < numberOfExams; i++) {
			line = reader.readLine();
			int comaIndex = line.indexOf(',');
			int currentValue = Integer.parseInt(
					(line.substring(0, comaIndex)));
			int duration = currentValue;
			ArrayList<Integer> students = new ArrayList<Integer>();
			////////////////////////
			// read every student //
			////////////////////////
			while (comaIndex > 0) {
				line = line.substring(comaIndex + 2);
				comaIndex = line.indexOf(',');
				if (comaIndex > 0) {
					currentValue = Integer.parseInt(
							line.substring(0, comaIndex));
				} else {
					currentValue = Integer.parseInt(line);
				}
				students.add(currentValue);
			}
			int size = students.size();
			Exam currentExam = new Exam(i, duration, size, students);
			exams.add(currentExam);
		}
		return exams;
	}

	/**
	 * 
	 * @param line [Entry:int]
	 * @return The number of entries following this line, according to
	 * the specified line.
	 */
	private int getNumberOfEntries(String line) {
		line = line.substring(line.indexOf(':') + 1, line.lastIndexOf(']'));
		int numberOfExams = Integer.parseInt(line);
		return numberOfExams;
	}
}
