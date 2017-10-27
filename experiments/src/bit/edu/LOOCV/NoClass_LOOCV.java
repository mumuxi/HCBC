package bit.edu.LOOCV;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;

import bit.edu.LOOCV.latticeConstuction.GIC_AddIntent_LOOCV;

//import bit.edu.cbr.latticeConstuction.Godin_Incremental_For_Numeric;
//import bit.edu.cbr.latticeConstuction.Godin_Incremental_Step;

public class NoClass_LOOCV {//�����в�����class��Ϣ

	public static void main(String[] args) throws Exception {
		BufferedReader strin = new BufferedReader(new InputStreamReader(
				System.in));
		String consoleStr = "";
		double[][] accuRate = new double[fileNum + 1][testSetp];
		OutputStream os = new FileOutputStream("data/cbr/expriment.txt");
		osScore = new FileOutputStream("data/cbr/" + scoreFile);
//		while ((consoleStr = strin.readLine()) != null) {
			consoleStr = "10";
			int switchFlag = Integer.parseInt(consoleStr);
			/*if(switchFlag < 0 || switchFlag > 10)
				break;*/
			if(switchFlag == 0) {
				for(int i = 0;i < fileNum + 1;i++) {
					for(int j = 0;j < testSetp;j++) {
						System.out.print(" " + accuRate[i][j]);
					}
					System.out.println();
				}
				/*continue;*/
			}
			
			if(switchFlag == 10) {
				long startClock = (new Date()).getTime();
				for(int j = 0;j < fileNum;j++) {
					String dataSource = filename + j;
					GIC_AddIntent_LOOCV gib = new GIC_AddIntent_LOOCV(attrCount, objCount, 1, dataSource);
					gib.computeGIM();
					for(int i = 0;i < testSetp;i++) {
						if(i % 100 == 0) {
							double acutemp = 0;
							for(int k = 0;k < i;k++) {
								acutemp += accuRate[j][k];
							}
							System.out.println(acutemp / (i + 1));
							int[][] result = gib.getAnaMatrix();
							for(int n = 0;n < NoClass_LOOCV.solutionPoint;n++) {
								for(int k = 0;k < NoClass_LOOCV.solutionPoint;k++) {
									System.out.print(result[n][k] + " ");
								}
								System.out.println();
							}
						}
						long startTic = (new Date()).getTime();
//						Godin_Incremental_For_Numeric gib = new Godin_Incremental_For_Numeric(attrCount, objCount, i + 1, dataSource);
//						Godin_Incremental_Combined gib = new Godin_Incremental_Combined(attrCount, objCount, i + 1, dataSource);
//						gib.computeGIM();
//						accuRate[j][i] = gib.startTestConImObj();
//						accuRate[j][i] = gib.startTest();
						accuRate[j][i] = gib.startTestConIm();
						long endTic = (new Date()).getTime();
						testTime = gib.getTestConsumingTime();
						totalTime += (endTic - startTic);
						String tempStr = i + ": " + accuRate[j][i] + "\ntotaltime:" + totalTime + ", testTime:" + testTime + "\n";
						os.write(tempStr.getBytes());
						gib.setSwitch(i + 2);
						if(i == stop) {
							break;
						}
					}
					int[][] result = gib.getAnaMatrix();
					for(int n = 0;n < NoClass_LOOCV.solutionPoint;n++) {
						for(int k = 0;k < NoClass_LOOCV.solutionPoint;k++) {
							System.out.print(result[n][k] + " ");
						}
						System.out.println();
					}
					System.out.println("******************************");
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
				System.out.println(tempStr);
				os.write(tempStr.getBytes());
				long endClock = (new Date()).getTime();
				System.out.println(MySimilarity_LOOCV.countOfConcepts);
				System.out.println(MySimilarity_LOOCV.countOfConcepts * 1.0 / (stop + 1));
				System.out.println("total time:" + (endClock - startClock));
				Toolkit kit = Toolkit.getDefaultToolkit(); 
				for(int b = 0;b < 3;b++) {
					Thread.sleep(500);
					kit.beep();
				}
				/*continue;*/
				return ;
			}
			//������ʼtrain���ݸ����
			for(int j = 0;j < fileNum;j++) {
//				String dataSource = filename + j;
//				Godin_Incremental_Combined gib = new Godin_Incremental_Combined(attrCount, objCount, switchFlag, dataSource);
//				gib.computeGIM();
//				long startTic = (new Date()).getTime();
				//start to test the concept lattice
//				accuRate[j][switchFlag - 1] = gib.startTestConImObj();
//				accuRate[j][switchFlag - 1] = gib.startTest();
//				accuRate[j][switchFlag - 1] = gib.startTestConIm();
//				long endTic = (new Date()).getTime();
//				accuRate[fileNum][switchFlag - 1] += accuRate[j][switchFlag - 1];
				
//				accuRate[j] = gib.testForBitSet();
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
//		}
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
	
	public static final int stop = 68760;
	public static long testTime = 0;
	public static long totalTime = 0;
	public final static double A = 0.3;
	public static String scoreFile = "score.txt";
	public static OutputStream osScore;
	public final static int solutionPoint = 18;//9;//8;//10;//5;//3;//2;//7;//10;//3;//2;//4;//2;//18;//6;//4;//2;//7;//
	//2;//7;//3;//
	public final static int point = 40;//275;//85;//7;//20;//21;//22;//43;//48;//117;//114;//32;//59;//16;//17;//15;//40;//22;//41;//22;//88;//
	//17;//22;//27;//
	public final static int fileNum = 1;
	public final static int NUM_OF_CLUSTER = 14;
	public final static int testSetp = 28056;//25010;//214;//10800;// 8124;//8124;//9;//for equal-step-increment validation 9, for 10 fold cross validation :10
	//5;//22;//26;//
	public final static int attrCount = 59;//85;//96;//33;//24;//25;//51;//121;//44;//117;//35;//38;
	// 63;//21;//20;//19;//25;//48;//26;//30;//29;//
	//19;//20;//19;
	//36;//92;//
	//
	//6;//1728;//


	public final static int objCount = 28056;//6876;//25010;//12960;//625;//306;//214;//1484;//178;//150;//351;/
	// /435;//5473;//151;//10800;//12960;//432;//160;//8124;//267;//1066;//1728;//958;//101;//160;//

	//5473;//151;//
	//101;//
	//"test.txt";//
	public final static String filename = "chess/chess.context";//"marketing/marketing.context";//"poker/poker.context";//"nursery/nursery.context";//"balance-scale/balance_scale.all.new.context";//
	//"haberman/haberman.context";//"glass/glass.context";//"yeast/yeast.context";//"wine/wine-9.crossValidation.context";//"iris/iris.crossValidation.context";//"ionosphere/ionosphere1.context";//"vote/vote.context";//"page/page.context";//"tae/tae.context";//
	//"firm/firm.context";//"nursery/nursery.context";//"mook/monks-2.context";//"hayes_roth/hayes-rothDO.all.new.context";//"mushroom/mushroom.context";//"chess/chess.context";
	//"hayes_roth/hayes-rothDO.test.context";//"spect/spect.all.new.context";//"flare/flare.context";//"car/car.all.new.context";//"balance-scale/balance_scale.all.new.context";//"tic-toc/tic-tac-toe.all.new.context";//"hayes_roth/hayes-rothDO.all.new.context";//"zoo/zoo.crossValidation.context";//
	//"balance-scale/balance_scale.all.new.context";//"mushroom/mushroom.context";//"chess/chess.context";
	//
	//
	//"glass/glass.context";//
	//page--37*5473 5  glass 50*214 7 iris--43*150
	//haberman--24*306 2 ionosphere--116*351
	//yeast--58*1484 10
	//wine--120*178 3
	//tae--62*151 3
	//vote--34*435
	//"tic-toc/tic-tac-toe.all.new.context";//"car/car.all.new.context";//
	//
	//"tic-toc/tic-tac-toe.all.new.context";
	//"car/car.all.new.context";//"zoo/zoo.all.new.context";//
	//
	//"car/car.all.new.context";//
	//
	//
}
