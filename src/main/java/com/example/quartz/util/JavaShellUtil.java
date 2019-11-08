package com.example.quartz.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author 逸之君
 */
public class JavaShellUtil {

    public static int ExecCommand(String command) {
        int retCode = 0;
        try {
            String sh = "/bin/sh " + command;
            Process process = Runtime.getRuntime().exec(sh, null, null);
            retCode = process.waitFor();
            ExecOutput(process);
        } catch (Exception e) {
            retCode = -1;
        }
        return retCode;
    }

    public static boolean ExecOutput(Process process) throws Exception {
        if (process == null) {
            return false;
        } else {
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            String output = "";
            while ((line = input.readLine()) != null) {
                output += line + "\n";
            }
            input.close();
            ir.close();
            if (output.length() > 0) {
            }
        }
        return true;
    }

}


