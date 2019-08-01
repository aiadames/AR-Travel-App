package com.example.artravel;

import com.example.artravel.models.Gems;

public class DetailViewModel {


    private String name;
    private String description;


    public DetailViewModel(Gems gems) {
        this.name = gems.getName();
        this.description = gems.getDescription();
    }

    public String getName(){
            return name;
    }

    public String getDescription(){
            return description;
        }



}
