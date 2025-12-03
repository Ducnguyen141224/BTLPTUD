package dao;

import java.sql.*;
import java.util.*;

import connectDB.ConnectDB;

public class Dashboard_DAO {

    // ============================
    // 1. Tổng doanh thu hôm nay
    // ============================
    public double getDoanhThuHomNay() {
        String sql =
                "SELECT SUM(tongTien) " +
                "FROM HOADON " +
                "WHERE CONVERT(date, ngayLap) = CONVERT(date, GETDATE())";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0;

        } catch (Exception e) { e.printStackTrace(); }

        return 0;
    }

    // ============================
    // 2. Số lượng món bán hôm nay
    // ============================
    public int getSoMonBanHomNay() {
        String sql =
                "SELECT SUM(ct.soLuong) " +
                "FROM CT_HOADON ct " +
                "JOIN HOADON hd ON ct.maHD = hd.maHD " +
                "WHERE CONVERT(date, hd.ngayLap) = CONVERT(date, GETDATE())";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (Exception e) { e.printStackTrace(); }

        return 0;
    }

    // ============================
    // 3. Doanh thu tháng này
    // ============================
    public double getDoanhThuThangNay() {
        String sql =
                "SELECT SUM(tongTien) " +
                "FROM HOADON " +
                "WHERE MONTH(ngayLap) = MONTH(GETDATE()) " +
                "AND YEAR(ngayLap) = YEAR(GETDATE())";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0;

        } catch (Exception e) { e.printStackTrace(); }

        return 0;
    }

    // ============================
    // 4. Doanh thu quý này
    // ============================
    public double getDoanhThuQuyNay() {
        String sql =
                "SELECT SUM(tongTien) " +
                "FROM HOADON " +
                "WHERE DATEPART(QUARTER, ngayLap) = DATEPART(QUARTER, GETDATE()) " +
                "AND YEAR(ngayLap) = YEAR(GETDATE())";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0;

        } catch (Exception e) { e.printStackTrace(); }

        return 0;
    }

    // ============================
    // 5. TỈ LỆ KHUNG GIỜ (Pie chart)
    // ============================
    public Map<String, Integer> getTiLeTheoKhungGio() {

        String sql =
                "SELECT " +
                "   CASE " +
                "       WHEN DATEPART(HOUR, ngayLap) BETWEEN 6 AND 10 THEN N'Sáng' " +
                "       WHEN DATEPART(HOUR, ngayLap) BETWEEN 11 AND 15 THEN N'Trưa' " +
                "       WHEN DATEPART(HOUR, ngayLap) BETWEEN 16 AND 20 THEN N'Tối' " +
                "       ELSE N'Đêm' " +
                "   END AS khungGio, " +
                "   COUNT(*) AS soLuong " +
                "FROM HOADON " +
                "GROUP BY " +
                "   CASE " +
                "       WHEN DATEPART(HOUR, ngayLap) BETWEEN 6 AND 10 THEN N'Sáng' " +
                "       WHEN DATEPART(HOUR, ngayLap) BETWEEN 11 AND 15 THEN N'Trưa' " +
                "       WHEN DATEPART(HOUR, ngayLap) BETWEEN 16 AND 20 THEN N'Tối' " +
                "       ELSE N'Đêm' " +
                "   END";

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("khungGio"), rs.getInt("soLuong"));
            }

        } catch (Exception e) { e.printStackTrace(); }

        return map;
    }

    // ============================
    // 6. Đặt bàn trong tuần (Bar chart)
    // ============================
    public Map<String, Integer> getThongKeDatBanTuanNay() {

        String sql =
                "SELECT DATENAME(WEEKDAY, ngayDat) AS ngay, COUNT(*) AS soLuong " +
                "FROM BANDAT " +
                "WHERE DATEPART(WEEK, ngayDat) = DATEPART(WEEK, GETDATE()) " +
                "AND YEAR(ngayDat) = YEAR(GETDATE()) " +
                "GROUP BY DATENAME(WEEKDAY, ngayDat)";

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("ngay"), rs.getInt("soLuong"));
            }

        } catch (Exception e) { e.printStackTrace(); }

        return map;
    }

    // ============================
    // 7. Top 3 món bán chạy
    // ============================
    public List<Map<String, Object>> getTop3MonBanChay() {

        String sql =
                "SELECT TOP 3 m.tenMon, SUM(ct.soLuong) AS soLuong " +
                "FROM CT_HOADON ct " +
                "JOIN MONAN m ON ct.maMon = m.maMon " +
                "GROUP BY m.tenMon " +
                "ORDER BY soLuong DESC";

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("tenMon", rs.getString("tenMon"));
                item.put("soLuong", rs.getInt("soLuong"));
                list.add(item);
            }

        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }
}
