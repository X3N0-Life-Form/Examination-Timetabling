package parse;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
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
 * 
 * @author Adrien Droguet
 * @see ExamSessionParser
 */
public class ExamSessionParserTests {

	public static final String testfileName_1 = "res/exam_comp_set1.exam";
	public static final String testfileName_2 = "res/exam_comp_set2.exam";
	private ExamSessionParser esp_1;
	private ExamSessionParser esp_2;
	private ExamSession es_1;
	private ExamSession es_2;
	
	@Before
	public void setUp() throws ExamParsingException, IOException {
		esp_1 = new ExamSessionParser(testfileName_1);
		es_1 = esp_1.parse();
		esp_2 = new ExamSessionParser(testfileName_2);
		es_2 = esp_2.parse();
	}
	
	@Test
	public void examEntry() throws ExamParsingException, IOException {
		assertTrue(es_1.getExams() != null);
	}
	
	/**
	 * Do we find the right number of exams?
	 */
	@Test
	public void examNumberParsed() {
		assertTrue(es_1.getExams().size() == 607);
	}
	
	/**
	 * Do the exams have the right duration?
	 * @throws ExamParsingException
	 * @throws IOException
	 */
	@Test
	public void examDuration() throws ExamParsingException, IOException {
		ArrayList<Exam> exams = es_1.getExams();
		assertTrue(exams.get(0).getDuration() == 195);
		assertTrue(exams.get(1).getDuration() == 135);
		assertTrue(exams.get(2).getDuration() == 120);
	}
	
	/**
	 * Do the exams have the correct size?
	 * @throws IOException 
	 * @throws ExamParsingException 
	 */
	@Test
	public void examSize() throws ExamParsingException, IOException {
		ArrayList<Exam> exams = es_1.getExams();
		assertTrue(exams.get(0).getSize() == 252);
		assertTrue(exams.get(1).getSize() == 85);
		assertTrue(exams.get(2).getSize() == 139);
	}
	
	@Test
	public void examConstraints_parsed() {
		ArrayList<Exam> exams = es_1.getExams();
		assertTrue(exams.get(11).getConstraints().size() > 0);
		assertTrue(exams.get(26).getConstraints().size() > 0);
		assertTrue(exams.get(98).getConstraints().size() > 0);
	}
	
	/////////////
	// periods //
	/////////////
	
	@Test
	public void periodEntry() {
		assertTrue(es_1.getPeriods() != null);
	}
	
	/**
	 * Do we find the right number of periods? 
	 */
	@Test
	public void periodNumberParsed() {
		assertTrue(es_1.getPeriods().size() == 54);
	}
	
	/**
	 * Do we have the right dates (Not a dirty hack test).
	 */
	@Test
	public void periodDates() {
		ArrayList<Period> periods = es_1.getPeriods();
		Calendar c = Calendar.getInstance();
		c.set(2005, 3, 15, 9, 30, 0);
		Date d1 = c.getTime();
		c.set(2005, 3, 15, 14, 0, 0);
		Date d2 = c.getTime();
		c.set(2005, 3, 18, 9, 30, 0);
		Date d3 = c.getTime();
		
		assertEquals(d1.toString(), periods.get(0).getDate_hour().toString());
		assertEquals(d2.toString(), periods.get(1).getDate_hour().toString());
		assertEquals(d3.toString(), periods.get(2).getDate_hour().toString());
	}
	
	@Test
	public void periodDurations() {
		ArrayList<Period> periods = es_1.getPeriods();
		assertTrue(periods.get(0).getDuration() == 210);
		assertTrue(periods.get(1).getDuration() == 210);
		assertTrue(periods.get(2).getDuration() == 210);
	}
	
	@Test
	public void periodCosts() {
		ArrayList<Period> periods = es_1.getPeriods();
		assertTrue(periods.get(0).getCost() == 0);
		assertTrue(periods.get(1).getCost() == 0);
		assertTrue(periods.get(2).getCost() == 0);
		assertTrue(periods.get(3).getCost() == 70);
		assertTrue(periods.get(4).getCost() == 50);
	}
	
	///////////
	// rooms //
	///////////
	
	@Test
	public void roomEntry() {
		assertTrue(es_1.getRooms() != null);
	}
	
	@Test
	public void roomNumberParsed() {
		assertTrue(es_1.getRooms().size() == 7);
	}
	
	@Test
	public void roomSizes() {
		ArrayList<Room> rooms = es_1.getRooms();
		assertTrue(rooms.get(0).getSize() == 260);
		assertTrue(rooms.get(1).getSize() == 100);
		assertTrue(rooms.get(2).getSize() == 129);
	}
	
	@Test
	public void roomCosts() {
		ArrayList<Room> rooms = es_1.getRooms();
		assertTrue(rooms.get(0).getCost() == 0);
		assertTrue(rooms.get(1).getCost() == 0);
		assertTrue(rooms.get(2).getCost() == 50);
	}
	
	/////////////////////////////
	// period hard constraints //
	/////////////////////////////
	@Test
	public void getEPeriodHardConstraint_OK() throws ExamParsingException {
		String[] constraints = new String[4];
		constraints[0] = "AFTER";
		constraints[1] = "EXAM_COINCIDENCE";
		constraints[2] = "EXCLUSION";
		assertTrue(esp_1.getEPeriodHardConstraint(constraints[0])
				== EPeriodHardConstraint.AFTER);
		assertTrue(esp_1.getEPeriodHardConstraint(constraints[1])
				== EPeriodHardConstraint.EXAM_COINCIDENCE);
		assertTrue(esp_1.getEPeriodHardConstraint(constraints[2])
				== EPeriodHardConstraint.EXCLUSION);
	}
	
	@Test (expected = ExamParsingException.class)
	public void getEPeriodHardConstraint_error() throws ExamParsingException {
		String mal = "malformed string";
		esp_1.getEPeriodHardConstraint(mal);
	}
	
	@Test
	public void periodHardConstraint_entry() {
		ArrayList<PeriodHardConstraint> PHCs = es_1.getPeriodHardConstraints();
		assertTrue (PHCs != null);
	}
	
	@Test
	public void periodHardConstraint_values() {
		ArrayList<PeriodHardConstraint> PHCs = es_1.getPeriodHardConstraints();
		assertTrue(PHCs.get(0).getE1Id() == 11);
		assertTrue(PHCs.get(0).getE2Id() == 10);
		assertTrue(PHCs.get(0).getConstraint() == EPeriodHardConstraint.AFTER);
		
		assertTrue(PHCs.get(1).getE1Id() == 26);
		assertTrue(PHCs.get(1).getE2Id() == 25);
		assertTrue(PHCs.get(1).getConstraint() == EPeriodHardConstraint.AFTER);
		
		assertTrue(PHCs.get(2).getE1Id() == 98);
		assertTrue(PHCs.get(2).getE2Id() == 97);
		assertTrue(PHCs.get(2).getConstraint() == EPeriodHardConstraint.AFTER);
	}
	
	///////////////////////////
	// room hard constraints //
	///////////////////////////
	@Test
	public void getERoomHardConstraint_OK() throws ExamParsingException {
		String constraint = "ROOM_EXCLUSIVE";
		assertTrue(esp_2.getERoomHardConstraint(constraint)
				== ERoomHardConstraint.ROOM_EXCLUSIVE);
	}
	
	@Test (expected = ExamParsingException.class)
	public void getERoomHardConstraint_error() throws ExamParsingException {
		String mal = "malformed string";
		esp_2.getERoomHardConstraint(mal);
	}
	
	@Test
	public void roomHardConstraint_entry() {
		ArrayList<RoomHardConstraint> RHCs = es_2.getRoomHardConstraints();
		assertTrue(RHCs != null);
	}
	
	/**
	 * Note: using second data set for testing.
	 */
	@Test
	public void roomHardConstraint_OK() {
		ArrayList<RoomHardConstraint> RHCs = es_2.getRoomHardConstraints();
		assertTrue(RHCs.get(0).getId() == 78);
		assertTrue(RHCs.get(0).getConstraint() == ERoomHardConstraint.ROOM_EXCLUSIVE);
		assertTrue(RHCs.get(1).getId() == 128);
		assertTrue(RHCs.get(1).getConstraint() == ERoomHardConstraint.ROOM_EXCLUSIVE);
	}
	
	//////////////////////////////
	// institutional weightings //
	//////////////////////////////
	@Test
	public void institutionalWeightings_entry() {
		InstitutionalWeightings IW = es_1.getInstitutionalWeightings();
		assertTrue(IW != null);
	}
	
	@Test
	public void institutionalWeightings_OK() {
		InstitutionalWeightings IW = es_1.getInstitutionalWeightings();
		assertTrue(IW.getTwoInADay() == 5);
		assertTrue(IW.getTwoInARow() == 7);
		assertTrue(IW.getPeriodSpread() == 5);
		assertTrue(IW.getNonMixedDurations() == 10);
		assertTrue(IW.getFrontLoad_1() == 100);
		assertTrue(IW.getFrontLoad_2() == 30);
		assertTrue(IW.getFrontLoad_3() == 5);
	}
}
