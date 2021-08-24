package kr.ac.hs.recipe.adapter.recipe;

import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.databinding.ItemIngredientRowBinding;

@EpoxyModelClass(layout = R.layout.item_ingredient_row)
abstract class IngredientModel extends EpoxyModelWithHolder<IngredientModel.IngredientHolder> {


    @EpoxyAttribute
    long id = 0L;

    @EpoxyAttribute
    String title = "";

    @EpoxyAttribute
    String amount = "";

    static class IngredientHolder extends EpoxyHolder {
        private ItemIngredientRowBinding binding;

        @Override
        protected void bindView(@NonNull View itemView) {
            binding = ItemIngredientRowBinding.bind(itemView);
        }
    }


    @Override
    public void bind(@NonNull IngredientHolder holder) {
        holder.binding.tvName.setText(title);
        holder.binding.tvAmount.setText(amount);
    }

    @Override
    public void unbind(@NonNull IngredientHolder holder) {
        holder.binding.getRoot().setOnClickListener(null);
    }

}
