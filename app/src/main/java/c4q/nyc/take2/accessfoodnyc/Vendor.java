package c4q.nyc.take2.accessfoodnyc;

import com.google.android.gms.maps.model.Marker;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.List;

public class Vendor {

    private final String id;
    private final boolean isYelp;
    private final String name;
    private final String address;
    private final String hours;
    private final double rating;
    private final ParseGeoPoint location;
    private final Marker marker;
    private final List<ParseObject> friends;
    private final boolean isLiked;
    private final String picture;

    public static class Builder {
        private final String id;

        private boolean isYelp = false;
        private String name = "";
        private String address = "";
        private String hours = "";
        private String picture = "";
        private double rating = 0;
        private ParseGeoPoint location;
        private Marker marker;
        private List<ParseObject> friends;
        private boolean isLiked = false;

        public Builder(String id) {
            this.id = id;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setHours(String hours) {
            this.hours = hours;
            return this;
        }

        public Builder setRating(double rating) {
            this.rating = rating;
            return this;
        }

        public Builder setLocation(ParseGeoPoint location) {
            this.location = location;
            return this;
        }

        public Builder setMarker(Marker marker) {
            this.marker = marker;
            return this;
        }

        public Builder setFriends(List<ParseObject> friends) {
            this.friends = friends;
            return this;
        }

        public Builder isLiked(boolean isLiked) {
            this.isLiked = isLiked;
            return this;
        }

        public Builder isYelp(boolean isYelp) {
            this.isYelp = isYelp;
            return this;
        }

        public Builder setPicture(String picture) {
            this.picture = picture;
            return this;
        }

        public Vendor build() {
            return new Vendor(this);
        }
    }

    private Vendor(Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        hours = builder.hours;
        marker = builder.marker;
        isLiked = builder.isLiked;
        isYelp = builder.isYelp;
        friends = builder.friends;
        location = builder.location;
        rating = builder.rating;
        picture = builder.picture;
    }

    public String getId() {
        return id;
    }

    public boolean isYelp() {
        return isYelp;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getHours() {
        return hours;
    }

    public double getRating() {
        return rating;
    }

    public ParseGeoPoint getLocation() {
        return location;
    }

    public Marker getMarker() {
        return marker;
    }

    public List<ParseObject> getFriends() {
        return friends;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public String getPicture() {
        return picture;
    }

}
