/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.ThuongHieu;
import repository.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ledin
 */
public class ThuongHieuService extends SellingApplicationImpl<ThuongHieu, Integer> {

    @Override
    public void insert(ThuongHieu entity) {
        String sql = """
                        INSERT INTO [dbo].[ThuongHieu]
                                   ([Ten]
                                   ,[TrangThai])
                             VALUES (?, ?)
                        """;

        JdbcHelper.update(sql,
                entity.getTen(),
                entity.getTrangThai());
    }

    @Override
    public void update(ThuongHieu entity) {
        String sql = """
                        UPDATE [dbo].[ThuongHieu]
                           SET [Ten] = ?
                              ,[TrangThai] = ?
                         WHERE ID = ?
                        """;

        JdbcHelper.update(sql,
                entity.getTen(),
                entity.getTrangThai(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        String delete_sql = """
                        DELETE FROM [dbo].[ThuongHieu]
                              WHERE ID = ?
                        """;

        JdbcHelper.update(delete_sql, id);
    }

    @Override
    public ThuongHieu selectById(Integer id) {
        String selectById = """
                        select * from ThuongHieu where ID = ?
                        """;
        List<ThuongHieu> list = this.selectBySql(selectById, id);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<ThuongHieu> selectBySql(String sql, Object... args) {
        List<ThuongHieu> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                ThuongHieu th = new ThuongHieu();
                th.setId(rs.getInt("ID"));
                th.setTen(rs.getString("Ten"));
                th.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(th);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ThuongHieu> selectAll() {
        String selectAll = """
                       select * from ThuongHieu
                       """;
        return this.selectBySql(selectAll);
    }

    public List<ThuongHieu> selectByKeyWord(String keyWord) {
        String selectByKeyWord = """
                        SELECT * 
                        FROM ThuongHieu
                        WHERE Ten LIKE ?
                          """;
        return this.selectBySql(selectByKeyWord, "%" + keyWord + "%%");
    }

    public List<ThuongHieu> selectByStatus(Boolean status) {
        String selectByStatus = """
                        SELECT * 
                        FROM ThuongHieu
                        WHERE TrangThai = ?
                          """;
        return this.selectBySql(selectByStatus, status);
    }

    public List<ThuongHieu> searchKeyWord(String keyWord, int page, int limit) {
        String selectBykeyWordOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM ThuongHieu
                           WHERE Ten LIKE ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(selectBykeyWordOffset,
                "%" + keyWord + "%%", (page - 1) * limit, limit);
    }

    public List<ThuongHieu> filterByStatus(Boolean status, int page, int limit) {
        String selectByStatusOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM ThuongHieu
                           WHERE TrangThai = ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(selectByStatusOffset,
                status, (page - 1) * limit, limit);
    }
}
