package alexman.shortcuts.kbsimulator;

/**
 * TODO
 *
 * @author Alex Mandelias
 */
public interface KeyboardSimulator {

	/**
	 * Emulates pressing the key associated with the given key code.
	 *
	 * @param keycode a valid Virtual Key Code of the KeyEvent class
	 */
	void keyPress(int keycode);

	/**
	 * Emulates releasing the key associated with the given key code.
	 *
	 * @param keycode a valid Virtual Key Code of the KeyEvent class
	 */
	void keyRelease(int keycode);
}
