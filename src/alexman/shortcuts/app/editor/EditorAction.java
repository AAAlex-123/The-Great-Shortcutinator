package alexman.shortcuts.app.editor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
enum EditorAction {

	LOAD {
		@Override
		public void perform(Object... args) throws FileNotFoundException, IOException {
			EditorPanel context = (EditorPanel) args[0];
			String filename = (String) args[1];

			try (Reader reader = new FileReader(filename)) {
				context.sm.load(reader);
			}
			context.lastLoadedFile = filename;
			context.loadedFile.setText(filename);
			context.history.clear();
		}
	},

	ADD {
		@Override
		public void perform(Object... args) {
			EditorPanel context = (EditorPanel) args[0];
			Shortcut shortcut = (Shortcut) args[1];

			EditorCommand command = new AddShortcut(context, shortcut);
			command.execute();
			context.history.add(command);
		}
	},

	REMOVE {
		@Override
		public void perform(Object... args) {
			EditorPanel context = (EditorPanel) args[0];
			Shortcut shortcut = (Shortcut) args[1];

			EditorCommand command = new RemoveShortcut(context, shortcut);
			command.execute();
			context.history.add(command);
		}
	},

	UNDO {
		@Override
		public void perform(Object... args) {
			EditorPanel context = (EditorPanel) args[0];

			context.history.undo();
		}
	},

	REDO {
		@Override
		public void perform(Object... args) {
			EditorPanel context = (EditorPanel) args[0];

			context.history.redo();
		}
	},

	RESET {
		@Override
		public void perform(Object... args) throws IOException {
			EditorPanel context = (EditorPanel) args[0];

			LOAD.perform(context, context.lastLoadedFile);
			context.history.clear();
		}
	},

	SAVE_AS {
		@Override
		public void perform(Object... args) throws IOException {
			EditorPanel context = (EditorPanel) args[0];
			String filename = (String) args[1];

			try (Writer writer = new FileWriter(filename)) {
				context.sm.store(writer);
			}
		}
	},

	SAVE {
		@Override
		public void perform(Object... args) throws IOException {
			EditorPanel context = (EditorPanel) args[0];

			SAVE_AS.perform(context, context.lastLoadedFile);
		}
	};

	abstract void perform(Object... args) throws IOException;
}
