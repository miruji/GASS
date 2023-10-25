package gass.io.log;

public class Log {
    public static boolean debugStackTraceFlag = false;
    public static String getStackTraceCallInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            String className = element.getClassName();
            String methodName = element.getMethodName();
            if (!className.equals(Log.class.getName()) && !methodName.equals("getStackTraceCallInfo")) {
                return className + ":" + methodName;
            }
        }
        return "unknown";
    }
    private boolean containsFlag(LogFlag flag, LogFlag[] flags) {
        for (LogFlag f : flags)
            if (f == flag)
                return true;

        return false;
    }
    public Log(LogType type, String message, LogFlag... flags) {
        StringBuilder result = new StringBuilder("[gass]["+type.toString()+"]: ");

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
