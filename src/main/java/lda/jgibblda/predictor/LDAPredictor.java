package lda.jgibblda.predictor;

import lda.jgibblda.Inferencer;
import lda.jgibblda.LDAOption;
import lda.jgibblda.Model;

/**
 * LDAEstimator Evaluator
 * 1. Input Data Format
 * <p>
 * 2. Output Result: topic
 *
 * @Author fuli.shen at 2017/10/10
 */
public class LDAPredictor {

    private Inferencer inferencer;

    public LDAPredictor(LDAOption option) throws Exception {
        //-inf -alpha 0.5 -beta 0.1  -ntopics 37 -niters 100 -model model -twords 30 -dir  data -dfile "sample.gz"
        this.inferencer = new Inferencer(option);
    }

    public int predict(String content) {
        int result;
        String[] docs = {content};
        try {
            Model newModel = this.inferencer.inference(docs);
            int[] topic = newModel.predictTopic();
            return topic[0];
        } catch (Exception e) {
            result = -1;
        }
        return result;
    }
}
