
package dao;

import connectDB.ConnectDB;
import entity.BanDat;
import entity.KhachHang; // Import KhachHang entity
import entity.Ban;       // Import Ban entity
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class BanDat_DAO {

    // Khởi tạo các DAO phụ thuộc để truy vấn các Entity liên quan
    private KhachHang_DAO khachHang_DAO = new KhachHang_DAO();
    private Ban_DAO ban_DAO = new Ban_DAO();

   
    private BanDat createBanDatFromResultSet(ResultSet rs) throws SQLException {
        // Lấy các mã khóa ngoại từ ResultSet
        String maKhachHang = rs.getString("maKH");
        String maBan = rs.getString("maBan");


        KhachHang khachHang = khachHang_DAO.timKhachHangTheoMa(maKhachHang); 
        Ban ban = ban_DAO.getBanById(maBan);

        // Lấy các trường thông thường
        String maDatBan = rs.getString("maDatBan");
        
        // Chuyển đổi java.sql.Date và java.sql.Time sang java.time.LocalDate và LocalTime
        LocalDate ngayDat = rs.getDate("ngayDat").toLocalDate();
        LocalTime gioDat = rs.getTime("gioDat").toLocalTime();
        
        int soLuongKhach = rs.getInt("soLuongKhach");
        double tienCoc = rs.getDouble("tienCoc");
        String trangThai = rs.getString("trangThai");
        String ghiChu = rs.getString("ghiChu");
        Time gioCheckInSQL = rs.getTime("gioCheckIn");
        LocalTime gioCheckIn = (gioCheckInSQL != null) ? gioCheckInSQL.toLocalTime() : null;

        BanDat bd = new BanDat(maDatBan, khachHang, ban, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu,gioCheckIn);
        bd.setGioCheckIn(gioCheckIn);  // gán giờ check-in

        return bd;
    }

    public ArrayList<BanDat> getAllBanDat() {
        ArrayList<BanDat> dsDatBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        // Cần SELECT cả mã KH và mã Bàn để query đối tượng
        String sql = "SELECT maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu, gioCheckIn FROM BANDAT";
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                dsDatBan.add(createBanDatFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsDatBan;
    }
    
    
    public BanDat getBanDatById(String maDatBan) {
        BanDat banDat = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu,gioCheckIn FROM BANDAT WHERE maDatBan = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    banDat = createBanDatFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm đặt bàn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return banDat;
    }

  
    public boolean addBanDat(BanDat banDat) throws Exception { 
        Connection con = ConnectDB.getConnection();
        String maBan = banDat.getBan().getMaBan();
        String trangthai = ban_DAO.getTrangThaiBan(maBan);

        LocalDate ngayHienTai = LocalDate.now();
        LocalTime gioHienTai = LocalTime.now();

        // ⭐ Xác định khách vào trực tiếp (không áp dụng kiểm tra giờ)
        boolean laKhachTrucTiep =
                banDat.getGhiChu() != null &&
                banDat.getGhiChu().equalsIgnoreCase("Khách vào trực tiếp");

        // ⚠️ Trường hợp 1: Bàn đang sử dụng
        if (trangthai.equals("Đang sử dụng")) {
            LocalTime gioChoPhep = gioHienTai.plusHours(4);

            if (!laKhachTrucTiep) {   // ⭐ tránh chặn khách trực tiếp
                if (banDat.getNgayDat().equals(ngayHienTai)
                 && banDat.getGioDat().isBefore(gioChoPhep)) {
                    throw new Exception("Bàn này đang được sử dụng — chỉ có thể đặt sau "
                            + gioChoPhep.truncatedTo(java.time.temporal.ChronoUnit.MINUTES) + ".");
                }
            }
        }

        // ⚠️ Trường hợp 2: Bàn trống nhưng đặt giờ quá khứ
        if (trangthai.equals("Trống")) {
            if (!laKhachTrucTiep) {   // ⭐ KHÔNG check với khách trực tiếp
                if (banDat.getNgayDat().equals(ngayHienTai)
                 && banDat.getGioDat().isBefore(gioHienTai)) {
                    throw new Exception("Giờ đặt phải sau thời điểm hiện tại ("
                            + gioHienTai.truncatedTo(java.time.temporal.ChronoUnit.MINUTES) + ").");
                }
            }
        }

        // ⚠️ Trường hợp 3: Kiểm tra xung đột đặt bàn (khách trực tiếp vẫn phải check để tránh chồng chéo)
        boolean xungDot = kiemTraXungDotDatBan(maBan, banDat.getNgayDat(), banDat.getGioDat());
        if (xungDot) {
            throw new Exception("Bàn này đã được đặt trong khoảng thời gian ±2 tiếng so với giờ bạn chọn.");
        }

        // --- INSERT DATABASE ---
        String sql = "INSERT INTO BANDAT (maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu,gioCheckIn) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int n = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, banDat.getMaDatBan());
            ps.setString(2, banDat.getKhachHang().getMaKH());
            ps.setString(3, maBan);
            ps.setDate(4, java.sql.Date.valueOf(banDat.getNgayDat()));
            ps.setTime(5, java.sql.Time.valueOf(banDat.getGioDat()));
            ps.setInt(6, banDat.getSoLuongKhach());
            ps.setDouble(7, banDat.getTienCoc());
            ps.setString(8, banDat.getTrangThai());
            ps.setString(9, banDat.getGhiChu());
            if ("Trống".equalsIgnoreCase(banDat.getTrangThai())) {
                ps.setNull(10, Types.TIME);
            } else {
                ps.setTime(10, 
                    banDat.getGioCheckIn() != null ? Time.valueOf(banDat.getGioCheckIn()) : null
                );
            }

            n = ps.executeUpdate();
        }

        return n > 0;
    }

    public boolean addBanDatTrucTiep(BanDat banDat) throws SQLException {
        Connection con = ConnectDB.getConnection();

        String sql = "INSERT INTO BANDAT " +
                "(maDatBan, maKH, maBan, ngayDat, gioDat, soLuongKhach, tienCoc, trangThai, ghiChu, gioCheckIn) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, banDat.getMaDatBan());
            ps.setString(2, banDat.getKhachHang().getMaKH());
            ps.setString(3, banDat.getBan().getMaBan());
            ps.setDate(4, Date.valueOf(banDat.getNgayDat()));
            ps.setTime(5, Time.valueOf(banDat.getGioDat()));
            ps.setInt(6, banDat.getSoLuongKhach());
            ps.setDouble(7, banDat.getTienCoc());
            ps.setString(8, banDat.getTrangThai());
            ps.setString(9, banDat.getGhiChu());
            if ("Đang sử dụng".equalsIgnoreCase(banDat.getTrangThai())) {
                ps.setTime(10, Time.valueOf(banDat.getGioCheckIn()));
            } else {
                ps.setNull(10, Types.TIME);
            }

            return ps.executeUpdate() > 0;
        }
    }



   
    public boolean updateBanDat(BanDat banDat) {
        Connection con = ConnectDB.getConnection();
        
        // --- SỬA LỖI TẠI ĐÂY ---
        // 1. Thêm ", gioCheckIn = ?" vào câu lệnh SQL
        String sql = "UPDATE BANDAT SET maKH = ?, maBan = ?, ngayDat = ?, gioDat = ?, soLuongKhach = ?, tienCoc = ?, trangThai = ?, ghiChu = ?, gioCheckIn = ? WHERE maDatBan = ?";
        
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, banDat.getKhachHang().getMaKH());
            ps.setString(2, banDat.getBan().getMaBan());
            ps.setDate(3, java.sql.Date.valueOf(banDat.getNgayDat()));
            ps.setTime(4, java.sql.Time.valueOf(banDat.getGioDat()));
            ps.setInt(5, banDat.getSoLuongKhach());
            ps.setDouble(6, banDat.getTienCoc());
            ps.setString(7, banDat.getTrangThai());
            ps.setString(8, banDat.getGhiChu());

            // --- SỬA LỖI TẠI ĐÂY: Tham số thứ 9 là gioCheckIn ---
            // Nếu trạng thái = Trống hoặc Đã đặt (chưa vào) -> không có giờ check-in (NULL)
            if ("Trống".equalsIgnoreCase(banDat.getTrangThai()) || "Đã đặt".equalsIgnoreCase(banDat.getTrangThai())) {
                // Nếu bạn muốn giữ giờ checkin khi update trạng thái khác thì cần logic khác, 
                // nhưng thường update về 'Đang sử dụng' mới cần set giờ.
                // Ở đây ta set theo object truyền vào:
                 if (banDat.getGioCheckIn() != null) {
                     ps.setTime(9, java.sql.Time.valueOf(banDat.getGioCheckIn()));
                 } else {
                     ps.setNull(9, java.sql.Types.TIME);
                 }
            } else {
                // Trường hợp 'Đang sử dụng', 'Hoàn thành'...
                ps.setTime(9, 
                    banDat.getGioCheckIn() != null ? java.sql.Time.valueOf(banDat.getGioCheckIn()) : null
                );
            }

            // --- SỬA LỖI TẠI ĐÂY: Tham số thứ 10 là maDatBan (Điều kiện WHERE) ---
            ps.setString(10, banDat.getMaDatBan());
            
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

    
    public boolean deleteBanDat(String maDatBan) {
        Connection con = ConnectDB.getConnection();
        String sql = "DELETE FROM BANDAT WHERE maDatBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

   
 // Trong lớp dao.BanDat_DAO

    public ArrayList<BanDat> searchBanDat(String keyword, String trangThaiLoc) {
        ArrayList<BanDat> dsDatBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        
        // Cần JOIN với bảng KhachHang để tìm kiếm theo Tên KH hoặc SĐT
        StringBuilder sql = new StringBuilder("SELECT BD.* FROM BANDAT BD JOIN KHACHHANG KH ON BD.maKH = KH.maKH WHERE 1=1");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 💡 SỬA LỖI: Đổi KH.tenKH thành KH.hoTenKH và KH.sdt thành KH.soDienThoai
            sql.append(" AND (BD.maDatBan LIKE ? OR KH.hoTenKH LIKE ? OR KH.soDienThoai LIKE ? OR BD.maBan LIKE ?)");
        }
        if (trangThaiLoc != null && !trangThaiLoc.equals("Tất cả")) {
            sql.append(" AND BD.trangThai = ?");
        }
        
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                ps.setString(paramIndex++, likeKeyword); // BD.maDatBan
                ps.setString(paramIndex++, likeKeyword); // KH.hoTenKH (Đã sửa)
                ps.setString(paramIndex++, likeKeyword); // KH.soDienThoai (Đã sửa)
                ps.setString(paramIndex++, likeKeyword); // BD.maBan
            }
            if (trangThaiLoc != null && !trangThaiLoc.equals("Tất cả")) {
                ps.setString(paramIndex++, trangThaiLoc);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Giả định hàm createBanDatFromResultSet() tồn tại và hoạt động đúng
                    dsDatBan.add(createBanDatFromResultSet(rs)); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsDatBan;
    }
    
   
    public String generateNewMaDatBan() {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT MAX(maDatBan) FROM BANDAT WHERE maDatBan LIKE 'DB%'";
        String lastId = null;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lastId = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lastId == null) {
            return "DB001";
        } else {
            // Giả định mã có dạng DBxxx
            try {
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("DB%03d", num);
            } catch (NumberFormatException e) {
                // Xử lý trường hợp mã không đúng format
                return "DB" + System.currentTimeMillis(); 
            }
        }
    }
    public double getTienCocByActiveMaBan(String maBan) {
        double tienCoc = 0;
        Connection con = ConnectDB.getConnection();
        
        // Tìm bản ghi BANDAT đang hoạt động (chưa thanh toán/hủy) cho bàn này
        String sql = "SELECT tienCoc FROM BANDAT WHERE maBan = ? "; //AND trangThai IN (N'Đang sử dụng', N'Đã đặt', N'Trống')";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tienCoc = rs.getDouble("tienCoc");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tiền cọc theo mã bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return tienCoc;
    }
 // Trong lớp dao.BanDat_DAO.java

    public boolean kiemTraXungDotDatBan(String maBan, LocalDate ngayDat, LocalTime gioDat) throws SQLException {
        Connection con = ConnectDB.getConnection();

        // Tính khoảng thời gian trước và sau 2 tiếng
        LocalTime gioBatDau = gioDat.minusHours(2);
        LocalTime gioKetThuc = gioDat.plusHours(2);
       
       
        String sql = "SELECT COUNT(*) FROM BANDAT " +
                "WHERE maBan = ? AND ngayDat = ? " +
                "AND trangThai NOT IN (N'Đã hủy', N'Hoàn thành') " +
                "AND CAST(gioDat AS TIME) BETWEEN CAST(? AS TIME) AND CAST(? AS TIME)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            ps.setDate(2, java.sql.Date.valueOf(ngayDat));
            ps.setTime(3, java.sql.Time.valueOf(gioBatDau));
            ps.setTime(4, java.sql.Time.valueOf(gioKetThuc));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true nếu có xung đột
                }
               
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra xung đột đặt bàn: " + e.getMessage());
            throw e;
        }

        return false;
 
  
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
    public BanDat getBanDatByMaBan(String maBan) {
        Connection con = ConnectDB.getConnection();
        BanDat banDat = null;

        String sql =
            "SELECT * FROM BANDAT " +
            "WHERE maBan = ? AND trangThai = N'Đang sử dụng'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    banDat = createBanDatFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return banDat;
    }

    public LocalTime getGioCheckInTheoBan(String maBan) {
        Connection con = ConnectDB.getConnection();
        String sql =
            "SELECT gioCheckIn FROM BANDAT " +
            "WHERE maBan = ? AND trangThai = N'Đang sử dụng'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Time t = rs.getTime("gioCheckIn");
                return t != null ? t.toLocalTime() : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public BanDat getBanDatDangSuDung(String maBan) {
        Connection con = ConnectDB.getConnection();
        BanDat banDat = null;

        String sql = "SELECT * FROM BANDAT WHERE maBan = ? AND trangThai = N'Đang sử dụng'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    banDat = createBanDatFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banDat;
    }
}
