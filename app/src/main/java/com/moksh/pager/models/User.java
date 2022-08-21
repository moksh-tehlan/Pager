package com.moksh.pager.models;

import java.io.Serializable;

// Serializalbe interface is used to convert an object to byte stream. So the user can pass the data between one
// acitivity to another
public class User implements Serializable {
    public String name,image,email,token;
}
