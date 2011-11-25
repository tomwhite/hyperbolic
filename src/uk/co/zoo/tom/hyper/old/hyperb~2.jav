import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ImageFilter;

public class HyperbolicFilter extends ImageFilter {

    private static ColorModel defaultRGB = ColorModel.getRGBdefault();
    private double coord[] = new double[2];

    private int raster[];
    private int srcW, srcH;
    private int sxoffset, syoffset;

    private int dstW, dstH;
    private int dxoffset, dyoffset;
	private int halfdstW, halfdstH;
	private int scaling;

    public HyperbolicFilter() {
		this(new Dimension(200, 200), 100);
    }

	public HyperbolicFilter(Dimension size, int scaling) {
// NB must be square!! (and even?)
		dstW = size.width;
		dstH = size.height;
		halfdstW = dstW / 2;
		halfdstH = dstH / 2;
		dxoffset = dstW / 2;
		dyoffset = dstH / 2;
		this.scaling = scaling;

		precompute();

// bad!
sxoffset = 495;
syoffset = 312;

	}

	double[][][] coords;

	private void precompute() {

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

	public void setDimensions(int width, int height) {
		srcW = width;
		srcH = height;

//		sxoffset = srcW / 2;
//		syoffset = srcH / 2;

		raster = new int[srcW * srcH];
		consumer.setDimensions(dstW, dstH);
	}

	public void translateSource(int x, int y) {
System.out.println("translate by " + x + " " + y);
		sxoffset += x;
		syoffset += y;
	}

    public void setColorModel(ColorModel model) {
        consumer.setColorModel(defaultRGB);
    }

    public void setHints(int hintflags) {
        consumer.setHints(TOPDOWNLEFTRIGHT
                          | COMPLETESCANLINES
                          | SINGLEPASS
                          | (hintflags & SINGLEFRAME));
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model,
                          byte pixels[], int off, int scansize) {
        int srcoff = off;
        int dstoff = y * srcW + x;
        for (int yc = 0; yc < h; yc++) {
            for (int xc = 0; xc < w; xc++) {
                raster[dstoff++] = model.getRGB(pixels[srcoff++] & 0xff);
            }
            srcoff += (scansize - w);
            dstoff += (srcW - w);
        }
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model,
                          int pixels[], int off, int scansize) {
        int srcoff = off;
        int dstoff = y * srcW + x;
        if (model == defaultRGB) {
            for (int yc = 0; yc < h; yc++) {
                System.arraycopy(pixels, srcoff, raster, dstoff, w);
                srcoff += scansize;
                dstoff += srcW;
            }
        } else {
            for (int yc = 0; yc < h; yc++) {
                for (int xc = 0; xc < w; xc++) {
                    raster[dstoff++] = model.getRGB(pixels[srcoff++]);
                }
                srcoff += (scansize - w);
                dstoff += (srcW - w);
            }
        }
    }

	public void imageComplete(int status) {
System.out.println("offset " + sxoffset + " " + syoffset);
//long timer = System.currentTimeMillis();
        if (status == IMAGEERROR || status == IMAGEABORTED) {
            consumer.imageComplete(status);
            return;
        }
		// Use 8-fold symmetry for efficiency
		int pixels[] = new int[dstW * dstH];
		int cx, cy;
		for (int dy = 0; dy < halfdstH; dy++) {
			for (int dx = 0; dx <= dy; dx++) {
				coord = coords[dx][dy];
				if (Double.isNaN(coord[0]) || Double.isNaN(coord[1])) {
					// NB if pixels is an instance var we can omit the following 8 assignments!
					pixels[(halfdstH + dy) * dstW + (halfdstW + dx)] = 0;
					pixels[(halfdstH + dy) * dstW + (halfdstW - dx)] = 0;
					pixels[(halfdstH - dy) * dstW + (halfdstW + dx)] = 0;
					pixels[(halfdstH - dy) * dstW + (halfdstW - dx)] = 0;
					pixels[(halfdstW + dx) * dstW + (halfdstH + dy)] = 0;
					pixels[(halfdstW + dx) * dstW + (halfdstH - dy)] = 0;
					pixels[(halfdstW - dx) * dstW + (halfdstH + dy)] = 0;
					pixels[(halfdstW - dx) * dstW + (halfdstH - dy)] = 0;
					continue;
				}
				cx = (int) Math.round(coord[0]);
				cy = (int) Math.round(coord[1]);
				pixels[(halfdstH + dy) * dstW + (halfdstW + dx)] = setPixels( cx,  cy);
				pixels[(halfdstH + dy) * dstW + (halfdstW - dx)] = setPixels(-cx,  cy);
				pixels[(halfdstH - dy) * dstW + (halfdstW + dx)] = setPixels( cx, -cy);
				pixels[(halfdstH - dy) * dstW + (halfdstW - dx)] = setPixels(-cx, -cy);
				pixels[(halfdstW + dx) * dstW + (halfdstH + dy)] = setPixels( cy,  cx);
				pixels[(halfdstW + dx) * dstW + (halfdstH - dy)] = setPixels(-cy,  cx);
				pixels[(halfdstW - dx) * dstW + (halfdstH + dy)] = setPixels( cy, -cx);
				pixels[(halfdstW - dx) * dstW + (halfdstH - dy)] = setPixels(-cy, -cx);
			}
		}
		consumer.setPixels(0, 0, dstW, dstH, defaultRGB, pixels, 0, dstW);
		consumer.imageComplete(status);
//System.out.println(System.currentTimeMillis() - timer);
	}

	private final int setPixels(final int cx, final int cy) {
		int sx = sxoffset + cx;
		int sy = syoffset + cy;
		if (sx < 0 || sy < 0 || sx >= srcW || sy >= srcH) {
			return Color.white.getRGB();
		} else {
			return raster[sy * srcW + sx];
		}
	}

}