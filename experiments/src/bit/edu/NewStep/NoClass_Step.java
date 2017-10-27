package bit.edu.NewStep;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;

import bit.edu.NewStep.latticeConstuction.Godin_Incremental_Step;

public class NoClass_Step {//�����в�����class��Ϣ

	public static void main(String[] args) throws IOException {
		BufferedReader strin = new BufferedReader(new InputStreamReader(
				System.in));
		String consoleStr = "";
		double[][] accuRate = new double[fileNum + 1][testSetp];
		OutputStream os = new FileOutputStream("data/cbr/expriment.txt");
		while ((consoleStr = strin.readLine()) != null) {
			int switchFlag = Integer.parseInt(consoleStr);
			if(switchFlag < 0 || switchFlag > 10)
				break;
			if(switchFlag == 0) {
				for(int i = 0;i < fileNum + 1;i++) {
					for(int j = 0;j < testSetp;j++) {
						System.out.print(" " + accuRate[i][j]);
					}
					System.out.println();
				}
				continue;
			}
			
			if(switchFlag == 10) {
				long startClock = (new Date()).getTime();
				for(int j = 0;j < fileNum;j++) {
					String dataSource = filename + j;
					for(int i = 0;i < testSetp;i++) {
						long startTic = (new Date()).getTime();
						Godin_Incremental_Step gib = new Godin_Incremental_Step(attrCount, objCount, i + 1, dataSource);
						gib.computeGIM();
//						accuRate[j][i] = gib.startTestConImObj();
//						accuRate[j][i] = gib.startTest();
//						accuRate[j][i] = gib.startTestConIm();
						long endTic = (new Date()).getTime();
						totalTime += (endTic - startTic);
						String tempStr = i + ": " + accuRate[j][i] + "\ntotaltime:" + totalTime + ", testTime:" + testTime + "\n";
						os.write(tempStr.getBytes());
					}
				}
				String tempStr = "mean: ";
				//֮ǰ�ļ���ƽ��ֵ����
				for(int i = 0;i < testSetp;i++) {
					for(int j = 0;j < fileNum;j++)
						accuRate[fileNum][i] += accuRate[j][i];
					accuRate[fileNum][i] = accuRate[fileNum][i] / fileNum;
					tempStr = tempStr + accuRate[fileNum][i] + " ";
				}
				
				
				tempStr += "\n";
				os.write(tempStr.getBytes());
				long endClock = (new Date()).getTime();
				System.out.println("total time:" + (endClock - startClock));
				continue;
			}
			//������ʼtrain���ݸ����
			for(int j = 0;j < fileNum;j++) {
				String dataSource = filename + j;
				Godin_Incremental_Step gib = new Godin_Incremental_Step(attrCount, objCount, switchFlag, dataSource);
				gib.computeGIM();
//				long startTic = (new Date()).getTime();
				//start to test the concept lattice
//				accuRate[j][switchFlag - 1] = gib.startTestConImObj();
//				accuRate[j][switchFlag - 1] = gib.startTest();
//				accuRate[j][switchFlag - 1] = gib.startTestConIm();
//				long endTic = (new Date()).getTime();
//				accuRate[fileNum][switchFlag - 1] += accuRate[j][switchFlag - 1];
				
				accuRate[j] = gib.testForBitSet();
			}
			String tempStr = "mean: ";
			for(int i = 0;i < testSetp;i++) {
				for(int j = 0;j < fileNum;j++) {
					accuRate[fileNum][i] += accuRate[j][i];
				}
				accuRate[fileNum][i] = accuRate[fileNum][i] / fileNum;
				tempStr = tempStr + accuRate[fileNum][i] + " ";
			}
			String sstr = "";
			for(int i = 0;i < fileNum;i++) {
				for(int j = 0;j < testSetp;j++) {
					System.out.print(accuRate[i][j] + " ");
					sstr += " " + accuRate[i][j];
				}
				System.out.println();
				sstr += "\n";
			}
			os.write(sstr.getBytes());
			os.write(tempStr.getBytes());
			System.out.println(tempStr);
//			accuRate[fileNum][switchFlag - 1] /= fileNum;
		}
		os.close();
	}
	
	/*
	public void expriment() throws NumberFormatException, IOException {
		BufferedReader strin = new BufferedReader(new InputStreamReader(
				System.in));
		String consoleStr = "";
		double[] accuRate = new double[testSetp];
		while ((consoleStr = strin.readLine()) != null) {
			int switchFlag = Integer.parseInt(consoleStr);
			if(switchFlag < 0 || switchFlag > 10)
				break;
			if(switchFlag == 0) {
				for(int i = 0;i < accuRate.length;i++)
					System.out.println((i + 1) + ": " + accuRate[i]);
				continue;
			}
			if(switchFlag == 10) {
				for(int i = 0;i < testSetp;i++) {
					String dataSource = filename;
					Godin_Incremental_Board gib = new Godin_Incremental_Board(attrCount, objCount, i + 1, dataSource);
					gib.computeGIM();
//					accuRate[i] = gib.startTestConImObj();
//					accuRate[i] = gib.startTest();
//					accuRate[i] = gib.startTestConIm();
				}
				continue;
			}
			//������ʼtrain���ݸ����
			String dataSource = filename;
			Godin_Incremental_Board gib = new Godin_Incremental_Board(attrCount, objCount, switchFlag, dataSource);
			gib.computeGIM();
			//start to test the concept lattice
//			accuRate[switchFlag - 1] = gib.startTestConImObj();
			accuRate[switchFlag - 1] = gib.startTest();
//			accuRate[switchFlag - 1] = gib.startTestConIm();
			System.out.print(switchFlag + " " + accuRate[switchFlag - 1]);
		}
	}
	*/
	//4 point 0.55
	public static int testTime = 0;
	public static int totalTime = 0;
	public final static double A = 0.3;
	public final static int solutionPoint = 9;//6;//4;//5;//2;//18;//10;//3;//7;//
	//4;//2;//7;//3;//
	public final static int point = 75;//27;//17;//41;//16;//117;//48;//22;//48;//59;//114;//32;//88;//43;//
	//21;//17;//22;//15;//20;//27;//
	public final static int fileNum = 5;
	public final static int NUM_OF_CLUSTER = 8;
	public final static int testSetp = 9;//9;//for equal-step-increment validation 9, for 10 fold cross validation :10
	//5;//22;//26;//
	public final static int attrCount = 85;//30;//20;//48;//21;//120;//33;//67;//19;//20;//19;
	//38;//25;//59;//63;//117;//36;//92;//51;//
	//26;//25;//29;//19;//24;//
	//6;//1728;//
	public final static int objCount = 6876;//958;//432;//1066;//10800;//8124;//12960;//28056;//
	//5473;//306;//1484;//151;//351;//150;//178;//214;//
	//1728;//267;//101;//160;//625;//101;//160;//
	//"test.txt";//
	public final static String filename = "marketing/marketing.context";//"tic-toc/tic-tac-toe.all.new.context";//"mook/monks-3.context";//"flare/flare.context";//"firm/firm.context";//"mushroom/mushroom.context";//"balance-scale/balance_scale.all.new.context";//"spect/spect.all.new.context";//"car/car.all.new.context";//"hayes_roth/hayes-rothDO.all.new.context";//
	//"zoo/zoo.crossValidation.context";//
	//"hayes_roth/hayes-rothDO.all.new.context";//
	//"spect/spect.all.new.context";//
	//
	//
	//
	//
	//"balance-scale/balance_scale.all.new.context";//
	//
	//
}
