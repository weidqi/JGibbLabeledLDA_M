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
