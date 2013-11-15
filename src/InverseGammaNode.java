import org.apache.commons.math3.special.Gamma;

import java.util.List;

public class InverseGammaNode extends ContinuousNode{

    /**
     * pdfParams.get(0) is alpha, pdfParams.get(1) is beta
     * @param pdfParams
     * @return
     */
    @Override
    public double pdf(double x, List<Node> pdfParams) {
        double alpha = pdfParams.get(0).getNodeValue().getValue();
        double beta = pdfParams.get(1).getNodeValue().getValue();
        double gamma = Gamma.gamma(x);

        return (Math.pow(beta, alpha)/gamma)*(Math.pow(x, (-1 * alpha)-1))*(Math.exp(-1*beta/x));
    }
}
