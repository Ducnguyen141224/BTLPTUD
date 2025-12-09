package dao;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.TheThanhVien;

import java.sql.*;

public class TheThanhVien_DAO {

    // =========================================================
    //  LẤY THẺ THEO MÃ KHÁCH HÀNG
    // =========================================================
    public TheThanhVien layTheTheoMaKH(String maKH) throws SQLException {
        TheThanhVien theTV = null;
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT maThe, diemTichLuy, loaiHang FROM THETHANHVIEN WHERE maKH = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKH);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String maThe = rs.getString("maThe");
                    int diem = rs.getInt("diemTichLuy");
                    String loaiHang = rs.getString("loaiHang");

                    KhachHang kh = new KhachHang(maKH);
                    theTV = new TheThanhVien(maThe, kh, diem, loaiHang);
                }
            }
        }

        return theTV;
    }

    // =========================================================
    //  LẤY THẺ THEO MÃ THẺ (CẦN THIẾT KHI LẬP HÓA ĐƠN)
    // =========================================================
    public TheThanhVien layTheTheoMaThe(String maThe) throws SQLException {
        TheThanhVien theTV = null;
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT maKH, diemTichLuy, loaiHang FROM THETHANHVIEN WHERE maThe = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maThe);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String maKH = rs.getString("maKH");
                    int diem = rs.getInt("diemTichLuy");
                    String loaiHang = rs.getString("loaiHang");

                    theTV = new TheThanhVien(maThe, new KhachHang(maKH), diem, loaiHang);
                }
            }
        }

        return theTV;
    }

    // =========================================================
    //  THÊM THẺ THÀNH VIÊN
    // =========================================================
    public boolean themTheThanhVien(TheThanhVien theTV) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "INSERT INTO THETHANHVIEN(maThe, maKH, diemTichLuy, loaiHang) VALUES (?, ?, ?, ?)";

        int affected = 0;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, theTV.getMaThe());
            stmt.setString(2, theTV.getKhachHang().getMaKH());
            stmt.setInt(3, theTV.getDiemTichLuy());
            stmt.setString(4, theTV.getLoaiHang());

            affected = stmt.executeUpdate();
        }

        return affected > 0;
    }

    // =========================================================
    //  CẬP NHẬT ĐIỂM + LOẠI HẠNG (DÙNG KHI CHỈNH SỬA)
    // =========================================================
    public boolean capNhatTheThanhVien(TheThanhVien theTV) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE THETHANHVIEN SET loaiHang = ?, diemTichLuy = ? WHERE maThe = ?";

        int affected = 0;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, theTV.getLoaiHang());
            stmt.setInt(2, theTV.getDiemTichLuy());
            stmt.setString(3, theTV.getMaThe());

            affected = stmt.executeUpdate();
        }

        return affected > 0;
    }

    // =========================================================
    //  CỘNG ĐIỂM TÍCH LUỸ (DÙNG SAU KHI LẬP HÓA ĐƠN)
    // =========================================================
    public boolean congDiem(String maThe, int diemCong) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE THETHANHVIEN SET diemTichLuy = diemTichLuy + ? WHERE maThe = ?";

        int affected = 0;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, diemCong);
            stmt.setString(2, maThe);

            affected = stmt.executeUpdate();
        }

        return affected > 0;
    }

    // =========================================================
    //  CẬP NHẬT LOẠI HẠNG TỰ ĐỘNG THEO ĐIỂM (Bạc/Vàng/Kim cương)
    // =========================================================
    public boolean capNhatLoaiHangTheoDiem(String maThe) throws SQLException {
        Connection con = ConnectDB.getConnection();

        String sql =
            "UPDATE THETHANHVIEN SET loaiHang = " +
            "   CASE " +
            "       WHEN diemTichLuy >= 250 THEN N'Kim cương' " +
            "       WHEN diemTichLuy >= 100 THEN N'Vàng' " +
            "       ELSE N'Bạc' " +
            "   END " +
            "WHERE maThe = ?";

        int affected = 0;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maThe);
            affected = stmt.executeUpdate();
        }

        return affected > 0;
    }

    // =========================================================
    //  XOÁ THẺ THEO MÃ KHÁCH HÀNG
    // =========================================================
    public boolean xoaTheThanhVienTheoMaKH(String maKH) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "DELETE FROM THETHANHVIEN WHERE maKH = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            return stmt.executeUpdate() > 0;
        }
    }

    // =========================================================
    //  PHÁT SINH MÃ THẺ MỚI (TVxxx)
    // =========================================================
    public String phatSinhMaThe() throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT MAX(maThe) FROM THETHANHVIEN WHERE maThe LIKE 'TV%'";

        String maMoi = "TV001";

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String maMax = rs.getString(1);
                if (maMax != null) {
                    try {
                        int so = Integer.parseInt(maMax.substring(2)) + 1;
                        maMoi = String.format("TV%03d", so);
                    } catch (Exception e) {
                        maMoi = "TV001"; // fallback
                    }
                }
            }
        }

        return maMoi;
    }
}
