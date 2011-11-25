package uk.co.zoo.tom.hyper;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class HyperbolicCanvas extends Canvas implements Scalable, Tileable {

	Image sourceImage;
	Image resultImage;

	HyperbolicImageSource his;
	ImageProducer producer;

	int startX, startY, endX, endY;
	int diameter;

	public HyperbolicCanvas(Image image, int imageWidth, int imageHeight, int diam, int scaling, boolean tiled) {
		sourceImage = image;
		resultImage = sourceImage;

		diameter = diam;

		his = new SimpleHyperbolicImageSource(image, 0, 0, imageWidth, imageHeight, diameter, scaling, tiled);
		producer = his.getSource();

		resultImage = createImage(producer);


		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				startX = e.getX();
				startY = e.getY();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter () {
			public void mouseDragged(MouseEvent e) {
				endX = e.getX();
				endY = e.getY();

				// only redraw if drag is more than a threshold distance (in hyperbolic plane??)

				if (Math.sqrt((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY)) > 20.0) {
					
					double[] startCoord = new double[2];
					his.itransform(startX - diameter / 2, startY - diameter / 2, startCoord);

					double[] endCoord = new double[2];
					his.itransform(endX - diameter /2 , endY - diameter / 2, endCoord);

					his.translateSource((int) Math.round(startCoord[0] - endCoord[0]), (int) Math.round(startCoord[1] - endCoord[1]));

					startX = endX;
					startY = endY;
				}

			}
		});

	}

	public void setScaling(int scaling) {
		his.setScaling(scaling);
	}

	public int getScaling() {
		return his.getScaling();
	}

	public void setTiled(boolean tiled) {
		his.setTiled(tiled);
	}

	public boolean isTiled() {
		return his.isTiled();
	}

	public void paint(Graphics g) {
		Dimension d = getSize();
		int x = (d.width - resultImage.getWidth(this)) / 2;
		int y = (d.height - resultImage.getHeight(this)) / 2;

		// Double buffer
		Image offImage = createImage(d.width, d.height);
		Graphics offGraphics = offImage.getGraphics();
		offGraphics.drawImage(resultImage, x, y, this);

		g.drawImage(offImage, x, y, this);
	}

	public void update(Graphics g) {
		paint(g);
	}
}

