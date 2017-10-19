package me.fmy.galaxy_a7.models;

/**
 * Created by Femmy on 7/24/2016.
 */
public class User {

    public int id;

    public String name;

    public String email;

    public String instagram;

    public int song;

    public String video;

    public User(){

    }

    public User(int id, String name, String email, String instagram){
        id          = id;
        name        = name;
        email       = email;
        instagram   = instagram;
    }

    public User(int id, String name, String email, String instagram, int song, String video){
        id          = id;
        name        = name;
        email       = email;
        instagram   = instagram;
        song        = song;
        video       = video;
    }

}
