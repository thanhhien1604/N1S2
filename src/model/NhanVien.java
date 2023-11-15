/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ledin
 */
public class NhanVien {

    private Integer id;
    private String ma;
    private String pass;
    private String ten;
    private String sdt;
    private String email;
    private Boolean chucVu;
    private Boolean trangThai;

    public NhanVien() {
    }

    public NhanVien(Integer id, String ma, String pass, String ten, String sdt, String email, Boolean chucVu, Boolean trangThai) {
        this.id = id;
        this.ma = ma;
        this.pass = pass;
        this.ten = ten;
        this.sdt = sdt;
        this.email = email;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }

    public NhanVien(String ma, String ten) {
        this.ma = ma;
        this.ten = ten;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getChucVu() {
        return chucVu;
    }

    public void setChucVu(Boolean chucVu) {
        this.chucVu = chucVu;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return this.getMa();
    }
}
