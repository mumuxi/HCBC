package bit.edu.FCBC;

import bit.edu.FCBC.latticeConstuction.FCBC_GIC;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
//import bit.edu.cbr.latticeConstuction.Godin_Incremental_For_Numeric;
//import bit.edu.cbr.latticeConstuction.Godin_Incremental_Step;

public class NoClass_FCBC {//�����в�����class��Ϣ

	public static void main(String[] args) throws Exception {
		BufferedReader strin = new BufferedReader(new InputStreamReader(
				System.in));
		String consoleStr = "";
		double[][] accuRate = new double[fileNum + 1][testSetp];
		OutputStream os = new FileOutputStream("data/cbr/expriment.txt");
		osScore = new FileOutputStream("data/cbr/" + scoreFile);
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
					FCBC_GIC gib = new FCBC_GIC(attrCount, objCount, 1, dataSource);
					gib.computeGIM();
					for(int i = 0;i < testSetp;i++) {
						if(i % 100 == 0) {
							double acutemp = 0;
							for(int k = 0;k < i;k++) {
								acutemp += accuRate[j][k];
							}
							System.out.println(acutemp / (i + 1));;
						}
						long startTic = (new Date()).getTime();
//						Godin_Incremental_For_Numeric gib = new Godin_Incremental_For_Numeric(attrCount, objCount, i + 1, dataSource);
//						GIC_AddIntent_LOOCV gib = new GIC_AddIntent_LOOCV(attrCount, objCount, i + 1, dataSource);
//						gib.computeGIM();
						accuRate[j][i] = gib.startTestConImObj();
//						accuRate[j][i] = gib.startTest();
						long endTic = (new Date()).getTime();
						testTime = gib.getTestConsumingTime();
						totalTime += (endTic - startTic);
						String tempStr = i + ": " + accuRate[j][i] + "\ntotaltime:" + totalTime + ", testTime:" + testTime + "\n";
						os.write(tempStr.getBytes());
						gib.setSwitch(i + 2);
					}
					int[][] result = gib.getAnaMatrix();
					for(int i = 0; i < NoClass_FCBC.solutionPoint; i++) {
						for(int k = 0; k < NoClass_FCBC.solutionPoint; k++) {
							System.out.print(result[i][k] + " ");
						}
						System.out.println();
					}
				}
				String tempStr = "mean: ";
				//10 fold cross validation
				double accuRateTotal = 0;
				double accuTemp = 0;
				for(int i = 0;i < fileNum;i++) {
					accuTemp = 0;
					for(int j = 0;j < testSetp;j++) {
						if(j == testSetp - 1) {
//							accuTemp += accuRate[i][j] * (objCount - (testSetp - 1) * objCount / testSetp);
							accuTemp += accuRate[i][j];
						} else {
//							int internal = objCount / testSetp;
//							accuTemp += accuRate[i][j] * internal;
							accuTemp += accuRate[i][j];;
						}
					}
					accuRateTotal += accuTemp;
					tempStr = tempStr + (1.0 * accuTemp / objCount) + " ";
				}
				tempStr += (accuRateTotal / (fileNum * objCount));
				//֮ǰ�ļ���ƽ��ֵ����
//				for(int i = 0;i < testSetp;i++) {
//					for(int j = 0;j < fileNum;j++)
//						accuRate[fileNum][i] += accuRate[j][i];
//					accuRate[fileNum][i] = accuRate[fileNum][i] / fileNum;
//					tempStr = tempStr + accuRate[fileNum][i] + " ";
//				}
				
				
				tempStr += "\n";
				os.write(tempStr.getBytes());
				long endClock = (new Date()).getTime();
				System.out.println(FCBC_LOOCV.countOfConcepts);
				System.out.println(FCBC_LOOCV.countOfConcepts / objCount);
				System.out.println("total time:" + (endClock - startClock));
				Toolkit kit = Toolkit.getDefaultToolkit(); 
				for(int b = 0;b < 3;b++) {
					Thread.sleep(500);
					kit.beep();
				}
				continue;
			}
			//������ʼtrain���ݸ����
			for(int j = 0;j < fileNum;j++) {
				String dataSource = filename + j;
				FCBC_GIC gib = new FCBC_GIC(attrCount, objCount, switchFlag, dataSource);
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
				for(int j = 0;j < fileNum;j++)
					accuRate[fileNum][i] += accuRate[j][i];
				accuRate[fileNum][i] = accuRate[fileNum][i] / fileNum;
				tempStr = tempStr + accuRate[fileNum][i] + " ";
			}
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
	
	public static long testTime = 0;
	public static long totalTime = 0;
	public final static double A = 0.3;
	public static String scoreFile = "score.txt";
	public static OutputStream osScore;
	public final static int solutionPoint = 9;//18;//5;//3;//7;//6;//2;//2;//10;//3;//
	//4;//2;//7;//3;//
	public final static int point = 75;//40;//27;// 15;//21;//41;//48;//17;//22;//48;//59;//114;//32;//88;//43;//
	//17;//22;//20;//27;//
	public final static int fileNum = 1;
	public final static int NUM_OF_CLUSTER = 4;
	public final static int testSetp = 6876;//958;//9;//for equal-step-increment validation 9, for 10 fold cross validation :10
	//5;//22;//26;//
	public final static int attrCount = 85;//59;//33;//19;//29;//48;//30;//67;//20;//19;
	//38;//25;//63;//117;//36;//92;//51;//
	//26;//25;//24;//
	//6;//1728;//
	public final static int objCount = 6876;//28056;//12960;//160;//101;//1066;//958;//
	//432;//
	//5473;//306;//1484;//151;//351;//150;//178;//214;//
	//1728;//267;//101;//160;//625;//
	//"test.txt";//
	public final static String filename = "marketing/marketing.context";//"chess/chess.context";//"nursery/nursery.context";//"hayes_roth/hayes-rothDO.all.new.context";//"zoo/zoo.crossValidation.context";//"flare/flare.context";//"tic-toc/tic-tac-toe.all.new.context";//"nursery/nursery.context";//"flare/flare.context";//
	//"mook/monks-1.context";//
	//"page/page.context";//"haberman/haberman.context";//"yeast/yeast.context";//"tae/tae.context";//"ionosphere/ionosphere1.context";//"iris/iris.context";//"wine/wine.context";//"glass/glass.context";//
	
	
	//"car/car.all.new.context";//"spect/spect.all.new.context";//"balance-scale/balance_scale.all.new.context";//
	//
	//
	//"car/car.all.new.context";//
	//
	//"tic-toc/tic-tac-toe.all.new.context";
	//"car/car.all.new.context";//"zoo/zoo.all.new.context";//
	//"balance-scale/balance_scale.all.new.context";
	//"car/car.all.new.context";//
	//"balance-scale/balance_scale.all.new.context";
	//"hayes_roth/hayes-rothDO.all.new.context";//
}
