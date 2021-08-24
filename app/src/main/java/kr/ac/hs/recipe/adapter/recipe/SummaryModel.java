package kr.ac.hs.recipe.adapter.recipe;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bumptech.glide.Glide;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.databinding.ItemSummaryRowBinding;

@EpoxyModelClass(layout = R.layout.item_summary_row)
abstract class SummaryModel extends EpoxyModelWithHolder<SummaryModel.SummaryHolder> {

    @EpoxyAttribute
    String calorie = "";

    @EpoxyAttribute
    String cookingTime = "";

    @EpoxyAttribute
    Bitmap img = null;

    static class SummaryHolder extends EpoxyHolder {
        private ItemSummaryRowBinding binding;

        @Override
        protected void bindView(@NonNull View itemView) {
            binding = ItemSummaryRowBinding.bind(itemView);
        }
    }


    @Override
    public void bind(@NonNull SummaryHolder holder) {
        holder.binding.tvCalorie.setText(calorie);
        holder.binding.tvTime.setText(cookingTime);
        Glide.with(holder.binding.ivFood).load(img).centerCrop().into(holder.binding.ivFood);
    }

}
