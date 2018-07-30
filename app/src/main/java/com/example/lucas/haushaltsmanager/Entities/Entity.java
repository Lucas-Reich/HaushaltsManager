package com.example.lucas.haushaltsmanager.Entities;

public abstract class Entity {
    private long mIndex;// vielleicht sollte ich die id selber erstellen. mit einem random uuid generator

    long getId() {
        return mIndex;
    }

    public abstract String toString();
}