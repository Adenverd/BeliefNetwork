package helpers;

import java.util.*;

import static java.lang.Math.*;

/**
 * Credits:
 * Normal (Gaussian) distribution: http://introcs.cs.princeton.edu/java/22library/Gaussian.java.html
 * Gamma function: http://introcs.cs.princeton.edu/java/91float/Gamma.java.html
 */
public class MathUtility {

    private static final double EPSILON = 0.001;
    private static final double sqrt2Pi = sqrt(2 * PI);

    public static boolean isEquals(double a, double b) {
        return isEquals(a, b, EPSILON);
    }

    public static boolean isEquals(double a, double b, double epsilon) {
        return abs(a - b) < epsilon;
    }

    public static boolean isGreaterThan(double a, double b) {
        return isGreaterThan(a, b, EPSILON);
    }

    public static boolean isGreaterThan(double a, double b, double epsilon) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static double sum(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        return sum;
    }

    /**
     * normal-pdf(x) with mu = 0, stddev = 1
     */
    public static double normalPdf(double x) {
        return exp(-x * x / 2) / sqrt2Pi;
    }

    /**
     * normal-pdf(x, mu, stddev) with given mu and stddev
     */
    public static double normalPdf(double x, double mu, double sigma) {
        return normalPdf((x - mu) / sigma) / sigma;
    }

    /**
     * normal-cdf(z) with mu = 0, stddev = 1
     */
    public static double normalCdf(double z) {
        if (z < -8.0) {
            return 0.0;
        }
        if (z > 8.0) {
            return 1.0;
        }
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * normalCdf(z);
    }

    /**
     * normal-cdf(z, mu, stddev) with given mu and stddev
     */
    public static double normalCdf(double z, double mu, double sigma) {
        return normalCdf((z - mu) / sigma);
    }

    /**
     * normal-inv(z)
     * @return x such that normal-cdf(z) = x
     */
    public static double normalInv(double y) {
        return normalInv(y, .00000001, -8, 8);
    }

    private static double normalInv(double y, double delta, double lo, double hi) {
        double mid = lo + (hi - lo) / 2;
        if (hi - lo < delta) {
            return mid;
        }
        return normalCdf(mid) > y
                ? normalInv(y, delta, lo, mid)
                : normalInv(y, delta, mid, hi);
    }

    /**
     * inverse-gamma-distribution-pdf(x) with given shape and scale
     * @url https://en.wikipedia.org/wiki/Inverse_gamma_distribution
     */
    public static double inverseGammaPdf(double x, double alpha, double beta) {
        double firstPart = pow(beta, alpha);
        double secondPart = pow(x, -alpha - 1);
        double thirdPart = exp(-beta / x);
        return firstPart * secondPart * thirdPart / gamma(alpha);
    }

    /**
     * gamma-function(x) using Lanczos approximation formula
     * @url http://en.wikipedia.org/wiki/Gamma_function
     */
    public static double gamma(double x) {
        return exp(logGamma(x));
    }

    private static double logGamma(double x) {
        double tmp = (x - 0.5) * log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
                + 24.01409822 / (x + 2) - 1.231739516 / (x + 3)
                + 0.00120858003 / (x + 4) - 0.00000536382 / (x + 5);
        return tmp + log(ser * sqrt2Pi);
    }

}
