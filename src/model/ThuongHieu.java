/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ledin
 */
public class ThuongHieu {

    private Integer id;
    private String ten;
    private boolean trangThai;

    public ThuongHieu() {
    }

    public ThuongHieu(Integer id, String ten, boolean trangThai) {
        this.id = id;
        this.ten = ten;
        this.trangThai = trangThai;
    }

    public ThuongHieu(String ten) {
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

    public boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public String showStatus() {
        if (trangThai == true) {
            return "Đang hoạt động";
        }
        return "Ngừng bán";
    }

    @Override
    public String toString() {
        return this.ten;
    }
}
