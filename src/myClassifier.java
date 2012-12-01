import java.util.HashMap;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesUpdateable;


public class myClassifier {

	//some special tags that need global classifier
	private static final String SPAM_TAG = "spam";
	private static final String GOOD_TAG = "good";
	private static NaiveBayesUpdateable spamClassifier = null;
	private static NaiveBayesUpdateable goodClassifier = null;
	private static HashMap<String, Classifier> userClassifiers = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public static void feedData(Post p, String username, Set<String> tags) {
		//if it is spam, 
		if (tags.contains(SPAM_TAG)) {
			if (spamClassifier == null) {
				spamClassifier = new NaiveBayesUpdateable();
			}
			spamClassifier.updateClassifier(postToInstance(p));
		}
		//if it is good
		if (tags.contains(GOOD_TAG)) {
			if (goodClassifier == null) {
				goodClassifier = new NaiveBayesUpdateable();
			}
			goodClassifier.updateClassifier(postToInstance(p));
		}
		//
	}
	
	public String[] getTags(Post p, String username) {
		
	}
	
	

}
