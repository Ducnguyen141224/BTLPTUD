package dao;

import java.sql.Connection;
import java.sql.Date; // Dùng java.sql.Date cho PreparedStatement
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.QuanLy; 

public class NhanVien_DAO {

    
    private NhanVien taoNhanVienTuResultSet(ResultSet rs) throws SQLException {
        String maNV = rs.getString("maNV");
        String hoTen = rs.getString("hoTen");
        String cccd = rs.getString("CCCD");
        String sdt = rs.getString("soDienThoai");
        String email = rs.getString("email");
        java.util.Date ngaySinh = null;
        Date sqlNgaySinh = rs.getDate("ngaySinh");
        if (sqlNgaySinh != null) {
            ngaySinh = new java.util.Date(sqlNgaySinh.getTime()); 
        }

        Boolean gioiTinh = rs.getBoolean("gioiTinh");
        if (rs.wasNull()) {
             gioiTinh = null; 
        }

        String trangThai = rs.getString("trangThai");

        // Xử lý QuanLy (khóa ngoại) bằng cách đọc maQL
        String maQL = rs.getString("maQL");
        QuanLy quanLy = null;
        if (maQL != null && !maQL.trim().isEmpty()) {
            // Chỉ cần mã QL để tạo đối tượng QuanLy đơn giản
            quanLy = new QuanLy(maQL); // Giả định có constructor QuanLy(String maQL)
        }

        return new NhanVien(maNV, hoTen, cccd, sdt, email, gioiTinh, ngaySinh, trangThai, quanLy);
    }

    
    public List<NhanVien> layTatCaNhanVien() {
        List<NhanVien> dsNV = new ArrayList<>();
        // Bỏ cột chucVu khỏi SELECT
        String sql = "SELECT maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL FROM NHANVIEN";

        try (Connection con = ConnectDB.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NhanVien nv = taoNhanVienTuResultSet(rs);
                dsNV.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNV;
    }

  
    public boolean themNhanVien(NhanVien nv) throws SQLException {
        // Bỏ cột chucVu khỏi INSERT
        String sql = "INSERT INTO NHANVIEN (maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; // Chỉ còn 9 tham số

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getHoTen());
            stmt.setString(3, nv.getCCCD());
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setString(5, nv.getEmail());

            // Chuyển java.util.Date sang java.sql.Date
            if (nv.getNgaySinh() != null) {
                stmt.setDate(6, new java.sql.Date(nv.getNgaySinh().getTime()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }

            // Xử lý Boolean (nullable nếu cần)
            if (nv.getGioiTinh() != null) {
                 stmt.setBoolean(7, nv.getGioiTinh());
            } else {
                 stmt.setNull(7, java.sql.Types.BIT); // Hoặc ném lỗi nếu giới tính là bắt buộc
            }

            stmt.setString(8, nv.getTrangThai()); // Tham số thứ 8

            // Xử lý maQL (tham số thứ 9)
            if (nv.getQuanLy() != null && nv.getQuanLy().getMaQL() != null) {
                stmt.setString(9, nv.getQuanLy().getMaQL());
            } else {
                stmt.setNull(9, java.sql.Types.NVARCHAR);
            }

            int n = stmt.executeUpdate();
            return n > 0;
        }
        // Ném SQLException lên nếu có lỗi (do khai báo throws SQLException)
    }

  
    public boolean capNhatNhanVien(NhanVien nv) throws SQLException {
        // Bỏ cột chucVu khỏi UPDATE
        String sql = "UPDATE NHANVIEN SET hoTen = ?, CCCD = ?, soDienThoai = ?, " +
                       "email = ?, ngaySinh = ?, gioiTinh = ?, trangThai = ?, maQL = ? " + // Chỉ còn 8 cột SET
                       "WHERE maNV = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nv.getHoTen());
            stmt.setString(2, nv.getCCCD());
            stmt.setString(3, nv.getSoDienThoai());
            stmt.setString(4, nv.getEmail());

            // Chuyển java.util.Date sang java.sql.Date
            if (nv.getNgaySinh() != null) {
                stmt.setDate(5, new java.sql.Date(nv.getNgaySinh().getTime()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            // Xử lý Boolean
             if (nv.getGioiTinh() != null) {
                 stmt.setBoolean(6, nv.getGioiTinh());
            } else {
                 stmt.setNull(6, java.sql.Types.BIT);
            }

            stmt.setString(7, nv.getTrangThai()); // Tham số thứ 7

            // Xử lý maQL (tham số thứ 8)
            if (nv.getQuanLy() != null && nv.getQuanLy().getMaQL() != null) {
                stmt.setString(8, nv.getQuanLy().getMaQL());
            } else {
                stmt.setNull(8, java.sql.Types.NVARCHAR);
            }

            stmt.setString(9, nv.getMaNV()); // WHERE clause (tham số thứ 9)

            int n = stmt.executeUpdate();
            return n > 0;
        }
         
    }

   
    public List<NhanVien> timNhanVien(String keyword) {
        List<NhanVien> dsNV = new ArrayList<>();
        // Câu truy vấn tìm chính xác theo maNV hoặc hoTen
        String sql = "SELECT maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL " +
                     "FROM NHANVIEN WHERE maNV = ? OR hoTen = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Truyền thẳng keyword cho cả 2 tham số
            stmt.setString(1, keyword);
            stmt.setString(2, keyword);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = taoNhanVienTuResultSet(rs);
                    dsNV.add(nv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra console nếu có lỗi tìm kiếm
        }
        return dsNV;
    }

   
    public NhanVien timNhanVienTheoMa(String maNV) {
         // Câu truy vấn tìm chính xác theo maNV
        String sql = "SELECT maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL " +
                     "FROM NHANVIEN WHERE maNV = ?";
        NhanVien nv = null; // Khởi tạo kết quả null

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maNV); // Gán mã NV cần tìm

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // Nếu tìm thấy
                    nv = taoNhanVienTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nv; 
    }
 // Thêm vào class NhanVien_DAO
    public String phatSinhMaNV() {
        String maNV = "NV001"; // Mặc định nếu chưa có
        try {
            java.sql.Connection con = connectDB.ConnectDB.getInstance().getConnection();
            String sql = "SELECT MAX(maNV) FROM NhanVien";
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet rs = stm.executeQuery(sql);

            if (rs.next()) {
                String maxMa = rs.getString(1);
                if (maxMa != null) {
                    // Tách phần số (NV005 -> 005)
                    int soHienTai = Integer.parseInt(maxMa.substring(2));
                    // Tăng lên 1 và format lại (006 -> NV006)
                    maNV = String.format("NV%03d", soHienTai + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maNV;
    }
}