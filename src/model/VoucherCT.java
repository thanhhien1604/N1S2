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
public class VoucherCT {

    private Integer id;
    private Date ngayBatDau;
    private Date ngayHetHan;
    private Integer soLuong;
    private Boolean kieuGiam;
    private Boolean trangThai;
    private Integer id_voucher;
    private Voucher vc;

    public VoucherCT() {
    }

    public VoucherCT(Integer id, Date ngayBatDau, Date ngayHetHan, Integer soLuong, Boolean kieuGiam, Boolean trangThai, Integer id_voucher, Voucher vc) {
        this.id = id;
        this.ngayBatDau = ngayBatDau;
        this.ngayHetHan = ngayHetHan;
        this.soLuong = soLuong;
        this.kieuGiam = kieuGiam;
        this.trangThai = trangThai;
        this.id_voucher = id_voucher;
        this.vc = vc;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Boolean getKieuGiam() {
        return kieuGiam;
    }

    public void setKieuGiam(Boolean kieuGiam) {
        this.kieuGiam = kieuGiam;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(Date ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public Integer getId_voucher() {
        return id_voucher;
    }

    public void setId_voucher(Integer id_voucher) {
        this.id_voucher = id_voucher;
    }

    public Voucher getVc() {
        return vc;
    }

    public void setVc(Voucher vc) {
        this.vc = vc;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

}
