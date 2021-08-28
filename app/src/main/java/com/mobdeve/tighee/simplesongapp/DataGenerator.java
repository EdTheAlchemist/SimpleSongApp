package com.mobdeve.tighee.simplesongapp;

import java.util.ArrayList;

public class DataGenerator {
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

        return data;
    }
}
