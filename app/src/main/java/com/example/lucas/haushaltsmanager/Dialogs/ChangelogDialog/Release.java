package com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class Release {
    private int mVersionMajor;
    private int mVersionMinor;
    private int mVersionPatch;
    private String mReleaseInformation;
    private List<Bug> mBugs;
    private List<Feature> mFeatures;
    private Calendar mReleaseDate;

    public Release(int major, @Nullable Integer minor, @Nullable Integer patch, @NonNull Calendar releaseDate, List<Feature> features, List<Bug> bugs, String releaseInformation) {

        mVersionMajor = major;
        mVersionMinor = minor != null ? minor : 0;
        mVersionPatch = patch != null ? patch : 0;

        mReleaseDate = releaseDate;

        mReleaseInformation = releaseInformation;

        mFeatures = features;
        mBugs = bugs;
    }

    public void setReleaseInformation(String releaseInformation) {
        mReleaseInformation = releaseInformation;
    }

    public String getReleaseInformation() {
        return mReleaseInformation;
    }

    public void addBugs(List<Bug> bugs) {
        mBugs.addAll(bugs);
    }

    public void addBug(Bug bug) {
        mBugs.add(bug);
    }

    public void addFeatures(List<Feature> features) {
        mFeatures.addAll(features);
    }

    public void addFeature(Feature feature) {
        mFeatures.add(feature);
    }

    public String getReleaseVersion() {

        return String.format("%s.%s.%s", mVersionMajor, mVersionMinor, mVersionPatch);
    }

    public String getReleaseDate() {

        return DateFormat.getDateInstance(DateFormat.SHORT).format(mReleaseDate.getTimeInMillis());
    }

    public List<Bug> getFixedBugs() {
        return mBugs;
    }

    public List<Feature> getAddedFeatures() {
        return mFeatures;
    }
}
