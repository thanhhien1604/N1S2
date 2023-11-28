
package service;

import java.util.ArrayList;
import java.util.List;
import model.HoaDon;
import repository.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.NhanVien;

/**
 *
 * @author ledin
 */
public class HoaDonService extends SellingApplicationImpl<HoaDon, Integer> {

    @Override
    public void insert(HoaDon entity) {
        String sql = """
                     INSERT INTO [dbo].[HoaDon]
                                ([NgayTao]
                                ,[TongTien]
                                ,[TrangThai]
                                ,[ID_NhanVien]
                                ,[ID_KhachHang])
                          VALUES(?, ?, ?, ?, ?)
                     """;
        JdbcHelper.update(sql,
                entity.getNgayTao(),
                entity.getTongTien(),
                entity.getTrangThai(),
                entity.getIdNV(),
                entity.getIdKH());
    }

    @Override
    public void update(HoaDon entity) {
        String sql = """
                     UPDATE [dbo].[HoaDon]
                        SET [TongTien] = ?
                           ,[TrangThai] = ?
                           ,[ID_KhachHang] = ?
                      WHERE ID = ?
                     """;
        JdbcHelper.update(sql,
                entity.getTongTien(),
                entity.getTrangThai(),
                entity.getIdKH(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
    }

    @Override
    public HoaDon selectById(Integer id) {
        String sql = """
                     SELECT 
                         hd.ID, 
                         hd.Ma, 
                         nv.Ma AS MaNV,
                         nv.Ten AS TenNV,
                         hd.NgayTao, 
                         hd.TongTien, 
                         hd.TrangThai
                     FROM 
                         dbo.HoaDon hd
                     JOIN 
                         dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                     WHERE hd.ID = ?
                     """;
        List<HoaDon> list = this.selectBySql(sql, id);

        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    public HoaDon selectByMa(String ma) {
        String sql = """
                     SELECT 
                         hd.ID, 
                         hd.Ma, 
                         nv.Ma AS MaNV,
                         nv.Ten AS TenNV,
                         hd.NgayTao, 
                         hd.TongTien, 
                         hd.TrangThai
                     FROM 
                         dbo.HoaDon hd
                     JOIN 
                         dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                     WHERE hd.Ma LIKE ?
                     """;
        List<HoaDon> list = this.selectBySql(sql, "%" + ma + "%");

        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<HoaDon> selectAll() {
        String sql = """
                     SELECT 
                         hd.ID, 
                         hd.Ma, 
                         nv.Ma AS MaNV,
                         nv.Ten AS TenNV,
                         hd.NgayTao, 
                         hd.TongTien, 
                         hd.TrangThai
                     FROM 
                         dbo.HoaDon hd
                     JOIN 
                         dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                     """;
        return this.selectBySql(sql);
    }

    @Override
    protected List<HoaDon> selectBySql(String sql, Object... args) {
        List<HoaDon> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                HoaDon hd = new HoaDon();

                hd.setId(rs.getInt("ID"));
                hd.setMa(rs.getString("Ma"));
                hd.setNgayTao(rs.getDate("NgayTao"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTrangThai(rs.getBoolean("TrangThai"));
                hd.setNv(new NhanVien(rs.getString("MaNV"), rs.getString("TenNV")));

                list.add(hd);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<HoaDon> selectByStatus() {
        String sql = """
                     SELECT 
                         hd.ID, 
                         hd.Ma, 
                         nv.Ma AS MaNV,
                         nv.Ten AS TenNV,
                         hd.NgayTao, 
                         hd.TongTien, 
                         hd.TrangThai
                     FROM 
                         dbo.HoaDon hd
                     JOIN 
                         dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                     WHERE hd.TrangThai = CAST(0 AS bit)
                     """;
        return this.selectBySql(sql);
    }
}
