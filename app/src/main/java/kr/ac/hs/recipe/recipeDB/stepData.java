package kr.ac.hs.recipe.recipeDB;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class stepData {
    public int RECIPE_ID; // 레시피 코드
    public int COOKING_NO; // 요리설명순서
    public String COOKING_DC; // 요리설명
    public String STRE_STEP_IMAGE_URL; // 과정 이미지 URL
    public String STEP_TIP; // 과정팁

    //firebaseDB는 객체 단위로 값을 넣을 때 반드시 매개변수가 비어있는 생성자를 요구함
    public stepData() {
    }

    public stepData(int RECIPE_ID, int COOKING_NO, String COOKING_DC, String STRE_STEP_IMAGE_URL, String STEP_TIP) {
        this.RECIPE_ID = RECIPE_ID;
        this.COOKING_NO = COOKING_NO;
        this.COOKING_DC = COOKING_DC;
        this.STRE_STEP_IMAGE_URL = STRE_STEP_IMAGE_URL;
        this.STEP_TIP = STEP_TIP;
    }

    @Override
    public String toString() {
        return "stepData{" +
                "RECIPE_ID=" + RECIPE_ID +
                ", COOKING_NO=" + COOKING_NO +
                ", COOKING_DC='" + COOKING_DC + '\'' +
                ", STRE_STEP_IMAGE_URL='" + STRE_STEP_IMAGE_URL + '\'' +
                ", STEP_TIP='" + STEP_TIP + '\'' +
                '}';
    }
}
