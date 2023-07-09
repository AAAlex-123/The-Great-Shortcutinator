package alexman.shortcuts.app.editor;

import javax.swing.JFrame;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.SimpleFormatter;
import alexman.shortcuts.shortcut.model.ShortcutModel;

public class Editor {

	public static void main(String[] args) {
		ShortcutModel sm = new ShortcutModel();
		IShortcutFormatter sf = new SimpleFormatter();

		JFrame editorFrame = new EditorFrame(sf, sm);
		editorFrame.setVisible(true);
	}
}
