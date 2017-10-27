package bit.edu.FCBC.latticeConstuction;

import bit.edu.FCBC.FCBC_LOOCV;
import bit.edu.FCBC.NoClass_FCBC;
import bit.edu.entity.ConIndexPair;
import bit.edu.entity.Concept;
import bit.edu.util.MapClone;
import java.util.Map.Entry;

import java.io.*;
import java.util.*;

public class FCBC_GIC {

	/**
	 * 
	 * @param attrCount
	 * @param objCount
	 * @param switchFlag
	 * @param dataSource
	 */
	
	public FCBC_GIC(int attrCount, int objCount, int switchFlag, String dataSource) {
		this.attrCount = attrCount;
		this.objCount = objCount;
		this.switchFlag = switchFlag;
		this.contextFileName = dataSource;
		this.initial();
	}
	
	public void setSwitch(int flag) {
		this.switchFlag = flag;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public void initial() {
		this.setTestConsumingTime(0);
		this.probabilityByLabel = new int[NoClass_FCBC.solutionPoint][attrCount - NoClass_FCBC.solutionPoint];
		this.context = new int[objCount][attrCount - 1];
		this.attsCount = new int[attrCount - 1];
		this.classValue = new int[objCount];
		this.remainConcepts = new HashMap<Integer, List<Concept>>(this.attrCount * 2);
		this.anaMatrix = new int[NoClass_FCBC.solutionPoint][NoClass_FCBC.solutionPoint];
		int count = 0;
		File input = new File(path + "/" + this.contextFileName);
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(input);
			br = new BufferedReader(fr);
			String temp = null;
			temp = br.readLine();
			if(temp != null) {
				String[] counts = temp.split(" ");
				if(counts.length != this.attrCount - 1)
					throw new RuntimeException(
							"with wrong column # of attribute!");
				for(int i = 0;i < counts.length;i++) {
					this.attsCount[i] = Integer.parseInt(counts[i]);
				}
			}
			while ((temp = br.readLine()) != null) {
				String[] data = temp.split(",");
				if (data.length != 2)
					throw new RuntimeException(
							"with wrong column # of attribute!");
				this.classValue[count] = Integer.valueOf(data[1]);
				String[] context = data[0].split(" ");
				if (context.length != this.attrCount - 1)
					throw new RuntimeException(
							"with wrong column # of attribute!");
				for (int i = 0; i < this.attrCount - 1; i++) {
					String value = context[i];
					if (value.trim().equals(""))
						throw new RuntimeException(
								"There is value of attribute missing!");
					this.context[count][i] = Integer.parseInt(value);
				}
				count++;
				if (count >= this.objCount)
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
//		for(int i = 0;i < this.objCount;i++) {
//			for(int j = 0;j < this.attrCount;j++) {
//				System.out.print(this.context[i][j] + " ");
//			}
//			System.out.println();
//		}
	}
	
	public void computeGIM() {
		long startTic = (new Date()).getTime();
		
		Vector<Integer> attrVect = this.bottomConcept.getAttrSet();
		ConIndexPair cip = this.bottomConcept.getId();
		cip.setIndexOfMap(this.attrCount - 1);
		cip.setIndexOfBucket(0);
		for(int i = 0;i < this.attrCount - 1;i++) {
			attrVect.add(i);
		}
		List<Concept> tlist = new ArrayList<Concept>();
		tlist.add(this.bottomConcept);
		this.remainConcepts.put(this.attrCount - 1, tlist);
		
//		int testStart = 0;
//		int testEnd = 0;
//		testStart = this.objCount * (switchFlag - 1) / NoClass_LOOCV.testSetp;
//		testEnd = this.objCount * switchFlag / NoClass_LOOCV.testSetp - 1;
		for(int i = 0;i < this.objCount;i++) {
//			if(i >= testStart && i <= testEnd) 
//				continue;
			this.computeRow(this.context[i]);
		}
		long endTic = (new Date()).getTime();
		System.out.println("time for GIM:" + (endTic - startTic));
		//����class lab��˳��
//		int classCount = this.classValue.length - 1;
//		for(int i = 0;i < this.objCount / 10;i++) {
//			int tempValue = this.classValue[classCount - i];
//			this.classValue[classCount - i] = this.classValue[testEnd - i];
//			this.classValue[testEnd - i] = tempValue;
//		}
//		int count = 0;
//		for (int j = 0; j <= this.attrCount; j++) {
//			List<Concept> list = this.concepts.get(j);
//			if(list != null)
//				for(int n = 0;n < list.size();n++) {
//					System.out.println(list.size());
//					count++;
//				}
//		}
//		System.out.println(count);
	}
	
	/*
	 *ÿ�θ������ݼ�����������Ҫ�޸ĵĵط�
	 *computeSimConImPaperObj(Concept, Map<Integer, List<Concept>>, int, int, int[], int[])
	 *computeSimConImPaper
	 *1.NoClass_LOOCV�и����ݼ���ص�static����
	 *2.startTestConImObj�в���ʱ��Ҫȥ���������ݵ����Ͳ���
	 */
	public double startTestConImObj() {
		int testStart = 0;
		int testEnd = 0;
		testStart = this.objCount * (switchFlag - 1) / NoClass_FCBC.testSetp;
		testEnd = this.objCount * switchFlag / NoClass_FCBC.testSetp - 1;
		int accuracy = 0;
		int conceptCount = 0;
		
		concepts = MapClone.clone(remainConcepts);
		this.removeObject(testStart, this.context[testStart]);
		int[] objsCount = new int[this.objCount];
		Concept constructConcept = new Concept();
//		Concept contrastConcept = new Concept();
//		for (int i = 0; i <= testCount - 1;i++) {
		for (int i = testStart; i <= testEnd; i++) {
			//ͳ�����Եĸ���ڵ����
			conceptCount = 0;
			for(int n = 0;n < this.attsCount.length;n++)
				this.attsCount[n] = 0;
			for(int n = 0;n < objsCount.length;n++)
				objsCount[n] = 0;
			Vector<Integer> constructAttr = new Vector<Integer>(this.context[i].length);
			for(int n = 0; n < this.context[i].length - NoClass_FCBC.solutionPoint; n++) {
				if(this.context[i][n] == 1)
					constructAttr.add(n);
			}
			constructConcept.setAttrSet(constructAttr);
			int maxCount = 0;
			for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter
					.hasNext();) {
				List<Concept> list = iter.next();
				if(list != null) {
					int listSize = list.size();
					for (int n = 0; n < listSize; n++) {
						Concept tempConcept = list.get(n);
						Vector<Integer> attrVector = tempConcept.getAttrSet();
						Vector<Integer> objVector = tempConcept.getObjSet();
						if(attrVector.size() == 0) {
							continue;
						}
						if(objVector.size() == 0) {
							continue;
						}
						conceptCount++;
						for(int k = 0;k < attrVector.size();k++) {
							this.attsCount[attrVector.get(k)]++;
						}
						int interCount = FCBC_GIC.intersect(attrVector, constructAttr).size();
						if(interCount > maxCount) {
							maxCount = interCount;
							constructConcept.setObjSet(tempConcept.getObjSet());
//							contrastConcept = tempConcept;
						}
						for(int k = 0;k < objVector.size();k++) {
							objsCount[objVector.get(k)]++;
						}
					}
				}
			}
			//ö�ٱ���
//			Concept maxConcept = MySimilarity.computeSimConImPaper(constructAttr, concepts, conceptCount, this.attsCount);
			Concept maxConcept = FCBC_LOOCV.computeSimConImPaperObj(constructConcept, this.concepts, conceptCount, this.attrCount, this.attsCount, objsCount);
			Vector<Integer> objOfMax = maxConcept.getObjSet();
//			Vector<Integer> attrOfMax = maxConcept.getAttrSet();
			//δɸѡ����
			/**
			 * For computation of AUC: calculate the portion of every class label as the probabilities.
			 */
			int objSize = objOfMax.size();
			Map<Integer, Double> scoreMap = new HashMap<Integer, Double>();
			for(int n = 0;n < objSize;n++) {
				int obj = this.classValue[objOfMax.get(n)];
				Double values = scoreMap.get(new Integer(obj));
				if(values == null) {
					scoreMap.put(new Integer(obj), 1.0);
				} else {
					scoreMap.put(new Integer(obj), 1.0 + values);
				}
			}
			for(Entry<Integer, Double> entry : scoreMap.entrySet()) {
				Integer key = entry.getKey();
				Double sim = entry.getValue();
				scoreMap.put(key, sim / objSize);
			}
			String scoreStr = "";
			for(int n = 1;n < NoClass_FCBC.solutionPoint;n++) {
				Double score = scoreMap.get(new Integer(n));
				if(score == null) {
					scoreStr += " 0";
				} else {
					scoreStr += " " + score.toString();
				}
			}
			scoreStr = this.classValue[i] + scoreStr + "\n";
			try {
				NoClass_FCBC.osScore.write(scoreStr.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			//未筛选最优
			int j = 0;
			for(;j < objOfMax.size();j++) {
				if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
					accuracy++;
					break;
				}
			}
			if(j >= objOfMax.size()) {
				j = 0;
			}
			this.anaMatrix[this.classValue[i] - 1][this.classValue[objOfMax.get(j)]-1]++;
			//����ɸѡģ��1
//			if(attrOfMax.size() == 0)
//				System.out.println(maxConcept);
//			if(objOfMax.size() == 1 || attrOfMax.get(attrOfMax.size() - 1) >= NoClass_LOOCV.point) {
//				for(int j = 0;j < objOfMax.size();j++) {
//					if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
//						accuracy++;
//						break;
//					}
//				}
//			}
//			else {
//				int solution = this.computeSolution(maxConcept);
//				if(solution == this.classValue[i])
//					accuracy++;
//			}
			
			//����ɸѡģ��2
		}
		return 1.0 * accuracy;
	}
	
	//for car
	public double[] testForBitSet() {
		int trainCount = this.objCount / 10;
		int testCount = this.objCount - trainCount;
		int[] objsCount = new int[this.objCount];
		int[] pointNum = new int[9];
		int result[] = new int[9];
		double[] rate = new double[9];
		int accuracy = 0;
		Concept constructConcept = new Concept();
		int conceptCount = 0;
		for(int i = 0;i < 9;i++) {
			pointNum[i] = this.objCount * (i + 1) / 10;
		}
//		for(int i = 0;i < trainCount;i++) {
//			for(int j = 0;j < this.attrCount - 1;j++) {
//				if(i == 0)
//					this.attsCount[j] = 0;
//				if(this.context[i][j] == 1)
//					this.attsCount[j]++;
//			}
//		}
		for (int i = trainCount; i < this.objCount; i++) {
			Vector<Integer> constructAttr = new Vector<Integer>(this.context[i].length - 2);
			for(int n = 0; n < this.context[i].length - NoClass_FCBC.solutionPoint; n++) {
				if(this.context[i][n] == 1)
					constructAttr.add(n);
			}
			for(int j = 1;j < 9;j++) {
				if(pointNum[j] == i)
					result[j] = accuracy;
			}
			conceptCount = 0;
			for(int n = 0;n < this.attsCount.length;n++)
				this.attsCount[n] = 0;
			for(int n = 0;n < objsCount.length;n++)
				objsCount[n] = 0;
			constructConcept.setAttrSet(constructAttr);
			int maxCount = 0;
			for (Iterator<List<Concept>> iter = concepts.values().iterator(); iter
					.hasNext();) {
				List<Concept> list = iter.next();
				if(list != null) {
					int listSize = list.size();
					for (int n = 0; n < listSize; n++) {
						Concept tempConcept = list.get(n);
						Vector<Integer> attrVector = tempConcept.getAttrSet();
						Vector<Integer> objVector = tempConcept.getObjSet();
						if(attrVector.size() == 0) {
							continue;
						}
						if(objVector.size() == 0) {
							continue;
						}
						conceptCount++;
						for(int k = 0;k < attrVector.size();k++) {
							this.attsCount[attrVector.get(k)]++;
						}
						int interCount = FCBC_GIC.intersect(attrVector, constructAttr).size();
						if(interCount > maxCount) {
							maxCount = interCount;
							constructConcept.setObjSet(tempConcept.getObjSet());
						}
						for(int k = 0;k < objVector.size();k++) {
							objsCount[objVector.get(k)]++;
						}
					}
				}
			}
			//ö�ٱ���computeSimConImPapercomputeSimilarity
//			Concept maxConcept = MySimilarity.computeSimilarity(constructAttr, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimMod(constructAttr, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimConImPaper(constructAttr, this.concepts, conceptCount, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityNew(constructAttr, context, this.concepts, i, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimConImPaperObj(constructConcept, this.concepts, conceptCount, this.attrCount, this.attsCount, objsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityModOnly(constructAttr, this.concepts, conceptCount, this.attsCount);
			Concept maxConcept = FCBC_LOOCV.computeSimilarityModOnly2(constructAttr, this.concepts, conceptCount, this.attsCount);
//			Concept maxConcept = MySimilarity.computeSimilarityModPath(constructAttr, this.concepts, conceptCount, this.attsCount);
			Vector<Integer> objOfMax = maxConcept.getObjSet();
			//δɸѡ����
			for(int j = 0;j < objOfMax.size();j++) {
				if(this.classValue[objOfMax.get(j)] == this.classValue[i]) {
					accuracy++;
					break;
				}
			}
			this.computeRow(this.context[i]);
			for(int j = 0;j < this.attrCount - 1;j++)
				if(this.context[i][j] == 1)
					this.attsCount[j]++;
		}
		System.out.println(1.0 * accuracy / testCount);
		for(int i = 1;i < 9;i++) {
			rate[i] = 1.0 * (accuracy - result[i]) / (this.objCount - pointNum[i]);
			System.out.println(rate[i]);
		}
		rate[0] = 1.0 * accuracy / testCount;
		return rate;
	}
	
	//ͨ�������������Ԫ�ص�pathȨ�أ�Ѱ�����Լ��Ͼ������Ȩ�غ͵Ķ���Ԫ��
	public int computeSolutionMod2(Concept target, int conceptCount, int[] attrsCount) {
		Vector<Integer> objTarget = target.getObjSet();
		int objsNum = objTarget.size();
		double min = 1.0 * conceptCount * (this.attrCount - 1);
		int minIndex = -1;
		for(int i = 0;i < objsNum;i++) {
			double tempMin = 0;
			for(int j = 0; j < this.attrCount - 1 - NoClass_FCBC.solutionPoint; j++) {
				int objIndex = objTarget.get(i);
				if(this.context[objIndex][j] == 0)
					break;
				tempMin += 1.0 * attrsCount[j] / conceptCount;
			}
			if(tempMin < min) {
				min = tempMin;
				minIndex = i;
			}
		}
		return this.classValue[objTarget.get(minIndex)];
	}
	
	//������Ԫ�ض�Ӧ�����Լ��ϵ�ģ�����ڵ����Լ��ϵ�ģ֮����ΪȨ�أ���ȡ��СȨ�صĶ���Ԫ��
	public int computeSolutionMod(Concept target, int[] objSup) {
		Vector<Integer> objTarget = target.getObjSet();
		int objsNum = objTarget.size();
		int attrsNum = target.getAttrSet().size();
		double[] objSupRes = new double[objsNum];
		double max = 0.0;
		int maxIndex = -1;
		for(int i = 0;i < objsNum;i++) {
			double temp = 1.0 * attrsNum / objSup[objTarget.get(i)];
			objSupRes[i] = temp;
			if(max < temp) {
				max = temp;
				maxIndex = i;
			}
			System.out.print(this.classValue[objTarget.get(i)] + " " + temp + " ");
		}
		System.out.println();
		return this.classValue[objTarget.get(maxIndex)];
	}
	
	@SuppressWarnings("unchecked")
	public void computeRow(int[] anObj) {
		row++;
		int classLab = 0;
		for(int i = NoClass_FCBC.point; i < anObj.length; i++) {
			if (anObj[i] == 1) {
				classLab = i - NoClass_FCBC.point;
				break;
			}
		}
		Vector<Integer> intent = new Vector<Integer>();
		for (int i = 0; i < anObj.length; i++) {
			if(anObj[i] == 1)
				intent.add(i);
			if(i < attrCount - NoClass_FCBC.solutionPoint - 1) {
				this.probabilityByLabel[classLab][i] += anObj[i];
			}
		}
		this.probabilityByLabel[classLab][attrCount - NoClass_FCBC.solutionPoint - 1]++;
		Concept concept = addIntent(intent, bottomConcept);
		concept.getObjSet().add(row);
		Vector<ConIndexPair> parents = concept.getParentConcept();
		while(parents.size() != 0) {
			int pSize = parents.size();
			Vector<ConIndexPair> newParents = new Vector<ConIndexPair>();
			for(int j = 0;j < pSize;j++) {
				ConIndexPair pCIP = parents.get(j);
				Concept parent = this.remainConcepts.get(pCIP.getIndexOfMap()).get(pCIP.getIndexOfBucket());
				Vector<Integer> obj = parent.getObjSet();
				if(obj.get(obj.size() - 1) < row)
					obj.add(row);
//				Vector<Integer> pObj = parent.getObjSet();
//				pObj = this.addByOrder(pObj, row);
				Vector<ConIndexPair> pa = parent.getParentConcept();
				for(int i = 0;i < pa.size();i++) {
					ConIndexPair tempCIP = pa.get(i);
					if(!newParents.contains(tempCIP)) {
						newParents.add(tempCIP);
					}
				}
			}
			parents = (Vector<ConIndexPair>) newParents.clone();
			newParents.clear();
		}
	}
	
	public Concept addIntent(Vector<Integer> intent, Concept generatorConcept) {
		generatorConcept = this.getMaximalConcept(intent, generatorConcept);
//		System.out.println(generatorConcept);
		if(generatorConcept.getAttrSet().size() == intent.size())
			return generatorConcept;
		Vector<ConIndexPair> generatorParent = generatorConcept.getParentConcept();
		Vector<Concept> newParents = new Vector<Concept>();
		boolean addParent = true;
		int gpSize = generatorParent.size();
//		System.out.println("gpsize" + gpSize);
		for(int i = 0;i < gpSize;i++) {
			ConIndexPair ciPair = generatorParent.get(i);
//			System.out.println(ciPair);
			Concept candidate = this.remainConcepts.get(ciPair.getIndexOfMap()).get(ciPair.getIndexOfBucket());
//			System.out.println(this.concepts);
			Vector<Integer> cIntent = candidate.getAttrSet();
			Vector<Integer> interset = intersect(cIntent, intent);
//			System.out.println(cIntent + " " + intent);
//			System.out.println("candidata" + candidate);
			if(interset.size() < cIntent.size()) {
				candidate = this.addIntent(interset, candidate);
			}
			addParent = true;
			for(int j = 0;j < newParents.size();) {
				Concept parent = newParents.get(j);
				Vector<Integer> left = candidate.getAttrSet();
				Vector<Integer> right = parent.getAttrSet();
				Vector<Integer> interset2 = intersect(left, right);
				if(interset2.size() == left.size()) {
					addParent = false;
					break;
				} else if(interset2.size() == right.size()) {
					newParents.remove(parent);
					continue;
				}
				j++;
			}
			if(addParent)
				newParents.add(candidate);
		}
		@SuppressWarnings("unchecked")
        Concept newConcept = new Concept(intent, (Vector<Integer>) generatorConcept.getObjSet().clone());
		List<Concept> bucket = this.remainConcepts.get(intent.size());
		if(bucket == null) {
			bucket = new ArrayList<Concept>();
			ConIndexPair cip = newConcept.getId();
			cip.setIndexOfMap(intent.size());
			cip.setIndexOfBucket(0);
			bucket.add(newConcept);
			this.remainConcepts.put(intent.size(), bucket);
		} else {
			ConIndexPair cip = newConcept.getId();
			cip.setIndexOfMap(intent.size());
			cip.setIndexOfBucket(bucket.size());
			bucket.add(newConcept);
		}
		int newSize = newParents.size();
		ConIndexPair geneCIP = generatorConcept.getId();
		ConIndexPair newCIP = newConcept.getId();
		Vector<ConIndexPair> geneParent = generatorConcept.getParentConcept();
		for(int j = 0;j < newSize;j++) {
			Concept parent = newParents.get(j);
			ConIndexPair pCIP = parent.getId();
//			System.out.println(parent);
			Vector<ConIndexPair> children = parent.getChildConcept();
			int cSize = children.size();
//			System.out.println(children + " " + geneCIP);
			for(int k = 0;k < cSize;k++) {
				if(children.get(k) == geneCIP) {
					children.remove(geneCIP);
					geneParent.remove(pCIP);
					break;
				}
			}
			newConcept.getParentConcept().add(pCIP);
			parent.getChildConcept().add(newCIP);
		}
		newConcept.getChildConcept().add(geneCIP);
		geneParent.add(newCIP);
		return newConcept;
	}
	
	@SuppressWarnings("unchecked")
	public void removeObject(Integer obj, int[] attrOfOjb)  {
		int classLab = 0;
		for(int i = NoClass_FCBC.point; i < attrOfOjb.length; i++) {
			if (attrOfOjb[i] == 1) {
				classLab = i - NoClass_FCBC.point;
				break;
			}
		}
		for (int i = 0; i < attrOfOjb.length; i++) {
			if(i < attrCount - NoClass_FCBC.solutionPoint - 1) {
				this.probabilityByLabel[classLab][i] -= attrOfOjb[i];
			}
		}
		this.probabilityByLabel[classLab][attrCount - NoClass_FCBC.solutionPoint - 1]--;
		Concept objConcept = new Concept();
		Concept bottomConcept = new Concept();
		for(int i = this.attrCount;i >=0;i--) {
			if(this.concepts.get(i) != null) {
				bottomConcept = this.concepts.get(i).get(0);
				break;
			}
		}
		Vector<ConIndexPair> parent = (Vector<ConIndexPair>) bottomConcept.getParentConcept().clone();
		Vector<ConIndexPair> newParent = null;
		Vector<Integer> intent = new Vector<Integer>();
		for(int i = 0;i < attrOfOjb.length;i++) {
			if(attrOfOjb[i] == 1) {
				intent.add(i);
			}
		}
		while(parent.size() != 0) {
			int pSize = parent.size();
			for(int i = 0;i < pSize;i++) {
				ConIndexPair CIP = parent.get(i);
				Concept concept = this.concepts.get(CIP.getIndexOfMap()).get(CIP.getIndexOfBucket());
				Vector<Integer> objVect = concept.getObjSet();
				Vector<Integer> attrVect = concept.getAttrSet();
				if(objVect.contains(obj)) {
					objConcept = concept;
					parent.clear();
					break;
				} else if(attrVect.size() > intent.size() && relationOf2Vector(intent, attrVect) == -1) {
					newParent = concept.getParentConcept();
					break;
				}
			}
			if(newParent != null) {
				parent = (Vector<ConIndexPair>) newParent.clone();
				newParent = null;
			}
		}
		Vector<ConIndexPair> objChild = objConcept.getChildConcept();
		if(objChild.size() >= 2) {
			this.deleObj(objConcept, obj);
		} else {
			this.clearConcept(objConcept, null, obj);
		}
//		int count = 0;
//		for (int j = 0; j <= this.attrCount; j++) {
//			List<Concept> tlist = this.concepts.get(j);
//			if(tlist != null)
//				for(int n = 0;n < tlist.size();n++) {
////					System.out.println(tlist.get(n));
//					count++;
//				}
//		}
	}
	
	@SuppressWarnings("unchecked")
	void clearConcept(Concept concept, ConIndexPair children, Integer obj) {
		Vector<ConIndexPair> childs = concept.getChildConcept();
		Vector<ConIndexPair> parents = concept.getParentConcept();
		childs.remove(children);
		ConIndexPair ccip = concept.getId();
		Vector<Integer> objectV = concept.getObjSet();
		objectV.remove(obj);
		if(children != null) {
			childs.remove(children);
		}
		//�ж��Ƿ�ΪgeneConcept
		boolean isGeneConcept = false;
		int childSize = childs.size();
		if(childSize >= 2)
			isGeneConcept = false;
		else {
			for(int i = 0;i < childSize;i++) {
				ConIndexPair childCIP = childs.get(i);
				Concept child = this.concepts.get(childCIP.getIndexOfMap()).get(childCIP.getIndexOfBucket());
				Vector<Integer> childObj = child.getObjSet();
				if(!childObj.contains(obj) && (childObj.size() == objectV.size()) || (childObj.contains(obj) && childObj.size() - 1 == objectV.size())) {//�����Ӧ��ɾ��
					isGeneConcept = true;
					break;
				} 
			}
		}
		if(isGeneConcept) {
			int conSize = parents.size();
			for(int i = 0;i < conSize;i++) {
				ConIndexPair cip1 = parents.get(i);
				this.clearConcept(this.concepts.get(cip1.getIndexOfMap()).get(cip1.getIndexOfBucket()), ccip, obj);
			}
			for(int i = 0;i < childSize;i++) {
				ConIndexPair childCIP = childs.get(i);
				Concept child = this.concepts.get(childCIP.getIndexOfMap()).get(childCIP.getIndexOfBucket());
				child.getParentConcept().remove(ccip);
			}
		}
		//�޸ıߣ�sup-sub relation��
		if(!isGeneConcept && children != null) {
			Concept removedChild = this.concepts.get(children.getIndexOfMap()).get(children.getIndexOfBucket());
			Vector<ConIndexPair> tempChilds = (Vector<ConIndexPair>) removedChild.getChildConcept().clone();
			int tempSize = tempChilds.size();
			for(int j = 0;j < tempSize;j++) {
				boolean isChild = true;//�Ƿ�����
				ConIndexPair right = tempChilds.get(j);
				Concept rightConcept = this.concepts.get(right.getIndexOfMap()).get(right.getIndexOfBucket());
				for(int i = 0;i < childSize;i++) {
					ConIndexPair left = childs.get(i);
					if(right.getIndexOfMap() <= left.getIndexOfMap())
						continue;
					Concept leftConcept = this.concepts.get(left.getIndexOfMap()).get(left.getIndexOfBucket());
					if(relationOf2Vector(leftConcept.getAttrSet(), rightConcept.getAttrSet()) == -1) {
						isChild = false;
						break;
					}
				}
				if(isChild) {
					childs.add(right);
					rightConcept.getParentConcept().add(ccip);
				}
			}
			this.deleObj(concept, obj);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deleObj(Concept concept, Integer obj) {
		Vector<ConIndexPair> parents = (Vector<ConIndexPair>) concept.getParentConcept();
		Vector<ConIndexPair> newParents = new Vector<ConIndexPair>();
		concept.getObjSet().remove(obj);
		while(parents.size() != 0) {
			int pSize = parents.size();
			for (int i = 0; i < pSize; i++) {
				ConIndexPair cip = parents.get(i);
				Concept parent = this.concepts.get(cip.getIndexOfMap()).get(
						cip.getIndexOfBucket());
				parent.getObjSet().remove(obj);
				Vector<ConIndexPair> pParent = parent.getParentConcept();
				for (int j = 0; j < pParent.size(); j++) {
					ConIndexPair tCip = pParent.get(j);
					if (!newParents.contains(tCip)) {
						newParents.add(tCip);
					}
				}
			}
			parents = (Vector<ConIndexPair>) newParents.clone();
			newParents.clear();
		}
	}
	
	public Concept getMaximalConcept(Vector<Integer> intent, Concept generatorConcept) {
		boolean isMaximal = true;
		while(isMaximal) {
			isMaximal = false;
			Vector<ConIndexPair> parents = generatorConcept.getParentConcept();
			for(int i = 0;i < parents.size();i++) {
				ConIndexPair ciPair = parents.get(i);
				Concept parent = this.remainConcepts.get(ciPair.getIndexOfMap()).get(ciPair.getIndexOfBucket());
				Vector<Integer> pIntent = parent.getAttrSet();
				Vector<Integer> interset = intersect(pIntent, intent);
				if(interset.size() == intent.size()) {
					generatorConcept = parent;
					isMaximal = true;
					break;
				}
			}
		}
		return generatorConcept;
	}

	public static Vector<Integer> intersect(Vector<Integer> left, Vector<Integer> right) {
		Vector<Integer> result = new Vector<Integer>();
		int i = 0;
		int j = 0;
		int lsize = left.size();
		int rsize = right.size();
		while (i < lsize && j < rsize) {
			int lvalue = left.get(i);
			int rvalue = right.get(j);
			if (lvalue < rvalue)
				i++;
			else if (lvalue > rvalue)
				j++;
			else {
				result.add(lvalue);
				i++;
				j++;
			}
		}
		return result;
	}

	/**
	public Vector<Integer> addByOrder(Vector<Integer> vector, int ele) {
		int start, end;
		start = 0;
		end = vector.size();
		if(vector.size() == 0 || vector.get(end - 1) < ele) {
			vector.add(ele);
			return vector;
		}
		while(start != end) {
			int middle = (start + end) / 2;
			if(middle >= vector.size()) {
				vector.add(ele);
				return vector;
			}
			int midValue = vector.get(middle);
			if(midValue == ele) {
				return vector;
			} else if(midValue < ele) {
				start = middle + 1;
			} else {
				end = middle;
			}
		}
		vector.add(start, ele);
		return vector;
	}
	*/
	/*
	 * 1 left contains right
	 * -1 right contains left
	 * 0 left equals right
	 * -2 independent
	 */
	public static int relationOf2Vector(Vector<Integer> left, Vector<Integer> right) {
		int result = 0;
		int i = 0, j = 0;
		int lsize = left.size();
		int rsize = right.size();
		if (lsize > rsize) {
			while (i < lsize && j < rsize) {
				int lvalue = left.get(i);
				int rvalue = right.get(j);
				if (lvalue == rvalue) {
					i++;
					j++;
					continue;
				}
				if (lvalue > rvalue)
					break;
				else {
					i++;
				}
			}
			if (j == rsize)
				result = 1;
			else
				result = -2;
		} else {
			while (i < lsize && j < rsize) {
				int lvalue = left.get(i);
				int rvalue = right.get(j);
				if (lvalue == rvalue) {
					i++;
					j++;
					continue;
				}
				if (lvalue < rvalue)
					break;
				else {
					j++;
				}
			}
			if (lsize == rsize && i == lsize)
				result = 0;
			else if (i == lsize)
				result = -1;
			else
				result = -2;
		}
		return result;
	}

	public static Vector<Integer> union(Vector<Integer> left, Vector<Integer> right) {
		Vector<Integer> result = new Vector<Integer>();
		int i = 0;
		int j = 0;
		int lsize = left.size();
		int rsize = right.size();
		while (i < lsize || j < rsize) {
			int lvalue;
			if (i < lsize)
				lvalue = left.get(i);
			else
				lvalue = MAXINT;
			int rvalue;
			if (j < rsize)
				rvalue = right.get(j);
			else
				rvalue = MAXINT;
			if (lvalue == rvalue) {
				result.add(lvalue);
				i++;
				j++;
			} else if (lvalue < rvalue) {
				result.add(lvalue);
				i++;
			} else {
				result.add(rvalue);
				j++;
			}
		}
		return result;
	}

//	public static void main(String[] args) {
//		String[] test = {"0", "1"};
//		Godin_Incremental_Board gim = new Godin_Incremental_Board(30, 100, test);
//	}

	public int[] getClassValue() {
		return classValue;
	}

	public void setClassValue(int[] classValue) {
		this.classValue = classValue;
	}

	public long getTestConsumingTime() {
		return testConsumingTime;
	}

	public void setTestConsumingTime(long testConsumingTime) {
		this.testConsumingTime = testConsumingTime;
	}

	public int[][] getAnaMatrix() {
		return anaMatrix;
	}

	public void setAnaMatrix(int[][] anaMatrix) {
		this.anaMatrix = anaMatrix;
	}

	static final String path = "./data/cbr";
	static final String suffix = ".data";
//	static final int cbr_AttrCount = 30;
	public final static int MAXINT = 1000000;
	public Map<Integer, List<Concept>> concepts;
	private Map<Integer, List<Concept>> remainConcepts;
	public int[][] anaMatrix;
	
	private int[][] probabilityByLabel;
	private long testConsumingTime;
	private int[] classValue;
	private int[] attsCount;
	private int[][] context;
	private String contextFileName;
	private int attrCount;
	private int objCount;
	private int switchFlag;
	private Concept bottomConcept = new Concept();
	int row = -1;
}
