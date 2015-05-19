import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Box;
import javax.swing.JPanel;

/**
 * The simulation panel for showing the timeline of notes
 * @author Andrew Wilder
 */
public class GBATrackerSimulationPanel extends JPanel {
	
	private GBATrackerFrame controller;
	
	/**
	 * Initialize the simulation panel and the simulation variables
	 * @param controller Reference to the main controller
	 */
	public GBATrackerSimulationPanel(GBATrackerFrame controller) {
		
		// No actual components, just a rigid area to maintain window size
		this.controller = controller;
		add(Box.createRigidArea(new Dimension(800, 300)));
	}
	
	/**
	 * Draw the simulation screen
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
