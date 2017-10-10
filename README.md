Labeled LDA in Java (based on JGibbLDA)
=======================================

This is a Java implementation of Labeled LDA based on the popular
[JGibbLDA](http://jgibblda.sourceforge.net/) package. The code has been heavily
refactored and a few additional options have been added. See sections below for
more details.

Data Format
-----------

The input data format is similar to the [JGibbLDA input data
format](http://jgibblda.sourceforge.net/#_2.3._Input_Data_Format), with some
minor cosmetic changes and additional support for document labels necessary for
Labeled LDA. We first describe the (modified) input format for unlabeled
documents, followed by the (new) input format for labeled documents.

**Changed from JGibbLDA**: All input/output files must be Gzipped.

### Unlabeled Documents

Unlabeled documents have the following format:

    document_1
    document_2
    ...
    document_m

where each document is a space-separated list of terms, i.e.,:

    document_i = term_1 term_2 ... term_n

**Changed from JGibbLDA**: The first line *should not* be an integer indicating
the number of documents in the file. The original JGibbLDA code has been
modified to identify the number of documents automatically.

**Note**: Labeled and unlabeled documents may be mixed in the input file, thus
you must ensure that unlabeled documents do not begin with a left square bracket
(see Labeled Document input format below). One easy fix is to prepend a space
character (' ') to each unlabeled document line.

### Labeled Documents

Labeled documents follow a format similar to unlabeled documents, but the with
labels given at the beginning of each line and surrounded by square brackets,
e.g.:

    [label_1,1 label_1,2 ... label_1,l_1] document_1
    [label_2,1 label_2,2 ... label_2,l_2] document_2
    ...
    [label_m,1 label_m,2 ... label_m,l_m] document_m

where each label is an integer in the range [0, K-1], for K equal to the number
of topics (-ntopics).

**Note**: Labeled and unlabeled documents may be mixed in the input file. An
unlabeled document is equivalent to labeling a document with every label in the
range [0, K-1].

Usage
-----

Please see the [JGibbLDA usage](http://jgibblda.sourceforge.net/#_2.2._Command_Line_&_Input_Parameter), noting the following changes:

*   All input files must be Gzipped. All output files are also Gzipped.

*   New options have been added:

    **-nburnin <int>**: Discard this many initial iterations when taking samples.

    **-samplinglag <int>**: The number of iterations between samples.

    **-infseparately**: Inference is done separately for each document, as if
    inference for each document was performed in isolation.

    **-unlabeled**: Ignore document labels, i.e., treat every document as
    unlabeled.

*   Some options have been deleted:

    **-wordmap**: Filename is automatically built based on model path.

JGibbLabeledLDA DATA 
-----
*  Training Simpling Data Format
```aidl
[17] 动态 虚胖 教程 动作 燃烧 脂肪 练出 腹肌 练出 人鱼 花钱 紧身 季节
[1] 住房 公积金 调查 专家 调查 房价 城市 地区 城市 县城 享受 人群 缴存 人群 职工 作用

more data see:  http://pan.baidu.com/s/1o7S52Ds
``` 
*  label_Category relationship
```
please check label_MainCategory.csv file
```

JGibbLabeledLDA Estimator Examle 
-----
```
package lda.jgibblda.estimator;

import lda.jgibblda.Estimator;
import lda.jgibblda.LDAOption;

public class LDAEstimator {
    public static void main(String args[]) {
        LDAOption option = new LDAOption();
        //-Xmx4096m -Xms4096m
        //-est -alpha 0.5 -beta 0.1  -ntopics 37 -niters 500 -model model -twords 30 -dir  data -dfile "category.gz"
        option.est = true;
        option.alpha = 0.5;
        option.beta = 0.1;
        option.K = 37;
        option.niters = 500;
        option.modelName = "JGibbLabeledLDA_Model";
        option.twords = 30;
        option.dir = "data";
        option.dfile = "category.gz";
        try {
            Estimator estimator = new Estimator(option);
            estimator.estimate();
        } catch (Exception e) {
            System.out.println("Error in main: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}


//  The Training Processing Log Info :  
lda.jgibblda.estimator.LDAEstimator -est -alpha 0.5 -beta 0.1 -ntopics 37 -niters 20 -model model -twords 30 -dir data -dfile category.gz
Dataset loaded:
	M:555197
	V:97409
Sampling 500 iterations!
Iteration      1 estimator consume time is 5s
Iteration      2 estimator consume time is 10s
Iteration      3 estimator consume time is 15s
...........
Iteration    492 estimator consume time is 2586s
Iteration    493 estimator consume time is 2591s
Iteration    494 estimator consume time is 2597s
Iteration    495 estimator consume time is 2602s
Iteration    496 estimator consume time is 2607s
Iteration    497 estimator consume time is 2612s
Iteration    498 estimator consume time is 2618s
Iteration    499 estimator consume time is 2623s
Iteration    500 estimator consume time is 2629s

Saving the final model!


//  Train Model Finished Make file List :
JGibbLabeledLDA_Model.others.gz
JGibbLabeledLDA_Model.phi.gz
JGibbLabeledLDA_Model.tassign.gz
JGibbLabeledLDA_Model.theta.gz
JGibbLabeledLDA_Model.twords.gz
JGibbLabeledLDA_Model.wordmap.gz
```
JGibbLabeledLDA Predictor  Examle
-----
```
package lda.jgibblda.predictor;

import lda.jgibblda.LDAOption;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by fuli.shen on 2017/10/10.
 */
public class LDAPredictorTest {
    LDAPredictor ldaEvaluator;

    @Before
    public void init() throws Exception {
        long s1 = System.currentTimeMillis();
        String modelPath = "E:\\work_document\\github\\JGibbLabeledLDA_M\\data";
        String modelName = "JGibbLabeledLDA_Model";
        //-inf -alpha 0.5 -beta 0.1  -ntopics 37 -niters 100 -model model -twords 30 -dir  data -dfile "sample.gz"
        LDAOption option = new LDAOption();
        option.alpha = 0.5;
        option.beta = 0.1;
        option.K = 37;
        option.niters = 100;
        option.modelName = modelName;// "model";
        option.twords = 30;
        option.dir = modelPath;//"data";
        option.inf = true;
        ldaEvaluator = new LDAPredictor(option);
        System.out.println("Loading JGibbLabeledLDA_Model consume time is  " + (System.currentTimeMillis() - s1) + " ms ");
    }

    @Test
    public void testLDAEvaluator() throws Exception {
        long s1 = System.currentTimeMillis();
        String content = "稻谷 价格 粮源 市场 宠儿 市场 稻米 价格 稻米 稻米 市场 支撑 高位 价格 上调 上调 空间 天气 粳稻 上市 进度 加快 东北 稻米 价格 业内人士 全国 稻米 市场 购销 提升 价格 籼稻 粳稻 市场 行情 籼稻 收购 粳稻 上市 收购 国家 粮食 拍卖 底价 高于 拍卖 底价 市场 价格 晚稻 收购 政策性 收购 晚稻 收购 籼稻 市场 价格 支撑 储备 需求 籼稻 价格 态势 市价 靠拢 籼稻 市场 分化 行情 旺季 市场 低价 竞争 格局 青睐 粮源 价格 粳稻 籼稻 行情 稻谷 价格 大米 粮仓 农业";
        int topic = ldaEvaluator.predict(content);
        System.out.println(topic);
        System.out.println("LDAPredict consume time is  " + (System.currentTimeMillis() - s1) + " ms ");
    }
}


// Predict Log Info :

Model loaded:
	alpha:0.5
	beta:0.1
	K:37
	M:555197
	V:97409
Loading JGibbLabeledLDA_Model consume time is  33983 ms 
Dataset loaded:
	M:1
	V:44
Sampling 100 iterations for inference!
Iteration    12
    31
36
LDAPredict consume time is  157 ms 

```
Contact
-------

Please direct questions to [fuli.shen](1121025745@qq.com).
