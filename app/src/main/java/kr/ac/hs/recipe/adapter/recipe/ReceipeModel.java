package kr.ac.hs.recipe.adapter.recipe;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bumptech.glide.Glide;

import kr.ac.hs.recipe.R;
import kr.ac.hs.recipe.databinding.ItemRecipeRowBinding;

@EpoxyModelClass(layout = R.layout.item_recipe_row)
abstract class ReceipeModel extends EpoxyModelWithHolder<ReceipeModel.ReceipeHolder> {


    @EpoxyAttribute
    long id = 0L;

    @EpoxyAttribute
    String title = "";

    @EpoxyAttribute
    String tip = "";

    @EpoxyAttribute
    String imgUrl = null;

    static class ReceipeHolder extends EpoxyHolder {
        private ItemRecipeRowBinding binding;

        @Override
        protected void bindView(@NonNull View itemView) {
            binding = ItemRecipeRowBinding.bind(itemView);
        }
    }



    @Override
    public void bind(@NonNull ReceipeHolder holder) {
        holder.binding.tvName.setText(title);
        if (TextUtils.isEmpty(tip)) {
            holder.binding.tvTip.setVisibility(View.GONE);
        } else {
            holder.binding.tvTip.setVisibility(View.VISIBLE);
            holder.binding.tvTip.setText(tip);
        }

        if (imgUrl != null && !imgUrl.equalsIgnoreCase("")){
            holder.binding.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.binding.imageView).load(imgUrl).centerCrop().into(holder.binding.imageView);
        }else{
            holder.binding.imageView.setVisibility(View.GONE);
        }

    }

    @Override
    public void unbind(@NonNull ReceipeHolder holder) {
        holder.binding.getRoot().setOnClickListener(null);
    }

}
