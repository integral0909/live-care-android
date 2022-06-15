package app.client.munchbear.munchbearclient.model;

public class SliderPromoCode {

    String promocode;
    String imageUrl;
    String title;

    public SliderPromoCode(String promocode, String imageUrl, String title) {
        this.promocode = promocode;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
