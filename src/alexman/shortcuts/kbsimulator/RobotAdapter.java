package alexman.shortcuts.kbsimulator;

import java.awt.AWTException;
import java.awt.Robot;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class RobotAdapter implements KeyboardSimulator {

	private final Robot r;

	public RobotAdapter() {
		try {
			r = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void keyPress(int keycode) {
		r.keyPress(keycode);
	}

	@Override
	public void keyRelease(int keycode) {
		r.keyRelease(keycode);
	}
}
