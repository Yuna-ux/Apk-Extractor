// Generated file. Do not modify.
package com.m.apk.extractor.skech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainBinding {
    public final LinearLayout rootView;
    public final LinearLayout linearMain;
    public final TextView textviewExtractor;
    public final EditText edittextPackage;
    public final TextView textviewWarn;
    public final Button buttonExtract;
    public final TextView textviewVersion;

    private MainBinding(LinearLayout rootView, LinearLayout linearMain, TextView textviewExtractor, EditText edittextPackage, TextView textviewWarn, Button buttonExtract, TextView textviewVersion) {
        this.rootView = rootView;
        this.linearMain = linearMain;
        this.textviewExtractor = textviewExtractor;
        this.edittextPackage = edittextPackage;
        this.textviewWarn = textviewWarn;
        this.buttonExtract = buttonExtract;
        this.textviewVersion = textviewVersion;
    }

    public LinearLayout getRoot() {
        return rootView;
    }

    public static MainBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static MainBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.main, parent, false);
        if (attachToParent) parent.addView(root);
        return bind(root);
    }

    public static MainBinding bind(View view) {
        LinearLayout rootView = (LinearLayout) view;
        LinearLayout linearMain = findChildViewById(view, R.id.linear_main);
        TextView textviewExtractor = findChildViewById(view, R.id.textviewExtractor);
        EditText edittextPackage = findChildViewById(view, R.id.edittextPackage);
        TextView textviewWarn = findChildViewById(view, R.id.textviewWarn);
        Button buttonExtract = findChildViewById(view, R.id.buttonExtract);
        TextView textviewVersion = findChildViewById(view, R.id.textviewVersion);

        if (linearMain == null || textviewExtractor == null || edittextPackage == null || textviewWarn == null || buttonExtract == null || textviewVersion == null) {
             throw new IllegalStateException("Required views are missing");
        }

        return new MainBinding(rootView, linearMain, textviewExtractor, edittextPackage, textviewWarn, buttonExtract, textviewVersion);
    }

    private static <T extends View> T findChildViewById(View rootView, int id) {
         if (rootView instanceof ViewGroup) {
              ViewGroup rootViewGroup = (ViewGroup) rootView;
              for (int i = 0; i < rootViewGroup.getChildCount(); i++) {
                   T view = rootViewGroup.getChildAt(i).findViewById(id);
                   if (view != null) return view;
              }
         }
         return null;
    }
}
