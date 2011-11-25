// immutable
public final class Complex {

	public static final Complex I = new Complex(0.0, 1.0);

	public final double re, im;

	public Complex() {
		re = 0.0;
		im = 0.0;
	}

	public Complex(final double x, final double y) {
		re = x;
		im = y;
	}

	public Complex(final Complex z) {
		re = z.re;
		im = z.im;
	}

	public Complex add(final Complex z) {
		return new Complex(re + z.re, im + z.im);
	}

	public Complex minus(final Complex z) {
		return new Complex(re - z.re, im - z.im);
	}

	public Complex negate() {
		return new Complex(-re, -im);
	}

	public Complex conjugate() {
		return new Complex(re, -im);
	}

	public Complex multiply(final Complex z) {
		return new Complex(re * z.re - im * z.im, re * z.im + im * z.re);
	}

	public Complex multiply(final double x) {
		return new Complex(re * x, im * x);
	}

	public Complex divide(final Complex z) {
		double d = z.re * z.re + z.im * z.im;
		return new Complex((re * z.re + im * z.im) / d, (im * z.re - re * z.im) / d);
	}

	public double abs2() {
		return re * re + im * im;
	}

	public double abs() {
		return Math.sqrt(abs2());
	}

	public double arg() {
		return Math.atan2(im,re);
	}

	public Complex normalize(){
		double r = abs();
		return new Complex(re / r, im / r);
	}

	public String toString(){
		return String.valueOf(re) + " + " + String.valueOf(im) + "i";
	}

}
