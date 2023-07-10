package alexman.shortcuts.shortcut;

import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * A simple {@code ShortcutFormatter} implementation which formats the Shortcut
 * as: {@code action = keySequence}. The separator character can be customised.
 *
 * @author Alex Mandelias
 */
public class SimpleFormatter implements IShortcutFormatter {

	private final char kvsep;

	/** Constructs a Formatter with '=' as the action-keys separator */
	public SimpleFormatter() {
		this('=');
	}

	/**
	 * Constructs a Simple Formatter with the given action-keys separator.
	 *
	 * @param actionKeysequenceSeparator the action-keys separator to use
	 *
	 * @throws IllegalArgumentException if the action-keys separator is whitespace
	 */
	public SimpleFormatter(char actionKeysequenceSeparator) {
		if (Character.isWhitespace(actionKeysequenceSeparator)) {
			throw new IllegalArgumentException(
			        "The action-key sequence separator cannot be whitespace");
		}

		this.kvsep = actionKeysequenceSeparator;
	}

	@Override
	public String format(Shortcut shortcut) {
		return String.format("%s %c %s", shortcut.getAction(), this.kvsep,
		        shortcut.getKeySequenceAsString());
	}

	@Override
	public Shortcut parse(String string) {
		char[] line = string.toCharArray();

		int keyStart, keyEnd, kvSep, valueStart, valueEnd;

		// find keyStart: key starts after the first non-whitespace character
		for (keyStart = 0; keyStart < line.length; keyStart++) {
			if (!Character.isWhitespace(line[keyStart]))
				break;
		}

		// skip all whitespace until key-value separator (kv-sep)
		boolean kvSepFound = false;
		for (kvSep = keyStart + 1; kvSep < line.length; kvSep++) {
			if (line[kvSep] == this.kvsep) {
				kvSepFound = true;
				break;
			}
		}

		// no kv-sep found, invalid line
		if (!kvSepFound) {
			return null;
		}

		// find keyEnd: go backwards from kv-sep till first non-whitespace character
		keyEnd = kvSep;
		keyEnd--;
		for (;; keyEnd--) {
			if (!Character.isWhitespace(line[keyEnd])) {
				keyEnd++;
				break;
			}
		}

		// find valueStart: value starts after the first non-whitespace character after kv-sep
		for (valueStart = kvSep + 1; valueStart < line.length; valueStart++) {
			if (!Character.isWhitespace(line[valueStart]))
				break;
		}

		// find valueEnd: value includes all whitespace, just trim trailing whitespace
		valueEnd = line.length;
		valueEnd--;
		for (;; valueEnd--) {
			if (!Character.isWhitespace(line[valueEnd])) {
				valueEnd++;
				break;
			}
		}

		String key = new String(line, keyStart, keyEnd - keyStart);
		String value = new String(line, valueStart, valueEnd - valueStart);

		return new Shortcut(key, value);
	}
}
