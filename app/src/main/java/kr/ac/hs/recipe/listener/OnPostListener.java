package kr.ac.hs.recipe.listener;

import kr.ac.hs.recipe.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}
