package lda.jgibblda;

/**
 * 非命令行模式参数模型
 */
public class LDAOption {

    //Specify whether we want to estimate model from scratch
    public boolean est = false;

    //Specify whether we want to continue the last estimation
    public boolean estc = false;

    //Specify whether we want to do inference
    public boolean inf = true;

    //Do inference for each document separately
    public boolean infSeparately = false;

    //Ignore document labels
    public boolean unlabeled = false;

    //Specify directory
    public String dir = "";

    //Specify data file (*.gz)
    public String dfile = "";

    //Specify the model name
    public String modelName = "";

    //Specify alpha
    public double alpha = -1;

    //Specify beta
    public double beta = -1;

    //Specify the number of topics
    public int K = 37;

    //Specify the number of iterations
    public int niters = 100;

    //Specify the number of burn-in iterations
    public int nburnin = 500;

    //Specify the sampling lag"
    public int samplingLag = 5;

    //Specify the number of most likely words to be printed for each topic"
    public int twords = 30;
}
