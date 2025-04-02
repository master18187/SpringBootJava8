package com.example.common.context;

public class ProfileHolder {

    public static final ThreadLocal<Profile> profileHolder = new ThreadLocal<>();

    public static void setProfile(Profile profile) {
        profileHolder.set(profile);
    }

    public static Profile getProfile() {
        return profileHolder.get();
    }

    public static void clearProfile() {
        profileHolder.remove();
    }
}
