import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    static String executablePath(String val) {
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

    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        Scanner scanner = new Scanner(System.in);
        List<String> builtin = List.of("echo", "exit", "type");
        while(true) {
            System.out.print("$ ");
            String cmd = scanner.nextLine();
            if("exit".equals(cmd))
                break;
            if (cmd.startsWith("echo ")) {
                System.out.println(cmd.replace("echo ", ""));
            } else if (cmd.startsWith("type ")) {
                String val = cmd.substring(5).trim();
                if(builtin.contains(val)) {
                    System.out.println(val+" is a shell builtin");
                } else {
                    String result = executablePath(val);
                    if(result!=null)
                        System.out.println(val+" is "+result);
                    else
                        System.out.println(val + ": not found");
                    continue;
                }
            } else {
                String[] input = cmd.split(" ");
                if(executablePath(input[0])!=null) {
                    input[0] = executablePath(input[0]);
                    ProcessBuilder processBuilder = new ProcessBuilder(input);
                    processBuilder.start();

                }
            }
                System.out.println(cmd + ": command not found");
        }
    }
}
