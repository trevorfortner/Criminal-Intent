package DialogueTesting;

import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DialogueTestingCityLandscape implements DialogueTestingStoryArea {
    private Stage window;
    private ImageView backgroundImageView;
    private ArrayList<DialogueTestingMission> characterList;
    private DialogueTestingStoryArea nextStoryArea;
    private double windowHeight;
    private double windowWidth;
    private int dialogueChoice;

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }
    public void setWindowWidth(double windowWidth){
        this.windowWidth = windowWidth;
    }
    public DialogueTestingCityLandscape(Stage window, double windowWidth, double windowHeight){
        this.window = window;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        characterList = new ArrayList<>();
        characterList.add(new DialogueTestingMissionBankRobbery());
        characterList.add(new DialogueTestingMissionHomeInvasion());
        nextStoryArea = new DialogueTestingNextStoryArea(window, windowWidth, windowHeight);
        backgroundImageView = new ImageView(new Image("file:Images/PixelCity.png", windowWidth, windowHeight, false, true));
        //Bank pic source: http://www.bridgeporttxhistorical.org/Images/Banks/Community%20Bank/New%20Building.jpg
        //Sneaking robber source: http://www.clipartkid.com/images/30/google-image-resuts-degVT8-clipart.jpg
        //Escaping robber source: http://www.freeiconspng.com/free-images/robber-icon-5029
    }

    public void setup(){
        Scene scene1;

        backgroundImageView.setOnMouseClicked(e -> goToNextArea());

        AnchorPane layout1 = new AnchorPane();

        AnchorPane.setRightAnchor(characterList.get(0).getCharacterImageView(windowHeight), 20.0);
        AnchorPane.setBottomAnchor(characterList.get(0).getCharacterImageView(windowHeight), 10.0);
        AnchorPane.setLeftAnchor(characterList.get(1).getCharacterImageView(windowHeight), windowWidth/13);
        AnchorPane.setBottomAnchor(characterList.get(1).getCharacterImageView(windowHeight), windowHeight/2.35);

        layout1.getChildren().addAll(backgroundImageView, characterList.get(0).getCharacterImageView(windowHeight), characterList.get(1).getCharacterImageView(windowHeight));

        for(DialogueTestingMission character: characterList){
            character.getCharacterImageView(windowHeight).setOnMouseClicked(e -> {
                setupWithLargerCharacter(character);

                if(dialogueChoice==0) {
                    dialogueChoice = character.talkToAtBank(this);
                }
                else {
                    character.talkToAtBankAfterRobbery();
                }

                setup();
            });

            character.getCharacterImageView(windowHeight).setOnMouseEntered(e -> {
                int depth = 70; //Setting the uniform variable for the glow width and height

                DropShadow borderGlow= new DropShadow();
                borderGlow.setOffsetY(0f);
                borderGlow.setOffsetX(0f);
                borderGlow.setColor(Color.LIGHTBLUE);
                borderGlow.setWidth(depth);
                borderGlow.setHeight(depth);

                character.getCharacterImageView(windowHeight).setEffect(borderGlow);
            });

            character.getCharacterImageView(windowHeight).setOnMouseExited(e -> character.getCharacterImageView(windowHeight).setEffect(null));
        }

        scene1 = new Scene(layout1);

        window.setScene(scene1);
    }

    //I think the playthrough() method mentioned in DesignBrainstorming.md would literally just be window.show()
    //oh wait it would have to run the setup() for nextStoryArea once I get around to implementing that too

    public void playthrough(){
        window.show();
    }

    private void goToNextArea(){
        nextStoryArea.setup();
        nextStoryArea.playthrough();
    }

    /*void moveRobberFirstTime(DialogueTestingMission character){
        backgroundImageView = new ImageView(new Image("file:Images/BankWithRobberInPosition2.png", windowWidth, windowHeight, false, true));
        setupWithLargerCharacter(character);
    }
    void moveRobberSecondTime(DialogueTestingMission character){
        backgroundImageView = new ImageView(new Image("file:Images/BankWithRobberInPosition3.png", windowWidth, windowHeight, false, true));
        setupWithLargerCharacter(character);
    }
    void moveRobberThirdTime(DialogueTestingMission character){
        backgroundImageView = new ImageView(new Image("file:Images/BankWithRobberInside.png", windowWidth, windowHeight, false, true));
        setupWithLargerCharacter(character);
    }
    void moveRobberFourthTime(DialogueTestingMission character){
        backgroundImageView = new ImageView(new Image("file:Images/BankWithRobberEscaping.png", windowWidth, windowHeight, false, true));
        setupWithLargerCharacter(character);
    }
    void moveRobberLastTime(){
        backgroundImageView = new ImageView(new Image("file:Images/PixelCity.png", windowWidth, windowHeight, false, true));
        setup();
    }*/

    private void setupWithLargerCharacter(DialogueTestingMission character){
        ImageView biggerCharacterImageView = new ImageView();
        biggerCharacterImageView.setImage(new Image(character.getCharacterImageView(windowHeight).getImage().impl_getUrl(), 500, windowHeight-200, false, true));
        biggerCharacterImageView.setScaleX(-1);
        biggerCharacterImageView.setPreserveRatio(true);
        biggerCharacterImageView.setSmooth(true);

        AnchorPane tempLayout = new AnchorPane();
        AnchorPane.setLeftAnchor(biggerCharacterImageView, windowWidth/5);
        AnchorPane.setBottomAnchor(biggerCharacterImageView, 0.0);
        tempLayout.getChildren().addAll(backgroundImageView, biggerCharacterImageView);
        Scene tempScene = new Scene(tempLayout);
        window.setScene(tempScene);
        biggerCharacterImageView.fitHeightProperty().unbind();
    }

}
