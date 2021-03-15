package com.yashas.regionaldata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.yashas.regionaldata.R;
import com.yashas.regionaldata.database.RegionEntity;
import com.yashas.regionaldata.utility.Utils;

import java.util.List;

public class RegionalAdapter extends RecyclerView.Adapter<RegionalAdapter.RegionalAdapterViewHolder>{
    private final List<RegionEntity> regionEntity;
    private final Context context;

    public RegionalAdapter(Context context, List<RegionEntity> regionEntity){
        this.regionEntity = regionEntity;
        this.context = context;
    }

    @NonNull
    @Override
    public RegionalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.region_item, parent, false);
        return new RegionalAdapterViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RegionalAdapterViewHolder holder, int position) {
        RegionEntity region = regionEntity.get(position);
        String lang = region.languages;
        String border = region.borders;
        holder.countryName.setText(region.name);
        holder.capital.setText(region.capital);
        holder.region.setText(region.region);
        holder.subRegion.setText(region.subRegion);
        holder.languages.setText(lang.substring(1, lang.length()-1));
        holder.borders.setText(border.substring(1, border.length()-1));
        holder.population.setText(String.valueOf(region.population));
        Utils.fetchSvg(context, region.flag, holder.flag);
    }

    @Override
    public int getItemCount() {
        return regionEntity.size();
    }

    public static class RegionalAdapterViewHolder extends RecyclerView.ViewHolder{
        private final AppCompatImageView flag;
        private final AppCompatTextView countryName;
        private final AppCompatTextView capital;
        private final AppCompatTextView region;
        private final AppCompatTextView subRegion;
        private final AppCompatTextView languages;
        private final AppCompatTextView borders;
        private final AppCompatTextView population;

        public RegionalAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            flag = (AppCompatImageView) itemView.findViewById(R.id.flag);
            countryName = (AppCompatTextView) itemView.findViewById(R.id.countryName);
            capital = (AppCompatTextView) itemView.findViewById(R.id.capital);
            region = (AppCompatTextView) itemView.findViewById(R.id.region);
            subRegion = (AppCompatTextView) itemView.findViewById(R.id.subRegion);
            languages = (AppCompatTextView) itemView.findViewById(R.id.languages);
            borders = (AppCompatTextView) itemView.findViewById(R.id.borders);
            population = (AppCompatTextView) itemView.findViewById(R.id.population);
        }
    }
}
