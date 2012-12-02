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
	private final double lambda = 1.0;
	
	public static void main(String[] args) {
		String[] trainStrings = {"我爱你","我不爱你","今天天气真好","我来打酱油"};
		String[] trainClass = {"+","-","-","jiangyou"};
		String testString = "打酱油";
		NaiveBayes nb = new NaiveBayes();
		for (int i = 0; i < trainClass.length; i ++) {
			nb.updateString(trainStrings[i], trainClass[i]);
		}
		System.out.println(nb.classifyString(testString));
	}
	
	NaiveBayes() {
		className = new ArrayList<String>();
		classCount = new HashMap<String, Integer>();
		count = new TreeMap<String, TreeMap<String, Integer> >();
	}
	
	public void update(Set<String> data, String classVal) {
		N ++;
		if (!className.contains(classVal)) {
			className.add(classVal);
			classCount.put(classVal, 1);
		} else {
			classCount.put(classVal, classCount.get(classVal) + 1); 
		}
		for (String gram : data) {
			if (!count.containsKey(gram)) {
				count.put(gram, new TreeMap<String, Integer>());
			}
			TreeMap<String, Integer> tree = count.get(gram);
			if (!tree.containsKey(classVal)) {
				tree.put(classVal, 0);
			}
			tree.put(classVal, tree.get(classVal) + 1);
		}
	}
	
	public String classify(Set<String> data) {
		double[] p = new double[className.size()];
		for (int i = 0; i < className.size(); i++) {
			p[i] = 1.0 * classCount.get(className.get(i)) / N;
		}
		for (String gram : count.keySet()) {
			TreeMap<String, Integer> tree = count.get(gram);
			if (data.contains(gram)) {
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
		//find max
		int maxIndex = 0;
		for (int i = 1; i < className.size(); i++) {
			if (p[i] > p[maxIndex])
				maxIndex = i;
		}
		return className.get(maxIndex);
	}
	
	public String classifyString(String str) {
		return classify(get2Grams(str));
	}

	public static HashSet<String> get2Grams(String str) {
		HashSet<String> data = new HashSet<String>();
		for (int i = 0; i < str.length() - 1; i++) {
			data.add(str.substring(i, i + 2));
		}		
		return data;
	}
	
	public void updateString(String str, String classVal) {
		update(get2Grams(str), classVal);
	}
}
