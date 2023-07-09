package alexman.shortcuts.app.main;

import java.io.IOException;

import javax.swing.JFrame;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.kbsimulator.RobotAdapter;
import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.SimpleFormatter;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.ShortcutModel;


public class Application {

	public static void main(String[] args) throws IOException {
		IShortcutModel sm = new ShortcutModel();
		IShortcutFormatter sf = new SimpleFormatter();
		InputProcessor ip = new InputProcessor(new RobotAdapter());

		JFrame applicationWindow = new ApplicationFrame(sm, sf, ip);
		applicationWindow.setVisible(true);
	}
}
