abstract class MatrixOperations {
  int availableThreads = Runtime.getRuntime().availableProcessors();

  private int rows;
  private int columns;
  private long[] elements;

  MatrixOperations(int numRows, int numColumns, long[] data) {
    rows = numRows;
    columns = numColumns;
    elements = data;
  }

  MatrixOperations() {}

  int getRows() {
    return rows;
  }

  int getColumns() {
    return columns;
  }

  long[] getElements() {
    return elements;
  }

  boolean isMatrixEmpty() {
    return elements == null;
  }

  void displayMatrix() {
    for (int i = 0; i < elements.length; ++i) {
      if (i != 0) {
        if (i % columns == 0) {
          System.out.println();
        } else {
          System.out.print(", ");
        }
      }

      long value = elements[i];
      int numDecimals = 0;

      while (value >= 10 || value <= -10) {
        numDecimals += 1;
        value /= 10;
      }

      String formattingSpaces = "";
      for (int j = numDecimals; j < 5; ++j) {
        formattingSpaces += " ";
      }
      if (value >= 0) {
        formattingSpaces += " ";
      }

      System.out.print(formattingSpaces);
      System.out.print(elements[i]);
    }

    System.out.println();
    System.out.println();
  }

  void printForParsing() {
    for (int i = 0; i < elements.length; ++i) {
      if (i != 0) {
        if (i % columns == 0) {
          System.out.println();
        } else {
          System.out.print(", ");
        }
      }

      System.out.print(elements[i]);
    }

    System.out.println();
    System.out.println();
  }

  abstract MatrixOperations add(MatrixOperations other);
  abstract MatrixOperations subtract(MatrixOperations other);
  abstract MatrixOperations multiplyWith(MatrixOperations other);

  MatrixOperations multiplyWith(MatrixOperations other, boolean useMultithreading) {
    if (!useMultithreading) {
      availableThreads = 0;
    }

    return this.multiplyWith(other);
  }
}
