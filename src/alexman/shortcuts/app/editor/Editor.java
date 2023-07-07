package alexman.shortcuts.app.editor;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import alexman.shortcuts.gui.ShortcutEditor;
import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.SimpleFormatter;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class Editor {

	/**
	 * TODO
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Editor");
		
		IShortcutFormatter sf = new SimpleFormatter();
		ShortcutEditor se = new ShortcutEditor(sf);
		frame.add(se);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setVisible(true);
	}
}
