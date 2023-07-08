package alexman.shortcuts.app.editor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import alexman.shortcuts.shortcut.IShortcutFormatter;

class EditorFrame extends JFrame {

	public EditorFrame(IShortcutFormatter sf) {
		super("The Great Shortcutinator - Editor");

		JPanel editorPanel = new EditorPanel(sf);
		add(editorPanel);

		setSize(640, 480);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
