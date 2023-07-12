package alexman.shortcuts.app.editor;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.SimpleFormatter;
import alexman.shortcuts.shortcut.model.ShortcutModel;

public class Editor {

	public static void main(String[] args) throws Exception {

		IShortcutFormatter sf = new SimpleFormatter();
		JFrame editorFrame;

		if (args.length == 0) {
			ShortcutModel sm = new ShortcutModel();
			sm.setFormatter(sf);

			editorFrame = new EditorFrame(sm);
			editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		} else if (args.length == 1) {
			String file = args[0];
			editorFrame = new EditorFrame(file, sf);
			editorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		} else {
			System.out.println("Usage: java Editor [file]");
			return;
		}

		editorFrame.setVisible(true);
	}
}
