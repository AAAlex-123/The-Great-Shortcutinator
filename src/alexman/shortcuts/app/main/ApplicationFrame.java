package alexman.shortcuts.app.main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.model.IShortcutModel;

class ApplicationFrame extends JFrame {

	public ApplicationFrame(IShortcutModel sm, IShortcutFormatter sf, InputProcessor ip) {
		super("The Great Shortcutinator");

		JPanel applicationPanel = new ApplicationPanel(sm, sf, ip);
		add(applicationPanel);

		setSize(800, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
