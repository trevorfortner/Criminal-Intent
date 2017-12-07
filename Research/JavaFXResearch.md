# 12/16 (1:30-2:30 PM)
## JavaFX videos that Jennifer Cremer sent me as a reference 
"JavaFX Java GUI Design Tutorials" playlist by youtube user thenewboston

*Aside: I may refer to Jennifer as Ferby at some point during these docs; it's how I generally refer to her.*

### Video 1
- javaFX is built into jdk, but still need to import
- Imports: 
    - javafx.application.Application
    - javafx.event.ActionEvent
    - javafx.event.EventHandler
    - javafx.scene.Scene
    - javafx.scene.control.Button
    - javafx.scene.layout.StackPane
    - javafx.stage.Stage
- https://github.com/buckyroberts/Source-Code-from-Tutorials/tree/master/JavaFX for all the code in the videos
- Make main extend Application
- Override start() method in Application
- Stage is Window, inners are Scene (this has nothing to do with the BankScene I was talking about yesterday, I just happened to use the same word before I did research)
- primaryStage.show() whenever wanting to display the window/stage
- Have to create a scene, then do primaryStage.setScene(newScene) before show(), maybe could setScene() between to show something like a robber walking in

### Video 2
- Make main also implement EventHandler<type_of_event_used>
- alt+insert on IntelliJ to implement methods from an interface being used (handle here)
- button.setOnAction(this) uses handle() method within the class it's done in
- Check the event.getSource to make sure

### Video 3
- Can do anonymous inner class to make the EventHandler and handle() function within button.setOnAction
- Makes it so don't need to check event.getSource()
- Disgusting code replication with this though
- Lambda expression = button.setAction(e -> /*code for handle() method*/ );
- If mult lines for handle(), use { } within the comment
- Make sure to update project language level to 8+ in IntelliJ to use lambdas

### Video 4
- Yup, use button.setOnAction to call primaryStage.setScene() onto the new scene to update the viewable portion
- layout.getChildren().addAll(Node...elements) to set up the full layout basically
- Scene constructor is (layout, width, height)

### Video 5
- Look onto the github mentioned in Video 1 notes for code for an AlertBox example
- I dont think I'll really need AlertBoxes that require to be worked with first, but can do it with stage.initModality(Modality.APPLICATION_MODAL);
- stage.showAndWait() shows until it's closed

*Sidenote: I'm not doing the 1-hour thing on purpose, I'm just doing it between family events*



# 12/16 (10:30 PM-1:30AM)

## Continuing the playlist
### Video 6
- Can make display() not void, can have it return a variable based on the button pressed
- For dialogue options in the game, could send an int that indicates the chat choice

### Video 7
- Can create a closeProgram() function so that extra things can happen when they press a button to close the window (also calls window.close(), obv) 
- Can do scene.setOnCloseRequest (paramaterize with a lambda that calls your closeProgram() ) in order to change what the default red X close button does
- closeProgram() can create a ConfirmBox and judge based on that response
- e.consume() will make it ignore the "close" event that was created by pressing the default red X button, then run your func

### Video 8
- Dont use Hbox for menus, it's just an example
- Layouts are a type of "Node" that can be added to another layout, or they can be placed within the setTop(Node node) methods of BorderPanes

### Video 9
- GridPane.setPadding() takes in an Insets object
- Insets object can be parameterized with four numbers for edge padding amounts (in pixels)
- GridPane.setVGap(double x) and .setHGap(double x) for setting vert and horiz gaps in grid respectively
- GridPane.setConstraints() takes in a Node, columnIndex int, and rowIndex int
- TextFields can use setPromptText() to put a grey-ish text in it when first shown
- TextField constructor can set default text (different from PromptText) that is a default input to the field
- Make sure to getChildren.addAll() on the grid even after setting constraints

### Video 10
- TextField.getText() returns what the user typed in the field
- Can input validate with a method that has a try-catch and returns a boolean based upon the result of validation (like trying to parseInt from a string)

### Video 11
- CheckBox.setSelected(boolean) sets default for the box being checked or not
- CheckBox.isSelected() returns if it's checked, obv

### Video 12
- ChoiceBox ~ dropdown list, set like an ArrayList => ChoiceBox<String> = new ChoiceBox<>();
- ChoiceBox.getItems().addAll() instead of getChildren().addAll() like with layouts
- ChoiceBox.setValue() lets you set a default value from something already in the list
- ChoiceBox.getValue() returns the choice

### Video 13
- ChoiceBox.getSelectionModel().selectedItemProperty().addListener() to add a listener to respond to changes in choices in box immediately 
- Lambda for addListener can be "(v, oldValue, newValue -> /*whatever you want to do*/ " )

### Video 14
- ComboBox emits actions, dont need to add listeners
- ComboBox.setPromptText() to set a default message in the box that doesn't necessarily need to be an option 
- ComboBox.setOnAction() with a lambda to do stuff whenever an item is selected (cant do with ChoiceBox without listener)
- ComboBox.setEditable(true) to allow user input, overrides prompt text

### Video 15
- "ListView.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);" to allow multiple item selection
- "ListView.getSelectionModel.getSelectedItems()" returns an ObservableList with all the selected choices
- Can iterate through ObservableList using enhanced for loop

### Video 16
- TreeItem<String> can hold other TreeItems or leaves
- TreeItem.setExpanded(true) sets default to be opened
- Can create makeBranch() function that takes in item name and a TreeItem parent
- makeBranch() would need to make a new TreeItem and does parent.getChildren().add(new TreeItem);
- TreeView constructor takes in a TreeItem parameter to act as the root
- TreeView.setShowRoot(false) makes user not have to open the root first
- Need to add the TreeView into whatever layout you're using
- Need to add listeners (same way as with ChoiceBox) onto the entire tree (can just call it on the TreeView) to react to selection changes

### Video 17
- Alt+insert on IntelliJ can create all getters and setters for a custom class
- Getters and setters NEED to follow naming conventions for JavaFX to work

### Video 18
- FXCollections.observableArrayList() is a type of ObservableList that can store java Objects
- Make sure to import the javaFX TableView, not swing
- Create TableColumns with title in constructor and do as he did for setting values
- Set items for TableView equal to a list of all products, add columns using .getColumns().addAll()

### Videos 19 & 20
- Skipping. More about Tables and can't think when I'd need them for the game

### Video 21
- Menu constructor takes in a String in constructor for what to show before opening
- Menu.getItems().addAll() lets you add MenuItems
- MenuItems constructor also takes in a String for title
- MenuBar constructor is empty, it's more of a layout type
- MenuBar.getMenus().addAll() allows you to add Menus onto the MenuBar
- Then add the MenuBar onto the layout (in his example, onto the BorderPane)
- Dots on the end of a MenuItem name generally means it'll open a new window

### Video 22
- SeperatorMenuItems can be added to the Menu to show a visible line between MenuItems in the Menu itself
- MenuItems can use .setOnAction() with lambdas because they create an event when clicked
- "_Edit" within the Menu constructor => the underscore makes it so the user can type alt+e to open the edit menu
- MenuItems can be disabled (greyed out and do nothing) using MenuItem.setDisable(true)

### Video 23
- CheckMenuItem is a MenuItem that lets you check an option within the Menu
- Use .isSelected() to see if it's checked or not (probably needed within setOnAction lambda function)
- .setSelected(boolean) for setting default (default false)

### Video 24
- ToggleGroup makes it so user can only select one MenuItem at any given time
- RadioMenuItem.setToggleGroup() takes in a ToggleGroup to check that only one MenuItem in the group is selected
- Add all the RadioMenuItems to the Menu (can't just add the ToggleGroup)

### Video 25
- JavaFX has some built in themes for a window
- Can code your own in CSS and use them
- src folder->New->Stylesheet to make a new CSS file that can be applied to other classes
- Within CSS file => ".root{ }" then all changes inside to change the Scene things
- Scene.getStylesheets().add() takes in the file name as a string and overrides the old one

### Video 26
- Label.setStyle() lets you put in a String that sets a custom style for that Label
- Within .css file => ".label{ }" then all changes inside to change every label in the program
- Can do "-fx-background-color: linear-gradient(hexNum, hexNum);" to give a background color gradient to whatever

### Video 27
- Button.getStyleClass.add(String) lets you use custom style classes when you put the name of the class in CSS in it
- Can also do it as an Id (.setId and # instead of . before CSS class name)

### Video 28
- StringProperty can be used instead of String to allow listeners to be able to be added
- SimpleStringProperty constructor takes in: the object holding it (normally "this"), the property/attribute name, and the default value

### Videos 29 & 30
- Skipping. More Properties stuff. Don't think I'll need.

### Video 31
- The .fxml file looks a lot like html, is edited by Scene Builder to organize the code easier

### Videos 32 & 33
- Skipping. More fxml stuff that I probably won't mess with.

### Video 34
- www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html for the Scene Builder download
- SceneBuilder lets you work with javafx visually instead of through code

### Video 35
- More SceneBuilder guiding. They're all pretty self-explanatory

*TODO: Research implementing custom graphics (in JavaFX?)*

# 12/17 (11AM-1PM)

## Researching custom images/graphics within JavaFX
### Source: javadocs for ImageView
- More imports:
    - javafx.scene.image.Image;
    - javafx.scene.image.ImageView;
- Image constructor takes in the file name (includes .png/.jpg/etc) as a String (assuming this requires the file being within the compiler directory)
- ImageView constructor takes no parameters, the image, or the String for the url of the image
- Can do ImageView.setViewport(Rectangle2D) to set an area for the image to reside within (set size)
- ImageView.setPreserveRatio(true) to not have a stretched/distorted image when enlarged
- Add the ImageViews into a layout of some sort (example I found uses HBox), then add the layout onto a scene, then scene onto the stage

## Researching reacting to mouse clicks within an image on certain locations
### Source: javadocs for ImageView
- ImageView inherits setOnMouseClicked() from being part of the Node class
- Maybe I could have images stacked on top of each other with a background image, and each foreground image has different onMouseClicked actions?

### Source: http://stackoverflow.com/questions/29459369/how-do-i-stack-an-image-on-to-an-existing-background-in-javafx
- Can create an ImageView that holds the background image, and a second one that holds a foreground image
- StackPane.setAlignment(ImageView, Position) lets you set where an image goes in the StackPane
- Put the background onto the layout first, then add the image that should go on top of it
- Basically this seems like an issue of using the correct layout.  HBox seems like it won't place an ImageView on top of another, StackPane looks like it can

# 12/26  (1:30PM-2PM)
## Making stages appear in certain locations (eg the bottom of the stage it's popping up above)
### Source: http://stackoverflow.com/questions/29350181/how-to-center-a-window-properly-in-java-fx
- Screen.getPrimary() allows you to use the screen that the application will be displayed on for coordinates
- This can be set to a Rectangle2D using .getVisualBounds() to refer to the screen as a rectangle
- Then call Stage.setX(Rectangle2D.getWidth()...) and do whatever else in the parentheses to make it go where it's needed