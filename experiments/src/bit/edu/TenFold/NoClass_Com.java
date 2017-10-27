package bit.edu.TenFold;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;

import bit.edu.TenFold.latticeConstuction.GIC_AddIntent;
import bit.edu.TenFold.latticeConstuction.Godin_Incremental_Combined;
//import bit.edu.cbr.latticeConstuction.Godin_Incremental_For_Numeric;
//import bit.edu.cbr.latticeConstuction.Godin_Incremental_Step;

public class NoClass_Com {//�����в�����class��Ϣ

	public static void main(String[] args) throws Exception {
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
//						Godin_Incremental_For_Numeric gib = new Godin_Incremental_For_Numeric(attrCount, objCount, i + 1, dataSource);
//						Godin_Incremental_Combined gib = new Godin_Incremental_Combined(attrCount, objCount, i + 1, dataSource);
//						gib.computeGIM();
						GIC_AddIntent gib = new GIC_AddIntent(attrCount, objCount, i + 1, dataSource);
						gib.computeGIM();
//						accuRate[j][i] = gib.startTestConImObj();
//						accuRate[j][i] = gib.startTest();
						accuRate[j][i] = gib.startTestConIm();
						long endTic = (new Date()).getTime();
						testTime += gib.getTestConsumingTime();
						totalTime += (endTic - startTic);
						String tempStr = i + ": " + accuRate[j][i] + "\ntotaltime:" + totalTime + ", testTime:" + testTime + "\n";
						os.write(tempStr.getBytes());
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
				System.out.println(MySimilarityCom.countOfConcepts);
				System.out.println(MySimilarityCom.countOfConcepts / objCount);
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
				Godin_Incremental_Combined gib = new Godin_Incremental_Combined(attrCount, objCount, switchFlag, dataSource);
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
	
	public static int testTime = 0;
	public static int totalTime = 0;
	public final static double A = 0.3;
	public final static int solutionPoint = 2;//3;//7;//5;//2;//18;//10;//
	//4;//2;//7;//3;//
	public final static int point = 22;//20;//21;//15;//32;//48;//117;//27;//17;//48;//48;//59;//114;//32;//88;//43;//
	//17;//22;//27;//
	public final static int fileNum = 1;
	public final static int NUM_OF_CLUSTER = 4;
	public final static int testSetp = 10;//9;//for equal-step-increment validation 9, for 10 fold cross validation :10
	//5;//22;//26;//
	public final static int attrCount = 25;//24;//29;//19;//35;//51;//120;//33;//20;//67;//19;//20;//
	//38;//25;//59;//63;//117;//36;//92;//51;//
	//26;//19;//30;//
	//6;//1728;//
	public final static int objCount = 267;//625;//101;//160;//435;//8124;//12960;//432;//28056;//
	//5473;//306;//1484;//151;//351;//150;//178;//214;//
	//1728;//101;//958;//160;//
	//"test.txt";//
	public final static String filename = "spect/spect.all.new.context";//"balance-scale/balance_scale.all.new.context";//"zoo/zoo.crossValidation.context";//"hayes_roth/hayes-rothDO.all.new.context";//"vote/vote.context";//"mushroom/mushroom.context";//"nursery/nursery.context";//"mook/monks-1.context";//"chess/chess.context";//
	//"page/page.context";//"haberman/haberman.context";//"yeast/yeast.context";//"tae/tae.context";//"ionosphere/ionosphere1.context";//"iris/iris.context";//"wine/wine.context";//"glass/glass.context";//
	
	
	//"car/car.all.new.context";//
	//
	//
	//"tic-toc/tic-tac-toe.all.new.context";//"car/car.all.new.context";//
	//
	//"tic-toc/tic-tac-toe.all.new.context";
	//"car/car.all.new.context";//"zoo/zoo.all.new.context";//
	//"balance-scale/balance_scale.all.new.context";
	//"car/car.all.new.context";//
	//"balance-scale/balance_scale.all.new.context";
	//"hayes_roth/hayes-rothDO.all.new.context";//
}
