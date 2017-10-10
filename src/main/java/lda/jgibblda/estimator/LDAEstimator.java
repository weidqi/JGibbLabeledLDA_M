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
