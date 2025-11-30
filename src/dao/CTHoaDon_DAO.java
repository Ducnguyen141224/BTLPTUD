package dao;

import connectDB.ConnectDB;
import entity.CT_HoaDon;
import entity.HoaDon;
import entity.MonAn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CTHoaDon_DAO {

    /**
     * THÊM CHI TIẾT HÓA ĐƠN
     */
    public boolean themCTHoaDon(CT_HoaDon ct, Connection con) throws SQLException {

        if (ct.getHoaDon() == null || ct.getMonAn() == null) {
            throw new IllegalArgumentException("Hóa đơn và Món ăn không được null.");
        }

        String sql = "INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ct.getHoaDon().getMaHoaDon());
            ps.setString(2, ct.getMonAn().getMaMonAn());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.tinhThanhTien());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * LẤY DANH SÁCH CHI TIẾT HÓA ĐƠN THEO MÃ HD
     */
    public ArrayList<CT_HoaDon> layDSCTHoaDonTheoMaHD(String maHD) {
        ArrayList<CT_HoaDon> ds = new ArrayList<>();

        String sql = "SELECT CTHD.maHD, CTHD.maMon, CTHD.soLuong, CTHD.thanhTien, " +
                     "MA.tenMon, MA.giaMon " +
                     "FROM CT_HOADON CTHD " +
                     "JOIN MONAN MA ON CTHD.maMon = MA.maMon " +
                     "WHERE CTHD.maHD = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    // Tạo món ăn ảo
                    MonAn mon = new MonAn(rs.getString("maMon"));
                    mon.setTenMonAn(rs.getString("tenMon"));
                    mon.setGiaMonAn(rs.getDouble("giaMon"));

                    // Tạo hóa đơn ảo
                    HoaDon hd = new HoaDon(rs.getString("maHD"));

                    // Tạo chi tiết hóa đơn
                    CT_HoaDon ct = new CT_HoaDon(hd, mon, rs.getInt("soLuong"));
                

                    ds.add(ct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    /**
     * LẤY 1 CHI TIẾT HÓA ĐƠN
     */
    public CT_HoaDon layCTHoaDon(String maHD, String maMon) throws SQLException {

        String sql = "SELECT maHD, maMon, soLuong, thanhTien FROM CT_HOADON WHERE maHD = ? AND maMon = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ps.setString(2, maMon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDon hd = new HoaDon(rs.getString("maHD"));
                    MonAn mon = new MonAn(rs.getString("maMon"));

                    return new CT_HoaDon(hd, mon, rs.getInt("soLuong"));
                }
            }
        }

        return null;
    }

    /**
     * CẬP NHẬT SỐ LƯỢNG
     */
    public boolean capNhatSoLuongCTHoaDon(String maHD, String maMon, int soLuongMoi) throws SQLException {

        String sql = "UPDATE CT_HOADON SET soLuong = ?, thanhTien = soLuong * (SELECT giaMon FROM MONAN WHERE maMon = ?) " +
                     "WHERE maHD = ? AND maMon = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, soLuongMoi);
            ps.setString(2, maMon);
            ps.setString(3, maHD);
            ps.setString(4, maMon);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * XÓA CHI TIẾT HÓA ĐƠN
     */
    public boolean xoaCTHoaDon(String maHD, String maMon) throws SQLException {

        String sql = "DELETE FROM CT_HOADON WHERE maHD = ? AND maMon = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ps.setString(2, maMon);

            return ps.executeUpdate() > 0;
        }
    }
}
