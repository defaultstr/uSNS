import java.util.HashMap;
import java.util.Set;

import com.usns.entities.Post;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instance;


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
			spamClassifier = new NaiveBayes();
		}
		if (userClassifiers == null) {
			userClassifiers = new HashMap<String, NaiveBayes>();
		}
	}
	
	public void feedData(Post p, String username, String tags) throws Exception {
		
		//if it is spam, 
		if (tags.equals(SPAM_TAG)) {
			
			spamClassifier.update(composeData(p), SPAM_TAG);
			//handle user preference
			NaiveBayes myClassifier = userClassifiers.get(username);
			if (myClassifier == null) {
				myClassifier = new NaiveBayes();
				userClassifiers.put(username, myClassifier);
			}
			myClassifier.update(composeData(p), SPAM_TAG);
		} else {
			spamClassifier.update(composeData(p), NO_SPAM_TAG);
		}
		
		//if it is good
		if (tags.equals(GOOD_TAG)) {
			//handle user preference
			NaiveBayes myClassifier = userClassifiers.get(username);
			if (myClassifier == null) {
				myClassifier = new NaiveBayes();
				userClassifiers.put(username, myClassifier);
			}
			myClassifier.update(composeData(p), GOOD_TAG);
		}
	}
	


	public String getTag(Post p, String username) throws Exception {
		double result = spamClassifier.classify(postToInstance(p, null));
		if (spamClassifier.)
		NaiveBayesUpdateable myClassifier = userClassifiers.get(username);
		
	}
	
	private Set<String> composeData(Post p) {
		Set<String> data = NaiveBayes.get2Grams(p.text);
		data.add(p.source);
		data.add(p.user);
		return data;
	}
	

}
