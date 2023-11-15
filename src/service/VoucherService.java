/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.Voucher;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.NhanVien;
import repository.JdbcHelper;

/**
 *
 * @author ledin
 */
public class VoucherService extends SellingApplicationImpl<Voucher, Integer> {

    @Override
    public void insert(Voucher entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Voucher entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Voucher selectById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Voucher> selectAll() {
        String sql = """
                     SELECT
                             dbo.Voucher.ID,
                             dbo.Voucher.Ma,
                             dbo.Voucher.Ten,
                             dbo.NhanVien.Ma AS MaNV,
                             dbo.NhanVien.Ten AS TenNV,
                             dbo.Voucher.NgayTao
                         FROM
                             dbo.NhanVien
                         INNER JOIN
                             dbo.Voucher ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien
                     """;

        return this.selectBySql(sql);
    }

    @Override
    protected List<Voucher> selectBySql(String sql, Object... args) {
        List<Voucher> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Voucher vc = new Voucher();
                vc.setId(rs.getInt("ID"));
                vc.setMa(rs.getString("Ma"));
                vc.setTen(rs.getString("Ten"));
                vc.setNgayTao(rs.getDate("NgayTao"));
                vc.setNv(new NhanVien(rs.getString("MaNV"), rs.getString("TenNV")));

                list.add(vc);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

}
