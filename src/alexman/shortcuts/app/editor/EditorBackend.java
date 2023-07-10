package alexman.shortcuts.app.editor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;
import java.util.function.Consumer;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.Shortcut;
import alexman.undo.Undoable;
import alexman.undo.UndoableHistory;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
class EditorBackend {

	static final String USER_DIR = System.getProperty("user.dir");

	private final IShortcutModel sm;
	private String lastLoadedFile;
	private final Optional<IShortcutFormatter> sf;
	private final UndoableHistory<EditorCommand> history = new UndoableHistory<>();

	private final Consumer<String> onLoadedFileChanged;
	private final Consumer<Boolean> onUndoEnabledChanged, onRedoEnabledChanged;

	public EditorBackend(IShortcutModel sm, IShortcutFormatter sf,
	        Consumer<String> onLoadedFileChanged, Consumer<Boolean> onUndoEnabledChanged,
	        Consumer<Boolean> onRedoEnabledChanged) {
		this.sm = sm;
		this.sf = Optional.ofNullable(sf);
		this.onLoadedFileChanged = onLoadedFileChanged;
		this.onUndoEnabledChanged = onUndoEnabledChanged;
		this.onRedoEnabledChanged = onRedoEnabledChanged;
	}

	public boolean fileIsLoaded() {
		return lastLoadedFile != null;
	}

	public IShortcutModel getModel() {
		return sm;
	}

	public IShortcutFormatter getFormatter() {
		return sf.orElse(sm.getFormatter());
	}

	public enum EditorAction {

		LOAD {
			@Override
			public void perform(Object... args) throws FileNotFoundException, IOException {
				EditorBackend context = (EditorBackend) args[0];
				String filename = (String) args[1];

				try (Reader reader = new FileReader(filename)) {
					context.sm.load(reader);
				}
				context.lastLoadedFile = filename;
				context.onLoadedFileChanged.accept(filename);
				context.history.clear();
				onHistoryChanged(context);
			}
		},

		ADD {
			@Override
			public void perform(Object... args) throws IllegalArgumentException {
				EditorBackend context = (EditorBackend) args[0];
				String action = (String) args[1];
				String keySequence = (String) args[2];
				Shortcut shortcut = new Shortcut(action, keySequence);

				EditorCommand command = new AddShortcut(context, shortcut);
				command.execute();
				context.history.add(command);
				onHistoryChanged(context);
			}
		},

		REMOVE {
			@Override
			public void perform(Object... args) {
				EditorBackend context = (EditorBackend) args[0];
				Shortcut shortcut = (Shortcut) args[1];

				EditorCommand command = new RemoveShortcut(context, shortcut);
				command.execute();
				context.history.add(command);
				onHistoryChanged(context);
			}
		},

		UNDO {
			@Override
			public void perform(Object... args) {
				EditorBackend context = (EditorBackend) args[0];

				context.history.undo();
				onHistoryChanged(context);
			}
		},

		REDO {
			@Override
			public void perform(Object... args) {
				EditorBackend context = (EditorBackend) args[0];

				context.history.redo();
				onHistoryChanged(context);
			}
		},

		RESET {
			@Override
			public void perform(Object... args) throws IOException, Exception {
				EditorBackend context = (EditorBackend) args[0];

				LOAD.perform(context, context.lastLoadedFile);
			}
		},

		SAVE_AS {
			@Override
			public void perform(Object... args) throws IOException, Exception {
				EditorBackend context = (EditorBackend) args[0];
				String filename = (String) args[1];

				try (Writer writer = new FileWriter(filename)) {
					context.sm.store(writer);
				}

				LOAD.perform(context, filename);
			}
		},

		SAVE {
			@Override
			public void perform(Object... args) throws IOException, Exception {
				EditorBackend context = (EditorBackend) args[0];

				SAVE_AS.perform(context, context.lastLoadedFile);
			}
		};

		public abstract void perform(Object... args) throws Exception;

		private static void onHistoryChanged(EditorBackend context) {
			context.onUndoEnabledChanged.accept(context.history.canUndo());
			context.onRedoEnabledChanged.accept(context.history.canRedo());
		}
	}

	private static abstract class EditorCommand implements Undoable {

		protected final EditorBackend context;

		public EditorCommand(EditorBackend context) {
			this.context = context;
		}
	}

	private static class AddShortcut extends EditorCommand {

		private final Shortcut shortcutToAdd;

		public AddShortcut(EditorBackend context, Shortcut shortcut) {
			super(context);
			this.shortcutToAdd = shortcut;
		}

		@Override
		public void execute() {
			context.sm.addShortcut(shortcutToAdd);
		}

		@Override
		public void unexecute() {
			context.sm.removeShortcut(shortcutToAdd);
		}
	}

	private static class RemoveShortcut extends EditorCommand {

		private final Shortcut shortcutToRemove;

		public RemoveShortcut(EditorBackend context, Shortcut shortcut) {
			super(context);
			this.shortcutToRemove = shortcut;
		}

		@Override
		public void execute() {
			context.sm.removeShortcut(shortcutToRemove);
		}

		@Override
		public void unexecute() {
			context.sm.addShortcut(shortcutToRemove);
		}
	}
}
