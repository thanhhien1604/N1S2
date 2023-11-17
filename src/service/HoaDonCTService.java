/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.HoaDon;
import model.HoaDonChiTiet;
import model.KhachHang;
import model.SanPham;
import model.SanPhamCT;
import repository.JdbcHelper;

/**
 *
 * @author ledin
 */
public class HoaDonCTService extends SellingApplicationImpl<HoaDonChiTiet, Integer> {

    @Override
    public void insert(HoaDonChiTiet entity) {
        String sql = """
                     INSERT INTO [dbo].[HoaDonChiTiet]
                                ([GiaBan]
                                ,[SoLuongSP]
                                ,[TongTien]
                                ,[ID_SanPhamCT]
                                ,[ID_HoaDon]
                                ,[ID_VoucherCT]
                                ,[ID_KhachHang])
                          VALUES (?, ?, ?, ?, ?, ?, ?)
                     """;
        JdbcHelper.update(sql,
                entity.getGia(),
                entity.getSoLuong(),
                entity.getTongTien(),
                entity.getIdSP(),
                entity.getIdHD(),
                entity.getIdVC(),
                entity.getIdKH()
        );
    }

    @Override
    public void update(HoaDonChiTiet entity) {
    }

    @Override
    public void delete(Integer id) {
    }

    @Override
    public HoaDonChiTiet selectById(Integer id) {
        String sql = """
                     SELECT
                         hdct.ID,
                         hd.Ma AS MaHD,
                         sp.Ma AS MaSP,
                         sp.Ten AS TenSP,
                         kh.Ma AS MaKH,
                         kh.Ten AS TenKH,
                         hdct.GiaBan,
                         hdct.SoLuongSP,
                         hdct.TongTien
                     FROM
                         dbo.HoaDonChiTiet hdct
                     JOIN
                         dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
                     JOIN
                         dbo.KhachHang kh ON hdct.ID_KhachHang = kh.ID
                     JOIN
                         dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
                     JOIN
                         dbo.SanPham sp ON spct.ID_SP = sp.ID
                     JOIN
                         dbo.VoucherCT vc ON hdct.ID_VoucherCT = vc.ID
                     WHERE ID
                     """;
        List<HoaDonChiTiet> list = this.selectBySql(sql, id);
        if (list == null) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public List<HoaDonChiTiet> selectAll() {
        String sql = """
                     SELECT
                         hdct.ID,
                         hd.Ma AS MaHD,
                         sp.Ma AS MaSP,
                         sp.Ten AS TenSP,
                         kh.Ma AS MaKH,
                         kh.Ten AS TenKH,
                         hdct.GiaBan,
                         hdct.SoLuongSP,
                         hdct.TongTien
                     FROM
                         dbo.HoaDonChiTiet hdct
                     JOIN
                         dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
                     JOIN
                         dbo.KhachHang kh ON hdct.ID_KhachHang = kh.ID
                     JOIN
                         dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
                     JOIN
                         dbo.SanPham sp ON spct.ID_SP = sp.ID
                     JOIN
                         dbo.VoucherCT vc ON hdct.ID_VoucherCT = vc.ID
                     """;

        return this.selectBySql(sql);
    }

    public List<HoaDonChiTiet> selectByMaHD(String maHD) {
        String sql = """
                     SELECT
                         hdct.ID,
                         hd.Ma AS MaHD,
                         sp.Ma AS MaSP,
                         sp.Ten AS TenSP,
                         hdct.GiaBan,
                         hdct.SoLuongSP,
                         hdct.TongTien
                     FROM
                         dbo.HoaDonChiTiet hdct
                     JOIN
                         dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
                     JOIN
                         dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
                     JOIN
                         dbo.SanPham sp ON spct.ID_SP = sp.ID
                     WHERE hd.Ma LIKE ?
                     """;

        return this.selectBySqlHD(sql, "%" + maHD + "%");
    }

    @Override
    protected List<HoaDonChiTiet> selectBySql(String sql, Object... args) {
        List<HoaDonChiTiet> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                HoaDonChiTiet hdct = new HoaDonChiTiet();

                hdct.setId(rs.getInt("ID"));
                hdct.setGia(rs.getDouble("GiaBan"));
                hdct.setSoLuong(rs.getInt("SoLuongSP"));
                hdct.setTongTien(rs.getDouble("TongTien"));
                hdct.setHd(new HoaDon(rs.getString("MaHD")));
                hdct.setSpct(new SanPhamCT(new SanPham(rs.getString("MaSP"),
                        rs.getString("TenSP"))));
                hdct.setKh(new KhachHang(rs.getString("MaKH"), rs.getString("TenKH")));

                list.add(hdct);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    protected List<HoaDonChiTiet> selectBySqlHD(String sql, Object... args) {
        List<HoaDonChiTiet> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                HoaDonChiTiet hdct = new HoaDonChiTiet();

                hdct.setId(rs.getInt("ID"));
                hdct.setGia(rs.getDouble("GiaBan"));
                hdct.setSoLuong(rs.getInt("SoLuongSP"));
                hdct.setTongTien(rs.getDouble("TongTien"));
                hdct.setHd(new HoaDon(rs.getString("MaHD")));
                hdct.setSpct(new SanPhamCT(new SanPham(rs.getString("MaSP"),
                        rs.getString("TenSP"))));

                list.add(hdct);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
