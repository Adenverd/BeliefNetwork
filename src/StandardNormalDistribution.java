import org.apache.commons.math3.distribution.NormalDistribution;

public class StandardNormalDistribution implements ContinuousDistribution{

    @Override
    public double sample() {
        NormalDistribution normalDistribution = new NormalDistribution(0.0, 1.0);
        return normalDistribution.sample();
    }
}
