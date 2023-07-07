package alexman.shortcuts;

import java.io.IOException;

import alexman.shortcuts.gui.ApplicationWindow;
import alexman.shortcuts.kbsimulator.RobotAdapter;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.ShortcutModel;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class MainApplication {

	/**
	 * TODO
	 *
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		IShortcutModel sm = new ShortcutModel();
		InputProcessor ip = new InputProcessor(new RobotAdapter());


		ApplicationWindow applicationWindow = new ApplicationWindow(sm, ip);
	}
}
