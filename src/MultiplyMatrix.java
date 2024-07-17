class MultiplyMatrix extends MatrixOperations{
  private MatrixOperations matrix1;
  private MatrixOperations matrix2;
  private int resultHeight;
  private int resultWidth;
  private long[] resultData;

  MultiplyMatrix(int rows, int cols, long[] data) {
    super(rows, cols, data);
  }

  class SectionCalculator implements Runnable {
    int startIdx;
    int endIdx;

    SectionCalculator(int startIndex, int endIndex) {
      startIdx = startIndex;
      endIdx = endIndex;
    }

    public void run() {
      for (int i = startIdx; i < endIdx; ++i) {
        for (int k = 0; k < matrix1.getColumns(); ++k) {
          for (int j = 0; j < resultWidth; ++j) {
            resultData[i * resultWidth + j] +=
              matrix1.getElements()[i * matrix1.getColumns() + k] * matrix2.getElements()[k * resultWidth + j];
          }
        }
      }
    }
  }

  MatrixOperations addMatrices(MatrixOperations matrix2) {
    MatrixOperations matrix1 = this;
    long[] resultArray = new long[matrix1.getRows() * matrix1.getColumns()];

    for (int i = 0; i < matrix1.getRows(); ++i) {
      for (int j = 0; j < matrix1.getColumns(); ++j) {
        int index = i * matrix1.getColumns() + j;

        resultArray[index] = matrix1.getElements()[index] + matrix2.getElements()[index];
      }
    }

    return new MultiplyMatrix(matrix1.getRows(), matrix1.getColumns(), resultArray);  
  }

  MatrixOperations subtractMatrices(MatrixOperations matrix2) {
    MatrixOperations matrix1 = this;
    long[] resultArray = new long[matrix1.getRows() * matrix1.getColumns()];

    for (int i = 0; i < matrix1.getRows(); ++i) {
      for (int j = 0; j < matrix1.getColumns(); ++j) {
        int index = i * matrix1.getColumns() + j;

        resultArray[index] = matrix1.getElements()[index] - matrix2.getElements()[index];
      }
    }

    return new MultiplyMatrix(matrix1.getRows(), matrix1.getColumns(), resultArray);
  }

  private void performParallelMultiplication(int numThreads) {
    Thread[] threads = new Thread[numThreads];
    int sectionSize = (resultHeight / numThreads) + (resultHeight % numThreads != 0 ? 1 : 0);

    for (int i = 0; i < numThreads; ++i) {
      int start = i * sectionSize;
      int end = (i == numThreads - 1 ? resultHeight : (i + 1) * sectionSize);

      threads[i] = new Thread(new SectionCalculator(start, end));
      threads[i].start();
    }

    try {
      for (int i = 0; i < numThreads; ++i) {
        threads[i].join();
      }
    } catch (InterruptedException e) {
      System.err.println("Thread supposed to compute line has been unexpectedly interrupted");
    }
  }

  private void performSequentialMultiplication() {
    new SectionCalculator(0, resultHeight).run();
  }

  MatrixOperations multiplyMatrices(MatrixOperations matrix_2) {
    matrix1 = this;
    matrix2 = matrix_2;
    resultHeight = matrix1.getRows();
    resultWidth = matrix2.getColumns();
    resultData = new long[resultHeight * resultWidth];

    int numThreads = resultHeight > availableThreads ? availableThreads : resultHeight;

    if (numThreads > 1) {
      performParallelMultiplication(numThreads);
    } else {
      performSequentialMultiplication();
    }

    return new MultiplyMatrix(resultHeight, resultWidth, resultData);
  }
}
