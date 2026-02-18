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
		// Your code goes here
        return "";
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
    
    // create a list to test
    List L = new List();

    // Suppose letters: a, b, c, c
    L.update('a');
    L.update('b');
    L.update('c');
    L.update('c');

    LanguageModel model = new LanguageModel(1, 42);
    model.calculateProbabilities(L);

    // create an array to hold L's data
    CharData[] arr = L.toArray();

    // test if the probabilities are correct
    for (int i = 0; i < arr.length; i++) {
        System.out.println(
            arr[i].chr + 
            " p=" + arr[i].p + 
            " cp=" + arr[i].cp
        );
    }

    // test that random logic works well
    for (int i = 0; i < 20; i++) {
        System.out.print(model.getRandomChar(L));
    }
    System.out.println();

    int N = 100000;
    int a=0,b=0,c=0;

    for (int i=0; i<N; i++) {
        char r = model.getRandomChar(L);
        if (r=='a') a++;
        else if (r=='b') b++;
        else if (r=='c') c++;
    }

    System.out.println("a%=" + (a/(double)N));
    System.out.println("b%=" + (b/(double)N));
    System.out.println("c%=" + (c/(double)N));

    double pa = a/(double)N;
    double pb = b/(double)N;
    double pc = c/(double)N;

    if (Math.abs(pa-0.25) < 0.02 && Math.abs(pb-0.25) < 0.02 && Math.abs(pc-0.5) < 0.02) {
        System.out.println("Random sampling test: PASSED");
    } else {
        System.out.println("Random sampling test: FAILED");
    }

    List L2 = new List();

    L2.update('x');
    for (int i = 0; i < 99; i++) {
        L2.update('y');
    }

    model.calculateProbabilities(L2);

    int xCount = 0;
    int yCount = 0;
    int N2 = 200000;

    for (int i = 0; i < N2; i++) {
        char r = model.getRandomChar(L2);
        if (r == 'x') xCount++;
        else if (r == 'y') yCount++;
    }

    System.out.println("x%=" + (xCount/(double)N2));
    System.out.println("y%=" + (yCount/(double)N2));

    double l2a = xCount/(double)N2;
    double l2b = yCount/(double)N2;

    if (Math.abs(l2a-0.01) < 0.02 && Math.abs(l2b-0.99) < 0.02) {
        System.out.println("Random sampling test: PASSED");
    } else {
        System.out.println("Random sampling test: FAILED");
    }

}
    
}
