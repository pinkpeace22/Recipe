package kr.ac.hs.recipe.recipeDB;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class recipeData {
    public int RECIPE_ID; // 레시피 코드
    public String RECIPE_NM_KO; // 레시피 이름
    public String SUMRY; // 간략(요약)소개
    public int NATION_CODE; // 유형코드
    public String NATION_NAME; // 유형분류
    public int TY_CODE; // 음식분류코드
    public String TY_NM; // 음식분류
    public String COOKING_TIME; // 조리시간
    public String CALORIE; // 칼로리
    public String QNT; // 분량
    public String LEVEL_NM; // 난이도
    public String IRDNT_CODE; // 재료별 분류명
    public String IMG_URL; // 대표이미지 URL
    public String DET_URL; // 상세 URL

    //firebaseDB는 객체 단위로 값을 넣을 때 반드시 매개변수가 비어있는 생성자를 요구함
    public recipeData() {
    }

    public recipeData(int RECIPE_ID, String RECIPE_NM_KO, String SUMRY, int NATION_CODE, String NATION_NAME, int TY_CODE, String TY_NM, String COOKING_TIME, String CALORIE, String QNT, String LEVEL_NM, String IRDNT_CODE, String IMG_URL, String DET_URL) {
        this.RECIPE_ID = RECIPE_ID;
        this.RECIPE_NM_KO = RECIPE_NM_KO;
        this.SUMRY = SUMRY;
        this.NATION_CODE = NATION_CODE;
        this.NATION_NAME = NATION_NAME;
        this.TY_CODE = TY_CODE;
        this.TY_NM = TY_NM;
        this.COOKING_TIME = COOKING_TIME;
        this.CALORIE = CALORIE;
        this.QNT = QNT;
        this.LEVEL_NM = LEVEL_NM;
        this.IRDNT_CODE = IRDNT_CODE;
        this.IMG_URL = IMG_URL;
        this.DET_URL = DET_URL;
    }

    @Override
    public String toString() {
        return "recipeData{" +
                "RECIPE_ID=" + RECIPE_ID +
                ", RECIPE_NM_KO='" + RECIPE_NM_KO + '\'' +
                ", SUMRY='" + SUMRY + '\'' +
                ", NATION_CODE=" + NATION_CODE +
                ", NATION_NAME='" + NATION_NAME + '\'' +
                ", TY_CODE=" + TY_CODE +
                ", TY_NM='" + TY_NM + '\'' +
                ", COOKING_TIME='" + COOKING_TIME + '\'' +
                ", CALORIE='" + CALORIE + '\'' +
                ", QNT='" + QNT + '\'' +
                ", LEVEL_NM='" + LEVEL_NM + '\'' +
                ", IRDNT_CODE='" + IRDNT_CODE + '\'' +
                ", IMG_URL='" + IMG_URL + '\'' +
                ", DET_URL='" + DET_URL + '\'' +
                '}';
    }
}
