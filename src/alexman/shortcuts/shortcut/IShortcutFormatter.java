package alexman.shortcuts.shortcut;

import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * Provides a concise way to format a Shortcut as a String, which can then be
 * parsed to return the same Shortcut.
 *
 * @author Alex Mandelias
 */
public interface IShortcutFormatter {

	/**
	 * Returns a String that describes a Shortcut in the desired format.
	 *
	 * @param shortcut the Shortcut
	 *
	 * @return the String that describes the Shortcut
	 *
	 * @throws NullPointerException if {@code shortcut == null}
	 *
	 * @implSpec the string this method returns should be parse-able by the
	 *           {@code parse} method so that the same Shortcut is generated. In
	 *           other words, the following should be true:
	 *
	 *           <pre>
	 *           {@code
	 *           ShortcutFormater sf;
	 *           Shortcut shortcut = new Shortcut("select all", "CONTROL + A");
	 *           Objects.equals(shortcut, sf.parse(sf.format(shortcut)));
	 *           }</pre>
	 */
	String format(Shortcut shortcut);

	/**
	 * Parses the given String and returns a Shortcut with that information.
	 *
	 * @param string the String to parse
	 *
	 * @return the Shortcut which results from the parsed string or {@code null} if
	 *         the string is malformed
	 *
	 * @throws NullPointerException if {@code string == null}
	 *
	 * @implSpec This method should return the same shortcut that was used with the
	 *           {@code format} method.that the same Shortcut is generated. In other
	 *           words, the following should be true:
	 *
	 *           <pre>
	 *           {@code
	 *           ShortcutFormatter sf;
	 *           String string = "select all = CONTROL + A";
	 *           Objects.equals(string, sf.format(sf.parse(string)));
	 *           }</pre>
	 */
	Shortcut parse(String string);
}
