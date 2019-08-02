package com.example.artravel;

import com.example.artravel.models.Path;

public class DetailedPathViewModel {

    private Path path;

    public void setPath(Path path) {
        this.path = path;
    }

    public String getPathName() {
        return path.getPathName();
    }

    public String getPathDescription() {
        return path.getPathDescription();
    }

    public float getPathRating() {
        return path.getPathRatingAvg();
    }

}


