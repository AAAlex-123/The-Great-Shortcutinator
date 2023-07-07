package alexman.shortcuts.app.prototype;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import alexman.shortcuts.InputProcessor;
import alexman.shortcuts.kbsimulator.RobotAdapter;
import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class Main extends JFrame {

	private static final InputProcessor inputProcessor = new InputProcessor(new RobotAdapter());

	public Main() {
		setLayout(new GridLayout(-1, 1));

		JPanel insertPanel = new JPanel();
		insertPanel.setLayout(new GridLayout(1, -1));
		JButton addNewButtonButton = new JButton("Add");
		JTextField newButtonName = new JTextField();
		JTextField newButtonShortcut = new JTextField();
		addNewButtonButton.addActionListener(e -> {
			addButton(newButtonName.getText(), newButtonShortcut.getText());
			newButtonName.setText("");
			newButtonShortcut.setText("");
			revalidate();
		});
		insertPanel.add(addNewButtonButton);
		insertPanel.add(newButtonName);
		insertPanel.add(newButtonShortcut);

		add(insertPanel);

		addButton("Export", "ALT+CONTROL+E");
		addButton("Blend Make", "ALT+CONTROL+B");

		addButton("New Tab", "CONTROL+T");
		addButton("New Window", "CONTROL+N");
	}

	private void addButton(String name, String shortcut) {
		addButton(new Shortcut(name, shortcut));
	}

	private void addButton(Shortcut shortcut) {
		JButton b = new JButton(shortcut.getAction());

		b.addActionListener(e -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			System.out.println(shortcut.getAction());

			inputProcessor.process(shortcut.getKeySequence());
		});

		this.add(b);
	}

	public static void main(String[] args) {
		JFrame frame = new Main();
		frame.setTitle("TheGreatShortcutinator");
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
