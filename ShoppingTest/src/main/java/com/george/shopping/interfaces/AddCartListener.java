package com.george.shopping.interfaces;

public interface AddCartListener {
    /**
     * 商品添加到购物车
     * @param id 商品id
     * @param name 商品名称
     */
    void addToCart(int id, String name);
}
