package c4q.nyc.take2.accessfoodnyc;

import java.util.Date;

public class Review {

    private Vendor vendor;
    private String title;
    private String description;
    private Date date;
    private int rating;

    public Review() {
    }

    public Review(Vendor vendor, String title, String description, Date date, int rating) {
        this.vendor = vendor;
        this.title = title;
        this.description = description;
        this.date = date;
        this.rating = rating;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
