/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.MauSac;
import repository.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ledin
 */
public class MauSacService extends SellingApplicationImpl<MauSac, Integer> {

    @Override
    public void insert(MauSac entity) {
        String sql = """
                        INSERT INTO [dbo].[MauSac]
                                   ([Ten]
                                   ,[TrangThai])
                             VALUES (?, ?)
                        """;

        JdbcHelper.update(sql,
                entity.getTen(),
                entity.isTrangThai());
    }

    @Override
    public void update(MauSac entity) {
        String sql = """
                        UPDATE [dbo].[MauSac]
                           SET [Ten] = ?
                              ,[TrangThai] = ?
                         WHERE ID = ?
                        """;

        JdbcHelper.update(sql,
                entity.getTen(),
                entity.isTrangThai(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        String delete_sql = """
                        DELETE FROM [dbo].[MauSac]
                              WHERE ID = ?
                        """;

        JdbcHelper.update(delete_sql, id);
    }

    @Override
    public MauSac selectById(Integer id) {
        String selectById = """
                        select * from MauSac where ID = ?
                        """;
        List<MauSac> list = this.selectBySql(selectById, id);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<MauSac> selectBySql(String sql, Object... args) {
        List<MauSac> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                MauSac ms = new MauSac();
                ms.setId(rs.getInt("ID"));
                ms.setTen(rs.getString("Ten"));
                ms.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(ms);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MauSac> selectAll() {
        String selectAll = """
                       select * from MauSac
                       """;
        return this.selectBySql(selectAll);
    }

    public List<MauSac> selectByKeyWord(String keyWord) {
        String selectByKeyWord = """
                        SELECT * 
                        FROM MauSac
                        WHERE Ten LIKE ?
                          """;
        return this.selectBySql(selectByKeyWord, "%" + keyWord + "%%");
    }

    public List<MauSac> selectByStatus(Boolean status) {
        String selectByStatus = """
                        SELECT * 
                        FROM MauSac
                        WHERE TrangThai = ?
                          """;
        return this.selectBySql(selectByStatus, status);
    }

    public List<MauSac> searchKeyWord(String keyWord, int page, int limit) {
        String selectBykeyWordOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM MauSac
                           WHERE Ten LIKE ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(selectBykeyWordOffset,
                "%" + keyWord + "%%", (page - 1) * limit, limit);
    }

    public List<MauSac> filterByStatus(Boolean status, int page, int limit) {
        String selectByStatusOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM MauSac 
                           WHERE TrangThai = ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(selectByStatusOffset,
                status, (page - 1) * limit, limit);
    }
}
