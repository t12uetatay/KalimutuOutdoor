package id.t12ue.kalimutu.helpers;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class Space extends RecyclerView.ItemDecoration {
    int space;
    int spanCount;

    public Space(int space, int spanCount) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) <= spanCount) {
            outRect.top = space;
        }
        outRect.right = space;
        outRect.left = space;
        outRect.bottom = space;
    }
}
