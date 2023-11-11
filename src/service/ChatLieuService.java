/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ChatLieu;
import repository.JdbcHelper;

/**
 *
 * @author ledin
 */
public class ChatLieuService extends SellingApplicationImpl<ChatLieu, Integer> {

    @Override
    public void insert(ChatLieu entity) {
        String sql = """
                        INSERT INTO [dbo].[ChatLieu]
                                   ([Ten])
                             VALUES (?)
                        """;

        JdbcHelper.update(sql,
                entity.getTen());
    }

    @Override
    public void update(ChatLieu entity) {
        String sql = """
                        UPDATE [dbo].[ChatLieu]
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
                        DELETE FROM [dbo].[ChatLieu]
                              WHERE ID = ?
                        """;

        JdbcHelper.update(delete_sql, id);
    }

    @Override
    public ChatLieu selectById(Integer id) {
        String selectById = """
                        select * from ChatLieu where ID = ?
                        """;
        List<ChatLieu> list = this.selectBySql(selectById, id);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<ChatLieu> selectBySql(String sql, Object... args) {
        List<ChatLieu> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                ChatLieu chatLieu = new ChatLieu();
                chatLieu.setId(rs.getInt("ID"));
                chatLieu.setTen(rs.getString("Ten"));
                list.add(chatLieu);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChatLieu> selectAll() {
        String selectAll = """
                       select * from ChatLieu
                       """;
        return this.selectBySql(selectAll);
    }

    public List<ChatLieu> selectByKeyWord(String keyWord) {
        String selectByKeyWord = """
                        SELECT * 
                        FROM ChatLieu
                        WHERE Ten LIKE ?
                          """;
        return this.selectBySql(selectByKeyWord, "%" + keyWord + "%%");
    }

    public List<ChatLieu> selectByStatus(Boolean status) {
        String selectByStatus = """
                        SELECT * 
                        FROM ChatLieu
                        WHERE TrangThai = ?
                          """;
        return this.selectBySql(selectByStatus, status);
    }

    public List<ChatLieu> selectPages(int page, int limit) {
        String sql = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM ChatLieu
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(sql, (page - 1) * limit, limit);
    }

    public List<ChatLieu> filterByStatus(Boolean status, int page, int limit) {
        String selectByStatusOffset = """
                       SELECT * 
                       FROM 
                       (
                           SELECT * 
                           FROM ChatLieu 
                           WHERE TrangThai = ?
                       ) AS FilteredResults
                       ORDER BY ID
                       OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                       """;
        return this.selectBySql(selectByStatusOffset,
                status, (page - 1) * limit, limit);
    }

}
