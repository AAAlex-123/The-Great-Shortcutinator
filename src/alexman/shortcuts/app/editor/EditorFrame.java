package alexman.shortcuts.app.editor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.model.ShortcutModel;

class EditorFrame extends JFrame {

		super("The Great Shortcutinator - Editor");

 		JPanel editorPanel = new EditorPanel(sf, sm);
		add(editorPanel);

		setSize(640, 480);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
