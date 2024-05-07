package gass.io.log;

public class Log {
    public static boolean debugStackTraceFlag = false;
    public static String getStackTraceCallInfo() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            final StackTraceElement element = stackTrace[i];
            final String className = element.getClassName();
            final String methodName = element.getMethodName();
            if (!className.equals(Log.class.getName()) && !methodName.equals("getStackTraceCallInfo"))
                return className + ":" + methodName;
        }
        return "unknown";
    }
    private boolean containsFlag(final LogFlag flag, final LogFlag[] flags) {
        for (final LogFlag f : flags)
            if (f == flag) return true;
        return false;
    }
    public Log(final LogType type, final String message, final LogFlag... flags) {
        final StringBuilder result = new StringBuilder("[gass]["+type.toString()+"]: ");

        if (containsFlag(LogFlag.stackTraceCallInfo, flags) || debugStackTraceFlag)
            result.append('[').append(getStackTraceCallInfo()).append("]: ");

        result.append(message);

        //
        if (type == LogType.error || containsFlag(LogFlag.strictExit, flags)) {
            System.err.println(result);
            System.exit(1);
        } else {
            System.out.println(result);
        }
    }
}
