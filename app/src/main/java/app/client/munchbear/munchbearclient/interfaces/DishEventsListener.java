package app.client.munchbear.munchbearclient.interfaces;

import app.client.munchbear.munchbearclient.model.dish.Dish;

/*
 * @author Nazar V.
 */
public interface DishEventsListener {

    void onDishAdded(Dish dish);
    void onDishEdited(Dish dish, int position);
}
