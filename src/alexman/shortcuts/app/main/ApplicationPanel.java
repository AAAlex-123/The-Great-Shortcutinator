package alexman.shortcuts.app.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.app.main.ApplicationBackend.ApplicationAction;
import alexman.shortcuts.app.util.DialogBuilder;
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

	private final ApplicationBackend backend;

	public ApplicationPanel(IShortcutModel sm, InputProcessor ip) {
		super(new BorderLayout());

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

		backend = new ApplicationBackend(sm, ip, (String filename) -> loadedFile.setText(filename));
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
			backend.process(shortcut.getKeySequence());
		});
		panel.add(button);
		return panel;
	}

	private void refreshPanelWithShortcuts() {
		shortcutPanel.removeAll();
		backend.getShortcuts().forEach(s -> shortcutPanel.add(createPanelForShortcut(s)));
		revalidate();
	}

	private class LoadActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser(ApplicationBackend.USER_DIR);
			int rv = jfc.showOpenDialog(ApplicationPanel.this);
			if (rv != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File file = jfc.getSelectedFile();
			String abs = file.getAbsolutePath();

			try {
				ApplicationAction.LOAD.perform(backend, abs);
				ApplicationPanel.this.refreshPanelWithShortcuts();
			} catch (Exception e1) {
				DialogBuilder.error(ApplicationPanel.this, e1.getMessage());
			}
		}
	}

	private class RelaodActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!backend.fileIsLoaded()) {
				DialogBuilder.noFileLoaded(ApplicationPanel.this);
				return;
			}

			try {
				ApplicationAction.RELOAD.perform(backend);
				ApplicationPanel.this.refreshPanelWithShortcuts();
			} catch (Exception e1) {
				DialogBuilder.error(ApplicationPanel.this, e1.getMessage());
			}
		}
	}

	private class EditActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!backend.fileIsLoaded()) {
				DialogBuilder.noFileLoaded(ApplicationPanel.this);
				return;
			}

			try {
				ApplicationAction.EDIT.perform(backend);
			} catch (Exception e1) {
				DialogBuilder.error(ApplicationPanel.this, e1.getMessage());
			}
		}
	}
}
