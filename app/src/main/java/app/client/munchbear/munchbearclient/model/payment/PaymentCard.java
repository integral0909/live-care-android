package app.client.munchbear.munchbearclient.model.payment;

public class PaymentCard {

    private String number;
    private String expiryDate;
    private int cvvCode;

    public PaymentCard(String number, String expiryDate, int cvvCode) {
        this.number = number;
        this.expiryDate = expiryDate;
        this.cvvCode = cvvCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(int cvvCode) {
        this.cvvCode = cvvCode;
    }
}
