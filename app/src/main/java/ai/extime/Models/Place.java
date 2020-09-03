package ai.extime.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by teaarte on 10.10.2017.
 */

public  class Place implements ClusterItem {

    public String title;

    public LatLng latLng;

    public Place(){

    }

    public Place(String title, LatLng latLng) {
        this.title = title;
        this.latLng = latLng;
    }
    @Override public LatLng getPosition() {
        return latLng;
    }
}
