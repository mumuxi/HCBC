package bit.edu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CalMetric {

	public static void main(String[] args) {
		context = new int[length + 1][length + 1];
		FileReader fr = null;
		BufferedReader br = null;
		int count = -1;
		try {
			fr = new FileReader(new File(path + "/" + inputDataFile));
			br = new BufferedReader(fr);
			String temp = null;
			while((temp = br.readLine()) != null) {
				count++;
				String[] values = temp.trim().split(" ");
				if(values.length != length) {
					throw new RuntimeException("wrong data number" + count);
				}
				for(int i = 0;i < values.length;i++) {
					context[count][i] = Integer.parseInt(values[i]);
				}
 			}
			double p0 = 0;
			double pe = 0;
			double sum = 0;
			double diagonal = 0;
			for(int i = 0;i < length;i++) {
				int rec = 0;
				for(int j = 0;j < length;j++) {
					rec += context[i][j];
					if(i == j) 
						diagonal += context[i][j];
				}
				context[i][length] = rec;
				sum += rec;
			}
			for(int i = 0;i < length;i++) {
				int rec = 0;
				for(int j = 0;j < length;j++) {
					rec += context[j][i];
				}
				context[length][i] = rec;
			}
			p0 = diagonal / sum;
			double tempV = 0;
			for(int i = 0;i < length;i++) {
				tempV += context[length][i] * context[i][length];
			}
			pe = tempV / (sum * sum);
			
			double kappa = (p0 - pe) / (1 - pe);
			System.out.println(kappa);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fr != null)
					fr.close();
				if(br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final String path = "./data";
	public static final String inputDataFile = "confusion-matrix.txt";
	public static final int length = 5;
	private static int[][] context;
}