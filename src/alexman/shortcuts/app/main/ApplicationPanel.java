package alexman.shortcuts.app.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.app.editor.Editor;
import alexman.shortcuts.shortcut.IShortcutFormatter;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
class ApplicationPanel extends JPanel {

	private final JPanel top, topLeft, topRight, shortcutPanel;
	private final JButton load, reload, edit;
	private final JLabel loadedFile;

	private final IShortcutModel sm;
	private final IShortcutFormatter sf;
	private final InputProcessor ip;
	private String lastLoadedFile;
	private final Supplier<String> userDir = () -> System.getProperty("user.dir");

	public ApplicationPanel(IShortcutModel sm, IShortcutFormatter sf, InputProcessor ip) {
		super(new BorderLayout());
		this.sm = sm;
		this.sf = sf;
		this.ip = ip;

		top = new JPanel(new BorderLayout());

		topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		load = new JButton("Load");
		load.addActionListener(new LoadActionListener());
		loadedFile = new JLabel("--- Click 'Load' to load a File ---");
		topLeft.add(load);
		topLeft.add(loadedFile);

		topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		reload = new JButton("Reload");
		reload.addActionListener(new RelaodActionListener());
		edit = new JButton("Edit");
		edit.addActionListener(new EditActionListener());
		topRight.add(reload);
		topRight.add(edit);

		top.add(topLeft, BorderLayout.CENTER);
		top.add(topRight, BorderLayout.EAST);
		this.add(top, BorderLayout.NORTH);

		shortcutPanel = new JPanel();
		shortcutPanel.setLayout(new GridLayout(-1, 1));
		add(shortcutPanel);
	}

	private void reloadShortcuts() {
		try {
			loadFile(lastLoadedFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			// TODO Auto-generated catch block
		}

		refresh();
	}

	private void refresh() {
		shortcutPanel.removeAll();

		for (Shortcut shortcut : sm.getShortcuts()) {
			shortcutPanel.add(createPanelForShortcut(shortcut));
		}

		revalidate();
	}

	private JPanel createPanelForShortcut(Shortcut shortcut) {
		JPanel panel = new JPanel();
		JButton button = new JButton(shortcut.getAction());
		button.addActionListener(e -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			ip.process(shortcut.getKeySequence());
		});
		panel.add(button);
		return panel;
	}

	private class LoadActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				JFileChooser jfc = new JFileChooser(userDir.get());
				int rv = jfc.showOpenDialog(ApplicationPanel.this);
				if (rv == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					String abs = file.getAbsolutePath();
					loadFile(abs);
					lastLoadedFile = abs;
					loadedFile.setText(abs);
					refresh();
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

	private class RelaodActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (lastLoadedFile == null) {
				JOptionPane.showMessageDialog(ApplicationPanel.this,
				        "Click 'Load' to load a file.", "No file has been loaded",
				        JOptionPane.INFORMATION_MESSAGE);
			} else {
				reloadShortcuts();
			}
		}
	}

	private class EditActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (lastLoadedFile == null) {
					JOptionPane.showMessageDialog(ApplicationPanel.this,
					        "Click 'Load' to load a file.", "No file has been loaded",
					        JOptionPane.INFORMATION_MESSAGE);
				} else {
					Editor.main(new String[] { lastLoadedFile });
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(ApplicationPanel.this,
				        "Internal error; Please contact the developer",
				        "Unexpected Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadFile(String filename) throws IOException {
		try (Reader reader = new FileReader(filename)) {
			sm.load(reader, this.sf);
			lastLoadedFile = filename;
		}
	}
}
