package kr.ac.hs.recipe.adapter.recipe;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.databinding.ItemTitleRowBinding;

@EpoxyModelClass(layout = R.layout.item_title_row)
abstract class TitleModel extends EpoxyModelWithHolder<TitleModel.TitleHolder> {

    @EpoxyAttribute
    String title = "";

    @EpoxyAttribute
    String textColorCode;

    static class TitleHolder extends EpoxyHolder {
        private ItemTitleRowBinding binding;

        @Override
        protected void bindView(@NonNull View itemView) {
            binding = ItemTitleRowBinding.bind(itemView);
        }

    }


    @Override
    public void bind(@NonNull TitleHolder holder) {
        holder.binding.tvtitle.setText(title);
        holder.binding.tvtitle.setTextColor(Color.parseColor(textColorCode));
    }

}
