package lda.jgibblda;
public class Inferencer
{
    // Train model
    public Model trnModel;
    public Dictionary globalDict;
    private LDAOption option;

    private Model newModel;

    //-----------------------------------------------------
    // Init method
    //-----------------------------------------------------
    public Inferencer(LDAOption option) throws Exception
    {
        this.option = option;
        trnModel = new Model();
        trnModel.initInfLDAOption(option);
        trnModel.initDefaultModel();

        globalDict = trnModel.data.localDict;
    }

    //inference new model ~ getting data from a specified dataset
    public Model inference() throws Exception
    {
        newModel = new Model(option, trnModel);
        newModel.init(true);
        newModel.initInf();

        System.out.println("Sampling " + newModel.niters + " iterations for inference!");
        System.out.print("Iteration");
        for (newModel.liter = 1; newModel.liter <= newModel.niters; newModel.liter++){
            System.out.format("%6d", newModel.liter);

            // for all newz_i
            for (int m = 0; m < newModel.M; ++m){
                for (int n = 0; n < newModel.data.docs.get(m).length; n++){
                    // sample from p(z_i|z_-1,w)
                    int topic = infSampling(m, n);
                    newModel.z[m].set(n, topic);
                }
            }//end foreach new doc

            if ((newModel.liter == newModel.niters) ||
                    (newModel.liter > newModel.nburnin && newModel.liter % newModel.samplingLag == 0)) {
                newModel.updateParams(trnModel);
            }

            System.out.print("\b\b\b\b\b\b");
        }// end iterations
        newModel.liter--;

        System.out.println("\nSaving the inference outputs!");
        String outputPrefix = newModel.dfile;
        if (outputPrefix.endsWith(".gz")) {
            outputPrefix = outputPrefix.substring(0, outputPrefix.length() - 3);
        }
        newModel.saveModel(outputPrefix + ".");

        return newModel;
    }
    //inference new model  getting data from a specified dataset
    public Model inference(String[] docs) throws Exception{
        newModel = new Model(option, docs, trnModel);
        newModel.init(true);
        newModel.initInf();
        System.out.println("Sampling " + newModel.niters + " iterations for inference!");
        System.out.print("Iteration");
        for (newModel.liter = 1; newModel.liter <= newModel.niters; newModel.liter++){
            System.out.format("%6d", newModel.liter);

            // for all newz_i
            for (int m = 0; m < newModel.M; ++m){
                for (int n = 0; n < newModel.data.docs.get(m).length; n++){
                    // sample from p(z_i|z_-1,w)
                    int topic = infSampling(m, n);
                    newModel.z[m].set(n, topic);
                }
            }//end foreach new doc

            if ((newModel.liter == newModel.niters) ||
                    (newModel.liter > newModel.nburnin && newModel.liter % newModel.samplingLag == 0)) {
                newModel.updateParams(trnModel);
            }

            System.out.print("\b\b\b\b\b\b");
        }// end iterations
        newModel.liter--;
        return newModel;
    }
    /**
     * do sampling for inference
     * m: document number
     * n: word number?
     */
    protected int infSampling(int m, int n)
    {
        // remove z_i from the count variables
        int topic = newModel.z[m].get(n);
        int _w = newModel.data.docs.get(m).words[n];
        int w = newModel.data.lid2gid.get(_w);

        newModel.nw[_w][topic] -= 1;
        newModel.nd[m][topic] -= 1;
        newModel.nwsum[topic] -= 1;
        newModel.ndsum[m] -= 1;

        int[] nw_inf_m__w = null;
        if (option.infSeparately) {
            nw_inf_m__w = newModel.nw_inf.get(m).get(_w);
            nw_inf_m__w[topic] -= 1;
            newModel.nwsum_inf[m][topic] -= 1;
        }

        double Vbeta = trnModel.V * newModel.beta;

        // get labels for this document
        int[] labels = newModel.data.docs.get(m).labels;

        // determine number of possible topics for this document
        int K_m = (labels == null) ? newModel.K : labels.length;

        // do multinomial sampling via cumulative method		
        double[] p = newModel.p;
        for (int k = 0; k < K_m; k++) {
            topic = labels == null ? k : labels[k];

            int nw_k, nwsum_k;
            if (option.infSeparately) {
                nw_k = nw_inf_m__w[topic];
                nwsum_k = newModel.nwsum_inf[m][topic];
            } else {
                nw_k = newModel.nw[_w][topic];
                nwsum_k = newModel.nwsum[topic];
            }

            p[k] = (newModel.nd[m][topic] + newModel.alpha) *
                (trnModel.nw[w][topic] + nw_k + newModel.beta) /
                (trnModel.nwsum[topic] + nwsum_k + Vbeta);
        }

        // cumulate multinomial parameters
        for (int k = 1; k < K_m; k++){
            p[k] += p[k - 1];
        }

        // scaled sample because of unnormalized p[]
        double u = Math.random() * p[K_m - 1];

        for (topic = 0; topic < K_m; topic++){
            if (p[topic] > u)
                break;
        }

        // map [0, K_m - 1] topic to [0, K - 1] topic according to labels
        if (labels != null) {
            topic = labels[topic];
        }

        // add newly estimated z_i to count variables
        newModel.nw[_w][topic] += 1;
        newModel.nd[m][topic] += 1;
        newModel.nwsum[topic] += 1;
        newModel.ndsum[m] += 1;

        if (option.infSeparately) {
            nw_inf_m__w[topic] += 1;
            newModel.nwsum_inf[m][topic] += 1;
        }

        return topic;
    }
}
