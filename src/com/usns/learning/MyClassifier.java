package com.usns.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.usns.entities.Post;


public class MyClassifier {

	//some special tags that need global classifier
	private static final String SPAM_TAG = "spam";
	private static final String NO_SPAM_TAG = "no_spam";
	private static final String GOOD_TAG = "good";
	private static NaiveBayes spamClassifier = null;
	private static HashMap<String, NaiveBayes> userClassifiers = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public MyClassifier() {
		if (spamClassifier == null) {
			spamClassifier = new NaiveBayes(2);
		}
		if (userClassifiers == null) {
			userClassifiers = new HashMap<String, NaiveBayes>();
		}
	}
	
	public void feedData(Post p, String username, String tags) throws Exception {
		
		//if it is spam, 
		if (tags.equals(SPAM_TAG)) {
			
			spamClassifier.update(getFieldData(p), getTextData(p), SPAM_TAG);
			//handle user preference
			NaiveBayes myClassifier = userClassifiers.get(username);
			if (myClassifier == null) {
				myClassifier = new NaiveBayes(2);
				userClassifiers.put(username, myClassifier);
			}
			myClassifier.update(getFieldData(p), getTextData(p), SPAM_TAG);
		} else {
			spamClassifier.update(getFieldData(p), getTextData(p), NO_SPAM_TAG);
		}
		
		//if it is good
		if (tags.equals(GOOD_TAG)) {
			//handle user preference
			NaiveBayes myClassifier = userClassifiers.get(username);
			if (myClassifier == null) {
				myClassifier = new NaiveBayes(2);
				userClassifiers.put(username, myClassifier);
			}
			myClassifier.update(getFieldData(p), getTextData(p), GOOD_TAG);
		}
	}
	


	public String getTag(Post p, String username) throws Exception {
		String result = spamClassifier.classify(getFieldData(p), getTextData(p));
		if (result.equals(SPAM_TAG))
			return SPAM_TAG;
		NaiveBayes myClassifier = userClassifiers.get(username);
		return myClassifier.classify(getFieldData(p), getTextData(p));
	}
	
	private Set<String> getTextData(Post p) {
		Set<String> data = NaiveBayes.get2Grams(p.text);
		data.add(p.source);
		data.add(p.user);
		return data;
	}
	
	private ArrayList<String> getFieldData(Post p) {
		ArrayList<String> data = new ArrayList<String>();
		data.add(p.user);
		data.add(p.source);
		return data;
	}
	

}
