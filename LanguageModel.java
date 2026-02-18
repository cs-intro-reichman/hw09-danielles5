import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		String window = "";
        char c;
        In in = new In(fileName);

        // Reads just enough characters to form the first window
        for (int i = 0; i < windowLength; i++) {
            char nextChar = in.readChar();
            window += nextChar;
        }

        // Processes the entire text, one character at a time
        while (!in.isEmpty()) {

        // Reads the next character (after the window)
        c = in.readChar();

        // Checks if the window is already in the map
        // code: tries to get the list of this window from the map.
        // Let’s call the retrieved list “probs” (it may be null)
        List probs = CharDataMap.get(window);

        // If the window was not found in the map
        // Creates a new empty list, and adds (window,list) to the map
        if (probs == null) {
            List newWinList = new List();
            CharDataMap.put(window, newWinList);
            probs = newWinList;
        }   

        // Calculates the counts of the current character.
        probs.update(c);

        // Advances the window: adds c to the window’s end, and deletes the
        // window's first character.
        window = window.substring(1) + c;
    }
        // The entire file has been processed, and all the characters have been counted.
        // Proceeds to compute and set the p and cp fields of all the CharData objects
        // in each linked list in the map.
        for (List probs : CharDataMap.values())
        calculateProbabilities(probs);
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {				
		CharData[] arr = probs.toArray();
        int totalChrs = 0;

        for (int i = 0; i < arr.length; i++) {
            totalChrs += arr[i].count;
        }

        double cumulative = 0.0;

        for (int i = 0; i < arr.length; i++) {
            arr[i].p = arr[i].count / (double)totalChrs;
            arr[i].cp = arr[i].p + cumulative;
            cumulative += arr[i].p;
        }
	}

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
		double random = randomGenerator.nextDouble();
        CharData[] arr = probs.toArray();

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].cp > random) {
                return arr[i].chr;
            }
        }
		return ' ';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Account for initialText being shorter than the window requested
        if (initialText.length() < windowLength) {
            return initialText;
        }

        // Otherwise, start the generation (starting with what we were given, initialText)
        String result = initialText;

        while (result.length() < textLength) {
        // Start with the correct window length
        String currentWindow = result.substring(result.length() - windowLength);

        // Look for corresponding probability in the HashMap
        List probs = CharDataMap.get(currentWindow);

        // Account for the window not existing in the HashMap / reaching the end
        if (probs == null) break;

        // If the window exists, get a random char and add it to result
        char nextChar = getRandomChar(probs);
        result += nextChar;
        }
        return result;
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
       int windowLength = Integer.parseInt(args[0]);
       String initialText = args[1];
       int generatedTextLength = Integer.parseInt(args[2]);
       Boolean randomGeneration = args[3].equals("random");
       String fileName = args[4];

       // Create the language model object
       LanguageModel Im;
       if (randomGeneration) {
        Im = new LanguageModel(windowLength);
       } else {
        Im = new LanguageModel(windowLength, 20);

        // Trains the model creating the map
        Im.train(fileName);

        // Generates the text, and prints it
        System.out.println(Im.generate(initialText, generatedTextLength));
       }
    
    }
}
