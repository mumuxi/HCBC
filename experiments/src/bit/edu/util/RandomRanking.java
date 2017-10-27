package bit.edu.util;


public class RandomRanking {
	
	public static int[] randomRank(int data) {
		int[] result = new int[data];
		for(int i = 0;i < data;i++) {
			result[i] = i;
		}
		for(int i = 0;i < data;i++) {
			int index = (int)Math.floor(Math.random() * data);
			int temp = result[i];
			result[i] = result[index];
			result[index] = temp;
		}
		return result;
	}

}
