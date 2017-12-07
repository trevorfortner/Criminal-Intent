package DialogueTesting;

//THIS CLASS IS SOLELY FOR THE PURPOSE OF TESTING THE CREATION OF DIALOGUE BOXES

import javafx.application.Application;
import javafx.stage.Stage;

public class DialogueTestingMain extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DialogueTestingCityLandscape bankStoryArea = new DialogueTestingCityLandscape(primaryStage, 800, 480);
        bankStoryArea.setup();
        bankStoryArea.playthrough();
        //once it's in CityLandscape, it'll just pass it down the line of StoryAreas and their setup()s and playthrough()s
    }
}
