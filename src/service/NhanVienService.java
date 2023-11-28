/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.NhanVien;
import repository.JdbcHelper;
import java.sql.ResultSet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class NhanVienService extends SellingApplicationImpl<NhanVien, Integer> {

    String insert_sql = """
                        INSERT INTO [dbo].[NhanVien]
                                        ([Ma]
                                        ,[Passwords]
                                        ,[Ten]
                                        ,[SDT]
                                        ,[Email]
                                        ,[ChucVu]
                                        ,[TrangThai])
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
    String update_sql = """
                        UPDATE [dbo].[NhanVien]
                            SET [Ma] = ?
                                     ,[Passwords] = ?
                                     ,[Ten] = ?
                                     ,[SDT] = ?
                                     ,[Email] = ?
                                     ,[ChucVu] = ?
                                     ,[TrangThai] = ?
                        WHERE ID = ?
                        """;
    String delete_sql = """
                        DELETE FROM [dbo].[NhanVien]
                        WHERE ID = ?
                        """;
    String select_all = """
                        select * from NhanVien
                        """;
    String selectById = """
                        select * from NhanVien
                        WHERE ID = ?
                        """;
    String selectByMa = """
                        select * from NhanVien
                        WHERE Ma= ?
                        """;

    @Override
    public void insert(NhanVien entity) {
        JdbcHelper.update(insert_sql,
                entity.getMa(), entity.getPass(),
                entity.getTen(), entity.getSdt(),
                entity.getEmail(), entity.getChucVu(),
                entity.getTrangThai());
    }

    @Override
    public void update(NhanVien entity) {
        JdbcHelper.update(update_sql,
                entity.getMa(), entity.getPass(),
                entity.getTen(), entity.getSdt(),
                entity.getEmail(), entity.getChucVu(),
                entity.getTrangThai(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(delete_sql, id);
    }

    @Override
    protected List<NhanVien> selectBySql(String sql, Object... args) {
        List<NhanVien> list = new ArrayList<>();
        try {

            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setId(rs.getInt("ID"));
                nv.setMa(rs.getString("Ma"));
                nv.setPass(rs.getString("Passwords"));
                nv.setTen(rs.getString("Ten"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("Email"));
                nv.setChucVu(rs.getBoolean("ChucVu"));
                nv.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(nv);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NhanVien selectById(Integer id) {
        List<NhanVien> list = this.selectBySql(selectById, id);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public NhanVien selectByMa(String ma) {
        String sql = """
                        select * from NhanVien
                        WHERE Ma like ?
                        """;
        List<NhanVien> list = this.selectBySql(sql, "%" + ma + "%");
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<NhanVien> selectAll() {
        return this.selectBySql(select_all);
    }

    public List<NhanVien> importExcel(String path) {
        List<NhanVien> list = new ArrayList<>();

        try {
            //Khởi tạo FileInputStream để đọc file Excel
            FileInputStream fis = new FileInputStream(new File(path));
            //Tạo một wordbook từ tệp Excel
            Workbook workbook = new XSSFWorkbook(fis);

            //Lấy ra vị trí trang của file Excel
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                NhanVien nv = new NhanVien();

                nv.setMa(row.getCell(0).getStringCellValue());
                // Kiểm tra kiểu dữ liệu của cột 2 (Mật khẩu)
                Cell passwordCell = row.getCell(1);
                if (passwordCell.getCellType() == CellType.STRING) {
                    nv.setPass(passwordCell.getStringCellValue());
                } else if (passwordCell.getCellType() == CellType.NUMERIC) {
                    // Chuyển số thành chuỗi, sử dụng (int) để loại bỏ phần số thập phân
                    nv.setPass(String.valueOf((int) passwordCell.getNumericCellValue()));
                }
                nv.setTen(row.getCell(2).getStringCellValue());
                Cell sdtCell = row.getCell(3);
                if (sdtCell != null) {
                    if (sdtCell.getCellType() == CellType.STRING) {
                        nv.setSdt(sdtCell.getStringCellValue());
                    } else if (sdtCell.getCellType() == CellType.NUMERIC) {
                        nv.setSdt(String.valueOf((int) sdtCell.getNumericCellValue()));
                    }
                }
                Cell emailCell = row.getCell(4);
                if (emailCell != null) {
                    if (emailCell.getCellType() == CellType.STRING) {
                        nv.setEmail(emailCell.getStringCellValue());
                    } else if (emailCell.getCellType() == CellType.NUMERIC) {
                        nv.setEmail(String.valueOf((int) emailCell.getNumericCellValue()));
                    }
                }
                nv.setChucVu(row.getCell(5).getBooleanCellValue());
                nv.setTrangThai(row.getCell(6).getBooleanCellValue());

                list.add(nv);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<NhanVien> selectByKeyWord(String keyword) {
        String sql = """
                           SELECT *
                             FROM [dbo].[NhanVien]
                             WHERE Ten LIKE ? OR Ma LIKE ? OR SDT LIKE ?
                     """;
        return this.selectBySql(sql, "%" + keyword + "%%",
                "%" + keyword + "%%",
                "%" + keyword + "%%");
    }

    public List<NhanVien> searchKeyWord(String keyWord, int pages, int limit) {
        String sql = """
                     SELECT * 
                     FROM 
                     (
                        SELECT *
                        FROM [dbo].[NhanVien]
                        WHERE Ten LIKE ? OR Ma LIKE ? OR SDT LIKE ?
                     ) AS FilteredResults
                     ORDER BY ID
                     OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                     """;
        return this.selectBySql(sql,
                "%" + keyWord + "%%",
                "%" + keyWord + "%%",
                "%" + keyWord + "%%",
                (pages - 1) * limit, limit);
    }
}
