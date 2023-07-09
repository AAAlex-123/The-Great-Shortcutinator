package alexman.shortcuts.shortcut.model;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import alexman.shortcuts.shortcut.IShortcutFormatter;

/**
 * Implements the ShortcutModel interface, which also acts as a
 * {@code ListModel} to use with a JList, which manages a list of Shortcuts. The
 * model can store a ShortcutFormatter for uniform load and store operations.
 *
 * @author Alex Mandelias
 */
public class ShortcutModel implements IShortcutModel, ListModel<Shortcut>, Iterable<Shortcut> {

	private final List<Shortcut> shortcuts = new LinkedList<>();
	private IShortcutFormatter sf;

	private final Set<ListDataListener> listDataListeners = new HashSet<>();

	/**
	 * Constructs a Shortcut Model with no Formatter. It is necessary to call
	 * {@link #setFormatter(IShortcutFormatter)} before attempting to call any of the
	 * {@code load} or {@code store} methods, as they require a Formatter.
	 */
	public ShortcutModel() {
		this(null);
	}

	/**
	 * Constructs a Shortcut Model with the given Formatter. If it is {@code null},
	 * it will be necessary to call {@link #setFormatter(IShortcutFormatter)} before
	 * attempting to call any of the {@code load} or {@code store} methods, as they
	 * require a Formatter.
	 *
	 * @param shortcutFormatter this Model's Formatter
	 */
	public ShortcutModel(IShortcutFormatter shortcutFormatter) {
		this.sf = shortcutFormatter;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Shortcut s : this) {
			sb.append(s).append('\n');
		}
		return sb.toString();
	}

	@Override
	public IShortcutModel load(Reader reader, IShortcutFormatter shortcutFormatter)
	        throws IOException {
		Objects.requireNonNull(reader, "reader cannot be null");
		Objects.requireNonNull(shortcutFormatter, "Shortcut Formatter cannot be null");

		shortcuts.clear();

		String nextLine;
		while ((nextLine = ShortcutModel.readLine(reader)) != null) {
			Shortcut s = shortcutFormatter.parse(nextLine);
			if (s != null) {
				shortcuts.add(s);
			}
		}

		ListDataEvent lde = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
		listDataListeners.forEach(ldl -> ldl.contentsChanged(lde));

		return this;
	}

	@Override
	public IShortcutModel store(Writer writer, IShortcutFormatter shortcutFormatter)
	        throws IOException {
		Objects.requireNonNull(writer, "writer cannot be null");
		Objects.requireNonNull(shortcutFormatter, "Shortcut Formatter cannot be null");

		for (Shortcut s : this) {
			writer.write(shortcutFormatter.format(s).toCharArray());
			writer.write(System.lineSeparator().toCharArray());
		}

		return this;
	}

	@Override
	public IShortcutFormatter getFormatter() {
		return sf;
	}

	@Override
	public final IShortcutModel setFormatter(IShortcutFormatter sf) {
		this.sf = sf;
		return this;
	}

	@Override
	public IShortcutModel addShortcut(Shortcut shortcut) {
		Objects.requireNonNull(shortcut, "shortcut cannot be null");

		shortcuts.add(shortcut);

		int index = shortcuts.size() - 1;
		ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index + 1);
		listDataListeners.forEach(ldl -> ldl.intervalAdded(lde));

		return this;
	}

	@Override
	public IShortcutModel removeShortcut(Shortcut shortcut) {
		Objects.requireNonNull(shortcut, "shortcut cannot be null");

		int index = shortcuts.indexOf(shortcut);
		boolean shortcutExists = shortcuts.remove(shortcut);
		if (!shortcutExists) {
			throw new IllegalArgumentException(String.format("Shortcut %s not found in model"));
		}

		ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index,
		        index + 1);
		listDataListeners.forEach(ldl -> ldl.intervalRemoved(lde));

		return this;
	}

	@Override
	public List<Shortcut> getShortcuts() {
		return Collections.unmodifiableList(shortcuts);
	}

	@Override
	public int getSize() {
		return shortcuts.size();
	}

	@Override
	public Shortcut getElementAt(int index) {
		return shortcuts.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listDataListeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listDataListeners.remove(l);
	}

	@Override
	public Iterator<Shortcut> iterator() {
		return new ShortcutModelIterator();
	}

	private class ShortcutModelIterator implements Iterator<Shortcut> {

		final Iterator<Shortcut> delegateIterator = getShortcuts().iterator();

		@Override
		public boolean hasNext() {
			return delegateIterator.hasNext();
		}

		@Override
		public Shortcut next() {
			return delegateIterator.next();
		}
	}

	private static String readLine(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();

		int next = reader.read();
		while (!((next == -1) || (next == '\n') || (next == '\r'))) {
			sb.append((char) next);
			next = reader.read();
		}

		// skip '\n' character of "\r\n"
		if (next == '\r')
			reader.skip(1);

		if (sb.isEmpty())
			return null;

		return sb.toString();
	}
}
