/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ledin
 */
public class HoaDonChiTiet {

    private Integer id;
    private Double gia;
    private Integer soLuong;
    private Double tongTien;
    private String SDT_KH;
    private Integer idSP;
    private Integer idHD;
    private Integer idKH;
    private Integer idVC;
    private SanPhamCT spct;
    private HoaDon hd;
    private KhachHang kh;
    private VoucherCT vcct;

    public HoaDonChiTiet() {
    }

    public HoaDonChiTiet(Integer id, Double gia, Integer soLuong, Double tongTien, String SDT_KH, Integer idSP, Integer idHD, Integer idKH, Integer idVC, SanPhamCT spct, HoaDon hd, KhachHang kh, VoucherCT vcct) {
        this.id = id;
        this.gia = gia;
        this.soLuong = soLuong;
        this.tongTien = tongTien;
        this.SDT_KH = SDT_KH;
        this.idSP = idSP;
        this.idHD = idHD;
        this.idKH = idKH;
        this.idVC = idVC;
        this.spct = spct;
        this.hd = hd;
        this.kh = kh;
        this.vcct = vcct;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getGia() {
        return gia;
    }

    public void setGia(Double gia) {
        this.gia = gia;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public String getSDT_KH() {
        return SDT_KH;
    }

    public void setSDT_KH(String SDT_KH) {
        this.SDT_KH = SDT_KH;
    }

    public Integer getIdSP() {
        return idSP;
    }

    public void setIdSP(Integer idSP) {
        this.idSP = idSP;
    }

    public Integer getIdHD() {
        return idHD;
    }

    public void setIdHD(Integer idHD) {
        this.idHD = idHD;
    }

    public Integer getIdKH() {
        return idKH;
    }

    public void setIdKH(Integer idKH) {
        this.idKH = idKH;
    }

    public Integer getIdVC() {
        return idVC;
    }

    public void setIdVC(Integer idVC) {
        this.idVC = idVC;
    }

    public SanPhamCT getSpct() {
        return spct;
    }

    public void setSpct(SanPhamCT spct) {
        this.spct = spct;
    }

    public HoaDon getHd() {
        return hd;
    }

    public void setHd(HoaDon hd) {
        this.hd = hd;
    }

    public KhachHang getKh() {
        return kh;
    }

    public void setKh(KhachHang kh) {
        this.kh = kh;
    }

    public VoucherCT getVcct() {
        return vcct;
    }

    public void setVcct(VoucherCT vcct) {
        this.vcct = vcct;
    }

}
