package com.usns.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.usns.entities.Post;


public class GlobalClassifier {

	//some special tags that need global classifier
	public static final String SPAM_TAG = "spam";
	public static final String NOT_SPAM_TAG = "not_spam";
	private NaiveBayes spamClassifier = null;
	private static GlobalClassifier instance = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	private GlobalClassifier() {
		if (spamClassifier == null) {
			spamClassifier = new NaiveBayes(2);
		}
	}
	
	public static GlobalClassifier getInstance() {
		if (instance == null)
			instance = new GlobalClassifier();
		return instance;
	}
	
	public void feedData(Post p,String tags){
		
		//if it is spam, 
		if (tags.equals(SPAM_TAG)) {
			spamClassifier.update(getFieldData(p), getTextData(p), SPAM_TAG);
		} else {
			spamClassifier.update(getFieldData(p), getTextData(p), NOT_SPAM_TAG);
		}
	}
	


	public String getTag(Post p){
		String result = spamClassifier.classify(getFieldData(p), getTextData(p));
		if (result == null)
			return NOT_SPAM_TAG;
		return result;
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
