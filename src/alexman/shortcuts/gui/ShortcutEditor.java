package alexman.shortcuts.gui;

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import alexman.shortcuts.shortcut.model.Shortcut;
import alexman.shortcuts.shortcut.model.ShortcutModel;
import alexman.shortcuts.shortcut.IShortcutFormatter;
import requirement.requirements.StringType;
import requirement.util.Requirements;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class ShortcutEditor extends JPanel {

	private JButton load, reset, save, add, remove;
	private JLabel loadedFile;
	private JPanel top, main, right, bottom;
	private final JList<Shortcut> shortcutList;

	private final ShortcutModel sm = new ShortcutModel();
	private final IShortcutFormatter sf;
	private String lastLoadedFile;
	private final Supplier<String> userDir = () -> System.getProperty("user.dir");

	public ShortcutEditor(IShortcutFormatter sf) {
		super(new BorderLayout());
		this.sf = sf;

		top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		load = new JButton("Load");
		load.addActionListener(new LoadActionListener());
		loadedFile = new JLabel("--- Click 'Load' to load a File ---");
		top.add(load);
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
		add = new JButton("+"); // TODO: replace with icon
		add.addActionListener(new AddActionListener());
		remove = new JButton("-"); // TODO: replace with icon
		remove.addActionListener(new RemoveActionListener());
		right.add(add);
		right.add(remove);
		this.add(right, BorderLayout.EAST);

		bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		reset = new JButton("Reset");
		reset.addActionListener(new ResetActionListener());
		save = new JButton("Save");
		save.addActionListener(new SaveActionListener());
		bottom.add(reset);
		bottom.add(save);
		this.add(bottom, BorderLayout.SOUTH);
	}

	private void loadFile(String filename) throws IOException {
		try (Reader reader = new FileReader(filename)) {
			sm.load(reader, this.sf);
			lastLoadedFile = filename;
		}
	}

	private void saveFile() throws IOException {
		saveFile(lastLoadedFile);
	}

	private void saveFile(String filename) throws IOException {
		try (Writer writer = new FileWriter(filename)) {
			sm.store(writer, this.sf);
		}
	}

	private class ShortcutCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Shortcut selected = (Shortcut) value;
			setText(sf.format(selected));
			return this;
		}
	}

	private class LoadActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser jfc = new JFileChooser(userDir.get());
				int rv = jfc.showOpenDialog(ShortcutEditor.this);
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
			sm.addShortcut(s);
		}
	}

	private class RemoveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Shortcut selected = shortcutList.getSelectedValue();
			if (selected != null) {
				sm.removeShortcut(selected);
			}
		}
	}

	private class ResetActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				loadFile(lastLoadedFile);
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
				int rv = jfc.showSaveDialog(ShortcutEditor.this);
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
