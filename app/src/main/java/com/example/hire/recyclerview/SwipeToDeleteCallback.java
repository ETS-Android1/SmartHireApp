package com.example.hire.recyclerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire.BottomNavigationActivity;
import com.example.hire.R;
import com.example.hire.database.HomeFragment;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private MyAdapter mAdapter;
    private Drawable icon;
    private final ColorDrawable background;

    public SwipeToDeleteCallback(MyAdapter adapter) {
        super(0,ItemTouchHelper.LEFT);
        mAdapter = adapter;
        icon = ContextCompat.getDrawable(mAdapter.context,
                R.drawable.employee_call);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Log.d("SWIPE", "onSwiped: " + position);
        mAdapter.deleteItem(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        /*new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(ContextCompat.getColor(, R.color.colorAccent))
                .addActionIcon(R.drawable.my_icon)
                .create()
                .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);*/
    }
}
