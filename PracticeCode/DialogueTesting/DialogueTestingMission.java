package DialogueTesting;

import javafx.scene.image.ImageView;

//This class is mostly to have all the functions defined for every character, so we won't have to hardcast every time
//If a character's not meant to be at a location, the talkToAtXLocation() will return null/do nothing/throw RuntimeException

class DialogueTestingMission {
    ImageView characterImageView;
    String missionname;

    public int talkToAtBank(DialogueTestingCityLandscape storyArea){
        throw new RuntimeException(missionname + " is not at the bank!");
    }

    public void talkToAtBankAfterRobbery(){
        throw new RuntimeException(missionname + " is not at the bank after the robbery!");
    }

    public void talkToAtHouse(){
        throw new RuntimeException(missionname + " is not at the house!");
    }

    ImageView getCharacterImageView(double windowHeight){
        characterImageView.setFitHeight(windowHeight/2);
        characterImageView.setPreserveRatio(true);
        characterImageView.setSmooth(true);
        characterImageView.setCache(true);
        return characterImageView;
    }
}
