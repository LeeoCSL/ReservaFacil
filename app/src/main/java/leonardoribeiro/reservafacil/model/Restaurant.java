package leonardoribeiro.reservafacil.model;


public class Restaurant {

    private String address;

    private String logo;

    private String email;

    private String name;

    private String key;


    public Restaurant() {
    }

    public Restaurant(String address, String logo, String name, String key,String email) {
        this.address = address;
        this.logo = logo;
        this.name = name;
        this.key = key;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        return "Restaurant{" +
                "address=" + address +
                ", logo='" + logo + '\'' +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}