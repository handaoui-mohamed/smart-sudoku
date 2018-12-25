package com.handaomo.smartsudoku.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.handaomo.smartsudoku.R;

public class ChoiceFragment extends ListFragment implements AdapterView.OnItemClickListener {
    GameFragment gameFragment;

    public ChoiceFragment() {
    }


    public void setParentFragment(GameFragment gameFragment) {
        this.gameFragment = gameFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choice, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.choices_values, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        gameFragment.setSelectedElement(position);
        getFragmentManager().popBackStackImmediate();
    }
}
