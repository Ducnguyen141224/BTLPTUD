package dao;

import connectDB.ConnectDB;
import entity.KhachHang;


import java.sql.*;
import java.util.ArrayList;

public class KhachHang_DAO {

    
    private KhachHang taoKhachHangTuResultSet(ResultSet rs) throws SQLException {
        String maKH = rs.getString("maKH");
        String hoTenKH = rs.getString("hoTenKH");
        String soDienThoai = rs.getString("soDienThoai");
        String email = rs.getString("email");
        boolean gioiTinh = rs.getBoolean("gioiTinh"); 
        
     
        return new KhachHang(maKH, hoTenKH, soDienThoai, email, gioiTinh); 
    }

    //------------------------------------------------------------------------------------------------------------------

 
    public ArrayList<KhachHang> layTatCaKhachHang() {
        ArrayList<KhachHang> dsKH = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String cauSQL = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG";
        
        try (PreparedStatement stmt = con.prepareStatement(cauSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                dsKH.add(taoKhachHangTuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả Khách hàng: " + e.getMessage());
            e.printStackTrace(); // In lỗi ra console để debug
        }
        // Connection Singleton không cần đóng ở đây
        return dsKH;
    }
    
    
    public KhachHang timKhachHangTheoMa(String maKH) {
        KhachHang kh = null;
        Connection con = ConnectDB.getConnection();
        String cauSQL = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG WHERE maKH = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    kh = taoKhachHangTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Khách hàng theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return kh;
    }
    /**
     * Tìm khách hàng theo Mã, Tên hoặc SĐT (Tìm gần đúng).
     * @param tuKhoa Từ khóa tìm kiếm.
     * @return Danh sách các dòng dữ liệu (Object[]) để hiển thị lên bảng.
     */
    public ArrayList<Object[]> timKhachHangTongHop(String tuKhoa) {
        ArrayList<Object[]> dsKetQua = new ArrayList<>();
        try {
            java.sql.Connection con = ConnectDB.getConnection();
            // Câu truy vấn tìm kiếm trên 3 cột: maKH, hoTenKH, soDienThoai
            String sql = "SELECT k.maKH, k.hoTenKH, t.maThe, k.soDienThoai, k.gioiTinh, k.email " +
                         "FROM KhachHang k LEFT JOIN TheThanhVien t ON k.maKH = t.maKH " +
                         "WHERE k.maKH LIKE ? OR k.hoTenKH LIKE ? OR k.soDienThoai LIKE ?";
            
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            String searchPattern = "%" + tuKhoa + "%"; // Thêm % để tìm gần đúng
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);
            
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String maKH = rs.getString("maKH");
                String tenKH = rs.getString("hoTenKH");
                String maThe = rs.getString("maThe") != null ? rs.getString("maThe") : "Chưa có";
                String sdt = rs.getString("soDienThoai");
                boolean gt = rs.getBoolean("gioiTinh");
                String email = rs.getString("email");
                
                dsKetQua.add(new Object[] { maKH, tenKH, maThe, sdt, gt ? "Nam" : "Nữ", email });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKetQua;
    }
   
    public KhachHang timKhachHangTheoSDT(String soDienThoai) {
        KhachHang kh = null;
        Connection con = ConnectDB.getConnection();
        String cauSQL = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG WHERE soDienThoai = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            stmt.setString(1, soDienThoai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    kh = taoKhachHangTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Khách hàng theo SĐT: " + e.getMessage());
            e.printStackTrace();
        }
        return kh;
    }
    
   

  
    public boolean themKhachHang(KhachHang khachHang) throws SQLException { 
        Connection con = ConnectDB.getConnection();
        String cauSQL = "INSERT INTO KHACHHANG (maKH, hoTenKH, soDienThoai, email, gioiTinh) VALUES (?, ?, ?, ?, ?)";
        int soLuongAnhHuong = 0;

        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            stmt.setString(1, khachHang.getMaKH());
            stmt.setString(2, khachHang.getHoTenKH());
            stmt.setString(3, khachHang.getSoDienThoai());
            stmt.setString(4, khachHang.getEmail());
     
            stmt.setBoolean(5, khachHang.isGioiTinh()); 

            soLuongAnhHuong = stmt.executeUpdate();
        } catch (SQLException e) {
            // Không in lỗi ở đây, ném lên để GUI xử lý
            throw e; 
        }
        return soLuongAnhHuong > 0;
    }

    
    public boolean capNhatKhachHang(KhachHang khachHang) {
        Connection con = ConnectDB.getConnection();
 
        String cauSQL = "UPDATE KHACHHANG SET hoTenKH = ?, soDienThoai = ?, email = ?, gioiTinh = ? WHERE maKH = ?";
        int soLuongAnhHuong = 0;
        
        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            stmt.setString(1, khachHang.getHoTenKH());
            stmt.setString(2, khachHang.getSoDienThoai());
            stmt.setString(3, khachHang.getEmail());
            stmt.setBoolean(4, khachHang.isGioiTinh()); 
            stmt.setString(5, khachHang.getMaKH());    
            
            soLuongAnhHuong = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return soLuongAnhHuong > 0;
    }

  
    public boolean xoaKhachHang(String maKH) {
        Connection con = ConnectDB.getConnection();
        String cauSQL = "DELETE FROM KHACHHANG WHERE maKH = ?";
        int soLuongAnhHuong = 0;
        
        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            stmt.setString(1, maKH);
            soLuongAnhHuong = stmt.executeUpdate();
        } catch (SQLException e) {
            
            System.err.println("Lỗi khi xóa Khách hàng '" + maKH + "': " + e.getMessage());
            e.printStackTrace();
         
        }
        return soLuongAnhHuong > 0;
    }

    

  
    public ArrayList<KhachHang> timKiemKhachHang(String tuKhoa) {
        ArrayList<KhachHang> dsKH = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String cauSQL = "SELECT maKH, hoTenKH, soDienThoai, email, gioiTinh FROM KHACHHANG WHERE maKH LIKE ? OR hoTenKH LIKE ? OR soDienThoai LIKE ?";
        
        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            String tuKhoaLike = "%" + tuKhoa + "%";
            stmt.setString(1, tuKhoaLike);
            stmt.setString(2, tuKhoaLike);
            stmt.setString(3, tuKhoaLike);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dsKH.add(taoKhachHangTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return dsKH;
    }

   
    public String phatSinhMaKH() {
        String maMoi = "KH001"; // Mã mặc định nếu chưa có KH nào

        String cauSQL = "SELECT TOP 1 maKH FROM KHACHHANG WHERE maKH LIKE 'KH[0-9][0-9][0-9]' ORDER BY maKH DESC";
        
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(cauSQL)) {
            
            if (rs.next()) {
                String maCuoi = rs.getString("maKH"); // Ví dụ: "KH001"
                try {
                    String phanSo = maCuoi.substring(2); // Lấy "001"
                    int so = Integer.parseInt(phanSo);    // Chuyển thành số 1
                    so++;                               // Tăng lên 2
                    maMoi = String.format("KH%03d", so); // Định dạng lại "KH002"
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    System.err.println("Lỗi định dạng mã khách hàng cuối cùng: " + maCuoi);
                    // Nếu mã cuối bị lỗi, vẫn trả về mã mặc định "KH001"
                    
                    maMoi = "KH001";
                }
            }
            // Nếu không có bản ghi nào khớp 'KH[0-9][0-9][0-9]', maMoi vẫn là "KH001"
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi phát sinh mã khách hàng: " + e.getMessage());
            e.printStackTrace();
          
            maMoi = "KH001";
        }
        return maMoi;
    }

    
    public KhachHang themHoacLayKhachHang(KhachHang khachHangTam) throws Exception {   
        KhachHang khTrongCSDL = timKhachHangTheoSDT(khachHangTam.getSoDienThoai());
        if (khTrongCSDL == null) {
            String maKHmoi = phatSinhMaKH();
            khachHangTam.setMaKH(maKHmoi);
            if (khachHangTam.getEmail() == null || khachHangTam.getEmail().isEmpty()) {
                 khachHangTam.setEmail(null); 
      
            }  
            if (themKhachHang(khachHangTam)) {
                return khachHangTam; 
            } else {        
                throw new Exception("Thêm Khách hàng mới vào CSDL thất bại (DAO trả về false).");
            }
        } else {
            if (!khTrongCSDL.getHoTenKH().equals(khachHangTam.getHoTenKH())) {
                khTrongCSDL.setHoTenKH(khachHangTam.getHoTenKH());
                capNhatKhachHang(khTrongCSDL); 
            }
            return khTrongCSDL;
        }
    }
    
 
    public Object[] layDongKhachHangTheoMa(String maKH) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String cauSQL = "SELECT kh.maKH, kh.hoTenKH, ISNULL(ttv.loaiHang, N'Chưa có') AS loaiHang, " +
                     "kh.soDienThoai, kh.gioiTinh, kh.email " +
                     "FROM KHACHHANG kh " +
                     "LEFT JOIN THETHANHVIEN ttv ON kh.maKH = ttv.maKH " + 
                     "WHERE kh.maKH = ?";

        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            stmt.setString(1, maKH);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Object[] dong = new Object[6]; 
                    dong[0] = rs.getString("maKH");
                    dong[1] = rs.getString("hoTenKH");
                    dong[2] = rs.getString("loaiHang"); 
                    dong[3] = rs.getString("soDienThoai");
                    dong[4] = rs.getBoolean("gioiTinh") ? "Nam" : "Nữ"; 
                    dong[5] = rs.getString("email");
                    return dong;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dòng khách hàng theo mã '" + maKH + "': " + e.getMessage());
            throw e; 
        }
        return null; 
    }
    
    
    public KhachHang layKhachHangTheoMa(String maKH) throws SQLException {
        KhachHang kh = null;
        Connection con = ConnectDB.getConnection();
        String cauSQL = "SELECT * FROM KHACHHANG WHERE maKH = ?"; // Lấy tất cả các cột
        try (PreparedStatement stmt = con.prepareStatement(cauSQL)) {
            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    kh = taoKhachHangTuResultSet(rs); 
                }
            }
        } catch (SQLException e) {
             System.err.println("Lỗi khi lấy khách hàng theo mã '" + maKH + "': " + e.getMessage());
            throw e;
        }
        return kh;
    }

    public ArrayList<Object[]> layTatCaKhachHangChoBang() throws SQLException {
        ArrayList<Object[]> danhSach = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String cauSQL = "SELECT kh.maKH, kh.hoTenKH, ISNULL(ttv.loaiHang, N'Chưa có') AS loaiHang, " +
                     "kh.soDienThoai, kh.gioiTinh, kh.email " +
                     "FROM KHACHHANG kh " +
                     "LEFT JOIN THETHANHVIEN ttv ON kh.maKH = ttv.maKH"; // LEFT JOIN

        try (PreparedStatement stmt = con.prepareStatement(cauSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] dong = new Object[6];
                dong[0] = rs.getString("maKH");
                dong[1] = rs.getString("hoTenKH");
                dong[2] = rs.getString("loaiHang");
                dong[3] = rs.getString("soDienThoai");
                dong[4] = rs.getBoolean("gioiTinh") ? "Nam" : "Nữ"; // Giả định true = Nam
                dong[5] = rs.getString("email");
                danhSach.add(dong);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả khách hàng cho bảng: " + e.getMessage());
            throw e;
        }
        return danhSach;
    }
    

}