/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ledin
 */
public class MauSac {

    private Integer id;
    private String ten;

    public MauSac() {
    }

    public MauSac(Integer id, String ten) {
        this.id = id;
        this.ten = ten;
    }

    public MauSac(String ten) {
        this.ten = ten;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    @Override
    public String toString() {
        return this.ten;
    }
}
