class MatrixMultiplyOperation {
  static void perform(String[] arguments, boolean isSimple) {
    if (arguments.length < 1 || (arguments.length == 1 && !arguments[0].contains("json"))) {
      System.err.println("Usage: java -cp \"out;libs/*\" MatrixOperation <path-to.json> [-v]");
      System.exit(1);
    }

    boolean verbose = false;
    String filePath = arguments[0];

    if (arguments.length > 1 && (arguments[0].equals("-v") || arguments[0].equals("--verbose")
      || arguments[1].equals("-v") || arguments[1].equals("--verbose"))) {
      verbose = true;
    }
    if (arguments.length > 1 && (arguments[0].equals("-v") || arguments[0].equals("--verbose"))) {
      filePath = arguments[1];
    }

    TimeRecorder timer1 = new TimeRecorder();
    timer1.begin();
    MatrixOperations[] matrices = MatrixBuilder.generateMatrices(filePath, isSimple);
    timer1.finish();
    System.out.println("Time spent generating matrices: " + timer1.getElapsedTime() + "ms");
    if (verbose) {
      matrices[0].printForParsing();
      matrices[1].printForParsing();
    }

    if (matrices[0].getColumns() != matrices[1].getRows()) {
      System.out.println("omitting matrix1 * matrix2 is not possible");
    } else {
      TimeRecorder timer2 = new TimeRecorder();
      System.out.println("calculating matrix1 * matrix2");
      timer2.begin();
      MatrixOperations result1 = matrices[0].multiplyWith(matrices[1]);
      timer2.finish();
      System.out.println("Time spent computing matrix1 * matrix2: " + timer2.getElapsedTime() + "milliseconds");
      if (verbose) {
        result1.printForParsing();
      }
    }

    if (matrices[1].getColumns() != matrices[0].getRows()) {
      System.out.println("omitting matrix2 * matrix1 is not possible");
    } else if (matrices[0].getRows() == matrices[1].getRows() && matrices[0].getColumns() == matrices[1].getColumns()) {
      System.out.println("omitting matrix2 * matrix1 is equal to matrix1 * matrix2");
    } else {
      TimeRecorder timer3 = new TimeRecorder();
      System.out.println("calculating matrix2 * matrix1");
      timer3.begin();
      MatrixOperations result2 = matrices[1].multiplyWith(matrices[0]);
      timer3.finish();
      System.out.println("Time spent computing matrix2 * matrix1: " + timer3.getElapsedTime() + "milliseconds");
      if (verbose) {
        result2.printForParsing();
      }
    }
  }
}
