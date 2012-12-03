package com.usns.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * a naive Bayes classifier, using a set of grams as its features
 * @author defaultstr
 *
 */
public class NaiveBayes {
	public ArrayList<String> className;
	private int N;
	//sum I(y = c_k)
	private HashMap<String, Integer> classCount;
	//sum I(x_j = 1, y = c_k), sum I(x_j = 0, y = c_k) = sum I(y = c_k) - sum I(x_j = 1, y = c_k)
	private TreeMap<String, TreeMap<String, Integer> > count;
	//some fields like username that may have more than two possible values
	private ArrayList<HashMap<String, HashMap<String, Integer>>>  fieldCount;
	private int fieldNum;
	private final double lambda = 1.0;
	//field attributes suffers less from spatial data, so lambda should be small
	private final double fieldLambda = 0.2;
	
	public static void main(String[] args) {
		String[] trainStrings = {"我爱你","我不爱你","今天天气真好","我来打酱油"};
		String[] trainClass = {"+","-","-","jiangyou"};
		String testString = "打酱油";
		NaiveBayes nb = new NaiveBayes(0);
		for (int i = 0; i < trainClass.length; i ++) {
			nb.update(new ArrayList<String>(), NaiveBayes.get2Grams(trainStrings[i]), trainClass[i]);
		}
		System.out.println(nb.classify(new ArrayList<String>(), NaiveBayes.get2Grams(testString)));
		
		ArrayList<String>[] trainArr = new ArrayList[4];
		for (int i = 0; i < 4; i++)
			trainArr[i] = new ArrayList<String>();
		trainArr[0].add("+");
		trainArr[0].add("1");
		trainArr[1].add("-");
		trainArr[1].add("2");
		trainArr[2].add(".");
		trainArr[2].add("3");
		trainArr[3].add("jiangyou");
		trainArr[3].add("4");
		ArrayList<String> testArr = new ArrayList<String>();
		testArr.add("jiangyou");
		testArr.add("5");
		NaiveBayes nb1 = new NaiveBayes(2);
		for (int i = 0; i < trainArr.length; i++) {
			nb1.update(trainArr[i], new HashSet<String>(), trainClass[i]);
		}
		System.out.println(nb1.classify(testArr, new HashSet<String>()));
	}
	
	NaiveBayes(int fieldNum) {
		className = new ArrayList<String>();
		classCount = new HashMap<String, Integer>();
		count = new TreeMap<String, TreeMap<String, Integer> >();
		fieldCount = new ArrayList<HashMap<String, HashMap<String, Integer>>>(fieldNum);
		for (int i = 0; i < fieldNum; i++)
			fieldCount.add(new HashMap<String, HashMap<String, Integer>>());
	}
	
	public void update(ArrayList<String> fieldData, Set<String> textData, String classVal) {
		N ++;
		if (!className.contains(classVal)) {
			className.add(classVal);
			classCount.put(classVal, 0);
		} 
		classCount.put(classVal, classCount.get(classVal) + 1); 
		
		for (String gram : textData) {
			if (!count.containsKey(gram)) {
				count.put(gram, new TreeMap<String, Integer>());
			}
			TreeMap<String, Integer> tree = count.get(gram);
			if (!tree.containsKey(classVal)) {
				tree.put(classVal, 0);
			}
			tree.put(classVal, tree.get(classVal) + 1);
		}
		
		for (int i = 0; i < fieldCount.size(); i++) {
			String x = fieldData.get(i);
			HashMap<String, HashMap<String, Integer>> Ij = fieldCount.get(i);
			if (!Ij.containsKey(x)) {
				Ij.put(x, new HashMap<String, Integer>());
			}
			HashMap<String, Integer> Ijx = Ij.get(x);
			if (!Ijx.containsKey(classVal)) {
				Ijx.put(classVal, 0);
			}
			Ijx.put(classVal, Ijx.get(classVal) + 1);
 		}
	}
	
	public String classify(ArrayList<String> fieldData, Set<String> textData) {
		double[] p = new double[className.size()];
		for (int i = 0; i < className.size(); i++) {
			p[i] = 1.0 * classCount.get(className.get(i)) / N;
		}
		for (String gram : count.keySet()) {
			TreeMap<String, Integer> tree = count.get(gram);
			if (textData.contains(gram)) {
				for (int i = 0; i < className.size(); i++) {
					//get sum I(x_j = 1, y = c_k)
					String curClass = className.get(i);
					double x = 0.0;
					if (tree.get(curClass) != null)
						x = tree.get(curClass);
					p[i] *= (x + lambda) / (classCount.get(curClass) + 2 * lambda);
				}
			} else {
				for (int i = 0; i < className.size(); i++) {
					String curClass = className.get(i);
					double x = classCount.get(curClass);
					if (tree.get(curClass) != null)
						x -= tree.get(curClass);
					p[i] *= (x + lambda) / (classCount.get(curClass) + 2 * lambda);
				}
			}
			//normalize
			double max = 0.0;
			for (int i = 0; i < className.size(); i++) {
				max = max > p[i]? max : p[i];
			}
			for (int i = 0; i < className.size(); i++) {
				p[i] /= max;
			}
		}
		
		for (int i = 0; i < fieldCount.size(); i++) {
			String x = fieldData.get(i);
			HashMap<String, HashMap<String, Integer>> Ij = fieldCount.get(i);
			HashMap<String, Integer> Ijx = Ij.get(x);
			//the x is not in training set, so this field can't provide any info
			if (Ijx == null) {
				continue;
			}
			for (int j = 0; j < className.size(); j++) {
				String curClass = className.get(j);
				Integer Ijxy = Ijx.get(curClass);
				if (Ijxy == null) {
					Ijxy = 0;
				}
				p[j] *= (Ijxy + fieldLambda) / (classCount.get(curClass) + Ij.size() * fieldLambda);
			}
			//normalize
			double max = 0.0;
			for (int j = 0; j < className.size(); j++) {
				max = max > p[j]? max : p[j];
			}
			for (int j = 0; j < className.size(); j++) {
				p[j] /= max;
			}
		}
		//find max
		int maxIndex = 0;
		for (int i = 1; i < className.size(); i++) {
			if (p[i] > p[maxIndex])
				maxIndex = i;
		}
		return className.get(maxIndex);
	}
	

	public static HashSet<String> get2Grams(String str) {
		HashSet<String> data = new HashSet<String>();
		for (int i = 0; i < str.length() - 1; i++) {
			data.add(str.substring(i, i + 2));
		}		
		return data;
	}
	
}
