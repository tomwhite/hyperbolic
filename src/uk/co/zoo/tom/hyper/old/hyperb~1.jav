import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class HyperbolicCanvas extends Canvas {

	Image sourceImage;
	Image resultImage;

	HyperbolicFilter filter;
	ImageProducer producer;

	Dimension size = new Dimension(400, 400);

	public HyperbolicCanvas(Image image, int scaling) {
		sourceImage = image;
		resultImage = sourceImage;

		filter = new HyperbolicFilter(size, scaling);
		producer = new FilteredImageSource(sourceImage.getSource(), filter);

		addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
System.out.println(e.getX() + " " + e.getY());
				double[] coord = new double[2];
				filter.itransform(e.getX() - 200, e.getY() - 200, coord);
				filter.translateSource((int) Math.round(coord[0]), (int) Math.round(coord[1]));
				distort();
			}
	
		});
	}

	public void distort() {
long timer = System.currentTimeMillis();
		resultImage = createImage(producer);
		repaint();
System.out.println("Total distort time (ms) " + (System.currentTimeMillis() - timer));
	}

	public void paint(Graphics g) {
		Dimension d = getSize();
		int x = (d.width - resultImage.getWidth(this)) / 2;
		int y = (d.height - resultImage.getHeight(this)) / 2;
		g.drawImage(resultImage, x, y, this);
	}
}

