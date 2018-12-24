package com.handaomo.smartsudoku.Fragments;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handaomo.smartsudoku.DTO.GridDto;
import com.handaomo.smartsudoku.R;
import com.handaomo.smartsudoku.Services.Api;
import com.handaomo.smartsudoku.Services.GamePreferences;
import com.handaomo.smartsudoku.Views.Grille;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameFragment extends Fragment {
    static boolean initialised = false;
    private Grille grid;
    private int currentX;
    private int currentY;
    TextView resultTxt;

    public GameFragment() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        String currentUser = GamePreferences.getInstance().getCurrentUser(getContext());
        ((TextView)view.findViewById(R.id.currentUserTxt)).setText(currentUser);

        grid = view.findViewById(R.id.sudoku_grid);

        resultTxt = view.findViewById(R.id.gameResult);
        if(!initialised) {
            loadNewConfig();
            resultTxt.setText(getString(R.string.loading_grid));
            initialised = true;
        }

        Button confirmBtn = view.findViewById(R.id.confirmBtn);
        Button reloadBtn = view.findViewById(R.id.reloadBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTxt.setText(grid.gagne() ? "You won!" : "You lost!!");
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewConfig();
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

    public void loadNewConfig(){
        Api.gridService.getRandomGrid().enqueue(new Callback<GridDto>() {
            @Override
            public void onResponse(Call<GridDto> call, Response<GridDto> response) {
                resultTxt.setText("");
                grid.applyNewConfig(response.body().configuration);
            }

            @Override
            public void onFailure(Call<GridDto> call, Throwable t) {

            }
        });
    }

    public void setSelectedElement(int selectedElement) {
        grid.set(currentX, currentY, selectedElement);
    }

    @Override
    public void onResume() {
        super.onResume();
        Grille.spacing = GamePreferences.getInstance().getGridSpacing(getContext());
    }
}
