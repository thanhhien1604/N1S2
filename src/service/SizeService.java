/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.Size;
import repository.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ledin
 */
public class SizeService extends SellingApplicationImpl<Size, Integer> {

    @Override
    public void insert(Size entity) {
        String sql = """
                        INSERT INTO [dbo].[Size]
                                   ([Ten])
                             VALUES (?)
                        """;

        JdbcHelper.update(sql,
                entity.getTen());
    }

    @Override
    public void update(Size entity) {
        String sql = """
                        UPDATE [dbo].[Size]
                           SET [Ten] = ?
                         WHERE ID = ?
                        """;

        JdbcHelper.update(sql,
                entity.getTen(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        String delete_sql = """
                        DELETE FROM [dbo].[Size]
                              WHERE ID = ?
                        """;

        JdbcHelper.update(delete_sql, id);
    }

    @Override
    public Size selectById(Integer id) {
        String selectById = """
                        select * from Size where ID = ?
                        """;
        List<Size> list = this.selectBySql(selectById, id);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<Size> selectBySql(String sql, Object... args) {
        List<Size> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Size th = new Size();
                th.setId(rs.getInt("ID"));
                th.setTen(rs.getString("Ten"));
                list.add(th);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Size> selectAll() {
        String selectAll = """
                       select * from Size
                       """;
        return this.selectBySql(selectAll);
    }

    public List<Size> selectByKeyWord(String keyWord) {
        String selectByKeyWord = """
                        SELECT * 
                        FROM Size
                        WHERE Ten LIKE ?
                          """;
        return this.selectBySql(selectByKeyWord, "%" + keyWord + "%%");
    }

    public List<Size> selectByStatus(Boolean status) {
        String selectByStatus = """
                        SELECT * 
                        FROM Size
                        WHERE TrangThai = ?
                          """;
        return this.selectBySql(selectByStatus, status);
    }

    public List<Size> selectPages(int page, int limit) {
        String sql = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM Size
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(sql, (page - 1) * limit, limit);
    }

    public List<Size> filterByStatus(Boolean status, int page, int limit) {
        String selectByStatusOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM Size 
                           WHERE TrangThai = ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(selectByStatusOffset,
                status, (page - 1) * limit, limit);
    }

}
