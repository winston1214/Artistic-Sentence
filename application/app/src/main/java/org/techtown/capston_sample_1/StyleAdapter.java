package org.techtown.capston_sample_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.ViewHolder> implements OnStyleClickListener{
    ArrayList<Style> items = new ArrayList<Style>();
    OnStyleClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.stylebutton, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Style item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickedListener(OnStyleClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClicked(ViewHolder holder, View view, int position){
        if(listener != null){
            listener.onItemClicked(holder, view,position);
        }
    }

    public void addItem(Style item){
        items.add(item);
    }

    public void setItems(ArrayList<Style> items){
        this.items = items;
    }

    public Style getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Style item){
        items.set(position,item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView, final OnStyleClickListener listener){
            super(itemView);

            imageView = itemView.findViewById(R.id.imageStyle);
            textView = itemView.findViewById(R.id.textStyleName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(listener != null){
                        listener.onItemClicked(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(Style item){
            imageView.setImageResource(item.getImage());
            textView.setText(item.getName());
        }
    }
}
