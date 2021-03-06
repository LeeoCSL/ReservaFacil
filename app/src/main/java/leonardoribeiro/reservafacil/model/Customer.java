package leonardoribeiro.reservafacil.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonardo.ribeiro on 18/10/2017.
 */

public class Customer {
    protected String key;
    protected String name;
    protected String phone;
    protected String email;
    protected String password;
    private String image;


    public Customer() {

    }

    public Customer(String key, String name, String phone, String email, String password, String image) {
        this.key = key;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;

        this.image = image;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    @Override
    public String toString() {
        return "Customer{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}