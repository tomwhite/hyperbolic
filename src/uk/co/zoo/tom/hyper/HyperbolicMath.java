package uk.co.zoo.tom.hyper;

/**
 * Class for calculating hyperbolic tangents, and converting between polar and
 * cartesian coordinates.
 */
public final class HyperbolicMath {

	private HyperbolicMath() {
	}

	public static double tanh(final double x) {
		double exp = Math.exp(2 * x);
		return (exp - 1) / (exp + 1);
	}

	public static double atanh(final double x) {
		return 0.5 * Math.log((1 + x)/(1 - x));
	}

	public static void polarToCartesian(final double[] polar, double[] cartesian) {
		cartesian[0] = polar[0] * Math.cos(polar[1]);
		cartesian[1] = polar[0] * Math.sin(polar[1]);
	}

	public static void cartesianToPolar(final double[] cartesian, double[] polar) {
		polar[0] = Math.sqrt(cartesian[0] * cartesian[0] + cartesian[1] * cartesian[1]);
		polar[1] = Math.atan2(cartesian[1], cartesian[0]);
	}



}
