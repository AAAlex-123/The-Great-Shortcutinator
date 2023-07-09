package alexman.shortcuts.app.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.Shortcut;
import alexman.shortcuts.shortcut.model.ShortcutModel;
import alexman.undo.UndoableHistory;
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

	final IShortcutModel sm;
	private final Optional<IShortcutFormatter> sf;
	private String lastLoadedFile;
	private final Supplier<String> userDir = () -> System.getProperty("user.dir");
	private final UndoableHistory<EditorCommand> history = new UndoableHistory<>();

	public EditorPanel(String file, IShortcutFormatter sf)
	        throws FileNotFoundException, IOException {
		this(new ShortcutModel(sf), null, false);

		loadFile(file);
	}

	public EditorPanel(ShortcutModel sm) {
		this(sm, null, true);
	}

	private EditorPanel(ShortcutModel sm, IShortcutFormatter sf, boolean loadEnabled) {
		super(new BorderLayout());
		this.sm = sm;
		this.sf = Optional.ofNullable(sf);

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

	private void loadFile(String filename) throws IOException {
		try (Reader reader = new FileReader(filename)) {
			sm.load(reader);
		}
		lastLoadedFile = filename;
		loadedFile.setText(filename);
		history.clear();
	}

	private void saveFile() throws IOException {
		saveFile(lastLoadedFile);
	}

	private void saveFile(String filename) throws IOException {
		try (Writer writer = new FileWriter(filename)) {
			sm.store(writer);
		}
	}

	private class ShortcutCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Shortcut selected = (Shortcut) value;
			setText(sf.orElse(sm.getFormatter()).format(selected));
			return this;
		}
	}

	private class LoadActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser jfc = new JFileChooser(userDir.get());
				int rv = jfc.showOpenDialog(EditorPanel.this);
				if (rv == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					String abs = file.getAbsolutePath();
					loadFile(abs);
					lastLoadedFile = abs;
					loadedFile.setText(abs);
				} else {
					// load cancelled
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
			} catch (IOException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
			}
		}
	}

	private class AddActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Requirements reqs = new Requirements();
			reqs.add("Name", StringType.ANY);
			reqs.add("Shortcut", StringType.ANY);
			reqs.fulfillWithDialog(null, "Add a new Shortcut");

			Shortcut s = new Shortcut((String) reqs.getValue("Name"), (String) reqs.getValue("Shortcut"));
			EditorCommand command = new AddShortcut(EditorPanel.this, s);
			command.execute();
			history.add(command);
		}
	}

	private class RemoveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Shortcut selected = shortcutList.getSelectedValue();
			if (selected != null) {
				EditorCommand command = new RemoveShortcut(EditorPanel.this, selected);
				command.execute();
				history.add(command);
			}
		}
	}

	private class UndoActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			history.undo();
		}
	}

	private class RedoActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			history.redo();
		}
	}

	private class ResetActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				loadFile(lastLoadedFile);
				history.clear();
			} catch (IOException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
			}
		}
	}

	private class SaveAsActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser jfc = new JFileChooser(userDir.get());
				int rv = jfc.showSaveDialog(EditorPanel.this);
				if (rv == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					String abs = file.getAbsolutePath();
					saveFile(abs);
				} else {
					// load cancelled
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
			} catch (IOException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
			}
		}
	}

	private class SaveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				saveFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
			}
		}
	}
}
