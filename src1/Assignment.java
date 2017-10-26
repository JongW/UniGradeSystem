import java.util.*;

public class Assignment implements SubmissionHistory {

	//nested data structure for submission
	public class Node implements Submission {		
		private String unikey;
		private Date time;
		private Integer grade;	 

		public Node(String unikey, Date time, Integer grade) {
			this.unikey = unikey;
			this.time = time;
			this.grade = grade;
		}	   

		public String getUnikey() { return unikey; }	   	
		public Date getTime() { return time; }	   	
		public Integer getGrade() { return grade; }   	
		public void setUnikey(String unikey) { this.unikey = unikey; }   	
		public void setTime(Date time) { this.time = time; }   	
		public void setGrade(Integer grade) { this.grade = grade; }   	   	
	}
	//end of nested data structures

	//declare data structures	

	private HashMap<String,TreeMap<Date,Submission>> timeSort;
	private HashMap<String,TreeMap<Integer,TreeMap<Date,Submission>>> gradeSort;
	private TreeMap<Integer,HashMap<String,String>> topGrade;
	
	public Assignment() {
		HashMap<String,TreeMap<Date,Submission>> timeSort = new HashMap<String,TreeMap<Date,Submission>>();
		this.timeSort = timeSort;

		HashMap<String,TreeMap<Integer,TreeMap<Date,Submission>>> gradeSort = new HashMap<String,TreeMap<Integer,TreeMap<Date,Submission>>>();
		this.gradeSort = gradeSort;

		TreeMap<Integer,HashMap<String,String>> topGrade = new TreeMap<Integer,HashMap<String,String>>();
		this.topGrade = topGrade;
	}

	public Integer getBestGrade(String unikey) {

		TreeMap<Integer,TreeMap<Date,Submission>> grade = gradeSort.get(unikey);
		
		//dealing with null and illegalargumentexception
		if (unikey == null) {
			throw new IllegalArgumentException();
		}
		if (!gradeSort.containsKey(unikey)) {
			return null;
		}   	
		
		//base case
		return grade.lastKey();
	}


	public Submission getSubmissionFinal(String unikey) {
		
		//dealing with null and illegalargumentexception
		TreeMap<Date,Submission> recent = timeSort.get(unikey);
		if (unikey == null) {
			throw new IllegalArgumentException();
		}
		if (!timeSort.containsKey(unikey)) {
			return null;
		}
		//base case
		return recent.lastEntry().getValue();
	}


	public Submission getSubmissionBefore(String unikey, Date deadline) {

		TreeMap<Date,Submission> recent = timeSort.get(unikey);
		//dealing with null and illegalargumentexception
		if (unikey == null) {
			throw new IllegalArgumentException();
		}
		if (!timeSort.containsKey(unikey)) {
			return null;
		}

		//when there are no submissions before the given date
		try {
			recent.floorEntry(deadline).getValue();
		}
		catch(NullPointerException e) {
			return null;
		}

		//base case
		return recent.floorEntry(deadline).getValue();
	}



	public List<String> listTopStudents() {

		List<String> empty = new ArrayList<String>();
		
		//return null if there is no entry
		try {
			topGrade.lastEntry().getValue();
		}
		catch(NullPointerException e) {
			return empty;
		}
		
		//base case
		HashMap<String,String> topStudents = topGrade.get(topGrade.lastKey());

		//iterate through topGrade and add the unikey to the return list
		for (String values : topStudents.values()) {
			if(!empty.contains(values))
			empty.add(values);
		}
		
		return empty;
	}

	public List<String> listRegressions() {
		List<String> regression = new ArrayList<String>();
		
		//compare the information in timeSort with gradeSort
		for (String keys : timeSort.keySet()) {
			String unikey = keys;
			int recent = getSubmissionFinal(unikey).getGrade();
			int best  = getBestGrade(unikey);
			
			//only add to list if regressed
			if (recent < best) {
				regression.add(unikey);
			}
		}
		return regression;
	}
	public Submission add(String unikey, Date timestamp, Integer grade) {

		//no given arguments should be null
		if (unikey == null || timestamp == null || grade == null) {
			throw new IllegalArgumentException();
		}
		Node submission = new Node(unikey,timestamp,grade);

		//adding elements to timeSort (data structure for getSubmissionBefore & getSubmissionFinal)
		TreeMap<Date,Submission> dateStore = new TreeMap<Date,Submission>();

		if(timeSort.containsKey(unikey)) {
			dateStore = timeSort.get(unikey);
			dateStore.put(timestamp, submission);
		}
		else {
			dateStore.put(timestamp, submission);  	
			timeSort.put(unikey, dateStore); 
		}
		
		//adding elements to gradeSort (data structure for getBestGrade)
		TreeMap<Integer,TreeMap<Date,Submission>> gradeStore = new TreeMap<Integer,TreeMap<Date,Submission>>();
		TreeMap<Date,Submission> gradeStoreNode = new TreeMap<Date,Submission>();

		if (gradeSort.containsKey(unikey)) {
			gradeStore = gradeSort.get(unikey);
			if(gradeStore.containsKey(grade)) {
				gradeStoreNode = gradeStore.get(grade);
				gradeStoreNode.put(timestamp, submission);
			}
			else {
				gradeStoreNode.put(timestamp, submission);
				gradeStore.put(grade, gradeStoreNode);
			}
		}
		else {
			gradeStoreNode.put(timestamp, submission);
			gradeStore.put(grade, gradeStoreNode);	
			gradeSort.put(unikey, gradeStore);
		}

		//adding elements to topGrade(data structure for listTopStudents)
		HashMap<String,String> gradeTop = new HashMap<String,String>();
		
		if (topGrade.containsKey(grade)) {
			gradeTop = topGrade.get(grade);
			gradeTop.put(unikey+timestamp,unikey);
		}
		else {
			gradeTop.put(unikey+timestamp,unikey);
			topGrade.put(grade, gradeTop);
		}   	

		return submission;
	}

	public void remove(Submission submission) {

		if (submission == null) {
			throw new IllegalArgumentException();
		}

		String unikey = submission.getUnikey(); 	
		Date time = submission.getTime();
		Integer grade = submission.getGrade();

		//remove from timeSort
		TreeMap<Date,Submission> dateStore = timeSort.get(unikey);
		dateStore.remove(time);
		//remove from hash map if the inside node is empty
		if(dateStore.isEmpty()) {
			timeSort.remove(unikey);
		}

		//remove from gradeSort
		TreeMap<Integer,TreeMap<Date,Submission>> gradeStore = gradeSort.get(unikey);
		TreeMap<Date,Submission> gradeStoreNode = gradeStore.get(grade);
		gradeStoreNode.remove(time);
		if(gradeStoreNode.isEmpty()) {
			gradeStore.remove(grade);
		}
		if(gradeStore.isEmpty()) {
			gradeSort.remove(unikey);
		}
		
		//remove from topGrade
		//used unikey+time as the key for hashmap
		HashMap<String,String> topGradeNode = topGrade.get(grade);
		topGradeNode.remove(unikey+time);
		if(topGradeNode.isEmpty()) {
			topGrade.remove(grade);
		}
		//remove from students
	}


}

