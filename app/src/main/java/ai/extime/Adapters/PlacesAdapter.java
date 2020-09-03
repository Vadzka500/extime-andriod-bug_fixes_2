package ai.extime.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import ai.extime.Fragments.PlacesFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import ai.extime.Models.Contact;
import ai.extime.Models.Place;
import com.extime.R;

/**
 * Created by teaarte on 10.10.2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private View mainView;

    private Context context;

    private ArrayList<Place> listOfFPlaces;

    private PlacesFragment placesFragment;

    @NonNull
    @Override
    public String getSectionName(int position) {
        return "NA";
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {

        CircleImageView placeCircleColor;
        ImageView placeIcn;
        TextView placeName;
        TextView placeText;
        View card;

        PlaceViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            placeCircleColor = (CircleImageView) itemView.findViewById(R.id.placeCircleColor);
            placeIcn = (ImageView) itemView.findViewById(R.id.placeIcn);
            placeName = (TextView) itemView.findViewById(R.id.placeName);
            placeText = (TextView) itemView.findViewById(R.id.placeText);
        }
    }

    private String getInitials(Contact contact){
        String initials = "";
        if (contact.getName() != null && !contact.getName().isEmpty()) {
            String names[] = contact.getName().split("\\s+");
            for (String namePart : names)
                initials += namePart.charAt(0);
        }
        return initials.toUpperCase();
    }

    public PlacesAdapter(Context context, ArrayList<Place> listOfFPlaces, PlacesFragment placesFragment) {
        this.context = context;
        this.listOfFPlaces = listOfFPlaces;
        this.placesFragment = placesFragment;
    }

    @Override
    public PlacesAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mainView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_places, viewGroup, false);
        return new PlacesAdapter.PlaceViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(final PlacesAdapter.PlaceViewHolder holder, int position) {
        holder.card.setOnClickListener(v -> {});
    }

    @Override
    public int getItemCount() {
        return listOfFPlaces.size();
    }

}
