package uk.co.zoo.tom.hyper;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;

public class HyperbolicImageApplet extends Applet {

	private final static int DEFAULT_DIAMETER = 400;
	private final static int DEFAULT_SCALING = 200;

	Image image;
	HyperbolicCanvas canvas;
	TextField textField;
	String scalingText;

	public void init() {

		String imageFile = getParameter("image");
		if (imageFile == null) {
			System.err.println("No image name");
			return;
		}

		try {
			URL url = new URL(getDocumentBase(), imageFile);
			image = getImage(url);

			try {
				MediaTracker mediaTracker = new MediaTracker(this);
				mediaTracker.addImage(image, 0);
				mediaTracker.waitForID(0);  // wait for image to load ...
				System.out.println("Image size is: " + image.getWidth(this) + " " + image.getHeight(this));

			} catch (InterruptedException e) {
            		System.err.println(e.getMessage());
				return; // unstable?
			}

		} catch (MalformedURLException e) {
            	System.err.println(e.getMessage());
			return;
		}

		scalingText = getParameter("scaling");
		if (scalingText == null) {
			scalingText = Integer.toString(DEFAULT_SCALING);
		}

		int scaling = DEFAULT_SCALING;
		try {
			scaling = Integer.parseInt(scalingText);
		} catch (NumberFormatException ex) {
			scalingText = Integer.toString(scaling);
		}

		String diameterText = getParameter("diameter");
		if (diameterText == null) {
			diameterText = Integer.toString(DEFAULT_DIAMETER);
		}

		int diameter = DEFAULT_DIAMETER;
		try {
			diameter = Integer.parseInt(diameterText);
		} catch (NumberFormatException ex) {
			diameterText = Integer.toString(diameter);
		}

		String tiled = getParameter("tiled");

		canvas = new HyperbolicCanvas(image, image.getWidth(this), image.getHeight(this),
								diameter, scaling,
								(tiled != null && tiled.equalsIgnoreCase("yes")));

		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);

		textField = new TextField(scalingText);
		Panel panel = new Panel();
		panel.add(new Label("Magnification"));
		panel.add(textField);
		add(panel, BorderLayout.SOUTH);

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					canvas.setScaling(Integer.parseInt(textField.getText()));
					scalingText = textField.getText();
				} catch (NumberFormatException ex) {
					textField.setText(scalingText);
				}
			}
		});

	}

}

