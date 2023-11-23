package model;

import java.sql.Date;

public class HoaDon {

    private Integer id;
    private String ma;
    private Date ngayTao;
    private Double tongTien;
    private Boolean trangThai;
    private Integer idNV;
    private Integer idKH;
    private NhanVien nv;
    private KhachHang kh;

    public HoaDon() {
    }

    public HoaDon(Integer id, String ma, Date ngayTao, Double tongTien, Boolean trangThai, Integer idNV, Integer idKH, NhanVien nv, KhachHang kh) {
        this.id = id;
        this.ma = ma;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.idNV = idNV;
        this.idKH = idKH;
        this.nv = nv;
        this.kh = kh;
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

    public Integer getIdKH() {
        return idKH;
    }

    public void setIdKH(Integer idKH) {
        this.idKH = idKH;
    }

    public KhachHang getKh() {
        return kh;
    }

    public void setKh(KhachHang kh) {
        this.kh = kh;
    }

}
