package com.handaomo.smartsudoku;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class GameFragment extends Fragment {
    private Grille grid;
    private int currentX;
    private int currentY;

    public GameFragment() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        grid = view.findViewById(R.id.sudoku_grid);
        Button confirmBtn = view.findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), grid.gagne() ? "You won!" : "You lost!!", Toast.LENGTH_LONG).show();
            }
        });

        grid.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;

                    case MotionEvent.ACTION_UP:
                        currentX = (int) event.getX();
                        currentY = (int) event.getY();
                        if (!grid.isNotFix(currentX, currentY)) performClick();
                        return true;
                }
                return false;
            }

            private void performClick() {
                Bundle bundle = new Bundle();
                bundle.putInt("selected", grid.getElementFromMatrix(currentX, currentY));
                ChoiceFragment choiceFragment = new ChoiceFragment();
                choiceFragment.setParentFragement(GameFragment.this);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, choiceFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    public void setSelectedElement(int selectedElement) {
        grid.set(currentX, currentY, selectedElement);
    }
}
