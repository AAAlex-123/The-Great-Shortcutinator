package alexman.shortcuts.prototype;

import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class Main extends JFrame {

	private static final Robot r;
	static {
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static final Map<String, Integer> VK_MAP = new HashMap<>();
	static {
		VK_MAP.put("ALT", KeyEvent.VK_ALT);
		VK_MAP.put("CONTROL", KeyEvent.VK_CONTROL);
		VK_MAP.put("SHIFT", KeyEvent.VK_SHIFT);
		VK_MAP.put("ESCAPE", KeyEvent.VK_ESCAPE);
	}

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

	private void addButton(String name, String key) {
		JButton b = new JButton(name);

		b.addActionListener((e) -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			System.out.println(name);
			// ALT+CONTROL+E

			String[] keys = key.split("\\+");
			// ["ALT", "CONTROL", "E"]

			for (int i = 0; i < keys.length; i++) {
				System.out.printf("pressing:  %s%n", keys[i]);
				r.keyPress(VK_MAP.getOrDefault(keys[i], (int) (keys[i].charAt(0))));
			}

			for (int i = keys.length - 1; i >= 0; i--) {
				System.out.printf("releasing: %s%n", keys[i]);
				r.keyRelease(VK_MAP.getOrDefault(keys[i], (int) keys[i].charAt(0)));
			}
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
