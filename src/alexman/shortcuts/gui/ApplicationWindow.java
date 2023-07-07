package alexman.shortcuts.gui;

import java.awt.GridLayout;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.shortcut.model.IShortcutModel;
import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class ApplicationWindow extends JFrame {

	private final IShortcutModel shortcutModel;
	private final InputProcessor inputProcessor;

	private final JPanel shortcutPanel;

	public ApplicationWindow(IShortcutModel shortcutModel, InputProcessor inputProcessor) {
		super("The Great Shortcutinator");
		this.shortcutModel = shortcutModel;
		this.inputProcessor = inputProcessor;

		JPanel main = new JPanel();

		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(e -> reloadShortcuts());
		main.add(refresh);

		shortcutPanel = new JPanel();
		shortcutPanel.setLayout(new GridLayout(-1, 1));
		main.add(shortcutPanel);

		add(main);

		reloadShortcuts();

		setSize(800, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
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
