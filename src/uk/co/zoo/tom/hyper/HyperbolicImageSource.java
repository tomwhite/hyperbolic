package uk.co.zoo.tom.hyper;

import java.awt.image.ImageProducer;

public interface HyperbolicImageSource extends Scalable, Tileable {

	public void transform(double x, double y, double[] retcoord);
	public void itransform(double x, double y, double[] retcoord);

	public void translateSource(int x, int y);

	public ImageProducer getSource();

}