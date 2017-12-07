package DialogueTesting;

import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

class DialogueTestingNextStoryArea implements DialogueTestingStoryArea {
    private Stage window;
    private ImageView backgroundImageView;
    private ArrayList<DialogueTestingMission> characterList;
    private double windowHeight;
    private double windowWidth;
    //StoryArea nextStoryArea (or Areas)
    //let's assume this storyArea won't care about a dialogue choice

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }
    public void setWindowWidth(double windowWidth){
        this.windowWidth = windowWidth;
    }
    DialogueTestingNextStoryArea(Stage window, double windowWidth, double windowHeight){
        this.window = window;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        backgroundImageView = new ImageView(new Image("file:Images/PixelCity.png", windowWidth, windowHeight, false, true));
        //source: https://i.ytimg.com/vi/Xx6t0gmQ_Tw/maxresdefault.jpg
        characterList = new ArrayList<>();
        characterList.add(new DialogueTestingMissionBankRobbery());
    }

    public void setup(){
        Scene scene1;

        backgroundImageView.setOnMouseClicked(e -> window.close());
        //change this to go to next StoryArea if there's another to attach

        AnchorPane layout1 = new AnchorPane();

        /*ImageView firstCharacterImageView = characterList.get(0).getCharacterImageView(windowHeight);
        firstCharacterImageView.setImage(new Image(characterList.get(0).getCharacterImageView(windowHeight).getImage().impl_getUrl(), 200.0, 400, false, true));

        AnchorPane.setLeftAnchor(firstCharacterImageView, 20.0);
        AnchorPane.setBottomAnchor(firstCharacterImageView, 0.0);
        firstCharacterImageView.setScaleX(-1);

        layout1.getChildren().addAll(backgroundImageView, firstCharacterImageView);*/
        layout1.getChildren().addAll(backgroundImageView);
        scene1 = new Scene(layout1);

        for(DialogueTestingMission character: characterList){
            character.getCharacterImageView(windowHeight).setOnMouseClicked(e -> {
                ImageView biggerCharacterImageView = new ImageView();
                biggerCharacterImageView.setImage(new Image(character.getCharacterImageView(windowHeight).getImage().impl_getUrl(), 200, windowHeight-50, false, true));
                biggerCharacterImageView.setScaleX(-1);
                biggerCharacterImageView.setPreserveRatio(true);
                biggerCharacterImageView.setSmooth(true);

                AnchorPane tempLayout = new AnchorPane();
                AnchorPane.setLeftAnchor(biggerCharacterImageView, 20.0);
                AnchorPane.setBottomAnchor(biggerCharacterImageView, 0.0);
                tempLayout.getChildren().addAll(backgroundImageView, biggerCharacterImageView);
                Scene tempScene = new Scene(tempLayout);
                window.setScene(tempScene);
                biggerCharacterImageView.fitHeightProperty().unbind();

                character.talkToAtHouse();

                DialogueTestingNextStoryArea tempStoryArea = new DialogueTestingNextStoryArea(window, windowWidth, windowHeight);
                tempStoryArea.setup();
            });

            character.getCharacterImageView(windowHeight).setOnMouseEntered(e -> {
                int depth = 70; //Setting the uniform variable for the glow width and height

                DropShadow borderGlow= new DropShadow();
                borderGlow.setOffsetY(0f);
                borderGlow.setOffsetX(0f);
                borderGlow.setColor(Color.GOLD);
                borderGlow.setWidth(depth);
                borderGlow.setHeight(depth);

                character.getCharacterImageView(windowHeight).setEffect(borderGlow);
            });

            character.getCharacterImageView(windowHeight).setOnMouseExited(e -> character.getCharacterImageView(windowHeight).setEffect(null));
        }
        window.setScene(scene1);
    }

    public void playthrough(){
        window.show();
    }

    /*private void goToNextArea(){
        nextStoryArea.setup();
        nextStoryArea.playthrough();
    }*/

}
