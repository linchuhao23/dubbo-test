package com.lin;

import org.thunlp.text.classifiers.BasicTextClassifier;

public class THUCTCTest {
	
	public static void main(String[] args) {
		
	}
	
	public static void runTrainAndTest() {
	    // 新建分类器对象
	    BasicTextClassifier classifier = new BasicTextClassifier();

	    // 设置参数
	    String defaultArguments = ""
	        + "-train F:\\multiangle\\Coding!\\NLP相关资料\\THUCNews\\THUCNewsPart "  // 训练语料的路径
	        + "-test F:\\multiangle\\Coding!\\NLP相关资料\\THUCNews\\THUCNewsPart "
	        //  + "-l C:\\Users\\do\\workspace\\TestJar\\my_novel_model "
	        //  + "-cdir E:\\Corpus\\书库_cleared "
	        //  + "-n 1 "
	        // + "-classify E:\\Corpus\\书库_cleared\\言情小说 "  // 设置您的测试路径。一般可以设置为与训练路径相同，即把所有文档放在一起。
	        + "-d1 0.7 "  // 前70%用于训练
	        + "-d2 0.3 "  // 后30%用于测试
	        + "-f 35000 " // 设置保留特征数，可以自行调节以优化性能
	        +  "-s .\\my_novel_model"  // 将训练好的模型保存在硬盘上，便于以后测试或部署时直接读取模型，无需训练
	        ;

	    // 初始化
	    classifier.Init(defaultArguments.split(" "));

	    // 运行
	    classifier.runAsBigramChineseTextClassifier();
	    
	    classifier.classifyText("", 10);
	    
	}

}
