package app.client.munchbear.munchbearclient.utils;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import app.client.munchbear.munchbearclient.model.dish.DishSize;
import app.client.munchbear.munchbearclient.model.modifier.ExcludeModifier;
import app.client.munchbear.munchbearclient.model.modifier.MandatoryModifier;
import app.client.munchbear.munchbearclient.model.modifier.Modifier;
import app.client.munchbear.munchbearclient.model.modifier.OptionalModifier;
import app.client.munchbear.munchbearclient.model.modifier.category.ExcludeModifierCategory;
import app.client.munchbear.munchbearclient.model.modifier.category.MandatoryModifierCategory;
import app.client.munchbear.munchbearclient.model.modifier.category.ModifierCategory;
import app.client.munchbear.munchbearclient.model.modifier.category.OptionalModifierCategory;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;

/**
 * @author Roman H.
 */

public class DishHelper {

//    public static double getCostFromOptionalModifiers(double totalCost, List<OptionalModifierCategory> categoryOptionalList,
//                                                      boolean isDollar) {
//        double cost = totalCost;
//        if (categoryOptionalList != null && categoryOptionalList.size() > 0) {
//            for (OptionalModifierCategory categoryOptional : categoryOptionalList) {
//                for (OptionalModifier optionalModifier: categoryOptional.getOptionalModifierList()) {
//                    if (optionalModifier.isSelected()) {
//                        double selectedPrice = optionalModifier.getPriceInDollar(isDollar, true);
//                        cost = cost + selectedPrice;
//                    }
//                }
//            }
//        }
//        return cost;
//    }

    /**
     * @param totalCost previous total sum of dish
     * @param categoryOptionalList list of all optional modifiers
     * @return sum in dollars of previous total sum of dish and all selected optional modifiers
     */
    public static double getDollarCostFromOptionalModifiers(double totalCost, List<OptionalModifierCategory> categoryOptionalList) {
        double cost = totalCost;
        if (categoryOptionalList != null && categoryOptionalList.size() > 0) {
            for (OptionalModifierCategory categoryOptional : categoryOptionalList) {
                for (OptionalModifier optionalModifier: categoryOptional.getOptionalModifierList()) {
                    if (optionalModifier.isSelected()) {
                        double selectedPrice = optionalModifier.getPriceInDollar(true);
                        cost = cost + selectedPrice;
                    }
                }
            }
        }
        return cost;
    }

    /**
     * @param totalCost previous total sum of dish
     * @param categoryOptionalList list of all optional modifiers
     * @return sum in loyalty points of previous total sum of dish and all selected optional modifiers
     */
    public static int getPointCostFromOptionalModifiers(int totalCost, List<OptionalModifierCategory> categoryOptionalList) {
        int cost = totalCost;
        if (categoryOptionalList != null && categoryOptionalList.size() > 0) {
            for (OptionalModifierCategory categoryOptional : categoryOptionalList) {
                for (OptionalModifier optionalModifier: categoryOptional.getOptionalModifierList()) {
                    if (optionalModifier.isSelected()) {
                        int selectedPrice = optionalModifier.getPricePoint();
                        cost = cost + selectedPrice;
                    }
                }
            }
        }
        return cost;
    }

    public static double getDollarCostFromMandatoryModifiers(double totalCost, List<MandatoryModifierCategory> categoryMandatoryList) {
        double cost = totalCost;
        if (categoryMandatoryList != null && categoryMandatoryList.size() > 0) {
            for (MandatoryModifierCategory categoryMandatory: categoryMandatoryList) {
                for (MandatoryModifier mandatoryModifier: categoryMandatory.getMandatoryModifierList()) {
                    if (mandatoryModifier.isSelected()) {
                        double selectedPrice = mandatoryModifier.getPriceInDollar();
                        cost = cost + selectedPrice;
                    }
                }
            }
        }
        return cost;
    }

    public static int getPointCostFromMandatoryModifiers(int totalCost, List<MandatoryModifierCategory> categoryMandatoryList) {
        int cost = totalCost;
        if (categoryMandatoryList != null && categoryMandatoryList.size() > 0) {
            for (MandatoryModifierCategory categoryMandatory: categoryMandatoryList) {
                for (MandatoryModifier mandatoryModifier: categoryMandatory.getMandatoryModifierList()) {
                    if (mandatoryModifier.isSelected()) {
                        int selectedPrice = mandatoryModifier.getPricePoint();
                        cost = cost + selectedPrice;
                    }
                }
            }
        }
        return cost;
    }

//    public static double getCostFromMandatoryModifiers(double totalCost, List<MandatoryModifierCategory> categoryMandatoryList,
//                                                       boolean isDollar) {
//        double cost = totalCost;
//        if (categoryMandatoryList != null && categoryMandatoryList.size() > 0) {
//            for (MandatoryModifierCategory categoryMandatory: categoryMandatoryList) {
//                for (MandatoryModifier mandatoryModifier: categoryMandatory.getMandatoryModifierList()) {
//                    if (mandatoryModifier.isSelected()) {
//                        double selectedPrice = mandatoryModifier.getPriceInDollar(isDollar);
//                        cost = cost + selectedPrice;
//                    }
//                }
//            }
//        }
//        return cost;
//    }

    /**
     * For first time getting restaurant menu first item of category of menu, dish size
     * and dish mandatory modifier must be selected
     */
    public static void initFirstState(List<DishCategory> dishCategoryList) {
        if (dishCategoryList != null && !dishCategoryList.isEmpty()) {
            selectFirsDishCategory(dishCategoryList);
            for (DishCategory dishCategory : dishCategoryList) {
                if (dishCategory != null && dishCategory.getDishList().size() > 0) {
                    for (Dish dish : dishCategory.getDishList()) {
                         selectFirstMandatoryModifier(dish.getMandatoryModifierList());
                         selectFirsDishSize(dish.getDishSizeList());
                    }
                }
            }
        }
    }

    private static void selectFirsDishSize(List<DishSize> dishSizeList) {
        if (dishSizeList != null && dishSizeList.size() > 0) {
            dishSizeList.get(0).setSelected(true);
        }
    }

    private static void selectFirstMandatoryModifier(List<MandatoryModifierCategory> mandatoryModifierCategoryList) {
        if (mandatoryModifierCategoryList != null && !mandatoryModifierCategoryList.isEmpty()) {
            for (MandatoryModifierCategory categoryMandatory: mandatoryModifierCategoryList) {
                if (categoryMandatory.getMandatoryModifierList() != null) {
                    categoryMandatory.getMandatoryModifierList().get(0).setSelected(true);
                }
            }
        }
    }

    private static void selectFirsDishCategory(List<DishCategory> dishCategoryList) {
        dishCategoryList.get(0).setSelected(true);
    }

    public static List<ExcludeModifier> getSelectedExclude(List<ExcludeModifierCategory> excludeModifierList) {
        List<ExcludeModifier> excludeModifiers = new ArrayList<>();
        if (excludeModifierList != null) {
            for (ExcludeModifierCategory categoryExclude : excludeModifierList) {
                excludeModifiers.addAll(categoryExclude.getSelectedList());
            }
        }
        return excludeModifiers;
    }

    public static List<OptionalModifier> getAllSelectedOptional(List<OptionalModifierCategory> optionalModifierList) {
        List<OptionalModifier> excludeModifiers = new ArrayList<>();
        if (optionalModifierList != null) {
            for (OptionalModifierCategory categoryOptional : optionalModifierList) {
                excludeModifiers.addAll(categoryOptional.getSelectedList());
            }
        }
        return excludeModifiers;
    }

    public static List<MandatoryModifier> getAllSelectedMandatory(List<MandatoryModifierCategory> mandatoryModifierCategoryList) {
        List<MandatoryModifier> mandatoryModifiers = new ArrayList<>();
        if (mandatoryModifierCategoryList != null) {
            for (MandatoryModifierCategory mandatoryCategory : mandatoryModifierCategoryList) {
                mandatoryModifiers.addAll(mandatoryCategory.getSelectedList());
            }
        }

        return mandatoryModifiers;
    }

    public static String getMandatoryModifierTitles(Resources resources, List<MandatoryModifierCategory> categoryMandatoryList) {
        StringBuilder titleBuilder = new StringBuilder();

        if (categoryMandatoryList != null && categoryMandatoryList.size() > 0) {
            for (int i = 0; i < categoryMandatoryList.size(); i++) {
                MandatoryModifierCategory category = categoryMandatoryList.get(i);
                addCategoryTitle(resources, titleBuilder, category);

                for (int j = 0; j < category.getSelectedList().size(); j++) {
                    MandatoryModifier modifier = category.getSelectedList().get(j);
                    addModifierTitle(resources, titleBuilder, modifier, 0,category.getSelectedList().size() > 1
                            && j < category.getSelectedList().size() - 1);
                }

                addNewLine(i, categoryMandatoryList.size(), titleBuilder);
            }
        }

        return titleBuilder.toString();
    }

    public static String getSelectedExcludeModifierTitle(Resources resources, List<ExcludeModifierCategory> categoryExcludeList) {
        StringBuilder stringBuilder = new StringBuilder();

        if (categoryExcludeList != null && categoryExcludeList.size() > 0) {
            for (int i = 0; i < categoryExcludeList.size(); i++) {
                ExcludeModifierCategory category = categoryExcludeList.get(i);

                if (category.getSelectedList().size() > 0) {
                    addCategoryTitle(resources, stringBuilder, category);

                    for (int j = 0; j < category.getSelectedList().size(); j++) {
                        ExcludeModifier modifier = category.getSelectedList().get(j);
                        addModifierTitle(resources, stringBuilder, modifier, 0, category.getSelectedList().size() > 1
                                && j < category.getSelectedList().size() - 1);
                    }

                    addNewLine(i, categoryExcludeList.size(), stringBuilder);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String getSelectedOptionalModifierTitle(Resources resources, List<OptionalModifierCategory> categoryOptionalList) {
        StringBuilder stringBuilder = new StringBuilder();

        if (categoryOptionalList != null) {
            for (int i = 0; i < categoryOptionalList.size(); i++) {
                OptionalModifierCategory category = categoryOptionalList.get(i);

                if (category.getSelectedList().size() > 0) {
                    addCategoryTitle(resources, stringBuilder, category);

                    for (int j = 0; j < category.getSelectedList().size(); j++) {
                        OptionalModifier modifier = category.getSelectedList().get(j);
                        addModifierTitle(resources, stringBuilder, modifier, modifier.getCounter() ,category.getSelectedList().size() > 1
                                && j < category.getSelectedList().size() - 1);
                    }

                    addNewLine(i, categoryOptionalList.size(), stringBuilder);
                }
            }
        }
        return stringBuilder.toString();
    }

    private static void addCategoryTitle(Resources resources, StringBuilder stringBuilder, ModifierCategory category) {
        stringBuilder.append(String.format(resources.getString(R.string.cart_dish_modifier_format_category),
                category.getName()));
        stringBuilder.append(" ");
    }

    private static void addModifierTitle(Resources resources, StringBuilder stringBuilder, Modifier modifier,
                                         int count, boolean withComma) {
        if (withComma) {
            String modifierStr;
            if (count > 1) {
                modifierStr = String.format(resources.getString(R.string.cart_dish_counter_modifier_value_format_comma),
                        modifier.getName(), count);
            } else {
                modifierStr = String.format(resources.getString(R.string.cart_dish_mandatory_modifier_value_format),
                        modifier.getName());
            }
            stringBuilder.append(modifierStr).append(" ");
        } else {
            if (count > 1) {
                String str = String.format(resources.getString(R.string.cart_dish_counter_modifier_value_format),
                        modifier.getName(), count);
                stringBuilder.append(str);
            } else {
                stringBuilder.append(modifier.getName());
            }
        }
    }

    private static void addNewLine(int index, int size, StringBuilder stringBuilder) {
        if (index < size - 1) {
            stringBuilder.append("\n");
        }
    }

    /**
     * Method clear all selection of category of dish and after then
     * selecting new category
     * @param newSelectCategory index of new category for selecting
     * @param dishCategoryList List of all category of dish
     */
    public static void reselectDishCategory(int newSelectCategory, List<DishCategory> dishCategoryList) {
        clearAllSelection(dishCategoryList);
        dishCategoryList.get(newSelectCategory).setSelected(true);
        CorePreferencesImpl.getCorePreferences().setSelectedRestaurantCategory(dishCategoryList);
    }

    /**
     * Method clear all selection of category of dish.
     * It`s need when user selected another category from main screen
     */
    private static void clearAllSelection(List<DishCategory> dishCategoryList) {
        for (DishCategory category : dishCategoryList) {
            category.setSelected(false);
        }
    }

    /**
     * @param dishCategoryList
     * @return index of selected category
     * Return index 0 if any category selected
     */
    public static int getSelectedCategoryIndex(List<DishCategory> dishCategoryList) {
        for (int i = 0; i < dishCategoryList.size(); i++) {
            if (dishCategoryList.get(i).isSelected()) {
                return i;
            }
        }

        return 0;
    }

}
