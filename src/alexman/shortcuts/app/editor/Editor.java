package alexman.shortcuts.app.editor;

import javax.swing.JFrame;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.SimpleFormatter;

public class Editor {

	public static void main(String[] args) {
		IShortcutFormatter sf = new SimpleFormatter();

		JFrame editorFrame = new EditorFrame(sf);
		editorFrame.setVisible(true);
	}
}
