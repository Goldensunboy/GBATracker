import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class provides convenience for displaying a tooltip when hovering
 * over components.
 * @author Andrew Wilder
 */
public class MessageMouseListener implements MouseListener {
	
	/** The message to display when hovering over a component */
	private GBATrackerFrame controller;
	private String message;
	
	/**
	 * Create a new instance of the message mouse listener
	 * @param message The message to display when hovering over a component
	 */
	public MessageMouseListener(GBATrackerFrame controller, String message) {
		this.controller = controller;
		this.message = message;
	}

	/**
	 * Display the message
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		controller.setTooltipText(message);
	}

	/**
	 * Clear the message
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		controller.setTooltipText(" ");
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// unused
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// unused
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// unused
	}
}
