package ru.relastic.meet007;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class CustomGridLayoutManager extends GridLayoutManager {
    private View childStub = null;

    public final static int SPAN_COUNT_FIX = 2;
    public CustomGridLayoutManager(Context context) {
        super(context, SPAN_COUNT_FIX,GridLayoutManager.VERTICAL,false);
    }


    @Override
    public int getPosition(@NonNull View view) {
        int i = super.getPosition(view);
        //System.out.println("--------------- public int getPosition(@NonNull View view): "+i);
        return i;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //System.out.println("--------------- recycler: "+recycler.toString());
        super.onLayoutChildren(recycler, state);
    }

    @Override
    public void addView(View child, int index) {
        //if (childStub==null) {
        //    childStub = child;
        //}
        //int position = getPosition(child)*2;
        //if (i!=5) {super.addView(child);}

        //super.addView(childStub);
        super.addView(child, index);
        //super.addView(child, index+1);
        //super.addView(child, index);
        //index =-1 всегда
        //System.out.println("--------------- : addView(View child, int index): "+position);
    }
    @Override
    public void addView(View child) {
        //if (childStub==null) {
        //    childStub = child;
        //}
        //int position = getPosition(child)*2;
        //if (i!=5) {super.addView(child);}

        super.addView(child);
        //super.addView(child, position);
        //addView(child,getPosition(child));
        //index =-1 всегда
        //System.out.println("--------------- :  addView(View child): "+getPosition(child));
    }


    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        //dont calling onCreate Element
        //System.out.println("--------------- positionStart/itemCount: "+positionStart+"/"+itemCount);
    }



}
