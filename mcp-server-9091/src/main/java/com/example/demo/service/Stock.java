package com.example.demo.service;

public class Stock {
    private String symbol;
    private String name;
    private int price;

    public Stock(String symbol, String name, int price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public int getPrice() { return price; }
}