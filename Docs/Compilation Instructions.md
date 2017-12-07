# Compile & Run from Command Line (Windows) 
-	Make sure your most recent jdk is in your path
    -	Find where your jdk is saved (mine was in C:/Program Files (x86)/Java), might be in Program Files
    -	Upon opening the command line, run the command
        -	`set path=%path%;(path to jdk folder)/bin`
-	Make sure JUnit4 is installed
    - The .jar files from this link will work just fine: https://github.com/junit-team/junit4/wiki/Download-and-Install
-   Starting in the Source directory, run:
    -   `javac GameController.java`
    -   `move *.class ../`
    -   `cd ../`
    -   `java GameController`

The reason for moving the class files is because the code needs to load files in other folders, which it won’t be able to find if the class files aren’t in the main directory for the game. (for example – it won’t be able to find “Images/PossSub1.png” when still in the Source folder)

# Compile & Run in IntelliJ (Windows or Mac)
-	Simply right click on GameController.java (in the Source directory) and click `Run` 

Apparently IntelliJ does it from the main directory, so there’s no need for any moving.  Run also does the compiling for you.


