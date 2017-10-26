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

public class TestAssignmentVisible {

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

	// Helper method to build the trivial example submission history
	private SubmissionHistory buildTinyExample() {
		SubmissionHistory history = new Assignment();
		// submission A:
		history.add("aaaa1234", getDate("2016/09/03 09:00:00"), 66);
		// submission B:
		history.add("aaaa1234", getDate("2016/09/03 16:00:00"), 86);
		// submission C:
		history.add("cccc1234", getDate("2016/09/03 16:00:00"), 73);
		// submission D:
		history.add("aaaa1234", getDate("2016/09/03 18:00:00"), 40);
		return history;
	}

	/* ****************************************************************
	 * The example tests described in the assignment specification
	 * **************************************************************** */

	@Test(timeout = 100)
	public void testExample1() {
		SubmissionHistory history = buildTinyExample();
		// This will return an Integer corresponding to the number 86
		Integer example1 = history.getBestGrade("aaaa1234");
		assertEquals(new Integer(86), example1);
	}

	@Test(timeout = 100)
	public void testExample2() {
		SubmissionHistory history = buildTinyExample();
		// This will return null
		Integer example2 = history.getBestGrade("zzzz1234");
		assertNull(example2);
	}

	@Test(timeout = 100)
	public void testExample3() {
		SubmissionHistory history = buildTinyExample();
		// Tell JUnit to expect an IllegalArgumentException
		thrown.expect(IllegalArgumentException.class);
		// This will throw new IllegalArgumentException();
		history.getBestGrade(null);
	}

	@Test(timeout = 100)
	public void testExample4() {
		SubmissionHistory history = buildTinyExample();
		// This will return a Submission corresponding to submission D
		Submission example4 = history.getSubmissionFinal("aaaa1234");
		testHelperEquals("aaaa1234", getDate("2016/09/03 18:00:00"), 40, example4);
	}

	@Test(timeout = 100)
	public void testExample5() {
		SubmissionHistory history = buildTinyExample();
		// This will return a Submission corresponding to submission C
		Submission example5 = history.getSubmissionFinal("cccc1234");
		testHelperEquals("cccc1234", getDate("2016/09/03 16:00:00"), 73, example5);
	}

	@Test(timeout = 100)
	public void testExample6() {
		SubmissionHistory history = buildTinyExample();
		// This will return a Submission corresponding to submission A
		Submission example6 = history.getSubmissionBefore("aaaa1234", getDate("2016/09/03 13:00:00"));
		testHelperEquals("aaaa1234", getDate("2016/09/03 09:00:00"), 66, example6);
	}

	@Test(timeout = 100)
	public void testExample7() {
		SubmissionHistory history = buildTinyExample();
		// This will return null
		Submission example7 = history.getSubmissionBefore("cccc1234", getDate("2016/09/03 13:00:00"));
		assertNull(example7);
	}

	@Test(timeout = 100)
	public void testExample8() {
		SubmissionHistory history = buildTinyExample();
		// This will return a list containing only {"aaaa1234"}
		// because that student's final submission had grade 40, but their best
		// was 86
		List<String> example8 = history.listRegressions();
		List<String> expected = Arrays.asList("aaaa1234");
		assertEquals(expected, example8);
	}

	@Test(timeout = 100)
	public void testExample9() {
		SubmissionHistory history = buildTinyExample();
		// This will return a list containing only {"aaaa1234"}
		// because that was the only student with the highest grade
		// (86, in this case)
		List<String> example9 = history.listTopStudents();
		List<String> expected = Arrays.asList("aaaa1234");
		assertEquals(expected, example9);
	}
	
	/* ****************************************************************
	 * Some more tests, as a bonus to help get you started:
	 * **************************************************************** */

	//what happens if you have a null argument to the add method
	@Test(timeout = 100)
	public void testAddNull() {
		SubmissionHistory history = new Assignment();

		// thrown.expect only lets us check for one exception per test case
		// but we can perform multiple checks if we use try/catch instead
		try {
			history.add(null, getDate("2016/09/03 13:00:00"), 49);
			fail("did not throw IllegalArgumentException when adding with a null unikey");
		} catch (IllegalArgumentException e) {
			; //do nothing, this is the exception we wanted to see
		}
		try {
			history.add("abcd1234", null, 49);
			fail("did not throw IllegalArgumentException when adding with a null date");
		} catch (IllegalArgumentException e) {
			; //do nothing, this is the exception we wanted to see
		}
		try {
			history.add("abcd1234", getDate("2016/09/03 13:00:00"), null);
			fail("did not throw IllegalArgumentException when adding with a null grade");
		} catch (IllegalArgumentException e) {
			; //do nothing, this is the exception we wanted to see
		}
	}

	//what happens if you have a null argument to the remove method
	@Test(timeout = 100)
	public void testRemoveNull() {
		SubmissionHistory history = new Assignment();
		thrown.expect(IllegalArgumentException.class);
		history.remove(null);
	}

	//what happens if there are no submissions
	@Test(timeout = 100)
	public void testListRegressions_EmptyHistory() {
		SubmissionHistory history = new Assignment();
		List<String> list = history.listRegressions();
		assertTrue(list.isEmpty());
	}

	//what happens if there are no submissions
	@Test(timeout = 100)
	public void testListTopStudents_EmptyHistory() {
		SubmissionHistory history = new Assignment();
		List<String> list = history.listTopStudents();
		assertTrue(list.isEmpty());
	}

	//make sure the return value of the add method is correct
	@Test(timeout = 100)
	public void testReturnValueOfAdd() {
		SubmissionHistory history = new Assignment();
		String unikey;
		Date timestamp;
		Integer grade;

		unikey = "aaaa1234";
		timestamp = getDate("2000/01/01 01:00:00");
		grade = 50;
		testHelperEquals(unikey, timestamp, grade, history.add(unikey, timestamp, grade));

		unikey = "aaaa1234";
		timestamp = getDate("2013/05/02 07:00:00");
		grade = 60;
		testHelperEquals(unikey, timestamp, grade, history.add(unikey, timestamp, grade));

		unikey = "sadf4363";
		timestamp = getDate("2013/05/02 07:00:00");
		grade = 60;
		testHelperEquals(unikey, timestamp, grade, history.add(unikey, timestamp, grade));

	}

	//does getSubmissionsBefore work correctly if the submissions were not added in order
	@Test(timeout = 100)
	public void testGetBefore_AddUnsorted() {
		SubmissionHistory history = new Assignment();
		
		Submission b = testHelperAdd(history, "aaaa1111", new Date(400000), 10);
		Submission j = testHelperAdd(history, "aaaa1111", new Date(2000000), 68);
		Submission c = testHelperAdd(history, "bbbb1111", new Date(600000), 57);
		Submission e = testHelperAdd(history, "aaaa1111", new Date(1000000), 68);
		Submission a = testHelperAdd(history, "aaaa1111", new Date(200000), 56);
		Submission h = testHelperAdd(history, "bbbb1111", new Date(1600000), 23);
		Submission f = testHelperAdd(history, "aaaa1111", new Date(1200000), 80);
		Submission d = testHelperAdd(history, "aaaa1111", new Date(800000), 23);
		Submission g = testHelperAdd(history, "bbbb1111", new Date(1400000), 40);
		Submission i = testHelperAdd(history, "aaaa1111", new Date(1800000), 50);
		
		testHelperEquals(a, history.getSubmissionBefore("aaaa1111", new Date(300000)));
		assertNull(history.getSubmissionBefore("bbbb1111", new Date(300000)));

		testHelperEquals(b, history.getSubmissionBefore("aaaa1111", new Date(400000)));
		assertNull(history.getSubmissionBefore("bbbb1111", new Date(400000)));

		testHelperEquals(b, history.getSubmissionBefore("aaaa1111", new Date(700000)));
		testHelperEquals(c, history.getSubmissionBefore("bbbb1111", new Date(700000)));

		testHelperEquals(d, history.getSubmissionBefore("aaaa1111", new Date(900000)));
		testHelperEquals(c, history.getSubmissionBefore("bbbb1111", new Date(900000)));

		testHelperEquals(e, history.getSubmissionBefore("aaaa1111", new Date(1100000)));
		testHelperEquals(c, history.getSubmissionBefore("bbbb1111", new Date(1100000)));

		testHelperEquals(f, history.getSubmissionBefore("aaaa1111", new Date(1300000)));
		testHelperEquals(c, history.getSubmissionBefore("bbbb1111", new Date(1300000)));

		testHelperEquals(f, history.getSubmissionBefore("aaaa1111", new Date(1500000)));
		testHelperEquals(g, history.getSubmissionBefore("bbbb1111", new Date(1500000)));

		testHelperEquals(f, history.getSubmissionBefore("aaaa1111", new Date(1700000)));
		testHelperEquals(h, history.getSubmissionBefore("bbbb1111", new Date(1700000)));

		testHelperEquals(i, history.getSubmissionBefore("aaaa1111", new Date(1900000)));
		testHelperEquals(h, history.getSubmissionBefore("bbbb1111", new Date(1900000)));

		testHelperEquals(j, history.getSubmissionBefore("aaaa1111", new Date(2100000)));
		testHelperEquals(h, history.getSubmissionBefore("bbbb1111", new Date(2100000)));

	}

	//does getBestGrade work correctly if the submissions were not added in order
	@Test(timeout = 100)
	public void testGetBest_AddUnsorted() {
		SubmissionHistory history = new Assignment();
		
		testHelperAdd(history, "aaaa1111", new Date(400000), 10);
		assertEquals(new Integer(10), history.getBestGrade("aaaa1111"));
		assertNull(history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(2000000), 68);
		assertEquals(new Integer(68), history.getBestGrade("aaaa1111"));
		assertNull(history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "bbbb1111", new Date(600000), 57);
		assertEquals(new Integer(68), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(1000000), 68);
		assertEquals(new Integer(68), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(200000), 56);
		assertEquals(new Integer(68), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "bbbb1111", new Date(1600000), 23);
		assertEquals(new Integer(68), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(1200000), 80);
		assertEquals(new Integer(80), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(800000), 23);
		assertEquals(new Integer(80), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "bbbb1111", new Date(1400000), 40);
		assertEquals(new Integer(80), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(1800000), 50);
		assertEquals(new Integer(80), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(57), history.getBestGrade("bbbb1111"));
	}

	//does getSubmissionFinal work correctly if the submissions were not added in order
	@Test(timeout = 100)
	public void testGetFinal_AddUnsorted() {
		SubmissionHistory history = new Assignment();
		
		Submission b = testHelperAdd(history, "aaaa1111", new Date(400000), 10);
		testHelperEquals(b, history.getSubmissionFinal("aaaa1111"));
		assertNull(history.getSubmissionFinal("bbbb1111"));

		Submission j = testHelperAdd(history, "aaaa1111", new Date(2000000), 68);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		assertNull(history.getSubmissionFinal("bbbb1111"));

		Submission c = testHelperAdd(history, "bbbb1111", new Date(600000), 57);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(c, history.getSubmissionFinal("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(1000000), 68);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(c, history.getSubmissionFinal("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(200000), 56);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(c, history.getSubmissionFinal("bbbb1111"));

		Submission h = testHelperAdd(history, "bbbb1111", new Date(1600000), 23);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(h, history.getSubmissionFinal("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(1200000), 80);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(h, history.getSubmissionFinal("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(800000), 23);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(h, history.getSubmissionFinal("bbbb1111"));

		testHelperAdd(history, "bbbb1111", new Date(1400000), 40);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(h, history.getSubmissionFinal("bbbb1111"));

		testHelperAdd(history, "aaaa1111", new Date(1800000), 50);
		testHelperEquals(j, history.getSubmissionFinal("aaaa1111"));
		testHelperEquals(h, history.getSubmissionFinal("bbbb1111"));
	}
	
	//does getBestSubmission work correctly as we remove submissions
	@Test(timeout = 100)
	public void testGetBest_Remove() {
		SubmissionHistory history = new Assignment();
		Submission b = testHelperAdd(history, "aaaa1111", new Date(400000), 10);
		Submission j = testHelperAdd(history, "aaaa1111", new Date(2000000), 68);
		Submission c = testHelperAdd(history, "bbbb1111", new Date(600000), 68);
		Submission e = testHelperAdd(history, "aaaa1111", new Date(1000000), 68);
		Submission a = testHelperAdd(history, "aaaa1111", new Date(200000), 56);
		Submission h = testHelperAdd(history, "bbbb1111", new Date(1600000), 23);
		Submission f = testHelperAdd(history, "aaaa1111", new Date(1200000), 80);
		Submission d = testHelperAdd(history, "aaaa1111", new Date(800000), 23);
		Submission g = testHelperAdd(history, "bbbb1111", new Date(1400000), 40);
		Submission i = testHelperAdd(history, "aaaa1111", new Date(1800000), 50);

		assertEquals(new Integer(80), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(68), history.getBestGrade("bbbb1111"));

		history.remove(i);
		assertEquals(new Integer(80), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(68), history.getBestGrade("bbbb1111"));

		history.remove(f);
		assertEquals(new Integer(68), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(68), history.getBestGrade("bbbb1111"));

		history.remove(j);
		assertEquals(new Integer(68), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(68), history.getBestGrade("bbbb1111"));

		history.remove(e);
		assertEquals(new Integer(56), history.getBestGrade("aaaa1111"));
		assertEquals(new Integer(68), history.getBestGrade("bbbb1111"));
	}

	//testing a larger example with the regressions method
	@Test(timeout = 100)
	public void testMoreRegressions() {
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
		
	}

	//testing a larger example with the regressions method
	@Test(timeout = 100)
	public void testMoreTopStudents() {
		SubmissionHistory history = new Assignment();
		Submission a1 = testHelperAdd(history, "a", new Date(100000), 10);
		Submission b1 = testHelperAdd(history, "b", new Date(100000), 10);
		Submission c1 = testHelperAdd(history, "c", new Date(100000), 10);
		Submission d1 = testHelperAdd(history, "d", new Date(100000), 10);
		Submission e1 = testHelperAdd(history, "e", new Date(100000), 10);
		Submission f1 = testHelperAdd(history, "f", new Date(100000), 15); //best

		Submission a2 = testHelperAdd(history, "a", new Date(200000), 10);
		Submission b2 = testHelperAdd(history, "b", new Date(200000), 5);
		Submission c2 = testHelperAdd(history, "c", new Date(200000), 5);
		Submission d2 = testHelperAdd(history, "d", new Date(200000), 15); //best
		Submission e2 = testHelperAdd(history, "e", new Date(200000), 15); //best
		Submission f2 = testHelperAdd(history, "f", new Date(200000), 5);
		
		//top grade is 15
		List<String> studentsExpected = Arrays.asList("d","e","f");
		List<String> studentsActual = history.listTopStudents();
		
		//sort both lists, to make it easier to compare them
		Collections.sort(studentsActual);
		Collections.sort(studentsActual);

		assertEquals(studentsExpected, studentsActual);
	}
	
	//testing an unusually high grade
	@Test(timeout = 100)
	public void testReallyHighGrade() {
		SubmissionHistory history = new Assignment();
		Submission a = testHelperAdd(history, "a", new Date(100000), 10);
		Submission b = testHelperAdd(history, "a", new Date(200000), 10000);
		Submission c = testHelperAdd(history, "a", new Date(300000), 50);
		
		assertEquals(new Integer(10000), history.getBestGrade("a"));
		testHelperEquals(c, history.getSubmissionFinal("a"));
		testHelperEquals(b, history.getSubmissionBefore("a", new Date(250000)));
	}
	
	
	/* ****************************************************************
	 * That's the end of the visible tests. There will be hidden tests,
	 * accounting for up to half the automarking grade!
	 * 
	 * You will need to do more testing yourself.
	 * 
	 * Some ideas:
	 * - test all the interface methods with the remove method
	 * - test all the interface methods with a mix of removes and adds
	 * - do (much) larger tests with more students and many more submissions
	 * - edge cases
	 * **************************************************************** */

}
