class TimeRecorder {
  private long startTimestamp = 0;
  private long endTimestamp = 0;

  void begin() {
    startTimestamp = System.currentTimeMillis();
  }

  void finish() {
    if (startTimestamp == 0) {
      System.err.println("TimeRecorder.begin() must be invoked prior to TimeRecorder.finish()");
    }

    endTimestamp = System.currentTimeMillis();
  }

  long getElapsedTime() {
    if (startTimestamp == 0 || endTimestamp == 0) {
      System.err.println("TimeRecorder.begin() and TimeRecorder.finish() must be invoked before TimeRecorder.getElapsedTime()");
      return -1;
    }

    return endTimestamp - startTimestamp;
  }
}
