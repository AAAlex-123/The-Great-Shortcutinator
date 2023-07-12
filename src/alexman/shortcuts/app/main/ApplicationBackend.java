package alexman.shortcuts.app.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.app.editor.Editor;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
class ApplicationBackend {

	static final String USER_DIR = System.getProperty("user.dir");

	private final IShortcutModel sm;
	private final InputProcessor ip;
	private String lastLoadedFile;

	private final Consumer<String> onLoadedFileChanged;

	public ApplicationBackend(IShortcutModel sm, InputProcessor ip,
	        Consumer<String> onLoadedFileChanged) {
		this.sm = sm;
		this.ip = ip;
		this.onLoadedFileChanged = onLoadedFileChanged;
	}

	public boolean fileIsLoaded() {
		return lastLoadedFile != null;
	}

	public List<Shortcut> getShortcuts() {
		return sm.getShortcuts();
	}

	public void process(List<List<Integer>> keySequence) {
		ip.process(keySequence);
	}

	public enum ApplicationAction {

		LOAD {
			@Override
			public void perform(Object... args) throws IOException {
				ApplicationBackend context = (ApplicationBackend) args[0];
				String filename = (String) args[1];

				try (Reader reader = new FileReader(filename)) {
					context.sm.load(reader);
					context.lastLoadedFile = filename;
					context.onLoadedFileChanged.accept(filename);
				}
			}
		},

		RELOAD {
			@Override
			public void perform(Object... args) throws IOException, Exception {
				ApplicationBackend context = (ApplicationBackend) args[0];

				LOAD.perform(context, context.lastLoadedFile);
			}
		},

		EDIT {
			@Override
			public void perform(Object... args) throws Exception {
				ApplicationBackend context = (ApplicationBackend) args[0];

				Editor.main(new String[] { context.lastLoadedFile });
			}
		};

		public abstract void perform(Object... args) throws Exception;
	}
}
