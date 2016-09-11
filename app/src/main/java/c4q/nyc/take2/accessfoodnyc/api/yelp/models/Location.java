
package c4q.nyc.take2.accessfoodnyc.api.yelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Location {

    @Expose
    private String city;

    @SerializedName("display_address")
    @Expose
    private List<String> displayAddress = new ArrayList<String>();

    @SerializedName("geo_accuracy")
    @Expose
    private Double geoAccuracy;

    @SerializedName("postal_code")
    @Expose
    private String postalCode;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @Expose
    private List<Object> address = new ArrayList<Object>();
    @Expose
    private Coordinate coordinate;
    @SerializedName("state_code")
    @Expose
    private String stateCode;

    /**
     * 
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The displayAddress
     */
    public List<String> getDisplayAddress() {
        return displayAddress;
    }

    /**
     * 
     * @param displayAddress
     *     The display_address
     */
    public void setDisplayAddress(List<String> displayAddress) {
        this.displayAddress = displayAddress;
    }

    /**
     * 
     * @return
     *     The geoAccuracy
     */
    public Double getGeoAccuracy() {
        return geoAccuracy;
    }

    /**
     * 
     * @param geoAccuracy
     *     The geo_accuracy
     */
    public void setGeoAccuracy(Double geoAccuracy) {
        this.geoAccuracy = geoAccuracy;
    }

    /**
     * 
     * @return
     *     The postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * 
     * @param postalCode
     *     The postal_code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * 
     * @return
     *     The countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 
     * @param countryCode
     *     The country_code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * 
     * @return
     *     The address
     */
    public List<Object> getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(List<Object> address) {
        this.address = address;
    }

    /**
     * 
     * @return
     *     The coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * 
     * @param coordinate
     *     The coordinate
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * 
     * @return
     *     The stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * 
     * @param stateCode
     *     The state_code
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

}
