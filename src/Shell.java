
public class Shell{
    public static void main(String[] args){
        boolean exit = false;

        while(!exit){
            try {
                switch (args[0]) {
                    case "ptime":
                        ptime();
                        break;
                    case "list":
                        list();
                        break;
                    case "cd":
                        cd(args[1]);
                        break;
                    case "mdir":
                        mdir(args[1]);
                        break;
                    case "rdir":
                        rdir(args[1]);
                        break;
                    case "history":
                        history();
                        break;
                    case "exit":
                        exit = true;
                        break;
                    default:
                        external(args);
                        break;
                }
            }
            catch (){

            }
        }
    }

    public static void ptime(){
        //Print time spent waiting on external programs
    }

    public static void list(){
        //Display info about current directory
    }

    public static void cd(String directory){
        //Change working directory
    }

    public static void mdir(String name){
        //Create a directory
    }

    public static void rdir(String name){
        //Delete a directory
    }

    public static void history(){
        //Display history of commands
    }

    public static void external(String[] args){
        //Use ProcessBuilder to execute the command as an external process
    }
}