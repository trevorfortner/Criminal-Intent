# Criminal Intent User Guide
## 1.  Overview
### 1.1 What is Criminal Intent?
Criminal Intent is a strategy game that’s loosely based upon This is the Police.  In this game, the user will manage a group of subordinates in order to gain as much money as possible.  Sending these subordinates out on missions will take time for them to return, so the user will need to manage their time wisely.  
### 1.2 Why make this?
While this isn't the most complex game, it was my first experience using a GUI-focused library (JavaFX).  I wanted to test my ability to load assets from other folders, use them for time-based animations, and react to user mouse/keyboard input appropriately.  Before this, my programs were mostly limited to using basic input methods such as Scanners, but now I feel as though I've begun to expand my horizon.  I've always been passionate about video games, from the community to the content, so I wanted to prove to myself that I would be able to create a game, albeit a simple one, from scratch.
### 1.3 Operating System Required
Currently, Criminal Intent is built to run on devices running Windows 7 or above, due to methods for referencing directories to reach resources such as images and stylesheets. Running the .jar file on a Linux or Mac device will likely give an image similar to the one in the Troubleshooting section below.
### 1.4 Supported Java Versions
Criminal Intent requires Java 8 (or above) to run.  It mostly uses JavaFX 8 to display everything, which comes with Java 8 and above.
### 1.5 Libraries Used
Criminal Intent uses JavaFX8, which comes with Java 8, to do most of the display and animations.  It also uses JUnit4 to run tests on the program, but it is not required to play the game.

## 2. Running the Program
The repo contains the .jar file (Criminal Intent.jar on the main folder of the repo) which should be everything needed to run the file.  In case you wanted to compile the source code yourself, this section will also include instructions for that.
### 2.1 Running from the .jar file
Double-click Criminal Intent.jar in the main folder.  The game will start to run from there.
### 2.2 Compiling and Running from Command Line
See Compilation Instructions.md in the Docs folder for detailed instructions.

## 3. Game Controls
### Close pop-up window (excluding in-game pause menu)
Press the ESC key on your keyboard to close any pop-up windows other than the in-game menu.  In terms of mission messages:
-   Just opened a mission: Will choose "Cancel"
-	Assigning members to a mission (including sending backup): Will choose “Cancel”
-	“Mission Success” message: Will close the pop-up
-	“Mission Failed” message: Will choose “Continue” (WARNING: May cause a subordinate to die due to backup not being sent)
### Access in-game pause menu
_Note: This will only work after the main menu and tutorial (whether skipped or not)._

Press the M key to open or close the in-game pause menu.  If the user is in the middle of a day sequence, this will pause the timers used to keep track of day progress.  Timers will continue upon closing the menu.  
### Day Heads-Up Display
![Day HUD](/Docs/DayHUD.png?raw=true)
1.	Stamina bar – this will affect how likely they are to succeed at a mission
2.	Name and strength – strength will also factor into how likely they are to pass a mission
3.	Mission marker – click on this to open a screen to indicate how you want to handle the opportunity

## 4. Troubleshooting
### The program looks like this:
![Possible Error](/Docs/PossibleError.png?raw=true)

This happens when the .jar file is in the wrong directory.  Move the .jar file so that it’s in the same directory as the Images and Source folders (as it is on the repo).  This is because the code uses references to these folders, which it can’t find unless it’s in the same directory as them.
### Customer Support
_Note: Please do not contact Customer Support for hints/codes/cheats._ 

_Note: All support is handled in English only._ 

Email: trevorfortner1@gmail.com

If you cannot find an answer to your issue in this user guide, you can submit a question/incident to the email above.  A response may take anywhere from 24-72 hours, depending on the nature of your problem. 
