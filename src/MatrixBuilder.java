import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

class MatrixBuilder {
  static int MATRIX_VALUES_BOUND = 1000000;

  static class JSONMatrixGenerator implements Runnable {
    int rows;
    int cols;
    long seed;
    boolean isSimple;
    MatrixOperations resultMatrix;

    JSONMatrixGenerator(int numRows, int numCols, long randomSeed, boolean simple) {
      rows = numRows;
      cols = numCols;
      seed = randomSeed;
      isSimple = simple;
    }

    MatrixOperations getGeneratedMatrix() {
      return resultMatrix;
    }

    public void run() {
      int size = rows * cols;
      long[] data = new long[size];
      Random random = new Random(seed);

      for (int i = 0; i < size; ++i) {
        data[i] = random.nextInt(MATRIX_VALUES_BOUND * 2) - MATRIX_VALUES_BOUND;
      }

      if (isSimple) {
        resultMatrix = new MultiplyMatrix(rows, cols, data);
      } else {
      //  resultMatrix = new AdvancedMatrix(rows, cols, data);
      }
    }
  }

  static MatrixOperations[] generateMatrices(String fileName, boolean simple) {
    String jsonData = "";
    MatrixOperations[] matrices = new MatrixOperations[2];

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }

      jsonData = sb.toString();
    } catch (Exception e) {
      System.err.println("The 2 matrices should be stored in a JSON file, which path should be passed as an argument");
      System.exit(1);
    }

    try {
      JSONObject jsonObj = new JSONObject(jsonData);
      JSONMatrixGenerator[] matrixGenerators = new JSONMatrixGenerator[2];
      Thread[] threads = new Thread[2];

      int rows1 = jsonObj.getInt("rows1");
      int cols1 = jsonObj.getInt("cols1");
      int rows2 = jsonObj.getInt("rows2");
      int cols2 = jsonObj.getInt("cols2");
      long seed = jsonObj.getInt("seed");

      if (rows1 < 1 || cols1 < 1 || rows2 < 1 || cols2 < 1) {
        System.err.print("The \"rows\" and \"cols\" properties of the JSON files must have a value of at least 1");
        System.exit(1);
      }
      if (cols1 != rows2 && cols2 != rows1) {
        System.err.print("One of the two matrices' \"cols\" must match the other matrix's \"rows\""
          + " for multiplication to be feasible");
        System.exit(1);
      }

      try {
        matrixGenerators[0] = new JSONMatrixGenerator(rows1, cols1, seed, simple);
        matrixGenerators[1] = new JSONMatrixGenerator(rows2, cols2, seed, simple);
        threads[0] = new Thread(matrixGenerators[0]);
        threads[1] = new Thread(matrixGenerators[1]);
        threads[0].start();
        threads[1].start();
        threads[0].join();
        threads[1].join();
      } catch (InterruptedException e) {
        System.err.println("Thread tasked with parsing matrices was unexpectedly interrupted");
      }

      matrices[0] = matrixGenerators[0].getGeneratedMatrix();
      matrices[1] = matrixGenerators[1].getGeneratedMatrix();
    } catch (JSONException e) {
      System.err.println("The JSON file should contain 3 properties: \"rows\", \"cols\", and \"seed\""
        + " (the latter being the seed from which the random numbers are generated to fill the matrices)");
      System.exit(1);
    }

    return matrices;
  }
}
