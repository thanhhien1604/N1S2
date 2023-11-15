/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author ledin
 */
public class Voucher {

    private Integer id;
    private Integer id_NV;
    private String ma;
    private String ten;
    private Date ngayTao;
    private NhanVien nv;

    public Voucher() {
    }

    public Voucher(Integer id, Integer id_NV, String ma, String ten, Date ngayTao, NhanVien nv) {
        this.id = id;
        this.id_NV = id_NV;
        this.ma = ma;
        this.ten = ten;
        this.ngayTao = ngayTao;
        this.nv = nv;
    }

    public Voucher(String ten, String ma, NhanVien nv) {
        this.ten = ten;
        this.ma = ma;
        this.nv = nv;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_NV() {
        return id_NV;
    }

    public void setId_NV(Integer id_NV) {
        this.id_NV = id_NV;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public NhanVien getNv() {
        return nv;
    }

    public void setNv(NhanVien nv) {
        this.nv = nv;
    }

    @Override
    public String toString() {
        return this.getTen();
    }
}
