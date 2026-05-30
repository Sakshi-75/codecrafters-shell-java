import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {

    static List<String> builtin = List.of("echo", "exit", "type", "pwd", "cd");

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("$ ");
            String cmd = scanner.nextLine();
            List<String> input = parseInput(cmd);
            switch (input.getFirst()) {
                case "exit": return;
                case "pwd": System.out.println(System.getProperty("user.dir")); break;
                case "type": executeTypeCommand(cmd); break;
                case "echo": executeEchoCommand(input.subList(1, input.size())); break;
                case "cd": executeCDCommand(cmd); break;
                default: {
                    if(executablePath(input.getFirst())!=null)
                        executeCustomCommand(input);
                    else
                        System.out.println(cmd + ": command not found");
                }
            }
        }
    }

    private static void executeCDCommand(String cmd) {
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
    }

    private static void executeEchoCommand(List<String> cmd) {
        System.out.println(String.join(" ", cmd));
//        String echo = cmd.replace("echo ", "");
//
//        if (echo.contains("'")) {
//            System.out.println(echo.substring(echo.indexOf("'")+1, echo.lastIndexOf("'")));
//        }else {
//            Object[] message = Arrays.stream(echo.split(" ")).filter(s -> !s.isBlank()).toArray();
//            StringBuilder finalMessage = new StringBuilder();
//            Arrays.stream(message).forEach(s -> finalMessage.append(s.toString()).append(" "));
//            System.out.println(finalMessage);
//        }
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

    private static void executeCustomCommand(List<String> input) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(input);
        Process process = processBuilder.start();
        process.getInputStream().transferTo(System.out);
        process.waitFor();
    }

    private static List<String> parseInput(String input) {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean insideSingleQuote = false;
        for (char c : input.toCharArray()) {
            if (c == '\'') {
                insideSingleQuote = !insideSingleQuote;
            } else if (c == ' ' && !insideSingleQuote) {
                if (!currentArg.isEmpty()) {
                    args.add(currentArg.toString());
                    currentArg = new StringBuilder();
                }
            } else {
                currentArg.append(c);
            }
        }
        if (!currentArg.isEmpty()) {
            args.add(currentArg.toString());
        }
        return args;
    }
}
