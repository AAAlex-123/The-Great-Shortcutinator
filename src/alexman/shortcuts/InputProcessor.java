package alexman.shortcuts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alexman.shortcuts.kbsimulator.KeyboardSimulator;
import alexman.shortcuts.shortcut.model.Shortcut;

/**
 * TODO
 *
 *
 * @author Alex Mandelias
 */
public class InputProcessor {

	private final Set<KeyboardSimulator> kbds = new HashSet<>();

	public InputProcessor(KeyboardSimulator... keyboardSimulator) {
		Arrays.asList(keyboardSimulator).forEach(kbds::add);
	}

	public boolean add(KeyboardSimulator keyboardSimulator) {
		return kbds.add(keyboardSimulator);
	}

	public boolean remove(KeyboardSimulator keyboardSimulator) {
		return kbds.remove(keyboardSimulator);
	}

	public void process(Shortcut shortcut) {
		process(shortcut.getKeySequence());
	}

	public void process(List<List<Integer>> keySequence) {
		for (List<Integer> word : keySequence) {
			processWord(word);
		}
	}

	public void processWord(List<Integer> word) {

		// press in order
		for (int i = 0; i < word.size(); i++) {
			Integer token = word.get(i);
			kbds.forEach(kbd -> kbd.keyPress(token));
		}

		// release in reverse order
		for (int i = word.size() - 1; i >= 0; i--) {
			Integer token = word.get(i);
			kbds.forEach(kbd -> kbd.keyRelease(token));
		}
	}
}
