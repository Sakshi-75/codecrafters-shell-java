import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    static List<String> builtin = List.of("echo", "exit", "type", "pwd", "cd");

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("$ ");
            String cmd = scanner.nextLine();
            if("exit".equals(cmd))
                break;
            if (cmd.startsWith("echo ")) {
                System.out.println(cmd.replace("echo ", ""));
            } else if (cmd.startsWith("type ")) {
                executeTypeCommand(cmd);
            } else if ("pwd".equals(cmd)) {
                System.out.println(Paths.get("").toAbsolutePath());
            } else if (cmd.startsWith("cd ")) {
                String newPath = cmd.split(" ")[1];
                System.out.println(newPath);
                if(Files.exists(Path.of(newPath))) {
                    System.out.println("Path exists");
                }
                else
                    System.out.println("cd: "+newPath+": No such file or directory");
            } else {
                String[] input = cmd.split(" ");
                if(executablePath(input[0])!=null)
                    executeCustomCommand(input);
                else
                    System.out.println(cmd + ": command not found");
            }
        }
    }

    private static void executeTypeCommand(String cmd) {
        String val = cmd.substring(5).trim();
        if(builtin.contains(val)) {
            System.out.println(val+" is a shell builtin");
        } else {
            String result = executablePath(val);
            if(result!=null)
                System.out.println(val+" is "+result);
            else
                System.out.println(val + ": not found");
        }
    }

    private static String executablePath(String val) {
        String[] dirs = System.getenv("PATH").split(File.pathSeparator);
        for(String dir: dirs) {
            String temp = dir+File.separator+val;
            File file = new File(temp);
            if (file.canExecute()) {
                return (file.getPath());
            }
        }
        return null;
    }

    private static void executeCustomCommand(String[] input) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(input);
        Process process = processBuilder.start();
        process.getInputStream().transferTo(System.out);
        process.waitFor();
    }
}
