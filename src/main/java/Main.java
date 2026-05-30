import java.io.File;
import java.io.IOException;
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
                String echo = cmd.replace("echo ", "");
                if (echo.contains("'")) {
                    System.out.println(echo.substring(echo.indexOf("'")+1, echo.lastIndexOf("'")));
                }else
                    System.out.println(echo.trim());
            } else if (cmd.startsWith("type ")) {
                executeTypeCommand(cmd);
            } else if ("pwd".equals(cmd)) {
                System.out.println(System.getProperty("user.dir"));
            } else if (cmd.startsWith("cd ")) {
                String newPath = cmd.split(" ")[1];
                Path otherPath;
                if("~".equals(newPath))
                    otherPath = Path.of(System.getenv("HOME"));
                else
                    otherPath = getNewPath(Path.of(newPath));
                if(Files.exists(otherPath)) {
                    System.setProperty("user.dir", otherPath.toString());
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

    private static Path getNewPath(Path other) {
        if(other.isAbsolute())
            return other;
        Path currentPath = Path.of(System.getProperty("user.dir"));
        return currentPath.resolve(other).normalize();
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
