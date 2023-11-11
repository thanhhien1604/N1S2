/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.DanhMuc;
import repository.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ledin
 */
public class DanhMucService extends SellingApplicationImpl<DanhMuc, Integer> {

    @Override
    public void insert(DanhMuc entity) {
        String sql = """
                        INSERT INTO [dbo].[DanhMuc]
                                   ([Ten])
                             VALUES (?)
                        """;

        JdbcHelper.update(sql,
                entity.getTen());
    }

    @Override
    public void update(DanhMuc entity) {
        String sql = """
                        UPDATE [dbo].[DanhMuc]
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
                        DELETE FROM [dbo].[DanhMuc]
                              WHERE ID = ?
                        """;

        JdbcHelper.update(delete_sql, id);
    }

    @Override
    public DanhMuc selectById(Integer id) {
        String selectById = """
                        select * from DanhMuc where ID = ?
                        """;
        List<DanhMuc> list = this.selectBySql(selectById, id);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<DanhMuc> selectBySql(String sql, Object... args) {
        List<DanhMuc> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                DanhMuc danhMuc = new DanhMuc();
                danhMuc.setId(rs.getInt("ID"));
                danhMuc.setTen(rs.getString("Ten"));
                list.add(danhMuc);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DanhMuc> selectAll() {
        String selectAll = """
                       select * from DanhMuc
                       """;
        return this.selectBySql(selectAll);
    }

    public List<DanhMuc> selectByKeyWord(String keyWord) {
        String selectByKeyWord = """
                        SELECT * 
                        FROM DanhMuc
                        WHERE Ten LIKE ?
                          """;
        return this.selectBySql(selectByKeyWord, "%" + keyWord + "%%");
    }

    public List<DanhMuc> selectByStatus(Boolean status) {
        String selectByStatus = """
                        SELECT * 
                        FROM DanhMuc
                        WHERE TrangThai = ?
                          """;
        return this.selectBySql(selectByStatus, status);
    }

    public List<DanhMuc> selectPages(int page, int limit) {
        String sql = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM DanhMuc
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(sql, (page - 1) * limit, limit);
    }

    public List<DanhMuc> filterByStatus(Boolean status, int page, int limit) {
        String selectByStatusOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM DanhMuc 
                           WHERE TrangThai = ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(selectByStatusOffset,
                status, (page - 1) * limit, limit);
    }

}
