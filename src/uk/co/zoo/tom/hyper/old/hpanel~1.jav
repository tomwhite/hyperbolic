import java.awt.*;

public class HPanel extends Panel {

	public void paint(Graphics g) {

		Image offImage = createImage(getSize().width, getSize().height);
		Graphics offGraphics = offImage.getGraphics();

		if (isShowing()) {
			Component component[] = getComponents();
			Rectangle clip = g.getClipBounds();

			for (int i = component.length - 1 ; i >= 0 ; i--) {
				Component comp = component[i];
				if (comp != null && 
						comp.getPeer() instanceof java.awt.peer.LightweightPeer &&
						comp.isVisible() == true) {

					// Do hyperbolic transformations...
					// And switch between on- and off-screen images...
					Rectangle cr = comp.getBounds();
					if ((clip == null) || cr.intersects(clip)) {
						Graphics cg = g.create(cr.x, cr.y, cr.width, cr.height);
						try {
							comp.paint(cg);
						} finally {
							cg.dispose();
						}
					}


				}
			}

			// Finally draw off-screen image
//			g.drawImage(offImage, 0, 0, this);

		}

	}

}