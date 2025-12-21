package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.QuanLy;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

	public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        // Sử dụng LEFT JOIN để lấy được tài khoản ngay cả khi maNV là NULL
        String sql = "SELECT tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai, "
                   + "       nv.maNV, nv.hoTen AS tenNV, "
                   + "       ql.maQL, ql.hoTen AS tenQL "  // Giả sử bảng QuanLy có cột hoTen
                   + "FROM TaiKhoan tk "
                   + "LEFT JOIN NhanVien nv ON tk.maNV = nv.maNV "
                   + "LEFT JOIN QuanLy ql ON tk.maQL = ql.maQL " // Liên kết thêm bảng QuanLy (dựa vào cột maQL trong ảnh bạn gửi)
                   + "WHERE tk.tenDangNhap = ? AND tk.matKhau = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 1. Kiểm tra trạng thái khóa
                    if ("Khóa".equalsIgnoreCase(rs.getString("trangThai")) || 
                        "Ngưng hoạt động".equalsIgnoreCase(rs.getString("trangThai"))) {
                        return null; 
                    }

                    // 2. Lấy thông tin chung
                    String userName = rs.getString("tenDangNhap");
                    String pass = rs.getString("matKhau");
                    String role = rs.getString("vaiTro");
                    String status = rs.getString("trangThai");

                    NhanVien nhanVien = null;
                    QuanLy quanLy = null;

                    // 3. Xử lý phân loại đối tượng dựa trên dữ liệu trả về
                    if (rs.getString("maNV") != null) {
                        // Đây là Nhân Viên
                        nhanVien = new NhanVien();
                        nhanVien.setMaNV(rs.getString("maNV"));
                        nhanVien.setHoTen(rs.getString("tenNV"));
                    } 
                    
                    if (rs.getString("maQL") != null) {
                        // Đây là Quản Lý
                        quanLy = new QuanLy();
                        quanLy.setMaQL(rs.getString("maQL"));
                        // Nếu bảng QuanLy có cột họ tên thì set vào, nếu không thì để trống hoặc xử lý tùy entity của bạn
                        try {
                            quanLy.setHoTen(rs.getString("tenQL"));
                        } catch (Exception e) {
                            // Bỏ qua nếu Entity QuanLy không có setter HoTen hoặc SQL ko có cột này
                        }
                    }

                    // 4. Tạo đối tượng TaiKhoan
                    // Lưu ý: Constructor này phải khớp với Entity TaiKhoan của bạn
                    TaiKhoan tk = new TaiKhoan(userName, pass, role, nhanVien, quanLy, status);
                    return tk;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


 // Kiểm tra thông tin tài khoản + email + CCCD
    public boolean kiemTraThongTin(String tenDangNhap, String email, String cccd) {
        try {Connection con = ConnectDB.getConnection();
            String sql = "SELECT t.tenDangNhap " +
                         "FROM TAIKHOAN t " +
                         "JOIN NHANVIEN n ON t.maNV = n.maNV " +
                         "WHERE t.tenDangNhap = ? AND n.email = ? AND n.CCCD = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tenDangNhap);
            ps.setString(2, email);
            ps.setString(3, cccd);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật mật khẩu
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        try {Connection con = ConnectDB.getConnection();
            String sql = "UPDATE TAIKHOAN SET matKhau = ? WHERE tenDangNhap = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, matKhauMoi);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
