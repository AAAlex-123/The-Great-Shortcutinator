package alexman.shortcuts.app.editor;

import alexman.shortcuts.shortcut.model.Shortcut;
import alexman.undo.Undoable;

abstract class EditorCommand implements Undoable {

	protected final EditorPanel context;

	public EditorCommand(EditorPanel context) {
		super();
		this.context = context;
	}
}

class AddShortcut extends EditorCommand {

	private final Shortcut shortcutToAdd;

	public AddShortcut(EditorPanel context, Shortcut shortcut) {
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

class RemoveShortcut extends EditorCommand {

	private final Shortcut shortcutToRemove;

	public RemoveShortcut(EditorPanel context, Shortcut shortcut) {
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
