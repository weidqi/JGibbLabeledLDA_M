package lda.jgibblda;

public class Estimator {
    // output model
    protected Model trnModel;
    LDAOption option;

    public Estimator(LDAOption option) throws Exception {
        this.option = option;

        trnModel = new Model(option);

        if (option.est) {
            trnModel.init(true);
        } else if (option.estc) {
            trnModel.init(false);
        }
    }

    public void estimate() {
        long s1 = System.currentTimeMillis();
        System.out.println("Sampling " + trnModel.niters + " iterations!");
        for (int startIter = ++trnModel.liter; trnModel.liter <= startIter - 1 + trnModel.niters; trnModel.liter++) {
            System.out.format("Iteration %6d ", trnModel.liter);

            // for all z_i
            for (int m = 0; m < trnModel.M; m++) {
                for (int n = 0; n < trnModel.data.docs.get(m).length; n++) {
                    // z_i = z[m][n]
                    // sample from p(z_i|z_-i, w)
                    int topic = sampling(m, n);
                    trnModel.z[m].set(n, topic);
                }// end for each word
            }// end for each document

            if ((trnModel.liter == startIter - 1 + trnModel.niters) ||
                    (trnModel.liter > trnModel.nburnin && trnModel.liter % trnModel.samplingLag == 0)) {
                trnModel.updateParams();
            }
            System.out.print("estimator consume time is "+ (System.currentTimeMillis() - s1) / 1000 + "s\n");
            //System.out.print("\b\b\b\b\b\b");
        }// end iterations
        trnModel.liter--;
        System.out.println("\nSaving the final model!");
        trnModel.saveModel();
    }

    /**
     * Do sampling
     *
     * @param m document number
     * @param n word number
     * @return topic id
     */
    public int sampling(int m, int n) {
        // remove z_i from the count variable
        int topic = trnModel.z[m].get(n);
        int w = trnModel.data.docs.get(m).words[n];

        trnModel.nw[w][topic] -= 1;
        trnModel.nd[m][topic] -= 1;
        trnModel.nwsum[topic] -= 1;
        trnModel.ndsum[m] -= 1;

        double Vbeta = trnModel.V * trnModel.beta;

        // get labels for this document
        int[] labels = trnModel.data.docs.get(m).labels;

        // determine number of possible topics for this document
        int K_m = (labels == null) ? trnModel.K : labels.length;

        // do multinominal sampling via cumulative method
        double[] p = trnModel.p;
        for (int k = 0; k < K_m; k++) {
            topic = labels == null ? k : labels[k];

            p[k] = (trnModel.nd[m][topic] + trnModel.alpha) *
                    (trnModel.nw[w][topic] + trnModel.beta) /
                    (trnModel.nwsum[topic] + Vbeta);
        }

        // cumulate multinomial parameters
        for (int k = 1; k < K_m; k++) {
            p[k] += p[k - 1];
        }

        // scaled sample because of unnormalized p[]
        double u = Math.random() * p[K_m - 1];

        for (topic = 0; topic < K_m; topic++) {
            if (p[topic] > u) //sample topic w.r.t distribution p
                break;
        }

        // map [0, K_m - 1] topic to [0, K - 1] topic according to labels
        if (labels != null) {
            topic = labels[topic];
        }

        // add newly estimated z_i to count variables
        trnModel.nw[w][topic] += 1;
        trnModel.nd[m][topic] += 1;
        trnModel.nwsum[topic] += 1;
        trnModel.ndsum[m] += 1;

        return topic;
    }
}
