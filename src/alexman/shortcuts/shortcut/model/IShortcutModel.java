package alexman.shortcuts.shortcut.model;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

import alexman.shortcuts.shortcut.IShortcutFormatter;

/**
 * Defines the interface for models of the Shortcut entity. Models use a
 * ShortcutFormatter to parse the contents of a file and to also save the
 * Shortcuts to a file.
 *
 * @author Alex Mandelias
 */
public interface IShortcutModel {

	/**
	 * Same as {@link #load(Reader, IShortcutFormatter)} but using the model's
	 * current Formatter as the second argument.
	 *
	 * @param reader the reader. It remains open after this method returns
	 *
	 * @return this model
	 *
	 * @throws IOException if an IO Exception occurs while reading from the reader
	 * @throws NullPointerException if the provided Formatter is {@code null}
	 */
	default IShortcutModel load(Reader reader) throws IOException {
		IShortcutFormatter sf = Objects.requireNonNull(getFormatter(),
		        "No ShortcutFormatter found, use setFormatter() before calling this method");

		return load(reader, sf);
	}

	/**
	 * Same as {@link #store(Writer, IShortcutFormatter)} but using the model's
	 * current Formatter as the second argument.
	 *
	 * @param writer the writer. It remains open after this method returns
	 *
	 * @return this model
	 *
	 * @throws IOException if an IO Exception occurs while writing to the writer
	 * @throws NullPointerException if the provided Formatter is {@code null}
	 */
	default IShortcutModel store(Writer writer) throws IOException {
		IShortcutFormatter sf = Objects.requireNonNull(getFormatter(),
		        "No ShortcutFormatter found, use setFormatter() before calling this method");

		return store(writer, sf);
	}

	/**
	 * Clears this model and loads Shortcuts from the given reader using the given
	 * Formatter.
	 *
	 * @param reader the reader. It remains open after this method returns
	 * @param sf the Formatter used to parse the data of the reader
	 *
	 * @return this model
	 *
	 * @throws IOException if an IO Exception occurs while reading from the reader
	 * @throws NullPointerException if the provided Formatter is {@code null}
	 */
	IShortcutModel load(Reader reader, IShortcutFormatter sf) throws IOException;

	/**
	 * Writes the Shortcuts of this model to the given writer using the given
	 * Formatter.
	 *
	 * @param writer the writer. It remains open after this method returns
	 * @param sf the Formatter used to format the data before writing them
	 *
	 * @return this model
	 *
	 * @throws IOException if an IO Exception occurs while writing to the writer
	 * @throws NullPointerException if the current Formatter is {@code null}
	 */
	IShortcutModel store(Writer writer, IShortcutFormatter sf) throws IOException;

	/**
	 * Returns this model's current Formatter which is used when calling the
	 * {@link #load(Reader)} and {@link #store(Writer)} methods.
	 *
	 * @return this model's current Formatter
	 */
	IShortcutFormatter getFormatter();

	/**
	 * Sets this model's current Formatter which is used when calling the
	 * {@link #load(Reader)} and {@link #store(Writer)} methods.
	 *
	 * @param sf the new Formatter to set. Passing {@code null} to this method will
	 *        cause both the aforementioned methods to throw a
	 *        {@code NullPointerException}
	 *
	 * @return this model
	 */
	IShortcutModel setFormatter(IShortcutFormatter sf);

	/**
	 * Adds a new Shortcut to this model.
	 *
	 * @param shortcut the Shortcut to add
	 *
	 * @return this model
	 *
	 * @throws NullPointerException if {@code shortcut == null}
	 */
	IShortcutModel addShortcut(Shortcut shortcut);

	/**
	 * Removes an existing Shortcut from this model.
	 *
	 * @param shortcut the Shortcut to remove
	 *
	 * @return this model
	 *
	 * @throws NullPointerException if {@code shortcut == null}
	 *         IllegalArgumentException if the Shortcut doesn't exist in this model
	 */
	IShortcutModel removeShortcut(Shortcut shortcut);

	/**
	 * Returns an unmodifiable List with this model's Shortcuts.
	 *
	 * @return the list
	 */
	List<Shortcut> getShortcuts();

	/**
	 * Removes all existing Shortcuts from this model.
	 *
	 * @return this model
	 */
	default IShortcutModel clear() {
		for (Shortcut shortcut : getShortcuts())
			removeShortcut(shortcut);

		return this;
	}
}
