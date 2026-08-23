package com.spendsmart.helpers;

import android.content.Context;
import com.spendsmart.R;

import java.util.ArrayList;
import java.util.HashMap;

// OOP: Utility class with static methods
public class CategoryHelper {

    // OOP: Static ArrayList
    public static final ArrayList<String> CATEGORIES = new ArrayList<>();

    // OOP: Static HashMap for emoji mapping
    private static final HashMap<String, String> EMOJI_MAP = new HashMap<>();
    private static final HashMap<String, Integer> COLOR_MAP = new HashMap<>();

    static {
        CATEGORIES.add("Food");
        CATEGORIES.add("Transport");
        CATEGORIES.add("Shopping");
        CATEGORIES.add("Bills");
        CATEGORIES.add("Health");
        CATEGORIES.add("Education");
        CATEGORIES.add("Other");

        EMOJI_MAP.put("Food",       "🍔");
        EMOJI_MAP.put("Transport",  "🚗");
        EMOJI_MAP.put("Shopping",   "🛍️");
        EMOJI_MAP.put("Bills",      "🧾");
        EMOJI_MAP.put("Health",     "💊");
        EMOJI_MAP.put("Education",  "📚");
        EMOJI_MAP.put("Other",      "💰");

        COLOR_MAP.put("Food",       R.color.cat_food);
        COLOR_MAP.put("Transport",  R.color.cat_transport);
        COLOR_MAP.put("Shopping",   R.color.cat_shopping);
        COLOR_MAP.put("Bills",      R.color.cat_bills);
        COLOR_MAP.put("Health",     R.color.cat_health);
        COLOR_MAP.put("Education",  R.color.cat_education);
        COLOR_MAP.put("Other",      R.color.cat_other);
    }

    public static String getEmoji(String category) {
        return EMOJI_MAP.getOrDefault(category, "💰");
    }

    public static int getColor(Context ctx, String category) {
        Integer resId = COLOR_MAP.get(category);
        if (resId != null) {
            return ctx.getColor(resId);
        }
        return ctx.getColor(R.color.primary);
    }

    public static String formatAmount(double amount) {
        if (amount == (long) amount) {
            return "Rs. " + (long) amount;
        }
        return String.format("Rs. %.2f", amount);
    }
}
