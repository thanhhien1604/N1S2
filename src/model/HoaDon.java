package model;

import java.sql.Date;

public class HoaDon {

    private Integer id;
    private String ma;
    private Date ngayTao;
    private Double tongTien;
    private Boolean trangThai;
    private Integer idNV;
    private NhanVien nv;

    public HoaDon() {
    }

    public HoaDon(Integer id, String ma, Date ngayTao, Double tongTien, Boolean trangThai, Integer idNV, NhanVien nv) {
        this.id = id;
        this.ma = ma;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.idNV = idNV;
        this.nv = nv;
    }

    public HoaDon(String ma) {
        this.ma = ma;
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

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    public NhanVien getNv() {
        return nv;
    }

    public void setNv(NhanVien nv) {
        this.nv = nv;
    }

    public Integer getIdNV() {
        return idNV;
    }

    public void setIdNV(Integer idNV) {
        this.idNV = idNV;
    }

}
