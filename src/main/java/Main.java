import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        Scanner scanner = new Scanner(System.in);
        List<String> builtin = List.of("echo", "exit", "type");
        while(true) {
            System.out.print("$ ");
            String cmd = scanner.nextLine();
            if("exit".equals(cmd))
                break;
            else if (cmd.startsWith("echo ")) {
                System.out.println(cmd.replace("echo ", ""));
            } else if (cmd.startsWith("type ")) {
                String val = cmd.substring(5).trim();
                if(builtin.contains(val)) {
                    System.out.println(val+" is a shell builtin");
                    continue;
                } else {
                    String[] dirs = System.getenv("PATH").split(File.pathSeparator);
                    StringBuilder p = new StringBuilder();
                    boolean flag = false;
                    for(String dir: dirs) {
                        if(!p.isEmpty())
                            p.append("/").append(dir);
                        else
                            p.append(dir);
                        String temp = p +"/"+val;
                        File file = new File(temp);
                        if (file.canExecute()) {
                           System.out.println(val+" is "+file.getPath());
                           flag = true;
                           break;
                        }
                    }
                    if(!flag)
                        System.out.println(val + ": not found");
                }
            } else
                System.out.println(cmd + ": command not found");
        }
    }
}
