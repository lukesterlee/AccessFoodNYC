package c4q.nyc.take2.accessfoodnyc;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Hoshiko on 8/22/15.
 */


public class MarkerCluster implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mBusinessId;

    public MarkerCluster(double lat, double lng, String title, String businessId) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mBusinessId = businessId;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getBizId() {
        return mBusinessId;
    }


}