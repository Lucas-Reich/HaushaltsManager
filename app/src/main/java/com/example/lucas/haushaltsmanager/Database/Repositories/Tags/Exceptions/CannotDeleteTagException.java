package com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class CannotDeleteTagException extends CouldNotDeleteEntityException {
    public CannotDeleteTagException(Tag tag) {
        super("Tag " + tag.getName() + " could not be deleted.");
    }
}
