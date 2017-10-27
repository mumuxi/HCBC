package bit.edu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CalculateAUC {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		List<Integer> labels = new ArrayList<Integer>();
		List<double[]> values = new ArrayList<double[]>();
		File file = new File("data/" + name);
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine()) != null) {
				String[] items = line.split(" ");
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
		int size = labels.size();
		for(int i = 0;i < dim - 1;i++) {
			positive = 0;
			int label_fixed = i + 1;
			boolean flag = true;
			double sumScore = 0.0;
			for(int n = 0;n < size;n++) {
				int label_n = labels.get(n);
				if(label_n == label_fixed) {
					flag = true;
					positive++;
				} else 
					flag = false;
				for(int m = n + 1;m < size;m++) {
					int label_m = labels.get(m);
					if((label_m == label_fixed && flag) || (label_m != label_fixed) && !flag)
						continue;
					double[] values_n = values.get(n);
					double[] values_m = values.get(m);
					if(changeProb(values_n,label_fixed - 1) > changeProb(values_m,label_fixed - 1)) {
						if(flag)
							sumScore += 1;
					} else if(Math.abs(changeProb(values_n,label_fixed - 1) - changeProb(values_m,label_fixed - 1)) < 0.0001) {
						sumScore += 0.5;
					} else {
						if(!flag)
							sumScore += 1;
					}
//					if(values_n[label_fixed - 1] > values_m[label_fixed - 1]) {
//						if(flag)
//							sumScore += 1;
//					} else if(values_n[label_fixed - 1] == values_m[label_fixed - 1]) {
//						sumScore += 0.5;
//					} else {
//						if(!flag)
//							sumScore += 1;
//					}
				}
			}
			acu += sumScore * positive / (positive * (size - positive));
			System.out.println(sumScore + " " + positive + " " + size);
			System.out.println(sumScore / (positive * (size - positive)));
		}
		System.out.println("final:" + acu / (size));
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
	public static String name = "nursery_8_1_p.txt";
}
