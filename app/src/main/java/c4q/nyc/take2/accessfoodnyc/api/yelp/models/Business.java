
package c4q.nyc.take2.accessfoodnyc.api.yelp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Business {

    @SerializedName("is_claimed")
    @Expose
    private Boolean isClaimed;
    @Expose
    private Double rating;
    @SerializedName("mobile_url")
    @Expose
    private String mobileUrl;
    @SerializedName("rating_img_url")
    @Expose
    private String ratingImgUrl;
    @SerializedName("review_count")
    @Expose
    private Integer reviewCount;
    @Expose
    private String name;
    @SerializedName("rating_img_url_small")
    @Expose
    private String ratingImgUrlSmall;
    @Expose
    private String url;
    @SerializedName("is_closed")
    @Expose
    private Boolean isClosed;
    @Expose
    private String id;
    @Expose
    private String phone;
    @SerializedName("snippet_text")
    @Expose
    private String snippetText;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @Expose
    private List<List<String>> categories = new ArrayList<List<String>>();
    @SerializedName("display_phone")
    @Expose
    private String displayPhone;
    @SerializedName("rating_img_url_large")
    @Expose
    private String ratingImgUrlLarge;
    @SerializedName("menu_provider")
    @Expose
    private String menuProvider;
    @SerializedName("menu_date_updated")
    @Expose
    private Integer menuDateUpdated;
    @SerializedName("snippet_image_url")
    @Expose
    private String snippetImageUrl;
    @Expose
    private Location location;

    /**
     * 
     * @return
     *     The isClaimed
     */
    public Boolean getIsClaimed() {
        return isClaimed;
    }

    /**
     * 
     * @param isClaimed
     *     The is_claimed
     */
    public void setIsClaimed(Boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    /**
     * 
     * @return
     *     The rating
     */
    public Double getRating() {
        return rating;
    }

    /**
     * 
     * @param rating
     *     The rating
     */
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     * 
     * @return
     *     The mobileUrl
     */
    public String getMobileUrl() {
        return mobileUrl;
    }

    /**
     * 
     * @param mobileUrl
     *     The mobile_url
     */
    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    /**
     * 
     * @return
     *     The ratingImgUrl
     */
    public String getRatingImgUrl() {
        return ratingImgUrl;
    }

    /**
     * 
     * @param ratingImgUrl
     *     The rating_img_url
     */
    public void setRatingImgUrl(String ratingImgUrl) {
        this.ratingImgUrl = ratingImgUrl;
    }

    /**
     * 
     * @return
     *     The reviewCount
     */
    public Integer getReviewCount() {
        return reviewCount;
    }

    /**
     * 
     * @param reviewCount
     *     The review_count
     */
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The ratingImgUrlSmall
     */
    public String getRatingImgUrlSmall() {
        return ratingImgUrlSmall;
    }

    /**
     * 
     * @param ratingImgUrlSmall
     *     The rating_img_url_small
     */
    public void setRatingImgUrlSmall(String ratingImgUrlSmall) {
        this.ratingImgUrlSmall = ratingImgUrlSmall;
    }

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The isClosed
     */
    public Boolean getIsClosed() {
        return isClosed;
    }

    /**
     * 
     * @param isClosed
     *     The is_closed
     */
    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    /**
     * 
     * @return
     *     The x
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 
     * @param phone
     *     The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 
     * @return
     *     The snippetText
     */
    public String getSnippetText() {
        return snippetText;
    }

    /**
     * 
     * @param snippetText
     *     The snippet_text
     */
    public void setSnippetText(String snippetText) {
        this.snippetText = snippetText;
    }

    /**
     * 
     * @return
     *     The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 
     * @param imageUrl
     *     The image_url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 
     * @return
     *     The categories
     */
    public List<List<String>> getCategories() {
        return categories;
    }

    /**
     * 
     * @param categories
     *     The categories
     */
    public void setCategories(List<List<String>> categories) {
        this.categories = categories;
    }

    /**
     * 
     * @return
     *     The displayPhone
     */
    public String getDisplayPhone() {
        return displayPhone;
    }

    /**
     * 
     * @param displayPhone
     *     The display_phone
     */
    public void setDisplayPhone(String displayPhone) {
        this.displayPhone = displayPhone;
    }

    /**
     * 
     * @return
     *     The ratingImgUrlLarge
     */
    public String getRatingImgUrlLarge() {
        return ratingImgUrlLarge;
    }

    /**
     * 
     * @param ratingImgUrlLarge
     *     The rating_img_url_large
     */
    public void setRatingImgUrlLarge(String ratingImgUrlLarge) {
        this.ratingImgUrlLarge = ratingImgUrlLarge;
    }

    /**
     * 
     * @return
     *     The menuProvider
     */
    public String getMenuProvider() {
        return menuProvider;
    }

    /**
     * 
     * @param menuProvider
     *     The menu_provider
     */
    public void setMenuProvider(String menuProvider) {
        this.menuProvider = menuProvider;
    }

    /**
     * 
     * @return
     *     The menuDateUpdated
     */
    public Integer getMenuDateUpdated() {
        return menuDateUpdated;
    }

    /**
     * 
     * @param menuDateUpdated
     *     The menu_date_updated
     */
    public void setMenuDateUpdated(Integer menuDateUpdated) {
        this.menuDateUpdated = menuDateUpdated;
    }

    /**
     * 
     * @return
     *     The snippetImageUrl
     */
    public String getSnippetImageUrl() {
        return snippetImageUrl;
    }

    /**
     * 
     * @param snippetImageUrl
     *     The snippet_image_url
     */
    public void setSnippetImageUrl(String snippetImageUrl) {
        this.snippetImageUrl = snippetImageUrl;
    }

    /**
     * 
     * @return
     *     The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

}
