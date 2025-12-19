package dao;

import connectDB.ConnectDB;
import entity.BanDat;
import entity.KhachHang;
import entity.Ban;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class BanDat_DAO {

    private final KhachHang_DAO khachHang_DAO = new KhachHang_DAO();
    private final Ban_DAO ban_DAO = new Ban_DAO();

    // ==========================
    //  TẠO ĐỐI TƯỢNG BanDat TỪ RESULTSET
    // ==========================
    private BanDat createBanDatFromResultSet(ResultSet rs) throws SQLException {

        String maDatBan = rs.getString("maDatBan");
        String maKH = rs.getString("maKH");
        String maBan = rs.getString("maBan");

        // Load đầy đủ Entity
        KhachHang kh = khachHang_DAO.timKhachHangTheoMa(maKH);
        Ban ban = ban_DAO.getBanById(maBan);

        LocalDate ngayDat = rs.getDate("ngayDat").toLocalDate();
        LocalTime gioDat = rs.getTime("gioDat").toLocalTime();

        int soLuong = rs.getInt("soLuongKhach");
        double tienCoc = rs.getDouble("tienCoc");
        String trangThai = rs.getString("trangThai");
        String ghiChu = rs.getString("ghiChu");

        Time sqlCheckin = rs.getTime("gioCheckIn");
        LocalTime gioCheckIn = sqlCheckin != null ? sqlCheckin.toLocalTime() : null;

        return new BanDat(maDatBan, kh, ban, ngayDat, gioDat, soLuong, tienCoc, trangThai, ghiChu, gioCheckIn);
    }

    // ==========================
    //  LẤY TẤT CẢ ĐẶT BÀN
    // ==========================
    public ArrayList<BanDat> getAllBanDat() {
        ArrayList<BanDat> ds = new ArrayList<>();
        Connection con = ConnectDB.getConnection();

        String sql =
                "SELECT maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu, gioCheckIn " +
                "FROM BANDAT";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(createBanDatFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ==========================
    //  LẤY ĐẶT BÀN THEO MÃ
    // ==========================
    public BanDat getBanDatById(String maDatBan) {
        Connection con = ConnectDB.getConnection();
        BanDat bd = null;

        String sql =
                "SELECT maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu, gioCheckIn " +
                "FROM BANDAT WHERE maDatBan = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bd = createBanDatFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bd;
    }


    // ==========================
    //  THÊM ĐẶT BÀN (CÓ KIỂM TRA XUNG ĐỘT)
    // ==========================
    public boolean addBanDat(BanDat banDat) throws Exception {

        Connection con = ConnectDB.getConnection();

        String maBan = banDat.getBan().getMaBan();
        String trangThaiBan = ban_DAO.getTrangThaiBan(maBan);

        LocalDate ngayNow = LocalDate.now();
        LocalTime gioNow = LocalTime.now();

        boolean laKhachTrucTiep =
                banDat.getGhiChu() != null &&
                banDat.getGhiChu().equalsIgnoreCase("Khách vào trực tiếp");

        // ==========================
        // ⛔ TRƯỜNG HỢP 1 – Bàn đang dùng
        // ==========================
        if (trangThaiBan.equals("Đang sử dụng") && !laKhachTrucTiep) {

            LocalTime gioChoPhep = gioNow.plusHours(4);

            if (banDat.getNgayDat().equals(ngayNow)
             && banDat.getGioDat().isBefore(gioChoPhep)) {

                throw new Exception(
                    "Bàn đang được sử dụng — chỉ đặt sau " +
                    gioChoPhep.toString()
                );
            }
        }

        // ==========================
        // ⛔ TRƯỜNG HỢP 2 – Bàn trống nhưng đặt giờ quá khứ
        // ==========================
        if (trangThaiBan.equals("Trống") && !laKhachTrucTiep) {
            if (banDat.getNgayDat().equals(ngayNow)
             && banDat.getGioDat().isBefore(gioNow)) {

                throw new Exception("Giờ đặt phải sau thời điểm hiện tại.");
            }
        }

        // ==========================
        // ⛔ TRƯỜNG HỢP 3 – KIỂM TRA XUNG ĐỘT
        //   ❗ KHÁCH TRỰC TIẾP → BỎ QUA
        // ==========================
        if (!laKhachTrucTiep) {
            boolean conflict =
                    kiemTraXungDotDatBan(maBan, banDat.getNgayDat(), banDat.getGioDat());

            if (conflict) {
                throw new Exception("Bàn đã có lịch đặt trong ±2 tiếng.");
            }
        }

        // ==========================
        //  INSERT DATABASE
        // ==========================
        String sql =
                "INSERT INTO BANDAT(maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu, gioCheckIn) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, banDat.getMaDatBan());
            ps.setString(2, banDat.getKhachHang().getMaKH());
            ps.setString(3, maBan);
            ps.setDate(4, Date.valueOf(banDat.getNgayDat()));
            ps.setTime(5, Time.valueOf(banDat.getGioDat()));
            ps.setInt(6, banDat.getSoLuongKhach());
            ps.setDouble(7, banDat.getTienCoc());
            ps.setString(8, banDat.getTrangThai());
            ps.setString(9, banDat.getGhiChu());

            if (banDat.getTrangThai().equalsIgnoreCase("Đang sử dụng")) {
                ps.setTime(10, Time.valueOf(LocalTime.now()));
            } else {
                ps.setNull(10, Types.TIME);
            }

            return ps.executeUpdate() > 0;

        }
    }

    // ==========================
    //  THÊM ĐẶT BÀN TRỰC TIẾP
    // ==========================
    public boolean addBanDatTrucTiep(BanDat bd) throws SQLException {

        Connection con = ConnectDB.getConnection();

        String sql =
            "INSERT INTO BANDAT(maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu, gioCheckIn) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bd.getMaDatBan());
            ps.setString(2, bd.getKhachHang().getMaKH());
            ps.setString(3, bd.getBan().getMaBan());
            ps.setDate(4, Date.valueOf(bd.getNgayDat()));
            ps.setTime(5, Time.valueOf(bd.getGioDat()));
            ps.setInt(6, bd.getSoLuongKhach());
            ps.setDouble(7, bd.getTienCoc());
            ps.setString(8, bd.getTrangThai());
            ps.setString(9, bd.getGhiChu());

            if ("Đang sử dụng".equalsIgnoreCase(bd.getTrangThai())) {
                ps.setTime(10, Time.valueOf(bd.getGioCheckIn()));
            } else {
                ps.setNull(10, Types.TIME);
            }

            return ps.executeUpdate() > 0;
        }
    }


    // ==========================
    //  UPDATE ĐẶT BÀN
    // ==========================
    public boolean updateBanDat(BanDat bd) {
        Connection con = ConnectDB.getConnection();

        String sql =
                "UPDATE BANDAT SET maKH=?, maBan=?, ngayDat=?, gioDat=?, soLuongKhach=?, tienCoc=?, trangThai=?, ghiChu=?, gioCheckIn=? " +
                "WHERE maDatBan=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bd.getKhachHang().getMaKH());
            ps.setString(2, bd.getBan().getMaBan());
            ps.setDate(3, Date.valueOf(bd.getNgayDat()));
            ps.setTime(4, Time.valueOf(bd.getGioDat()));
            ps.setInt(5, bd.getSoLuongKhach());
            ps.setDouble(6, bd.getTienCoc());
            ps.setString(7, bd.getTrangThai());
            ps.setString(8, bd.getGhiChu());

            if (bd.getGioCheckIn() != null)
                ps.setTime(9, Time.valueOf(bd.getGioCheckIn()));
            else
                ps.setNull(9, Types.TIME);

            ps.setString(10, bd.getMaDatBan());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
    //  XOÁ
    // ==========================
    public boolean deleteBanDat(String maDatBan) {
        Connection con = ConnectDB.getConnection();

        String sql = "DELETE FROM BANDAT WHERE maDatBan=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
    //  KIỂM TRA XUNG ĐỘT ĐẶT BÀN
    // ==========================
    public boolean kiemTraXungDotDatBan(String maBan, LocalDate ngayDat, LocalTime gioDat) throws SQLException {

        Connection con = ConnectDB.getConnection();

        LocalTime start = gioDat.minusHours(2);
        LocalTime end = gioDat.plusHours(2);

        String sql =
                "SELECT COUNT(*) FROM BANDAT WHERE maBan=? AND ngayDat=? " +
                "AND trangThai NOT IN (N'Đã hủy', N'Hoàn thành') " +
                "AND CAST(gioDat AS TIME) >= CAST(? AS TIME) " +
                "AND CAST(gioDat AS TIME) <= CAST(? AS TIME)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);
            ps.setDate(2, Date.valueOf(ngayDat));
            ps.setTime(3, Time.valueOf(start));
            ps.setTime(4, Time.valueOf(end));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        }
        return false;
    }

    // ==========================
    //  LẤY ĐẶT BÀN ĐANG SỬ DỤNG
    // ==========================
    public BanDat getBanDatDangSuDung(String maBan) {
        Connection con = ConnectDB.getConnection();
        BanDat bd = null;

        String sql =
            "SELECT TOP 1 * FROM BANDAT " +
            "WHERE maBan=? AND trangThai=N'Đang sử dụng' " +
            "ORDER BY ngayDat DESC, gioDat DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) bd = createBanDatFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bd;
    }

    // ==========================
    //  SINH MÃ
    // ==========================
    public String generateNewMaDatBan() {
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT MAX(maDatBan) FROM BANDAT WHERE maDatBan LIKE 'DB%'";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getString(1) != null) {
                String last = rs.getString(1);
                int num = Integer.parseInt(last.substring(2)) + 1;
                return String.format("DB%03d", num);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "DB001";
    }

    // ==========================
    //  LẤY TIỀN CỌC
    // ==========================
    public double getTienCocByActiveMaBan(String maBan) {
        Connection con = ConnectDB.getConnection();
        double coc = 0;

        String sql =
            "SELECT TOP 1 tienCoc FROM BANDAT " +
            "WHERE maBan=? AND trangThai=N'Đang sử dụng' " +
            "ORDER BY ngayDat DESC, gioDat DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) coc = rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coc;
    }
    public boolean updateGioCheckIn(String maDatBan, LocalTime gioCheckIn) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE BANDAT SET gioCheckIn = ? WHERE maDatBan = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            if (gioCheckIn != null)
                ps.setTime(1, Time.valueOf(gioCheckIn));
            else
                ps.setNull(1, Types.TIME);

            ps.setString(2, maDatBan);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi update giờ check-in: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTrangThaiBanDat(String maBanDat, String trangThai) {
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "UPDATE BanDat SET trangThai = ? WHERE maDatBan = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, trangThai);
            stmt.setString(2, maBanDat);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }
}
