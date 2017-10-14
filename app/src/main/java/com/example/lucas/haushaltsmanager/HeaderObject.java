package com.example.lucas.haushaltsmanager;

class HeaderObject {

    private String title;
    private double totalPrice;
    private String baseCurrency;

    public HeaderObject() {

        //TODO make base currency variable
        this.baseCurrency = "€";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
}
