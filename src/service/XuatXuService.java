/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.XuatXu;
import repository.JdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ledin
 */
public class XuatXuService extends SellingApplicationImpl<XuatXu, Integer> {

    String insert_sql = """
                        INSERT INTO [dbo].[XuatXu]
                                   ([Ten]
                                   ,[TrangThai])
                             VALUES (?, ?)
                        """;

    String update_sql = """
                        UPDATE [dbo].[XuatXu]
                           SET [Ten] = ?
                              ,[TrangThai] = ?
                         WHERE ID = ?
                        """;

    String delete_sql = """
                        DELETE FROM [dbo].[XuatXu]
                              WHERE ID = ?
                        """;

    String selectById = """
                        select * from XuatXu where ID = ?
                        """;

    String selectByKeyWord = """
                        SELECT * 
                        FROM XuatXu
                        WHERE Ten LIKE ?
                          """;

    String selectByStatus = """
                        SELECT * 
                        FROM XuatXu
                        WHERE TrangThai = ?
                          """;

    String selectAll = """
                       select * from XuatXu
                       """;

    String selectBykeyWordOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM XuatXu
                           WHERE Ten LIKE ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;

    String selectbyStatusOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM XuatXu
                           WHERE TrangThai = ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;

    @Override
    public void insert(XuatXu entity) {
        JdbcHelper.update(insert_sql,
                entity.getName(), entity.getStatus());
    }

    @Override
    public void update(XuatXu entity) {
        JdbcHelper.update(update_sql,
                entity.getName(),
                entity.getStatus(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(delete_sql, id);
    }

    @Override
    public XuatXu selectById(Integer id) {
        List<XuatXu> list = this.selectBySql(selectById, id);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<XuatXu> selectBySql(String sql, Object... args) {
        List<XuatXu> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                XuatXu xx = new XuatXu();
                xx.setId(rs.getInt("ID"));
                xx.setName(rs.getString("Ten"));
                xx.setStatus(rs.getBoolean("TrangThai"));
                list.add(xx);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<XuatXu> selectByKeyWord(String keyWord) {
        return this.selectBySql(selectByKeyWord, "%" + keyWord + "%%");
    }

    public List<XuatXu> selectByStatus(Boolean status) {
        return this.selectBySql(selectByStatus, status);
    }

    public List<XuatXu> selectAll() {
        return this.selectBySql(selectAll);
    }

    public List<XuatXu> searchKeyWord(String keyWord, int page, int limit) {
        return this.selectBySql(selectBykeyWordOffset,
                "%" + keyWord + "%%", (page - 1) * limit, limit);
    }

    public List<XuatXu> filterByStatus(Boolean status, int page, int limit) {
        return this.selectBySql(selectbyStatusOffset,
                status, (page - 1) * limit, limit);
    }
}
