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
    

