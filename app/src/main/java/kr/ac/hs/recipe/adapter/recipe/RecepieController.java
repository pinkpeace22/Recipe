package kr.ac.hs.recipe.adapter.recipe;

import android.os.Handler;

import com.airbnb.epoxy.EpoxyController;

import java.util.List;

import kr.ac.hs.recipe.L;
import kr.ac.hs.recipe.recipeDB.SummaryData;
import kr.ac.hs.recipe.recipeDB.ingredientsData;
import kr.ac.hs.recipe.recipeDB.stepData;

public class RecepieController extends EpoxyController {

    private SummaryData summaryData = null;
    private List<stepData> receipeList = null;
    private List<ingredientsData> ingredientList = null;

    public RecepieController(Handler modelBuildingHandler, Handler diffingHandler) {
        super(modelBuildingHandler, diffingHandler);
    }

    public void updateSummaryItems(SummaryData item) {
        this.summaryData = item;
        requestModelBuild();
    }

    public void updateReceipeItems(List<stepData> items) {
        this.receipeList = items;
        requestModelBuild();
    }

    public void updateIngredientItems(List<ingredientsData> items) {
        this.ingredientList = items;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        L.i(":::buildModels");
        long index = 0L;
        List<stepData> recepieResults = receipeList;
        List<ingredientsData> ingredientResults = ingredientList;
        SummaryData summaryResult = summaryData;

        if (summaryResult != null) {
            new SummaryModel_().id(index++)
                    .calorie(summaryResult.calorie)
                    .cookingTime(summaryResult.cookingTime)
                    .img(summaryResult.imgUrl)
                    .addTo(this);
        }


        if (ingredientResults != null) {
            add(new TitleModel_().id("ingredient_title").title("재료").textColorCode("#ff5733"));
            for (ingredientsData data : ingredientResults) {
                new IngredientModel_().id(index++)
                        .title(data.IRDNT_NM)
                        .amount(data.IRDNT_CPCTY).addTo(this);
            }
        }

        if (recepieResults != null) {
            add(new TitleModel_().id("receipe_title").title("레시피").textColorCode("#ff5733"));
            for (stepData data : recepieResults) {
                new ReceipeModel_().id(data.COOKING_NO)
                        .title("[" + data.COOKING_NO + "] " + data.COOKING_DC)
                        .tip(data.STEP_TIP)
                        .imgUrl(data.STRE_STEP_IMAGE_URL)
                        .addTo(this);
            }
        }


    }
}
