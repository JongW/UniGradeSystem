import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AssignmentTest {

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Set up JUnit to be able to check for expected exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// This will make it a bit easier for us to make Date objects
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	// This will make it a bit easier for us to make Date objects
	private static Date getDate(String s) {
		try {
			return df.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("The test case is broken, invalid SimpleDateFormat parse");
		}
		// unreachable
		return null;
	}

	// helper method to compare two Submissions using assertions
	private static void testHelperEquals(Submission expected, Submission actual) {
		assertEquals(expected.getUnikey(), actual.getUnikey());
		assertEquals(expected.getTime(), actual.getTime());
		assertEquals(expected.getGrade(), actual.getGrade());
	}

	// helper method to compare two Submissions using assertions
	private static void testHelperEquals(String unikey, Date timestamp, Integer grade, Submission actual) {
		assertEquals(unikey, actual.getUnikey());
		assertEquals(timestamp, actual.getTime());
		assertEquals(grade, actual.getGrade());
	}
	
	// helper method that adds a new appointment AND checks the return value is correct
	private static Submission testHelperAdd(SubmissionHistory history, String unikey, Date timestamp, Integer grade) {
		Submission s = history.add(unikey, timestamp, grade);
		testHelperEquals(unikey, timestamp, grade, s);
		return s;
	}

	//////////////////////////////////////////////////code above was provided by assignment ////////////////////////////////////////////////////

	
	@Test(timeout = 100)
	public void testRemoveFinal() {
		//to test multiple unikeys that have the same date for submission
		SubmissionHistory history = new Assignment();
		
		Submission a = testHelperAdd(history, "a", new Date(400000), 10);
		Submission a1 = testHelperAdd(history, "a", new Date(500000), 10);
		
		Submission b = testHelperAdd(history, "b", new Date(400000), 68);
		Submission b1 = testHelperAdd(history, "b", new Date(300000), 19);
		
		//remove submission with more recent date
		testHelperEquals(a1, history.getSubmissionFinal("a"));
		history.remove(a1);		
		testHelperEquals(a, history.getSubmissionFinal("a"));
		
		//remove submission with older date
		testHelperEquals(b, history.getSubmissionFinal("b"));
		history.remove(b1);
		testHelperEquals(b, history.getSubmissionFinal("b"));
		
		//remove both submissions and check if that returns a null
		history.remove(a);	
		assertNull(history.getSubmissionFinal("a"));
	}
	
	@Test(timeout = 100)
	public void testRemoveBefore() {
		//to test multiple unikeys that have the same date for submission
		SubmissionHistory history = new Assignment();
		
		Submission a = testHelperAdd(history, "a", new Date(400000), 10);
		Submission a1 = testHelperAdd(history, "a", new Date(500000), 10);
		
		Submission b = testHelperAdd(history, "b", new Date(400000), 68);
		Submission b1 = testHelperAdd(history, "b", new Date(500000), 19);
		
		//remove submission that is returned when less than date(500000)
		testHelperEquals(a1, history.getSubmissionBefore("a", new Date(500000)));
		history.remove(a1);
		testHelperEquals(a, history.getSubmissionBefore("a", new Date(500000)));
		
		//should make no different if you remove non highest grade
		testHelperEquals(b, history.getSubmissionBefore("b", new Date(400000)));
		history.remove(b);
		testHelperEquals(b1, history.getSubmissionBefore("b", new Date(500000)));
		
		//remove both submissinos and check fi that returns a null
		history.remove(a);
		assertNull(history.getSubmissionBefore("a", new Date(700000)));			
	}
	
	@Test(timeout = 100)
	public void testRemoveTopList() {
		//original submission objects were taken from provided test cases
		SubmissionHistory history = new Assignment();
		
		Submission a = testHelperAdd(history, "a", new Date(100000), 10);
		Submission b = testHelperAdd(history, "b", new Date(100000), 10);
		Submission c = testHelperAdd(history, "c", new Date(100000), 10);
		Submission d = testHelperAdd(history, "d", new Date(100000), 10);
		Submission e = testHelperAdd(history, "e", new Date(100000), 10);
		Submission f = testHelperAdd(history, "f", new Date(100000), 15); //best
		
		List<String> studentsExpected = Arrays.asList("f");
		List<String> studentsActual = history.listTopStudents();
		
		Collections.sort(studentsExpected);
		Collections.sort(studentsActual);

		assertEquals(studentsExpected, studentsActual);
		
		history.remove(f);
		
		studentsExpected = Arrays.asList("a","b","c","d","e");
		studentsActual = history.listTopStudents();
		
		Collections.sort(studentsExpected);
		Collections.sort(studentsActual);
		
		assertEquals(studentsExpected, studentsActual);

	}
	
	@Test(timeout = 100)
	public void testRemoveRegression() {
		//original submission objects were taken from provided test cases
		SubmissionHistory history = new Assignment();
		Submission a1 = testHelperAdd(history, "a", new Date(100000), 10);
		Submission b1 = testHelperAdd(history, "b", new Date(100000), 10);
		Submission c1 = testHelperAdd(history, "c", new Date(100000), 10);
		Submission d1 = testHelperAdd(history, "d", new Date(100000), 10);
		Submission e1 = testHelperAdd(history, "e", new Date(100000), 10);
		Submission f1 = testHelperAdd(history, "f", new Date(100000), 10);

		Submission a2 = testHelperAdd(history, "a", new Date(200000), 10);
		Submission b2 = testHelperAdd(history, "b", new Date(200000), 5); //regression
		Submission c2 = testHelperAdd(history, "c", new Date(200000), 5); //regression
		Submission d2 = testHelperAdd(history, "d", new Date(200000), 15);
		Submission e2 = testHelperAdd(history, "e", new Date(200000), 15);
		Submission f2 = testHelperAdd(history, "f", new Date(200000), 5); //regression
		
		List<String> studentsExpected = Arrays.asList("b","c","f");
		List<String> studentsActual = history.listRegressions();
		
		//sort both lists, to make it easier to compare them
		Collections.sort(studentsActual);
		Collections.sort(studentsActual);

		assertEquals(studentsExpected, studentsActual);
		
		history.remove(b2);

		studentsExpected = Arrays.asList("c","f");
		studentsActual = history.listRegressions();
		
		//sort both lists, to make it easier to compare them
		Collections.sort(studentsActual);
		Collections.sort(studentsActual);

		assertEquals(studentsExpected, studentsActual);
	}
	
	@Test(timeout = 100)
	public void testLargeGetBestGradeMixed() {
		//original submission objects were taken from provided test cases
		SubmissionHistory history = new Assignment();
		Submission a = testHelperAdd(history, "a", new Date(100000), 10);
		Submission b = testHelperAdd(history, "a", new Date(200000), 11);
		Submission c = testHelperAdd(history, "a", new Date(300000), 12);
		Submission d = testHelperAdd(history, "a", new Date(400000), 13);
		Submission e = testHelperAdd(history, "a", new Date(500000), 14);
		Submission f = testHelperAdd(history, "a", new Date(600000), 15);
		Submission g = testHelperAdd(history, "a", new Date(700000), 16);
		Submission h = testHelperAdd(history, "a", new Date(800000), 17);
		Submission i = testHelperAdd(history, "a", new Date(900000), 18);
		Submission j = testHelperAdd(history, "a", new Date(100001), 19);
		Submission k = testHelperAdd(history, "a", new Date(100002), 20);
		Submission l = testHelperAdd(history, "a", new Date(100003), 21);
		Submission m = testHelperAdd(history, "a", new Date(100004), 22);
		Submission n = testHelperAdd(history, "a", new Date(100005), 23);
		Submission o = testHelperAdd(history, "a", new Date(100006), 24);
		Submission p = testHelperAdd(history, "a", new Date(100007), 25);
		Submission q = testHelperAdd(history, "a", new Date(100008), 26);
		Submission r = testHelperAdd(history, "a", new Date(100009), 27);
		Submission s = testHelperAdd(history, "a", new Date(100010), 28);
		Submission t = testHelperAdd(history, "a", new Date(100020), 29);
		Submission u = testHelperAdd(history, "a", new Date(100030), 30);
		Submission v = testHelperAdd(history, "a", new Date(100040), 31);
		Submission w = testHelperAdd(history, "a", new Date(100050), 32);
		Submission x = testHelperAdd(history, "a", new Date(100060), 33);
		Submission y = testHelperAdd(history, "a", new Date(100070), 34);
		Submission z = testHelperAdd(history, "a", new Date(100080), 35);
		Submission a1 = testHelperAdd(history, "a", new Date(10090), 36);
		Submission b1 = testHelperAdd(history, "a", new Date(100100), 37);
		Submission c1 = testHelperAdd(history, "a", new Date(100200), 38);
		Submission d1 = testHelperAdd(history, "a", new Date(100300), 39);
		Submission e1 = testHelperAdd(history, "a", new Date(100400), 40);
		Submission f1 = testHelperAdd(history, "a", new Date(100500), 41);
		Submission g1 = testHelperAdd(history, "a", new Date(100600), 42);
		Submission h1 = testHelperAdd(history, "a", new Date(100700), 43);
		Submission i1 = testHelperAdd(history, "a", new Date(100800), 44);

		assertEquals(new Integer(44), history.getBestGrade("a"));
		
		Submission j1 = testHelperAdd(history, "a", new Date(100900), 45);
		
		assertEquals(new Integer(45), history.getBestGrade("a"));
		
		history.remove(j1);
		history.remove(i1);
		history.remove(h1);
		history.remove(g1);
		history.remove(f1);
		history.remove(e1);
		history.remove(d1);
		
		assertEquals(new Integer(38), history.getBestGrade("a"));
		
	}
	
	@Test(timeout = 100)
	public void testLargeGetFinalMixed() {
		//original submission objects were taken from provided test cases
		SubmissionHistory history = new Assignment();
		Submission a = testHelperAdd(history, "a", new Date(10000), 10);
		Submission b = testHelperAdd(history, "a", new Date(20000), 11);
		Submission c = testHelperAdd(history, "a", new Date(30000), 12);
		Submission d = testHelperAdd(history, "a", new Date(40000), 13);
		Submission e = testHelperAdd(history, "a", new Date(50000), 14);
		Submission f = testHelperAdd(history, "a", new Date(60000), 15);
		Submission g = testHelperAdd(history, "a", new Date(70000), 16);
		Submission h = testHelperAdd(history, "a", new Date(80000), 17);
		Submission i = testHelperAdd(history, "a", new Date(90000), 18);
		Submission j = testHelperAdd(history, "a", new Date(100001), 19);
		Submission k = testHelperAdd(history, "a", new Date(100002), 20);
		Submission l = testHelperAdd(history, "a", new Date(100003), 21);
		Submission m = testHelperAdd(history, "a", new Date(100004), 22);
		Submission n = testHelperAdd(history, "a", new Date(100005), 23);
		Submission o = testHelperAdd(history, "a", new Date(100006), 24);
		Submission p = testHelperAdd(history, "a", new Date(100007), 25);
		Submission q = testHelperAdd(history, "a", new Date(100008), 26);
		Submission r = testHelperAdd(history, "a", new Date(100009), 27);
		Submission s = testHelperAdd(history, "a", new Date(100010), 28);
		Submission t = testHelperAdd(history, "a", new Date(100020), 29);
		Submission u = testHelperAdd(history, "a", new Date(100030), 30);
		Submission v = testHelperAdd(history, "a", new Date(100040), 31);
		Submission w = testHelperAdd(history, "a", new Date(100050), 32);
		Submission x = testHelperAdd(history, "a", new Date(100060), 33);
		Submission y = testHelperAdd(history, "a", new Date(100070), 34);
		Submission z = testHelperAdd(history, "a", new Date(100080), 35);
		Submission a1 = testHelperAdd(history, "a", new Date(10090), 36);
		Submission b1 = testHelperAdd(history, "a", new Date(100100), 37);
		Submission c1 = testHelperAdd(history, "a", new Date(100200), 38);
		Submission d1 = testHelperAdd(history, "a", new Date(100300), 39);
		Submission e1 = testHelperAdd(history, "a", new Date(100400), 40);
		Submission f1 = testHelperAdd(history, "a", new Date(100500), 41);
		Submission g1 = testHelperAdd(history, "a", new Date(100600), 42);
		Submission h1 = testHelperAdd(history, "a", new Date(100700), 43);
		Submission i1 = testHelperAdd(history, "a", new Date(100800), 44);

		testHelperEquals(i1, history.getSubmissionFinal("a"));
		
		Submission j1 = testHelperAdd(history, "a", new Date(100900), 45);
		
		testHelperEquals(j1, history.getSubmissionFinal("a"));
		
		history.remove(j1);
		history.remove(i1);
		history.remove(h1);
		history.remove(g1);
		history.remove(f1);
		history.remove(e1);
		history.remove(d1);
		
		testHelperEquals(c1, history.getSubmissionFinal("a"));
		
	}
	
	@Test(timeout = 100)
	public void testLargeGetBeforeMixed() {
		//original submission objects were taken from provided test cases
		SubmissionHistory history = new Assignment();
		Submission a = testHelperAdd(history, "a", new Date(10000), 10);
		Submission b = testHelperAdd(history, "a", new Date(20000), 11);
		Submission c = testHelperAdd(history, "a", new Date(30000), 12);
		Submission d = testHelperAdd(history, "a", new Date(40000), 13);
		Submission e = testHelperAdd(history, "a", new Date(50000), 14);
		Submission f = testHelperAdd(history, "a", new Date(60000), 15);
		Submission g = testHelperAdd(history, "a", new Date(70000), 16);
		Submission h = testHelperAdd(history, "a", new Date(80000), 17);
		Submission i = testHelperAdd(history, "a", new Date(90000), 18);
		Submission j = testHelperAdd(history, "a", new Date(100001), 19);
		Submission k = testHelperAdd(history, "a", new Date(100002), 20);
		Submission l = testHelperAdd(history, "a", new Date(100003), 21);
		Submission m = testHelperAdd(history, "a", new Date(100004), 22);
		Submission n = testHelperAdd(history, "a", new Date(100005), 23);
		Submission o = testHelperAdd(history, "a", new Date(100006), 24);
		Submission p = testHelperAdd(history, "a", new Date(100007), 25);
		Submission q = testHelperAdd(history, "a", new Date(100008), 26);
		Submission r = testHelperAdd(history, "a", new Date(100009), 27);
		Submission s = testHelperAdd(history, "a", new Date(100010), 28);
		Submission t = testHelperAdd(history, "a", new Date(100020), 29);
		Submission u = testHelperAdd(history, "a", new Date(100030), 30);
		Submission v = testHelperAdd(history, "a", new Date(100040), 31);
		Submission w = testHelperAdd(history, "a", new Date(100050), 32);
		Submission x = testHelperAdd(history, "a", new Date(100060), 33);
		Submission y = testHelperAdd(history, "a", new Date(100070), 34);
		Submission z = testHelperAdd(history, "a", new Date(100080), 35);
		Submission a1 = testHelperAdd(history, "a", new Date(10090), 36);
		Submission b1 = testHelperAdd(history, "a", new Date(100100), 37);
		Submission c1 = testHelperAdd(history, "a", new Date(100200), 38);
		Submission d1 = testHelperAdd(history, "a", new Date(100300), 39);
		Submission e1 = testHelperAdd(history, "a", new Date(100400), 40);
		Submission f1 = testHelperAdd(history, "a", new Date(100500), 41);
		Submission g1 = testHelperAdd(history, "a", new Date(100600), 42);
		Submission h1 = testHelperAdd(history, "a", new Date(100700), 43);
		Submission i1 = testHelperAdd(history, "a", new Date(100800), 44);

		testHelperEquals(i1, history.getSubmissionBefore("a",new Date (100800)));
		
		Submission j1 = testHelperAdd(history, "a", new Date(100900), 45);
		
		testHelperEquals(j1, history.getSubmissionBefore("a",new Date (100900)));
		
		history.remove(j1);
		history.remove(i1);
		history.remove(h1);
		history.remove(g1);
		history.remove(f1);
		history.remove(e1);
		history.remove(d1);
		
		testHelperEquals(c1, history.getSubmissionBefore("a",new Date (100800)));
		
	}
	
	@Test(timeout = 100)
	public void testLargeListTopMixed() {
		//original submission objects were taken from provided test cases
		SubmissionHistory history = new Assignment();
		Submission a = testHelperAdd(history, "a", new Date(10000), 10);
		Submission b = testHelperAdd(history, "a", new Date(20000), 11);
		Submission c = testHelperAdd(history, "a", new Date(30000), 12);
		Submission d = testHelperAdd(history, "a", new Date(40000), 13);
		Submission e = testHelperAdd(history, "a", new Date(50000), 14);
		Submission f = testHelperAdd(history, "a", new Date(60000), 15);
		Submission g = testHelperAdd(history, "a", new Date(70000), 16);
		Submission h = testHelperAdd(history, "a", new Date(80000), 17);
		Submission i = testHelperAdd(history, "a", new Date(90000), 18);
		Submission j = testHelperAdd(history, "a", new Date(100001), 19);
		Submission k = testHelperAdd(history, "a", new Date(100002), 20);
		Submission l = testHelperAdd(history, "a", new Date(100003), 21);
		Submission m = testHelperAdd(history, "a", new Date(100004), 22);
		Submission n = testHelperAdd(history, "a", new Date(100005), 23);
		Submission o = testHelperAdd(history, "a", new Date(100006), 24);
		Submission p = testHelperAdd(history, "a", new Date(100007), 25);
		Submission q = testHelperAdd(history, "a", new Date(100008), 26);
		Submission r = testHelperAdd(history, "a", new Date(100009), 27);
		Submission s = testHelperAdd(history, "a", new Date(100010), 28);
		Submission t = testHelperAdd(history, "a", new Date(100020), 29);
		Submission u = testHelperAdd(history, "a", new Date(100030), 30);
		Submission v = testHelperAdd(history, "a", new Date(100040), 31);
		Submission w = testHelperAdd(history, "a", new Date(100050), 32);
		Submission x = testHelperAdd(history, "a", new Date(100060), 33);
		Submission y = testHelperAdd(history, "a", new Date(100070), 34);
		Submission z = testHelperAdd(history, "a", new Date(100080), 35);
		Submission a1 = testHelperAdd(history, "a", new Date(10090), 36);
		Submission b1 = testHelperAdd(history, "a", new Date(100100), 37);
		Submission c1 = testHelperAdd(history, "a", new Date(100200), 38);
		Submission d1 = testHelperAdd(history, "a", new Date(100300), 39);
		
		Submission e1 = testHelperAdd(history, "a", new Date(200400), 40);
		Submission f1 = testHelperAdd(history, "a", new Date(100500), 40);
		Submission g1 = testHelperAdd(history, "b", new Date(100600), 40);
		Submission h1 = testHelperAdd(history, "c", new Date(100700), 40);
		Submission i1 = testHelperAdd(history, "d", new Date(100800), 40);

		List<String> studentsExpected = Arrays.asList("a","b","c","d");
		List<String> studentsActual = history.listTopStudents();
		
		Collections.sort(studentsExpected);
		Collections.sort(studentsActual);
		
		assertEquals(studentsExpected, studentsActual);

		Submission j1 = testHelperAdd(history, "e", new Date(100800), 40);
		
		studentsExpected = Arrays.asList("a","b","c","d","e");
		studentsActual = history.listTopStudents();
		
		Collections.sort(studentsExpected);
		Collections.sort(studentsActual);
		
		assertEquals(studentsExpected, studentsActual);
		
		history.remove(j1);
		history.remove(i1);
		history.remove(h1);
		history.remove(g1);
		history.remove(f1);
		history.remove(e1);
		history.remove(d1);
		
		studentsExpected = Arrays.asList("a");
		studentsActual = history.listTopStudents();
		
		Collections.sort(studentsExpected);
		Collections.sort(studentsActual);
		
		assertEquals(studentsExpected, studentsActual);
				
	}
	
	

}
