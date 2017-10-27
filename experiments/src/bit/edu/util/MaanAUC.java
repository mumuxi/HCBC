package bit.edu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MaanAUC {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		List<Integer> labels = new ArrayList<Integer>();
		List<double[]> values = new ArrayList<double[]>();
		File file = new File("data/"
				+ "" + name);
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine()) != null) {
				String[] items = line.split(" ");
				System.out.println(items.length);
				if(dim == -1)
					dim = items.length;
				if(dim != items.length)
					throw new RuntimeException("wrong dimention");
				labels.add(Integer.parseInt(items[0]));
				double[] valueArray = new double[dim - 1];
				for(int i = 1;i < dim;i++) {
					valueArray[i - 1] = Double.parseDouble(items[i]);
				}
				values.add(valueArray);
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		double acu = 0.0;
		int positive = 0;
		int allsize = labels.size();
		for(int i = 0;i < dim - 1;i++) {
			for(int j = 0;j < dim - 1;j++) {
				if(i == j)
					continue;
				int size = labels.size();
				positive = 0;
				int label_fixed_i = i + 1;
				int label_fixed_j = j + 1;
				boolean flag = true;
				double sumScore = 0.0;
				for(int n = 0;n < allsize;n++) {
					int label_n = labels.get(n);
					if(label_n == label_fixed_i) {
						flag = true;
						positive++;
					} else if(label_n == label_fixed_j) {
						flag = false;
					} else {
						size--;
						continue;
					}
					for(int m = n + 1;m < allsize;m++) {
						int label_m = labels.get(m);
						if((label_m != label_fixed_i) && (label_m != label_fixed_j))
							continue;
						if((label_m == label_fixed_i && flag) || (label_m == label_fixed_j) && !flag)
							continue;
						double[] values_n = values.get(n);
						double[] values_m = values.get(m);
//						double value_n = values_n[label_fixed_i - 1] / (values_n[label_fixed_i - 1] + values_n[label_fixed_j - 1]);
//						double value_m = values_m[label_fixed_i - 1] / (values_m[label_fixed_i - 1] + values_m[label_fixed_j - 1]);
						double value_n = values_n[label_fixed_i - 1];
						double value_m = values_m[label_fixed_i - 1];
						if(value_n > value_m) {
							if(flag)
								sumScore += 1;
						} else if(Math.abs(value_n - value_m) < 0.0001) {
							sumScore += 0.5;
						} else {
							if(!flag)
								sumScore += 1;
						}
					}
				}
				acu += sumScore / (positive * (size - positive)) * positive / allsize;
				System.out.println(sumScore + " " + positive + " " + size);
				System.out.println(sumScore / (positive * (size - positive)) * positive / allsize);
				System.out.println("auc" + acu + "\n");
			}
		}
		System.out.println(acu);
		System.out.println("final:" + acu * 1 / ((dim - 2)));
	}
	
	public static double isMaximum(double[] array, int index) {
		double value = array[index];
		boolean flag = true;
		int k = -1;
		for(int i = 0;i < array.length;i++) {
			double temp = array[i];
			if(temp > value) {
				k = i;
				break;
			} else if(temp == value) {
				if(i != index) {
					flag = false;
				}
			}
		}
		if(k != -1)
			return 0.0;
		if(flag) {
			return 1.0;
		} else
			return 0.5;
	}
	
	public static double original(double[] array, int index) {
		return array[index];
	}
	
	public static double changeProb(double[] array, int index) {
		double value = array[index];
		boolean flag = true;
		int k = -1;
		for(int i = 0;i < array.length;i++) {
			double temp = array[i];
			if(temp > value) {
				k = i;
				break;
			} else if(temp == value) {
				if(i != index) {
					flag = false;
				}
			}
		}
//		if(k != -1)
//			return 0.0;
		double result = value + 1.0 / array.length;
		if(flag) {
			return result;
		} else 
			return value;
	}
	public static int dim = -1;
	public static String name = "FCBC/nursery.txt";
}
