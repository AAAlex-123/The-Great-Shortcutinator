package alexman.shortcuts;

import static alexman.shortcuts.shortcut.model.Shortcut.Parser.format;
import static alexman.shortcuts.shortcut.model.Shortcut.Parser.parse;

import java.util.List;

import alexman.shortcuts.shortcut.model.Shortcut;

public class MainTest {

	public static void main(String[] args) {
		/* Shortcut model test @foff
		ShortcutModel sm = new ShortcutModel();

		ShortcutFormatter sf = new SimpleFormatter();
		Shortcut s = new Shortcut("select all", "CONTROL + A");
		System.out.println(Objects.equals(s, sf.parse(sf.format(s))));

		String s1 = "select all = CONTROL + a";
		System.out.println(Objects.equals(s1, sf.format(sf.parse(s1))));
		@fon */

		//* Shortcut test @foff
		Shortcut s1 = new Shortcut("Run Java Application", "ALT+SHIFT+X J");
		Shortcut s2 = new Shortcut("Run Java Application", List.of(List.of(18, 16, 88), List.of(74)));
		System.out.println(s1.getKeySequence());
		System.out.println(s2.getKeySequence());
		System.out.println(s1.equals(s2));

		String s = "CONTROL+A SHIFT+B C";
		Shortcut sh = new Shortcut("dummy", s);
		System.out.println(sh);
		System.out.println(sh.getKeySequenceAsString());
		System.out.println(new Shortcut("dummy copy", sh.getKeySequenceAsString()));

		System.out.println(s);
		System.out.println(format(parse(s)));
		System.out.println(sh.getKeySequence());
		System.out.println(parse(format(sh.getKeySequence())));
		// @fon */
	}
}
