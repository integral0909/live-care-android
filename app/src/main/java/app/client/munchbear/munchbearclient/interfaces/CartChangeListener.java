package app.client.munchbear.munchbearclient.interfaces;

public interface CartChangeListener {
    void deleteDish();
    void clearCart();
    void editDish(int position);
}
