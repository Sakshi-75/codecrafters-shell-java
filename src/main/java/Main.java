import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("$ ");
            String cmd = scanner.nextLine();
            if("exit".equals(cmd))
                break;
            else if (cmd.startsWith("echo ")) {
                String val = cmd.replace("echo ", "");
                System.out.println(val);
            }
            else
                System.out.println(cmd + ": command not found");
        }
    }
}
