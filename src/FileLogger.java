import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

class FileLogger {
    private String path;
    private String errors;

    FileLogger(String pathToLog) {
        path = pathToLog;
    }

    void addError(String error) {
        errors += error + "/n";
    }

    void addError(List<String> error) {
        error.forEach(e -> errors += e + "/n");
    }

    void flushToFile() {
        if (!errors.isEmpty()) {
            try {
                FileUtils.writeStringToFile(new File(path), errors, "UTF8");
                errors = "";
            } catch (java.io.IOException ex) {
                System.out.println("Could not write to file with name: " + path);
            }
        }
    }
}
