
package service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import model.SanPhamCT;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.ChatLieu;
import model.DanhMuc;
import model.MauSac;
import model.NhanVien;
import model.SanPham;
import model.Size;
import model.ThuongHieu;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import repository.JdbcHelper;

/**
 *
 * @author ledin
 */
public class SanPhamCTService extends SellingApplicationImpl<SanPhamCT, Integer> {

    @Override
    public void insert(SanPhamCT entity) {
        String sql = """
                    INSERT INTO [dbo].[SanPhamChiTiet]
                                ([Gia]
                                ,[SoLuong]
                                ,[MaSP]
                                ,[TrangThai]
                                ,[ID_SP]
                                ,[ID_Size]
                                ,[ID_MauSac]
                                ,[ID_ChatLieu])
                          VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                     """;

        JdbcHelper.update(sql,
                entity.getGia(),
                entity.getSoLuong(),
                entity.getMaSP(),
                entity.getTrangThai(),
                entity.getId_sanPham(),
                entity.getId_size(),
                entity.getId_mauSac(),
                entity.getId_chatLieu());
    }

    @Override
    public void update(SanPhamCT entity) {
        String sql = """
                     UPDATE [dbo].[SanPhamChiTiet]
                        SET [Gia] = ?
                           ,[SoLuong] = ?
                           ,[MaSP] = ?
                           ,[TrangThai] = ?
                           ,[ID_SP] = ?
                           ,[ID_Size] = ?
                           ,[ID_MauSac] = ?
                           ,[ID_ChatLieu] = ?
                      WHERE ID = ?
                     """;

        JdbcHelper.update(sql,
                entity.getGia(),
                entity.getSoLuong(),
                entity.getMaSP(),
                entity.getTrangThai(),
                entity.getId_sanPham(),
                entity.getId_size(),
                entity.getId_mauSac(),
                entity.getId_chatLieu(),
                entity.getId());
    }

    public void updateSoLuong(SanPhamCT entity) {
        String sql = """
                     UPDATE [dbo].[SanPhamChiTiet]
                        SET SoLuong = ?
                     ,[TrangThai] = ?
                      WHERE ID = ?
                     """;

        JdbcHelper.update(sql,
                entity.getSoLuong(),
                entity.getTrangThai(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = """
                     DELETE FROM [dbo].[SanPhamChiTiet]
                           WHERE ID = ?
                     """;

        JdbcHelper.update(sql, id);
    }

    @Override
    public SanPhamCT selectById(Integer id) {
        String sql = """
                 SELECT 
                        spct.ID,  
                        spct.MaSP, 
                        nv.Ma AS MaNV,
                        nv.Ten AS TenNV,
                        sp.Ten AS TenSP, 
                        spct.Gia, 
                        spct.SoLuong, 
                        sz.Ten AS Size, 
                        ms.Ten AS MauSac, 
                        cl.Ten AS ChatLieu, 
                        dm.Ten AS DanhMuc, 
                        th.Ten AS ThuongHieu, 
                        spct.TrangThai
                    FROM 
                        dbo.SanPhamChiTiet spct
                    JOIN 
                        dbo.SanPham sp ON spct.ID_SP = sp.ID
                    JOIN 
                        dbo.MauSac ms ON spct.ID_MauSac = ms.ID
                    JOIN 
                        dbo.Size sz ON spct.ID_Size = sz.ID
                    JOIN 
                        dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
                    JOIN 
                        dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
                    JOIN 
                        dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
                    JOIN 
                        dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
                 WHERE spct.ID = ?
                 """;

        List<SanPhamCT> list = this.selectBySql(sql, id);

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public SanPhamCT selectByMa(String ma) {
        String sql = """
                    SELECT 
                            spct.ID,  
                            spct.MaSP, 
                            nv.Ma AS MaNV,
                            nv.Ten AS TenNV,
                            sp.Ten AS TenSP, 
                            spct.Gia, 
                            spct.SoLuong, 
                            sz.Ten AS Size, 
                            ms.Ten AS MauSac, 
                            cl.Ten AS ChatLieu, 
                            dm.Ten AS DanhMuc, 
                            th.Ten AS ThuongHieu, 
                            spct.TrangThai
                        FROM 
                            dbo.SanPhamChiTiet spct
                        JOIN 
                            dbo.SanPham sp ON spct.ID_SP = sp.ID
                        JOIN 
                            dbo.MauSac ms ON spct.ID_MauSac = ms.ID
                        JOIN 
                            dbo.Size sz ON spct.ID_Size = sz.ID
                        JOIN 
                            dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
                        JOIN 
                            dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
                        JOIN 
                            dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
                        JOIN 
                            dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
                     WHERE spct.MaSP LIKE ?
                     """;
        List<SanPhamCT> list = this.selectBySql(sql, "%" + ma + "%");
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<SanPhamCT> selectAll() {
        String sql = """
                    SELECT 
                        spct.ID,  
                        spct.MaSP, 
                        nv.Ma AS MaNV,
                        nv.Ten AS TenNV,
                        sp.Ten AS TenSP, 
                        spct.Gia, 
                        spct.SoLuong, 
                        sz.Ten AS Size, 
                        ms.Ten AS MauSac, 
                        cl.Ten AS ChatLieu, 
                        dm.Ten AS DanhMuc, 
                        th.Ten AS ThuongHieu, 
                        spct.TrangThai
                    FROM 
                        dbo.SanPhamChiTiet spct
                    JOIN 
                        dbo.SanPham sp ON spct.ID_SP = sp.ID
                    JOIN 
                        dbo.MauSac ms ON spct.ID_MauSac = ms.ID
                    JOIN 
                        dbo.Size sz ON spct.ID_Size = sz.ID
                    JOIN 
                        dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
                    JOIN 
                        dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
                    JOIN 
                        dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
                    JOIN 
                        dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
                     """;
        return this.selectBySql(sql);
    }

    @Override
    protected List<SanPhamCT> selectBySql(String sql, Object... args) {
        List<SanPhamCT> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                SanPhamCT spct = new SanPhamCT();
                spct.setId(rs.getInt("ID"));
                spct.setGia(rs.getDouble("Gia"));
                spct.setSoLuong(rs.getInt("SoLuong"));
                spct.setMaSP(rs.getString("MaSP"));
                spct.setTrangThai(rs.getBoolean("TrangThai"));
                spct.setSanPham(new SanPham(rs.getString("TenSP"),
                        new ThuongHieu(rs.getString("ThuongHieu")),
                        new DanhMuc(rs.getString("DanhMuc")),
                        new NhanVien(rs.getString("MaNV"), rs.getString("TenNV"))
                ));
                spct.setSize(new Size(rs.getString("Size")));
                spct.setMauSac(new MauSac(rs.getString("MauSac")));
                spct.setChatLieu(new ChatLieu(rs.getString("ChatLieu")));

                list.add(spct);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<SanPhamCT> selectByKeyWord(String keyword) {
        String sql = """
                            SELECT 
                                spct.ID,  
                                spct.MaSP, 
                                nv.Ma AS MaNV,
                                nv.Ten AS TenNV,
                                sp.Ten AS TenSP, 
                                spct.Gia, 
                                spct.SoLuong, 
                                sz.Ten AS Size, 
                                ms.Ten AS MauSac, 
                                cl.Ten AS ChatLieu, 
                                dm.Ten AS DanhMuc, 
                                th.Ten AS ThuongHieu, 
                                spct.TrangThai
                            FROM 
                                dbo.SanPhamChiTiet spct
                            JOIN 
                                dbo.SanPham sp ON spct.ID_SP = sp.ID
                            JOIN 
                                dbo.MauSac ms ON spct.ID_MauSac = ms.ID
                            JOIN 
                                dbo.Size sz ON spct.ID_Size = sz.ID
                            JOIN 
                                dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
                            JOIN 
                                dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
                            JOIN 
                                dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
                            JOIN 
                                dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
                     WHERE sp.Ten LIKE ? OR dm.Ten LIKE ? OR th.Ten LIKE ? 
                     """;
        return this.selectBySql(sql,
                "%" + keyword + "%%",
                "%" + keyword + "%%",
                "%" + keyword + "%%");
    }

    public List<SanPhamCT> searchKeyWord(String keyWord, int pages, int limit) {
        String sql = """
                     SELECT * 
                     FROM 
                     (
                         SELECT 
                            spct.ID,  
                            spct.MaSP, 
                            nv.Ma AS MaNV,
                            nv.Ten AS TenNV,
                            sp.Ten AS TenSP, 
                            spct.Gia, 
                            spct.SoLuong, 
                            sz.Ten AS Size, 
                            ms.Ten AS MauSac, 
                            cl.Ten AS ChatLieu, 
                            dm.Ten AS DanhMuc, 
                            th.Ten AS ThuongHieu, 
                            spct.TrangThai
                        FROM 
                            dbo.SanPhamChiTiet spct
                        JOIN 
                            dbo.SanPham sp ON spct.ID_SP = sp.ID
                        JOIN 
                            dbo.MauSac ms ON spct.ID_MauSac = ms.ID
                        JOIN 
                            dbo.Size sz ON spct.ID_Size = sz.ID
                        JOIN 
                            dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
                        JOIN 
                            dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
                        JOIN 
                            dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
                        JOIN 
                            dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
                     WHERE sp.Ten LIKE ? OR dm.Ten LIKE ? OR th.Ten LIKE ? 
                     ) AS FilteredResults
                     ORDER BY ID
                     OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                     """;
        return this.selectBySql(sql,
                "%" + keyWord + "%%",
                "%" + keyWord + "%%",
                "%" + keyWord + "%%",
                (pages - 1) * limit, limit);
    }

    public List<SanPhamCT> importExcel(String path) {
        List<SanPhamCT> list = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(1);

            for (Row row : sheet) {
                SanPhamCT spct = new SanPhamCT();
                spct.setGia(row.getCell(0).getNumericCellValue());
                spct.setSoLuong((int) row.getCell(1).getNumericCellValue());
                spct.setMaSP(row.getCell(2).getStringCellValue());
                spct.setTrangThai(row.getCell(3).getBooleanCellValue());
                spct.setId_sanPham((int) row.getCell(4).getNumericCellValue());
                spct.setId_size((int) row.getCell(5).getNumericCellValue());
                spct.setId_mauSac((int) row.getCell(6).getNumericCellValue());
                spct.setId_chatLieu((int) row.getCell(7).getNumericCellValue());

                list.add(spct);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return list;
    }

    public List<SanPhamCT> selectPage(String keyword) {
        String sql = """
                            SELECT 
                                spct.ID,  
                                spct.MaSP, 
                                nv.Ma AS MaNV,
                                nv.Ten AS TenNV,
                                sp.Ten AS TenSP, 
                                spct.Gia, 
                                spct.SoLuong, 
                                sz.Ten AS Size, 
                                ms.Ten AS MauSac, 
                                cl.Ten AS ChatLieu, 
                                dm.Ten AS DanhMuc, 
                                th.Ten AS ThuongHieu, 
                                spct.TrangThai
                            FROM 
                                dbo.SanPhamChiTiet spct
                            JOIN 
                                dbo.SanPham sp ON spct.ID_SP = sp.ID
                            JOIN 
                                dbo.MauSac ms ON spct.ID_MauSac = ms.ID
                            JOIN 
                                dbo.Size sz ON spct.ID_Size = sz.ID
                            JOIN 
                                dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
                            JOIN 
                                dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
                            JOIN 
                                dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
                            JOIN 
                                dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
                        WHERE (sp.Ten LIKE ? OR spct.MaSP LIKE ?) 
                            AND spct.TrangThai = CAST(1 AS bit)
                     """;
        return this.selectBySql(sql,
                "%" + keyword + "%%",
                "%" + keyword + "%%");
    }

    public List<SanPhamCT> selectStatus(String keyword, int pages, int limit) {
        String sql = """
                        SELECT * 
                            FROM 
                            (
                                SELECT 
                                    spct.ID,  
                                    spct.MaSP, 
                                    nv.Ma AS MaNV,
                                    nv.Ten AS TenNV,
                                    sp.Ten AS TenSP, 
                                    spct.Gia, 
                                    spct.SoLuong, 
                                    sz.Ten AS Size, 
                                    ms.Ten AS MauSac, 
                                    cl.Ten AS ChatLieu, 
                                    dm.Ten AS DanhMuc, 
                                    th.Ten AS ThuongHieu, 
                                    spct.TrangThai
                                FROM 
                                    dbo.SanPhamChiTiet spct
                                JOIN 
                                    dbo.SanPham sp ON spct.ID_SP = sp.ID
                                JOIN 
                                    dbo.MauSac ms ON spct.ID_MauSac = ms.ID
                                JOIN 
                                    dbo.Size sz ON spct.ID_Size = sz.ID
                                JOIN 
                                    dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
                                JOIN 
                                    dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
                                JOIN 
                                    dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
                                JOIN 
                                    dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
                            WHERE (sp.Ten LIKE ? OR spct.MaSP LIKE ?) 
                                    AND spct.TrangThai = CAST(1 AS bit) 
                            ) AS FilteredResults
                            ORDER BY ID
                            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                     """;
        return this.selectBySql(sql,
                "%" + keyword + "%%",
                "%" + keyword + "%%",
                (pages - 1) * limit, limit);
    }
}
