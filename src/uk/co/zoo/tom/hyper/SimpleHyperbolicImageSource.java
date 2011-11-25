package uk.co.zoo.tom.hyper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

public class SimpleHyperbolicImageSource implements HyperbolicImageSource {


	private ColorModel colorModel = ColorModel.getRGBdefault();

	private double coord[] = new double[2];

	private int srcPixels[];
	private int srcW, srcH;
	private int sxoffset, syoffset;

	private int dstPixels[];
	private int dstW, dstH;
	private int dxoffset, dyoffset;
	private int halfdstW, halfdstH;
	private int scaling;
	private boolean tiled;

	private MemoryImageSource memoryImageSource;

	// diameter is size of (square) destination image
	public SimpleHyperbolicImageSource(Image img, int x, int y, int w, int h, int diameter, int scaling, boolean tiled) {

		srcW = w;
		srcH = h;
		sxoffset = srcW / 2;
		syoffset = srcH / 2;

		// Get source image pixels

        srcPixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(img, x, y, w, h, srcPixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            return;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");
            return;
        }

		colorModel = pg.getColorModel();

		// Initialise destination dimension information

		dstW = diameter;
		dstH = diameter;
		halfdstW = dstW / 2;
		halfdstH = dstH / 2;
		dxoffset = dstW / 2;
		dyoffset = dstH / 2;
		this.scaling = scaling;
		this.tiled = tiled;

		dstPixels = new int[dstW * dstH];

		computeTransformedPoints();

		computePixels();

		memoryImageSource = new MemoryImageSource(dstW, dstH, dstPixels, 0, dstW);
		memoryImageSource.setAnimated(true);
		memoryImageSource.setFullBufferUpdates(true);

	}

    public void transform(double x, double y, double[] retcoord) {

		// (r, theta) -> (tanh(r/2), theta)

		double[] cartesian = {x, y};
		double[] polar = {0, 0};

		HyperbolicMath.cartesianToPolar(cartesian, polar);
		polar[0] = halfdstW * HyperbolicMath.tanh(polar[0] / scaling);
		HyperbolicMath.polarToCartesian(polar, retcoord);

    }

    public void itransform(double x, double y, double[] retcoord) {

		// (r, theta) -> (2atanh(r), theta)
		double[] cartesian = {x, y};
		double[] polar = {0, 0};

		HyperbolicMath.cartesianToPolar(cartesian, polar);
		polar[0] = scaling * HyperbolicMath.atanh(polar[0] / halfdstW);
		HyperbolicMath.polarToCartesian(polar, retcoord);

    }

	public void translateSource(int x, int y) {
		sxoffset += x;
		syoffset += y;

		recomputePixels();
	}

	public ImageProducer getSource() {
		return (ImageProducer) memoryImageSource;
	}

	public void setScaling(int scaling) {
		if (this.scaling != scaling) {
			this.scaling = scaling;

			// re-scaling messes up the transformed points in coords
			// so recompute!
			computeTransformedPoints();

			recomputePixels();

		}
	}

	public int getScaling() {
		return scaling;
	}

	public void setTiled(boolean tiled) {
		this.tiled = tiled;
	}

	public boolean isTiled() {
		return tiled;
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	private double[][][] coords;

	private void computeTransformedPoints() {

		// An array of hyperbolically transformed points...
		// Fill only lower half since by sym coords[x][y] == coords[y][x]
		//
		// 0?????????
		// 00????????
		// 000???????
		// 0000??????
		// 00000?????
		// 000000????
		// 0000000???
		// 00000000??
		// 000000000?
		// 0000000000
		//
		// 0 = filled, ? = irrelevant

		coords = new double[halfdstW][halfdstH][2];

		for (int y = 0; y < halfdstH; y++) {
			for (int x = 0; x <= y; x++) {
				itransform(x, y, coords[x][y]);
			}
		}

	}

	private void computePixels() {
		// Use 8-fold symmetry for efficiency
		int cx, cy;
		for (int dy = 0; dy < halfdstH; dy++) {
			for (int dx = 0; dx <= dy; dx++) {
				coord = coords[dx][dy];
				if (Double.isNaN(coord[0]) || Double.isNaN(coord[1])) {
					continue;
				}
				cx = (int) Math.round(coord[0]);
				cy = (int) Math.round(coord[1]);
				dstPixels[(halfdstH + dy) * dstW + (halfdstW + dx)] = setPixels( cx,  cy);
				dstPixels[(halfdstH + dy) * dstW + (halfdstW - dx)] = setPixels(-cx,  cy);
				dstPixels[(halfdstH - dy) * dstW + (halfdstW + dx)] = setPixels( cx, -cy);
				dstPixels[(halfdstH - dy) * dstW + (halfdstW - dx)] = setPixels(-cx, -cy);
				dstPixels[(halfdstW + dx) * dstW + (halfdstH + dy)] = setPixels( cy,  cx);
				dstPixels[(halfdstW + dx) * dstW + (halfdstH - dy)] = setPixels(-cy,  cx);
				dstPixels[(halfdstW - dx) * dstW + (halfdstH + dy)] = setPixels( cy, -cx);
				dstPixels[(halfdstW - dx) * dstW + (halfdstH - dy)] = setPixels(-cy, -cx);
			}
		}
	}

	private void recomputePixels() {
		computePixels();
		memoryImageSource.newPixels();
	}

	private final int setPixels(final int cx, final int cy) {
		int sx, sy;
		// tiled image
		if (isTiled()) {
			sx = (sxoffset + cx) % srcW;
			sy = (syoffset + cy) % srcH;
			if (sx < 0) {
				sx += srcW;
			}
			if (sy < 0) {
				sy += srcH;
			}
			return srcPixels[sy * srcW + sx];
		}
		// standard image
		sx = sxoffset + cx;
		sy = syoffset + cy;
		if (sx < 0 || sy < 0 || sx >= srcW || sy >= srcH) {
			return Color.white.getRGB();
		} else {
			return srcPixels[sy * srcW + sx];
		}
	}

}