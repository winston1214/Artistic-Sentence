package org.techtown.capston_sample_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class StyleFragment extends Fragment {

    Style selectedStyle = new Style(R.drawable.ic_launcher_background, "");
    ImageButton buttoninfo;
    Button findstylebutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_style, container, false);

        findstylebutton = (Button) view.findViewById(R.id.btnFindStyle);
        findstylebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StyleFindActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        buttoninfo = (ImageButton) view.findViewById(R.id.buttonInfo);
        buttoninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InfoActivity.class);
                startActivityForResult(intent, 101);
            }
        });
        buttoninfo.setVisibility(View.INVISIBLE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        StyleAdapter adapter = new StyleAdapter();

        adapter.addItem(new Style(R.drawable.picasso_icon, "Picasso"));
        adapter.addItem(new Style(R.drawable.monet_icon, "Monet"));
        adapter.addItem(new Style(R.drawable.popart_icon, "Pop Art"));
        adapter.addItem(new Style(R.drawable.none_icon, "None"));

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(new OnStyleClickListener() {
            @Override
            public void onItemClicked(StyleAdapter.ViewHolder holder, View view, int position) {
                selectedStyle = adapter.getItem(position);
                Toast.makeText(getActivity(),selectedStyle.getName() + " 선택됨",Toast.LENGTH_SHORT).show();

                ((MainActivity) getActivity()).pager.setCurrentItem(3);
            }
        });

        return view;
    }
}