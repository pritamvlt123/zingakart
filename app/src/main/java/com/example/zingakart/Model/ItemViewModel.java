package com.example.zingakart.Model;

public class ItemViewModel {
    private String name;
    private String price;
    private String discount_price;
   // private int thumbnail;

    public ItemViewModel() {
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public ItemViewModel(String name, String discount_price, String price) {
        this.name = name;
        this.price = price;
       // this.thumbnail = thumbnail;
        this.discount_price = discount_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    //public int getThumbnail() {
      //  return thumbnail;
    //}

    //public void setThumbnail(int thumbnail) {
      //  this.thumbnail = thumbnail;
   // }
}