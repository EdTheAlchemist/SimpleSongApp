package com.mobdeve.tighee.simplemusicapp;

import java.util.ArrayList;

public class HelperClass {
    // Constants for intent actions
    public static final String
        PLAY_ACTION = "com.mobdeve.tighee.simplemusicapp.play_action",
        PAUSE_ACTION = "com.mobdeve.tighee.simplemusicapp.pause_action",
        STOP_ACTION = "com.mobdeve.tighee.simplemusicapp.stop_action";

    // Constants for intent keys
    public static final String
        DURATION_INTENT_KEY = "DURATION_INTENT_KEY",
        TITLE_INTENT_KEY = "TITLE_INTENT_KEY",
        ARTIST_INTENT_KEY = "ARTIST_INTENT_KEY",
        ALBUM_IMAGE_INTENT_KEY = "ALBUM_IMAGE_INTENT_KEY";

    // In the repository, none of the resources are shared, so if you're coming from there, please
    // supply your own data.
    public static ArrayList<Song> generateData() {
        ArrayList<Song> data = new ArrayList<>();

        data.add(new Song(
                "Inferno (インフェルノ)",
                "Mrs. Green Apple",
                "Attitude",
                R.drawable.inferno_attitude,
                R.raw.mrs_green_apple_inferno
        ));
        data.add(new Song(
                "Departure!",
                "Masatoshi Ono",
                "Hunter x Hunter OST",
                R.drawable.departure_hunter_x_hunter,
                R.raw.masatoshi_ono_departure
        ));
        data.add(new Song(
                "Kyouran Hey Kids!!",
                "THE ORAL CIGARETTES",
                "FIXION",
                R.drawable.hey_kids_fixion,
                R.raw.the_oral_cigarettes_hey_kids
        ));
        data.add(new Song(
                "LOST IN PARADISE",
                "ALI feat. AKLO",
                "LOST IN PARADISE (single)",
                R.drawable.lost_in_paradise_single,
                R.raw.ali_ft_aklo_lost_in_paradise
        ));
        data.add(new Song(
                "Flyers",
                "BRADIO",
                "Power of Life",
                R.drawable.flyers_power_of_life,
                R.raw.bradio_flyers
        ));
        data.add(new Song(
                "Sudachi no Uta",
                "Anemoneria",
                "Wonder Egg Edition",
                R.drawable.sudachi_no_uta_anemoeria,
                R.raw.wonder_egg_edition_sudachi_no_uta
        ));
        data.add(new Song(
                "Centimeter (センチメートル)",
                "The Peggies",
                "Centimeter (Single)",
                R.drawable.centimeter_single,
                R.raw.the_peggies_centimeter
        ));
        data.add(new Song(
                "VORACITY",
                "Myth and Roid",
                "VORACITY (Single)",
                R.drawable.voracity_single,
                R.raw.myth_and_roid_voracity
        ));

        return data;
    }
}
