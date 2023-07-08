package alexman.shortcuts.app.main;

import java.awt.GridLayout;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JPanel;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
class ApplicationPanel extends JPanel {

	private final JPanel shortcutPanel;

	private final IShortcutModel shortcutModel;
	private final InputProcessor inputProcessor;

	public ApplicationPanel(IShortcutModel sm, InputProcessor ip) {
		this.shortcutModel = sm;
		this.inputProcessor = ip;

		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(e -> reloadShortcuts());
		add(refresh);

		shortcutPanel = new JPanel();
		shortcutPanel.setLayout(new GridLayout(-1, 1));
		add(shortcutPanel);

		reloadShortcuts();
	}

	private void reloadShortcuts() {
		shortcutPanel.removeAll();

		try (Reader reader = new FileReader(
		        "C:\\Users\\alexm\\projects\\Java\\TheGreatShortcutinator\\shortcuts.txt")) {
			// TODO: fix with new model
			// shortcutModel.load(reader, '=');
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Shortcut shortcut : shortcutModel.getShortcuts()) {
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
			inputProcessor.process(shortcut.getKeySequence());
		});
		panel.add(button);
		return panel;
	}
}
