package com.usns.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.usns.entities.Post;


public class MyClassifier {

	//some special tags that need global classifier
	public static final String SPAM_TAG = "spam";
	public static final String NOT_SPAM_TAG = "not_spam";
	public static final String GOOD_TAG = "good";
	public static final String NOT_GOOD_TAG = "not_good";
	private NaiveBayes spamClassifier = null;
	private NaiveBayes goodClassifier = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public MyClassifier() {
		if (spamClassifier == null) {
			spamClassifier = new NaiveBayes(2);
		}
		if (goodClassifier == null) {
			goodClassifier = new NaiveBayes(2);
		}
	}
	
	public void feedData(Post p,String tags){
		
		//if it is spam, 
		if (tags.equals(SPAM_TAG)) {
			spamClassifier.update(getFieldData(p), getTextData(p), SPAM_TAG);
		} else {
			spamClassifier.update(getFieldData(p), getTextData(p), NOT_SPAM_TAG);
			if (tags.equals(GOOD_TAG)) {
				goodClassifier.update(getFieldData(p), getTextData(p), GOOD_TAG);
			} else {
				goodClassifier.update(getFieldData(p), getTextData(p), NOT_GOOD_TAG);
			}
		}
	}
	


	public String getTag(Post p){
		String result = spamClassifier.classify(getFieldData(p), getTextData(p));
		if (result == null)
			return NOT_GOOD_TAG;
		if (result.equals(SPAM_TAG))
			return SPAM_TAG;
		result = goodClassifier.classify(getFieldData(p), getTextData(p));
		if (result == null)
			return NOT_GOOD_TAG;
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
