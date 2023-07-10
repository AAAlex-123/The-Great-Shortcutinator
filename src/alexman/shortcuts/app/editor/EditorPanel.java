package alexman.shortcuts.app.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import alexman.shortcuts.app.editor.EditorBackend.EditorAction;
import alexman.shortcuts.app.util.DialogBuilder;
import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.model.Shortcut;
import alexman.shortcuts.shortcut.model.ShortcutModel;
import requirement.requirements.StringType;
import requirement.util.Requirements;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
class EditorPanel extends JPanel {

	private JButton load, undo, redo, reset, saveAs, save, add, remove;
	private JLabel loadedFile;
	private JPanel top, main, right, bottom, bottomLeft, bottomRight;
	private final JList<Shortcut> shortcutList;

	private final EditorBackend backend;

	public EditorPanel(String filename, IShortcutFormatter sf)
	        throws FileNotFoundException, Exception {
		this(new ShortcutModel(sf), null, false);

		EditorAction.LOAD.perform(this, filename);
	}

	public EditorPanel(ShortcutModel sm) {
		this(sm, null, true);
	}

	private EditorPanel(ShortcutModel sm, IShortcutFormatter sf, boolean loadEnabled) {
		super(new BorderLayout());
		backend = new EditorBackend(sm, sf, (String filename) -> loadedFile.setText(filename));

		top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		if (loadEnabled) {
			load = new JButton("Load");
			load.addActionListener(new LoadActionListener());
		}
		loadedFile = new JLabel("--- Click 'Load' to load a File ---");
		if (loadEnabled) {
			top.add(load);
		}
		top.add(loadedFile);
		this.add(top, BorderLayout.NORTH);

		shortcutList = new JList<>();
		shortcutList.setCellRenderer(new ShortcutCellRenderer());
		shortcutList.setModel(sm);
		main = new JPanel(new FlowLayout());
		main.add(shortcutList);
		this.add(main, BorderLayout.CENTER);

		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add = new JButton("Add");
		add.setAlignmentX(CENTER_ALIGNMENT);
		add.addActionListener(new AddActionListener());
		remove = new JButton("Remove");
		remove.setAlignmentX(CENTER_ALIGNMENT);
		remove.addActionListener(new RemoveActionListener());
		right.add(add);
		right.add(Box.createRigidArea(new Dimension(0, 5)));
		right.add(remove);
		this.add(right, BorderLayout.EAST);

		bottom = new JPanel(new BorderLayout());

		bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		undo = new JButton("Undo");
		undo.addActionListener(new UndoActionListener());
		redo = new JButton("Redo");
		redo.addActionListener(new RedoActionListener());
		reset = new JButton("Reset");
		reset.addActionListener(new ResetActionListener());
		bottomLeft.add(undo);
		bottomLeft.add(redo);
		bottomLeft.add(reset);

		bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		saveAs = new JButton("Save As");
		saveAs.addActionListener(new SaveAsActionListener());
		save = new JButton("Save");
		save.addActionListener(new SaveActionListener());
		bottomRight.add(saveAs);
		bottomRight.add(save);

		bottom.add(bottomLeft, BorderLayout.WEST);
		bottom.add(bottomRight, BorderLayout.EAST);
		this.add(bottom, BorderLayout.SOUTH);
	}

	private class ShortcutCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Shortcut selected = (Shortcut) value;
			setText(backend.getFormatter().format(selected));
			return this;
		}
	}

	private class LoadActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser(EditorBackend.USER_DIR);
			int dialogResult = jfc.showOpenDialog(EditorPanel.this);
			if (dialogResult != JFileChooser.APPROVE_OPTION) {
				return;
			}

			String filename = jfc.getSelectedFile().getAbsolutePath();

			try {
				EditorAction.LOAD.perform(backend, filename);
			} catch (Exception e1) {
				DialogBuilder.error(EditorPanel.this, e1.getMessage());
			}
		}
	}

	private class AddActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Requirements reqs = new Requirements();
			reqs.add("Name", StringType.NON_EMPTY);
			reqs.add("Shortcut", StringType.NON_EMPTY);

			Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, EditorPanel.this);
			reqs.fulfillWithDialog(frame, "Add a new Shortcut");

			if (!reqs.fulfilled())
				return;

			// they are actually strings, but EditorAction#perform takes objects
			Object name = reqs.getValue("Name");
			Object shortcut = reqs.getValue("Shortcut");

			try {
				EditorAction.ADD.perform(backend, name, shortcut);
			} catch (IllegalArgumentException e1) {
				new DialogBuilder(EditorPanel.this)
				        .warning()
				        .title("Invalid Shortcut")
				        .message("Key sequence <%s> is invalid", shortcut)
				        .show();
			} catch (Exception e1) {
				// will never throw
			}
		}
	}

	private class RemoveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Shortcut selected = shortcutList.getSelectedValue();

			if (selected == null) {
				new DialogBuilder(EditorPanel.this)
				        .information()
				        .title("No Shortcut selected")
				        .message("Select a Shortcut to delete")
				        .show();
				return;
			}

			try {
				EditorAction.REMOVE.perform(backend, selected);
			} catch (Exception e1) {
				// will never throw
			}
		}
	}

	private class UndoActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				EditorAction.UNDO.perform(backend);
			} catch (Exception e1) {
				// will never throw
			}
		}
	}

	private class RedoActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				EditorAction.REDO.perform(backend);
			} catch (Exception e1) {
				// will never throw
			}
		}
	}

	private class ResetActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!backend.fileIsLoaded()) {
				DialogBuilder.noFileLoaded(EditorPanel.this);
				return;
			}

			try {
				EditorAction.RESET.perform(backend);
			} catch (Exception e1) {
				DialogBuilder.error(EditorPanel.this, e1.getMessage());
			}
		}
	}

	private class SaveAsActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser(EditorBackend.USER_DIR);
			int dialogResult = jfc.showSaveDialog(EditorPanel.this);
			if (dialogResult != JFileChooser.APPROVE_OPTION)
				return;

			String filename = jfc.getSelectedFile().getAbsolutePath();

			try {
				EditorAction.SAVE_AS.perform(backend, filename);
			} catch (Exception e1) {
				DialogBuilder.error(EditorPanel.this, e1.getMessage());
			}
		}
	}

	private class SaveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!backend.fileIsLoaded()) {
				DialogBuilder.noFileLoaded(EditorPanel.this);
				return;
			}

			try {
				EditorAction.SAVE.perform(backend);
			} catch (Exception e1) {
				DialogBuilder.error(EditorPanel.this, e1.getMessage());
			}
		}
	}
}
