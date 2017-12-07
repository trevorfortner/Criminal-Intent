import javafx.application.Application;
import javafx.stage.Stage;

//this class is something that can be called by all tests, solely to help initialize JavaFX
//while this could be moved out of each test class, the @BeforeClass setUpClass() needed to be in each test individually to use it before each test
public class simpleApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        //just need to call start to initialize JavaFX
    }
}