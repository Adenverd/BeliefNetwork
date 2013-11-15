import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.List;

public class NormalNode extends ContinuousNode{
    @Override
    /**
     * pdfParams.get(0) is mean, pdfParams.get(1) is stdDev (o^2)
     */
    public double pdf(double x, List<Node> pdfParams) {
        double mean = pdfParams.get(0).getNodeValue().getValue();
        double stdDev = pdfParams.get(1).getNodeValue().getValue();
        NormalDistribution normalDistribution = new NormalDistribution(mean, stdDev);

        return normalDistribution.density(x);
    }
}
