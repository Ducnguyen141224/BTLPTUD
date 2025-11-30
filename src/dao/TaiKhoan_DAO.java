package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

	public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
		String sql = 
		        "SELECT tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai, "
		      + "       nv.maNV, nv.hoTen "
		      + "FROM TaiKhoan tk "
		      + "JOIN NhanVien nv ON tk.maNV = nv.maNV "
		      + "WHERE tk.tenDangNhap = ? AND tk.matKhau = ?";

	    try (Connection con = ConnectDB.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, tenDangNhap);
	        ps.setString(2, matKhau);

	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {

	            // Nếu tài khoản bị khóa → return null để báo lỗi đăng nhập
	            if ("Khóa".equalsIgnoreCase(rs.getString("trangThai"))) {
	                return null;
	            }

	            // Tạo nhân viên (CHỈ QUAN TÂM MA_NV + HO_TEN lúc đăng nhập)
	            NhanVien nv = new NhanVien();
	            nv.setMaNV(rs.getString("maNV"));
	            nv.setHoTen(rs.getString("hoTen"));

	            // Tạo tài khoản
	            TaiKhoan tk = new TaiKhoan(
	                    rs.getString("tenDangNhap"),
	                    rs.getString("matKhau"),
	                    rs.getString("vaiTro"),
	                    nv,           // Gán nhân viên
	                    null,         // QuanLy = null (không cần lúc đăng nhập)
	                    rs.getString("trangThai")
	            );

	            return tk;  // 🔥 TRẢ VỀ TÀI KHOẢN ĐẦY ĐỦ
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null; // Đăng nhập sai
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
