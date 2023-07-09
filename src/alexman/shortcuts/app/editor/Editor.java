package alexman.shortcuts.app.editor;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.SimpleFormatter;
import alexman.shortcuts.shortcut.model.ShortcutModel;

public class Editor {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		IShortcutFormatter sf = new SimpleFormatter();
		JFrame editorFrame;

		if (args.length == 0) {
			ShortcutModel sm = new ShortcutModel();
			sm.setFormatter(sf);

			editorFrame = new EditorFrame(sm);

		} else if (args.length == 1) {
			String file = args[0];
			editorFrame = new EditorFrame(file, sf);

		} else {
			System.out.println("Usage: java Editor [file]");
			return;
		}

		editorFrame.setVisible(true);
	}
}
