import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shell{
    ArrayList<String> history = new ArrayList<String>();
    double timeWaiting = 0;
    Path cwd = Paths.get(System.getProperty("user.dir"));

    public static void main(String[] args){
        Shell shell = new Shell();
        Scanner keyboard = new Scanner(System.in);
        boolean exit = false;

        do {
            try {
                System.out.print("[" + shell.getCwd() + "]: ");
                String userInput = keyboard.nextLine();
                String[] input = splitCommand(userInput);
                exit = shell.execute(input);
                shell.history.add(userInput);
            }
            catch (Exception e){
                System.out.println("Invalid Command");
            }
        } while(!exit);
    }

    public String getCwd() {
        return cwd.toString();
    }

    public boolean execute(String[] input)throws IOException, InterruptedException{
        if ("ptime".equals(input[0])) {
            ptime(timeWaiting);
        }
        else if ("list".equals(input[0])) {
            list();
        }
        else if ("cd".equals(input[0])) {
            cwd = cd(input.length < 2 ? null : input[1], cwd);
        }
        else if ("mdir".equals(input[0])) {
            mdir(input[1], cwd);
        }
        else if ("rdir".equals(input[0])) {
            rdir(input[1], cwd);
        }
        else if ("history".equals(input[0])) {
            printHistory();
        }
        else if ("exit".equals(input[0])) {
            return true;
        }
        else if ("^".equals(input[0])) {
            previous(input[1]);
        }
        else {
            timeWaiting += external(input);
        }
        return false;
    }

    /**
     *
     * @param index
     */
    public void previous(String index)throws IOException, InterruptedException {
        execute(splitCommand(history.get(Integer.valueOf(index) + 1)));
    }

    /**
     * @param time
     * Displays the time spent waiting on external programs to execute
     */
    public void ptime(double time){
        //Print time spent waiting on external programs
        System.out.printf("%.4f\n", time/1000);
    }

    /**
     * Lists the contents of the current directory
     */
    public void list(){
        //Display info about current directory
        File current = new File(getCwd());
        for (File child: current.listFiles()) {
            StringBuilder output = new StringBuilder();
            output.append(child.isDirectory() ? "d" : "-");
            output.append(child.canRead() ? "r" : "-");
            output.append(child.canWrite() ? "w" : "-");
            output.append(child.canExecute() ? "x" : "-");
            output.append(String.format(" %10d ", child.length()));
            String pattern = "MMM dd, yyyy HH:mm";
            SimpleDateFormat date = new SimpleDateFormat(pattern);
            output.append(date.format(new Date(child.lastModified())));
            output.append(" " + child.getName());
            System.out.println(output.toString());
        }
    }

    /**
     *
     * @param directory
     * @param curPath
     * @return
     */
    public Path cd(String directory, Path curPath){
        //Change working directory
        Path newPath = curPath;
        if(directory == null){
            return Paths.get(System.getProperty("user.home"));
        }
        else if(directory.compareTo("..") == 0){
            newPath = newPath.getParent();
            if(newPath != null){
                return newPath;
            }
        }
        else{
            return Paths.get(curPath.toString(), directory);
        }
        return curPath;
    }

    /**
     *
     * @param name
     * @param curPath
     */
    public void mdir(String name, Path curPath){
        //Create a directory
        File newFile = new File(curPath.toString(), name);
        if (!newFile.mkdir()){
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param name
     * @param curPath
     */
    public void rdir(String name, Path curPath){
        //Delete a directory
        File newFile = new File(curPath.toString(), name);
        if(newFile.delete()){
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     */
    public void printHistory(){
        //Display history of commands
        for (int i = 0; i < history.size(); i++){
            System.out.println((i+1) + ". " + history.get(i));
        }
    }

    public double external(String[] input) throws IOException, InterruptedException {
        int pipeIndex = Arrays.binarySearch(input, "|");
        //Use ProcessBuilder to execute the command as an external process
        if (pipeIndex < 0) {
            ProcessBuilder pb = new ProcessBuilder(input[input.length - 1].equals("&") ? Arrays.copyOf(input, input.length - 1) : input);
            pb.directory(new File(getCwd()));
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            double startTime = System.currentTimeMillis();

            Process p = pb.start();
            if (!input[input.length - 1].equals("&")) {
                p.waitFor();
            }

            double endTime = System.currentTimeMillis();
            return endTime - startTime;
        }
        else{
            String[] pb1Cmd = Arrays.copyOfRange(input, 0, pipeIndex);
            String[] pb2Cmd = Arrays.copyOfRange(input, pipeIndex + 1, input[input.length - 1].equals("&") ? input.length - 1 : input.length);
            ProcessBuilder pb1 = new ProcessBuilder(pb1Cmd);
            ProcessBuilder pb2 = new ProcessBuilder(pb2Cmd);
            pb1.directory(new File(getCwd()));
            pb2.directory(new File(getCwd()));

            pb1.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb2.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            double startTime = System.currentTimeMillis();
            Process p1 = pb1.start();
            Process p2 = pb2.start();
            InputStream in = p1.getInputStream();
            OutputStream out = p2.getOutputStream();

            int c;
            while ((c = in.read()) != -1){
                out.write(c);
            }

            out.flush();
            out.close();

            if (!input[input.length - 1].equals("&")) {
                p1.waitFor();
                p2.waitFor();
            }

            double endTime = System.currentTimeMillis();
            return endTime - startTime;
        }
    }
    /*
        String[] command = {"nanoabc", "ProcessExample.java"};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(System.getProperty("user.dir")));
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        try {
            long start = System.currentTimeMillis();
            Process p = pb.start();

            System.out.println("Starting to wait");
            p.waitFor();
            long end = System.currentTimeMillis();
            System.out.printf("Waited for %d milliseconds\n", end - start);
        }
     */

    /**
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from: https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
     * Code written by Dean Matthias
     */
    public static String[] splitCommand(String command) {
        java.util.List<String> matchList = new java.util.ArrayList<>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }

        return matchList.toArray(new String[matchList.size()]);
    }
}