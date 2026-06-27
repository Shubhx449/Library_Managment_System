package model;

import java.time.LocalDateTime;

public class Category {
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String d){
        this.description = d;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime t){
        this.createdAt = t;
    }

    @Override
    public String toString() {
        return String.format("Category[id=%d, name=%s]", id, name);
    }
}