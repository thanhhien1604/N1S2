/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.KhachHang;
import java.sql.ResultSet;
import java.sql.SQLException;
import repository.JdbcHelper;

/**
 *
 * @author ledin
 */
public class KhachHangService extends SellingApplicationImpl<KhachHang, Integer> {

    @Override
    public void insert(KhachHang entity) {
        String sql = """
                     INSERT INTO [dbo].[KhachHang]
                                ([Ten]
                                ,[NgaySinh]
                                ,[GioiTinh]
                                ,[SDT])
                          VALUES (?, ?, ?, ?)
                     """;
        JdbcHelper.update(sql,
                entity.getTen(),
                entity.getNgaySinh(),
                entity.getGioiTinh(),
                entity.getSdt());
    }

    @Override
    public void update(KhachHang entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public KhachHang selectById(Integer id) {
        String sql = "SELECT * FROM KhachHang WHERE ID = ?";

        List<KhachHang> list = this.selectBySql(sql, id);
        if (list == null) {
            return null;
        }

        return list.get(0);
    }

    public KhachHang selectBySDT(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE SDT like ?";

        List<KhachHang> list = this.selectBySql(sql, "%" + sdt + "%");
        if (list == null) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public List<KhachHang> selectAll() {
        String sql = "SELECT * FROM KhachHang";

        return this.selectBySql(sql);
    }

    @Override
    protected List<KhachHang> selectBySql(String sql, Object... args) {
        List<KhachHang> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setId(rs.getInt("ID"));
                kh.setMa(rs.getString("Ma"));
                kh.setTen(rs.getString("Ten"));
                kh.setNgaySinh(rs.getDate("NgaySinh"));
                kh.setGioiTinh(rs.getBoolean("GioiTinh"));
                kh.setSdt(rs.getString("SDT"));

                list.add(kh);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<KhachHang> selestPages(int pages, int limit) {
        String sql = """
                     SELECT * 
                     FROM 
                     (
                         SELECT * FROM KhachHang
                     ) AS FilteredResults
                     ORDER BY ID
                     OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                     """;
        return this.selectBySql(sql, (pages - 1) * limit, limit);
    }
}
