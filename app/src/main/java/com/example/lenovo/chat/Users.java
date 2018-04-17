package com.example.lenovo.chat;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

//@Table(database = MyDatabase.class)
public class Users {
//    extends
//} BaseModel {
//    @Column
//    @PrimaryKey(autoincrement = true)
//    int id;
//    @Column
//    String name;
//    @Column
//    String image;
//    @Column
//    String status;

    String id;
    String name;
    int image;
    String status;

    public Users() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Users(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public Users(String id,String name, int image, String status) {
        this.id=id;
        this.name = name;
        this.image = image;
        this.status = status;
    }

//    public int getId() {
//        return id;
//    }

//    public void setId(int id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

