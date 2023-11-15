/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.VoucherCT;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.NhanVien;
import model.Voucher;
import repository.JdbcHelper;

/**
 *
 * @author ledin
 */
public class VoucherCTService extends SellingApplicationImpl<VoucherCT, Integer> {

    @Override
    public void insert(VoucherCT entity) {
        String sql = """
                     INSERT INTO [dbo].[VoucherCT]
                                           ([NgayBatDau]
                                           ,[NgayHetHan]
                                           ,[SoLuong]
                                           ,[KieuGiam]
                                           ,[ID_Voucher])
                          VALUES (?, ?, ?, ?, ?)
                     """;

        JdbcHelper.update(sql,
                entity.getNgayBatDau(),
                entity.getNgayHetHan(),
                entity.getSoLuong(),
                entity.getKieuGiam(),
                entity.getId_voucher());
    }

    @Override
    public void update(VoucherCT entity) {
        String sql = """
                     UPDATE [dbo].[VoucherCT]
                             SET [NgayBatDau] = ?
                                ,[NgayHetHan] = ?
                                ,[SoLuong] = ?
                                ,[KieuGiam] = ?
                                ,[ID_Voucher] = ?
                     WHERE ID = ?
                     """;

        JdbcHelper.update(sql,
                entity.getNgayBatDau(),
                entity.getNgayHetHan(),
                entity.getSoLuong(),
                entity.getKieuGiam(),
                entity.getId_voucher(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = """
                    DELETE FROM [dbo].[VoucherCT]
                        WHERE ID = ?
                     """;

        JdbcHelper.update(sql, id);
    }

    @Override
    public VoucherCT selectById(Integer id) {
        String sql = """
                     SELECT     dbo.VoucherCT.ID, 
                                dbo.Voucher.Ma AS MaVoucher, 
                                dbo.NhanVien.Ma AS MaNV, 
                                dbo.NhanVien.Ten AS TenNV,
                                dbo.Voucher.Ten, 
                                dbo.VoucherCT.NgayBatDau, 
                                dbo.VoucherCT.NgayHetHan, 
                                dbo.VoucherCT.SoLuong, 
                                dbo.VoucherCT.KieuGiam, 
                                dbo.Voucher.TrangThai
                     FROM           dbo.NhanVien INNER JOIN dbo.Voucher
                                    ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien 
                                    INNER JOIN dbo.VoucherCT
                                    ON dbo.Voucher.ID = dbo.VoucherCT.ID_Voucher
                     WHERE dbo.VoucherCT.ID = ?
                     """;
        List<VoucherCT> list = this.selectBySql(sql, id);
        if (list == null) {
            return null;
        }

        return list.get(0);
    }

    @Override
    protected List<VoucherCT> selectBySql(String sql, Object... args) {
        List<VoucherCT> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                VoucherCT vcct = new VoucherCT();
                vcct.setId(rs.getInt("ID"));
                vcct.setNgayBatDau(rs.getDate("NgayBatDau"));
                vcct.setNgayHetHan(rs.getDate("NgayHetHan"));
                vcct.setSoLuong(rs.getInt("SoLuong"));
                vcct.setKieuGiam(rs.getBoolean("KieuGiam"));
                vcct.setTrangThai(rs.getBoolean("TrangThai"));
                vcct.setVc(new Voucher(
                        rs.getString("Ten"),
                        rs.getString("MaVoucher"),
                        new NhanVien(rs.getString("MaNV"), rs.getString("TenNV"))
                ));

                list.add(vcct);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
             throw new RuntimeException();
        }
    }

    @Override
    public List<VoucherCT> selectAll() {
        String sql = """
                     SELECT     dbo.VoucherCT.ID, 
                                dbo.Voucher.Ma AS MaVoucher, 
                                dbo.NhanVien.Ma AS MaNV, 
                                dbo.NhanVien.Ten AS TenNV,
                                dbo.Voucher.Ten, 
                                dbo.VoucherCT.NgayBatDau, 
                                dbo.VoucherCT.NgayHetHan, 
                                dbo.VoucherCT.SoLuong, 
                                dbo.VoucherCT.KieuGiam, 
                                dbo.VoucherCT.TrangThai
                     FROM           dbo.NhanVien INNER JOIN dbo.Voucher
                                    ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien 
                                    INNER JOIN dbo.VoucherCT
                                    ON dbo.Voucher.ID = dbo.VoucherCT.ID_Voucher
                     """;
        return this.selectBySql(sql);
    }

    public List<VoucherCT> selectByKeyWord(Integer id_voucher) {
        String sql = """
                           SELECT   dbo.VoucherCT.ID, 
                                    dbo.Voucher.Ma AS MaVoucher, 
                                    dbo.NhanVien.Ma AS MaNV, 
                                    dbo.NhanVien.Ten AS TenNV,
                                    dbo.Voucher.Ten, 
                                    dbo.VoucherCT.NgayBatDau, 
                                    dbo.VoucherCT.NgayHetHan, 
                                    dbo.VoucherCT.SoLuong, 
                                    dbo.VoucherCT.KieuGiam, 
                                    dbo.VoucherCT.TrangThai
                         FROM           dbo.NhanVien INNER JOIN dbo.Voucher
                                        ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien 
                                        INNER JOIN dbo.VoucherCT
                                        ON dbo.Voucher.ID = dbo.VoucherCT.ID_Voucher
                     WHERE dbo.Voucher.ID = ?
                     """;
        return this.selectBySql(sql, id_voucher);
    }

    public List<VoucherCT> selectByStatus(Boolean status, Integer soLuong, int nam, Integer id_voucher) {
        String sql = """
                        SELECT  dbo.VoucherCT.ID, 
                                dbo.Voucher.Ma AS MaVoucher, 
                                dbo.NhanVien.Ma AS MaNV, 
                                dbo.NhanVien.Ten AS TenNV,
                                dbo.Voucher.Ten, 
                                dbo.VoucherCT.NgayBatDau, 
                                dbo.VoucherCT.NgayHetHan, 
                                dbo.VoucherCT.SoLuong, 
                                dbo.VoucherCT.KieuGiam, 
                                dbo.VoucherCT.TrangThai
                        FROM        dbo.NhanVien INNER JOIN dbo.Voucher
                                    ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien 
                                    INNER JOIN dbo.VoucherCT
                                    ON dbo.Voucher.ID = dbo.VoucherCT.ID_Voucher
                 WHERE  dbo.VoucherCT.TrangThai = ISNULL(?, dbo.VoucherCT.TrangThai)
                     AND dbo.VoucherCT.SoLuong = ISNULL(?, dbo.VoucherCT.SoLuong)
                     AND ? BETWEEN YEAR(dbo.VoucherCT.NgayBatDau) AND YEAR(dbo.VoucherCT.NgayHetHan)
                     AND dbo.Voucher.ID = ?
                     """;
        return this.selectBySql(sql, status, soLuong, nam, id_voucher);
    }

    public List<VoucherCT> searchKeyWord(Integer id_voucher, int pages, int limit) {
        String sql = """
                     SELECT * 
                     FROM 
                     (
                              SELECT    dbo.VoucherCT.ID, 
                                        dbo.Voucher.Ma AS MaVoucher, 
                                        dbo.NhanVien.Ma AS MaNV,
                                        dbo.NhanVien.Ten AS TenNV,
                                        dbo.Voucher.Ten, 
                                        dbo.VoucherCT.NgayBatDau, 
                                        dbo.VoucherCT.NgayHetHan, 
                                        dbo.VoucherCT.SoLuong, 
                                        dbo.VoucherCT.KieuGiam, 
                                        dbo.VoucherCT.TrangThai
                              FROM          dbo.NhanVien INNER JOIN dbo.Voucher
                                            ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien 
                                            INNER JOIN dbo.VoucherCT
                                            ON dbo.Voucher.ID = dbo.VoucherCT.ID_Voucher
                        WHERE dbo.Voucher.ID = ?
                     ) AS FilteredResults
                     ORDER BY ID
                     OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                     """;
        return this.selectBySql(sql,
                id_voucher, (pages - 1) * limit, limit);
    }

    public List<VoucherCT> filterStatus(Boolean status, Integer soLuong, int nam, Integer id_voucher, int pages, int limit) {
        String sql = """
                     SELECT * 
                     FROM 
                     (
                              SELECT    dbo.VoucherCT.ID, 
                                        dbo.Voucher.Ma AS MaVoucher, 
                                        dbo.NhanVien.Ma AS MaNV,
                                        dbo.NhanVien.Ten AS TenNV,
                                        dbo.Voucher.Ten, 
                                        dbo.VoucherCT.NgayBatDau, 
                                        dbo.VoucherCT.NgayHetHan, 
                                        dbo.VoucherCT.SoLuong, 
                                        dbo.VoucherCT.KieuGiam, 
                                        dbo.VoucherCT.TrangThai
                              FROM          dbo.NhanVien INNER JOIN dbo.Voucher
                                            ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien 
                                            INNER JOIN dbo.VoucherCT
                                            ON dbo.Voucher.ID = dbo.VoucherCT.ID_Voucher
                        WHERE  dbo.VoucherCT.TrangThai = ISNULL(?, dbo.VoucherCT.TrangThai)
                                AND dbo.VoucherCT.SoLuong = ISNULL(?, dbo.VoucherCT.SoLuong)
                                AND ? BETWEEN YEAR(dbo.VoucherCT.NgayBatDau) AND YEAR(dbo.VoucherCT.NgayHetHan)
                                AND dbo.Voucher.ID = ?
                     ) AS FilteredResults
                     ORDER BY ID
                     OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                     """;
        return this.selectBySql(sql,
                status, soLuong, nam, id_voucher, (pages - 1) * limit, limit);
    }
}
