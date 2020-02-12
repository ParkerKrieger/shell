# 1. Requirements

Write a command shell that processes users inputs. The shell should use the string [\<current dir>]: as it's prompt.
The shell interprets the first word given as the command and all others as parameters. The shell must first check to see
 if a command is built in, and if not try to run it as an external command. When running external commands, the shell 
 will wait for the program to end before prompting for a new command unless an & is entered at the end. If the command
 fails, the shell will let the user know it was an invalid command. 
 
 ## Tracking Time
 The shell should track the number of seconds spent running child processes. Entering the command 'ptime' will display 
 the number of seconds spent waiting on child processes. 
 
 ## List of Files and Directories
 The shell must provide a 'list' command. The first four character displayed correspond to the directory, whether or not
 the user can read, write, or execute. The next 10 characters contain the size of the file in bytes, right justified. 
 Another field will be given to show the last time the file was modified. Finally, the last field will be the name of 
 the file.
 
 ## Working Directory and Directory Changes
 The shell needs to provide a built in 'cd' command. This command only needs to allow changing the directory one level 
 at a time. If the user just enters 'cd', the shell should move to the home directory.
 
 ## Create and Remove Directories
 The 'mdir' command should create a file with the name given as a parameter. If the file already exists, an error message
 will be given. When the command 'rdir' is run, the file will be removed. If it doesn't exist, an error message will be 
 displayed.
 
 ## Command History
 The shell should keep a history of commands run. The command 'history' should display the complete command shell history.
 By using the '^' character, commands should be able to be executed from the history. 
 
 ## Piping Between External Programs
 The shell needs to be able to pipe between two external programs. It doesn't need to pipe indefinitely and only between
 external commands. 
 
 ## Exiting the shell
 The command 'exit' is used to leave the shell
 
# 2. Design

The program will run from the command line using text input and output. 

## Input

The user will input the name of the process they wish to run along with a list of parameters to accompany it. The shell 
will support any number of parameters.

## Output

The shell will wait for processes to finish before prompting for more input, unless an & is added to the end of an 
external command. Output will be written to the shell as it is available.

# 3. Implementation
History is held in an ArrayList

ProcessBuilder is used to execute external commands

System.currentTimeMillis() is used to track time spent waiting for external commands

The System property "user.dir" is used to find the users directory

The System property "user.home" is used to find the home directory

java.nio.file.Path is used to manipulate directories in a cross-platform way

The File class is used to get info for the 'list' command

java.text.SimpleDateFormat is used to format the date for the 'list' command.

The shell should not crash from bad input


#4. Verification

## Test Case 1


## Test Case 2
