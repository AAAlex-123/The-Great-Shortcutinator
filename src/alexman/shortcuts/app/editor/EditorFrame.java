package alexman.shortcuts.app.editor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.model.ShortcutModel;

class EditorFrame extends JFrame {

	public EditorFrame(String file, IShortcutFormatter sf)
	        throws FileNotFoundException, IOException {
		super("The Great Shortcutinator - Editor");
		configurePanel(new EditorPanel(file, sf));
	}

	public EditorFrame(ShortcutModel sm) {
		super("The Great Shortcutinator - Editor");
		configurePanel(new EditorPanel(sm));
	}

	private void configurePanel(JPanel editorPanel) {
		add(editorPanel);
		setSize(640, 480);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
