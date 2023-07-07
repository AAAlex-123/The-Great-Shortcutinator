package alexman.shortcuts.shortcut.model;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents a series of key presses that perform an action.
 *
 * @author Alex Mandelias
 */
public class Shortcut {

	private final String action;
	private final List<List<Integer>> keySequence;

	/**
	 * Constructs a Shortcut by parsing a String. Calling
	 * {@link #getKeySequenceAsString()} shall return exactly the same String.
	 * <p>
	 * To indicate that pressing 'Alt Shift X' followed by 'J' performs the action
	 * 'Run Java Application', the following Shortcut should be created:
	 * {@code new Shortcut("Run Java Application", "ALT+SHIFT+X J")}.
	 * <p>
	 * For more information about the format of the string, refer to the
	 * {@link Parser#parse(String)} method.
	 *
	 * @param action a description of what the Shortcut will do
	 * @param keySequence the key presses needed to perform the Shortcut as a String
	 *
	 * @throws IllegalArgumentException if any of the individual tokens are invalid.
	 *         For more information, refer to the {@link Parser#isInvalid(String)}
	 *         method.
	 */
	public Shortcut(String action, String keySequence) {
		this(action, Shortcut.Parser.parse(keySequence));
	}

	/**
	 * Constructs a Shortcut by using directly the List of List of Integers
	 * provided. Calling {@link #getKeySequence()} shall return a reference to that
	 * sequence.
	 * <p>
	 * To indicate that pressing 'Alt Shift X' followed by 'J' performs the action
	 * 'Run Java Application', the following Shortcut should be created:
	 * {@code new Shortcut("Run Java Application", List.of(List.of(18, 16, 88), List.of(74)))}.
	 * <p>
	 * For more information about the contents of the list, refer to the
	 * {@link Parser#parse(String)} method.
	 *
	 * @param action a description of what the Shortcut will do
	 * @param keySequence the key presses needed to perform the Shortcut as a List
	 *
	 * @throws IllegalArgumentException if any of the key codes are invalid. For
	 *         more information, refer to the {@link Parser#isInvalid(int)} method.
	 */
	public Shortcut(String action, List<List<Integer>> keySequence) {

		List<Integer> invalidKeyCodes = Parser.getInvalid(keySequence);
		if (invalidKeyCodes.size() > 0) {
			throw new IllegalArgumentException(
			        "The following key codes do not correspond to a Virtual Key Code of the KeyEvent class: "
			                + invalidKeyCodes);
		}

		this.action = action;
		this.keySequence = keySequence;
	}

	/**
	 * Returns this Shortcut's action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Returns this Shortcut's key sequence as a List, each item of which contains
	 * key codes that are to be pressed at the same time. Pressing them at the same
	 * time means pressing them in order and then releasing them in reverse order.
	 * <p>
	 * For more information, refer to the {@link Parser#parse(String)} method.
	 *
	 * @return the key sequence
	 */
	public List<List<Integer>> getKeySequence() {
		return keySequence;
	}

	/**
	 * Returns this Shortcut's key sequence as a formatted string which can be used
	 * to create another Shortcut with the same key sequence as this Shortcut.
	 * <p>
	 * For more information, refer to the {@link Parser#parse(String)} method.
	 *
	 * @return the key sequence
	 */
	public String getKeySequenceAsString() {
		return Shortcut.Parser.format(keySequence);
	}

	@Override
	public String toString() {
		return String.format("Shortcut [action=%s, keySequence=%s]", action,
		        getKeySequenceAsString());
	}

	@Override
	public int hashCode() {
		return Objects.hash(action, keySequence);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Shortcut))
			return false;
		Shortcut other = (Shortcut) obj;
		return Objects.equals(action, other.action) && Objects.equals(keySequence, other.keySequence);
	}

	/**
	 * Defines static methods for parsing strings into lists of lists of key codes
	 * and vice versa. Additional lower level methods are available.
	 *
	 * @author Alex Mandelias
	 */
	public static class Parser {

		private Parser() {
			// don't allow instantiation
		}

		// https://stackoverflow.com/questions/664896/get-the-vk-int-from-an-arbitrary-char-in-java#answer-665267
		private static final Map<String, Integer> keyTextToCode = new HashMap<>();
		private static final Map<Integer, String> keyCodeToText = new HashMap<>();

		// populate both maps with the public static fields of the KeyEvent class
		static {

			// filter "public static final int VK_..." fields
			Predicate<Field> isValidField = field -> {
				int mod = field.getModifiers();
				return Modifier.isPublic(mod) && Modifier.isStatic(mod)
				        && Modifier.isFinal(mod) &&
				        field.getType().equals(int.class) &&
				        field.getName().startsWith("VK_");
			};

			// add the information of the filtered fields into the maps
			Arrays.asList(KeyEvent.class.getDeclaredFields())
			        .stream()
			        .filter(isValidField)
			        .forEach(field -> {
				        String keyText = field.getName().substring(3).toUpperCase(); // strip 'VK_'
				        try {
					        int keyCode = field.getInt(null);
					        keyTextToCode.put(keyText, keyCode);
					        keyCodeToText.put(keyCode, keyText);
				        } catch (IllegalArgumentException | IllegalAccessException e) {
					        throw new RuntimeException(e);
				        }
			        });
		}

		/**
		 * Parses a String to return a valid List of consecutive combinations of key
		 * codes.
		 * <p>
		 * The key sequence defines which keys to press. The combinations of keys are
		 * separated by a single ' ' character. Each combination of keys is a
		 * '+'-separated string consisting of names of the public static fields of the
		 * {@link KeyEvent} class, omitting the "VK_" prefix.
		 * <p>
		 * The resulting List of Lists of Integers, defines which keys to press. Each
		 * individual List contains the consecutive combinations of keys to be pressed.
		 * Each key code of the combination of keys is a valid key code of the
		 * {@link KeyEvent} class.
		 * <p>
		 * For example, parsing the string {@code "ALT+SHIFT+X J"} returns the
		 * equivalent of {@code List.of(List.of(18, 16, 88), List.of(74))}.
		 *
		 * @param keySequence the key presses, which adhere to the above specification
		 *
		 * @return the key codes for the given key sequence, which adhere to the above
		 *         specification
		 *
		 * @throws IllegalArgumentException if any of the tokens of a key combination
		 *         does not correspond to a Virtual Key Code of the KeyEvent class,
		 *         meaning that there isn't a public static field named
		 *         {@code VK_<token>}.
		 */
		public static List<List<Integer>> parse(String keySequence) {

			List<String> invalidTokens = Parser.getInvalid(keySequence);
			if (invalidTokens.size() > 0) {
				throw new IllegalArgumentException(
				        "The following tokens do not correspond to a field of the KeyEvent class that is related to virtual key code: "
				                + invalidTokens);
			}

			return toWordStream(keySequence).map(
			        word -> toTokenStream(word).map(keyTextToCode::get)
			                .toList())
			        .toList();
		}

		/**
		 * Formats the given List of combinations of key codes into a human-readable
		 * String, which, when parsed, returns a List equal to the given one.
		 * <p>
		 * For more information about the contents of the list and about the format of
		 * the string, refer to the {@link Parser#parse(String)} method.
		 *
		 * @param keySequence the key codes to format into a human-readable String.
		 *
		 * @return the human-readable String which describes the given key codes
		 */
		public static String format(List<List<Integer>> keySequence) {

			List<Integer> invalidKeyCodes = Parser.getInvalid(keySequence);
			if (invalidKeyCodes.size() > 0) {
				throw new IllegalArgumentException(
				        "The following key codes do not correspond to a Virtual Key Code of the KeyEvent class: "
				                + invalidKeyCodes);
			}

			return fromWordStream(keySequence.stream().map(
			        wordList -> fromTokenStream(wordList.stream().map(keyCodeToText::get))));
		}

		/**
		 * Returns the invalid tokens of the key sequence.
		 *
		 * @param keySequence the key sequence from which to extract the invalid tokens
		 *
		 * @return a list of the invalid tokens
		 *
		 * @see #isInvalid(String)
		 */
		public static List<String> getInvalid(String keySequence) {
			return toWordStream(keySequence)
			        .flatMap(Parser::toTokenStream)
			        .filter(Parser::isInvalid)
			        .toList();
		}

		/**
		 * Returns the invalid key codes of the key sequence.
		 *
		 * @param keySequence the key sequence from which to extract the invalid key
		 *        codes
		 *
		 * @return a list of the invalid key codes
		 *
		 * @see #isInvalid(int)
		 */
		public static List<Integer> getInvalid(List<List<Integer>> keySequence) {
			return keySequence.stream()
			        .flatMap(List::stream)
			        .filter(Parser::isInvalid)
			        .toList();
		}

		/**
		 * Checks if the given token corresponds to a Virtual Key Code of the KeyEvent
		 * class, meaning that there is a public static field named {@code VK_<token>}.
		 *
		 * @param token the token to check
		 *
		 * @return {@code true} if the token is valid, {@code false} otherwise
		 */
		public static boolean isValid(String token) {
			return keyTextToCode.containsKey(token);
		}

		/**
		 * The logical negation of {@link #isValid(String)}.
		 *
		 * @param token the token to check
		 *
		 * @return {@code true} if the token is invalid, {@code false} otherwise
		 */
		public static boolean isInvalid(String token) {
			return !isValid(token);
		}

		/**
		 * Checks if the given key code corresponds to a Virtual Key Code of the
		 * KeyEvent class, meaning that there is a public static final int field, whose
		 * name starts with {@code VK_}, and whose value is the given key code.
		 *
		 * @param keyCode the key code to check
		 *
		 * @return {@code true} if the key code is valid, {@code false} otherwise
		 */
		public static boolean isValid(int keyCode) {
			return keyCodeToText.containsKey(keyCode);
		}

		/**
		 * The logical negation of {@link #isValid(int)}.
		 *
		 * @param keyCode the key code to check
		 *
		 * @return {@code true} if the key code is invalid, {@code false} otherwise
		 */
		public static boolean isInvalid(int keyCode) {
			return !isValid(keyCode);
		}

		private static final String wordSplitString = " ";
		private static final String tokenSplitString = "+";

		private static Stream<String> toWordStream(String keySequence) {
			return Arrays.asList(keySequence.split(Pattern.quote(wordSplitString))).stream();
		}

		private static Stream<String> toTokenStream(String word) {
			return Arrays.asList(word.split(Pattern.quote(tokenSplitString))).stream();
		}

		private static String fromWordStream(Stream<String> words) {
			return String.join(wordSplitString, words.toList());
		}

		private static String fromTokenStream(Stream<String> tokens) {
			return String.join(tokenSplitString, tokens.toList());
		}
	}
}
