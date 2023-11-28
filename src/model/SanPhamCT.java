package model;

public class SanPhamCT {

    private Integer id;
    private Integer id_sanPham;
    private Integer id_size;
    private Integer id_mauSac;
    private Integer id_chatLieu;
    private Double gia;
    private Integer soLuong;
    private String maSP;
    private Boolean trangThai;
    private SanPham sanPham;
    private Size size;
    private MauSac mauSac;
    private ChatLieu chatLieu;

    public SanPhamCT() {
    }

    public SanPhamCT(Integer id, Integer id_sanPham, Integer id_size, Integer id_mauSac, Integer id_chatLieu, Double gia, Integer soLuong, String maSP, Boolean trangThai, SanPham sanPham, Size size, MauSac mauSac, ChatLieu chatLieu) {
        this.id = id;
        this.id_sanPham = id_sanPham;
        this.id_size = id_size;
        this.id_mauSac = id_mauSac;
        this.id_chatLieu = id_chatLieu;
        this.gia = gia;
        this.soLuong = soLuong;
        this.maSP = maSP;
        this.trangThai = trangThai;
        this.sanPham = sanPham;
        this.size = size;
        this.mauSac = mauSac;
        this.chatLieu = chatLieu;
    }

    public SanPhamCT(String maSP) {
        this.maSP = maSP;
    }

    public SanPhamCT(SanPham sanPham, Size size, MauSac mauSac, ChatLieu chatLieu) {
        this.sanPham = sanPham;
        this.size = size;
        this.mauSac = mauSac;
        this.chatLieu = chatLieu;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_sanPham() {
        return id_sanPham;
    }

    public void setId_sanPham(Integer id_sanPham) {
        this.id_sanPham = id_sanPham;
    }

    public Integer getId_size() {
        return id_size;
    }

    public void setId_size(Integer id_size) {
        this.id_size = id_size;
    }

    public Integer getId_mauSac() {
        return id_mauSac;
    }

    public void setId_mauSac(Integer id_mauSac) {
        this.id_mauSac = id_mauSac;
    }

    public Integer getId_chatLieu() {
        return id_chatLieu;
    }

    public void setId_chatLieu(Integer id_chatLieu) {
        this.id_chatLieu = id_chatLieu;
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

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public MauSac getMauSac() {
        return mauSac;
    }

    public void setMauSac(MauSac mauSac) {
        this.mauSac = mauSac;
    }

    public ChatLieu getChatLieu() {
        return chatLieu;
    }

    public void setChatLieu(ChatLieu chatLieu) {
        this.chatLieu = chatLieu;
    }

}
