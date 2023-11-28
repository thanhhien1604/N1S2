package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ChatLieu;
import model.HoaDon;
import model.HoaDonChiTiet;
import model.MauSac;
import model.SanPham;
import model.SanPhamCT;
import model.Size;
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
                                ,[ID_HoaDon])
                          VALUES (?, ?, ?, ?, ?)
                     """;
        JdbcHelper.update(sql,
                entity.getGia(),
                entity.getSoLuong(),
                entity.getTongTien(),
                entity.getIdSP(),
                entity.getIdHD()
        );
    }

    @Override
    public void update(HoaDonChiTiet entity) {
        String sql = """
                     UPDATE [dbo].[HoaDonChiTiet]
                        SET [SoLuongSP] = ?
                      WHERE ID = ?
                     """;
        JdbcHelper.update(sql,
                entity.getSoLuong(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = """
                     Delete from HoaDonChiTiet
                     Where ID = ?
                     """;
        JdbcHelper.update(sql, id);
    }

    @Override
    public HoaDonChiTiet selectById(Integer id) {
        String sql = """
                     SELECT
                            hdct.ID,
                            hd.Ma AS MaHD,
                            sp.Ma AS MaSP,
                            sp.Ten AS TenSP,
                            size.Ten AS Size,
                            ms.Ten AS Mau,
                            cl.Ten AS ChatLieu,
                            hdct.GiaBan,
                            hdct.SoLuongSP,
                            hdct.TongTien,
                            hdct.ID_SanPhamCT
                        FROM
                            dbo.HoaDonChiTiet hdct
                        JOIN
                            dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
                        JOIN
                            dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
                        JOIN
                            dbo.Size size ON spct.Id_Size = size.ID
                        JOIN
                            dbo.MauSac ms ON spct.Id_MauSac = ms.ID
                        JOIN
                            dbo.ChatLieu cl ON spct.Id_ChatLieu = cl.ID
                        JOIN
                            dbo.SanPham sp ON spct.ID_SP = sp.ID
                     WHERE hdct.ID = ?
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
                         hdct.TongTien,
                         hdct.ID_SanPhamCT
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
                             size.Ten AS Size,
                             ms.Ten AS Mau,
                             cl.Ten AS ChatLieu,
                             hdct.GiaBan,
                             hdct.SoLuongSP,
                             hdct.TongTien,
                             hdct.ID_SanPhamCT
                         FROM
                             dbo.HoaDonChiTiet hdct
                         JOIN
                             dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
                         JOIN
                             dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
                         JOIN
                             dbo.Size size ON spct.Id_Size = size.ID
                         JOIN
                             dbo.MauSac ms ON spct.Id_MauSac = ms.ID
                         JOIN
                             dbo.ChatLieu cl ON spct.Id_ChatLieu = cl.ID
                         JOIN
                             dbo.SanPham sp ON spct.ID_SP = sp.ID
                     WHERE hd.Ma LIKE ?
                     """;

        return this.selectBySql(sql, "%" + maHD + "%");
    }

    public List<HoaDonChiTiet> sumHoaDon(String maHD) {
        String sql = """
                     SELECT
                         hdct.ID,
                         hd.Ma AS MaHD,
                         sp.Ma AS MaSP,
                         sp.Ten AS TenSP,
                         size.Ten AS Size,
                         ms.Ten AS Mau,
                         cl.Ten AS ChatLieu,
                         hdct.GiaBan,
                         hdct.SoLuongSP,
                         SUM(hdct.TongTien) AS TongTien,
                         hdct.ID_SanPhamCT
                     FROM
                            dbo.HoaDonChiTiet hdct
                        JOIN
                            dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
                        JOIN
                            dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
                        JOIN
                            dbo.Size size ON spct.Id_Size = size.ID
                        JOIN
                            dbo.MauSac ms ON spct.Id_MauSac = ms.ID
                        JOIN
                            dbo.ChatLieu cl ON spct.Id_ChatLieu = cl.ID
                        JOIN
                            dbo.SanPham sp ON spct.ID_SP = sp.ID
                     WHERE hd.Ma LIKE ?
                     GROUP BY
                         hd.Ma
                     """;

        return this.selectBySql(sql, "%" + maHD + "%");
    }

    public List<HoaDonChiTiet> selectByMaSP(String maSP) {
        String sql = """
                     SELECT
                            hdct.ID,
                            hd.Ma AS MaHD,
                            sp.Ma AS MaSP,
                            sp.Ten AS TenSP,
                            size.Ten AS Size,
                            ms.Ten AS Mau,
                            cl.Ten AS ChatLieu,
                            hdct.GiaBan,
                            hdct.SoLuongSP,
                            hdct.TongTien,
                            hdct.ID_SanPhamCT
                        FROM
                            dbo.HoaDonChiTiet hdct
                        JOIN
                            dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
                        JOIN
                            dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
                        JOIN
                            dbo.Size size ON spct.Id_Size = size.ID
                        JOIN
                            dbo.MauSac ms ON spct.Id_MauSac = ms.ID
                        JOIN
                            dbo.ChatLieu cl ON spct.Id_ChatLieu = cl.ID
                        JOIN
                            dbo.SanPham sp ON spct.ID_SP = sp.ID
                     WHERE sp.Ma LIKE ?
                     """;

        return this.selectBySql(sql, "%" + maSP + "%");
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
                hdct.setIdSP(rs.getInt("ID_SanPhamCT"));
                hdct.setHd(new HoaDon(rs.getString("MaHD")));
                hdct.setSpct(new SanPhamCT(
                        new SanPham(rs.getString("MaSP"),
                                rs.getString("TenSP")),
                        new Size(rs.getString("Size")),
                        new MauSac(rs.getString("Mau")),
                        new ChatLieu(rs.getString("ChatLieu"))
                ));

                list.add(hdct);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
