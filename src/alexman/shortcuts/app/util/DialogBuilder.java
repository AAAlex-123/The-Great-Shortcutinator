package alexman.shortcuts.app.util;

import java.awt.Component;

import javax.swing.JOptionPane;

@SuppressWarnings("hiding")
public class DialogBuilder {

	public static void noFileLoaded(Component parent) {
		new DialogBuilder(parent)
		        .information()
		        .title("No file has been loaded")
		        .message("Click 'Load' to load a file.")
		        .show();
	}

	public static void error(Component parent, String message) {
		new DialogBuilder(parent)
		        .error()
		        .title("Unexpected Error")
		        .message("Forward this message to the developer and try again\n<%s>.", message)
		        .show();
	}

	private Component parent;
	private String title;
	private String message;
	private int messageType;

	public DialogBuilder(Component parent) {
		this.parent = parent;
	}

	public void show() {
		JOptionPane.showMessageDialog(parent, message, title, messageType);
	}

	public DialogBuilder title(String title) {
		this.title = title;
		return this;
	}

	public DialogBuilder message(String format, Object... args) {
		this.message = String.format(format, args);
		return this;
	}

	public DialogBuilder content(String title, String format, Object... args) {
		title(title);
		message(message, args);
		return this;
	}

	public DialogBuilder error() {
		return messageType(JOptionPane.ERROR_MESSAGE);
	}

	public DialogBuilder information() {
		return messageType(JOptionPane.INFORMATION_MESSAGE);
	}

	public DialogBuilder warning() {
		return messageType(JOptionPane.WARNING_MESSAGE);
	}

	public DialogBuilder question() {
		return messageType(JOptionPane.QUESTION_MESSAGE);
	}

	public DialogBuilder plain() {
		return messageType(JOptionPane.PLAIN_MESSAGE);
	}

	public DialogBuilder messageType(int messageType) {
		this.messageType = messageType;
		return this;
	}
}
