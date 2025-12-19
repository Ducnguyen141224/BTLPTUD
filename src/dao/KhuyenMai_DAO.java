package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhuyenMai;

public class KhuyenMai_DAO {

    
    private KhuyenMai createKhuyenMaiFromResultSet(ResultSet rs) throws Exception {
        String maKM = rs.getString("maKM");
        String tenKM = rs.getString("tenKM");
        String moTa = rs.getString("moTa");
        double phanTramGiam = rs.getDouble("phanTramGiam");

        LocalDate ngayBD = rs.getDate("ngayBatDau").toLocalDate();
        LocalDate ngayKT = rs.getDate("ngayKetThuc").toLocalDate();

        return new KhuyenMai(maKM, tenKM, moTa, phanTramGiam, ngayBD, ngayKT);
    }

    // 1. Lấy tất cả khuyến mãi - ĐÃ SỬA
    public ArrayList<KhuyenMai> getAllKhuyenMai() {
        ArrayList<KhuyenMai> dsKhuyenMai = new ArrayList<>();
        String sql = "SELECT * FROM KHUYENMAI";
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                KhuyenMai km = createKhuyenMaiFromResultSet(rs);
                dsKhuyenMai.add(km);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Đóng theo thứ tự ngược lại
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return dsKhuyenMai;
    }

    // 2. Thêm khuyến mãi mới
 // 2. Thêm khuyến mãi mới - ĐÃ SỬA LỖI THIẾU maQL
    public boolean themKhuyenMai(KhuyenMai km) {
        int n = 0;
        // Thêm cột maQL vào câu lệnh INSERT và thêm 1 dấu ?
        String sql = "INSERT INTO KHUYENMAI (maKM, tenKM, moTa, phanTramGiam, ngayBatDau, ngayKetThuc, maQL) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setString(3, km.getMoTa());
            ps.setDouble(4, km.getPhanTramGiam());
            ps.setDate(5, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(6, Date.valueOf(km.getNgayKetThuc()));
            
   
            String maQL = "QL001";
            ps.setString(7, maQL); 

            n = ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return n > 0;
    }

    // 3. Cập nhật (Sửa) khuyến mãi
    public boolean suaKhuyenMai(KhuyenMai km) {
        int n = 0;
        String sql = "UPDATE KHUYENMAI SET tenKM = ?, moTa = ?, phanTramGiam = ?, ngayBatDau = ?, ngayKetThuc = ? WHERE maKM = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getTenKM());
            ps.setString(2, km.getMoTa()); 
            ps.setDouble(3, km.getPhanTramGiam());
            ps.setDate(4, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(5, Date.valueOf(km.getNgayKetThuc()));
            ps.setString(6, km.getMaKM()); 

            n = ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return n > 0;
    }

    // 4. Xóa khuyến mãi theo mã
    public boolean xoaKhuyenMai(String maKhuyenMai) {
        int n = 0;
        String sql = "DELETE FROM KHUYENMAI WHERE maKM = ?";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKhuyenMai);
            n = ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return n > 0;
    }

    // 5. Tìm khuyến mãi theo mã - ĐÃ SỬA
    public KhuyenMai timKhuyenMaiTheoMa(String id) {
        KhuyenMai km = null;
        String sql = "SELECT * FROM KHUYENMAI WHERE maKM = ?";
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                km = createKhuyenMaiFromResultSet(rs);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return km;
    }

    // 6. Tìm danh sách khuyến mãi theo tên gần đúng - ĐÃ SỬA
    public ArrayList<KhuyenMai> timKhuyenMaiTheoTen(String ten) {
        ArrayList<KhuyenMai> dsKhuyenMai = new ArrayList<>();
        String sql = "SELECT * FROM KHUYENMAI WHERE tenKM LIKE ?";
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + ten + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                KhuyenMai km = createKhuyenMaiFromResultSet(rs);
                dsKhuyenMai.add(km);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return dsKhuyenMai;
    }
 // Lấy danh sách khuyến mãi đang có hiệu lực
 // Trong file dao/KhuyenMai_DAO.java

    public ArrayList<KhuyenMai> getKhuyenMaiDangDienRa() {
        ArrayList<KhuyenMai> dsKM = new ArrayList<>();
        
        // Câu lệnh SQL lấy các khuyến mãi đang diễn ra (Ngày bắt đầu <= Hiện tại <= Ngày kết thúc)
        String sql = "SELECT * FROM KHUYENMAI WHERE ngayBatDau <= GETDATE() AND ngayKetThuc >= GETDATE()";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maKM = rs.getString("maKM");
                String tenKM = rs.getString("tenKM");
                String moTa = rs.getString("moTa");
                
                // --- LỖI Ở ĐÂY: Bạn đang để là "GiaTriGiam" ---
                // --- SỬA THÀNH: "phanTramGiam" ---
                double phanTram = rs.getDouble("phanTramGiam"); 

                LocalDate ngayBD = rs.getDate("ngayBatDau").toLocalDate();
                LocalDate ngayKT = rs.getDate("ngayKetThuc").toLocalDate();
                
                // Nếu constructor của bạn có maQL/NhanVien thì thêm vào, nếu không thì null hoặc bỏ qua
                // Giả sử constructor giống các hàm trên:
                KhuyenMai km = new KhuyenMai(maKM, tenKM, moTa, phanTram, ngayBD, ngayKT);
                dsKM.add(km);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsKM;
    }
 // Thêm vào class KhuyenMai_DAO
    public String phatSinhMaKM() {
        String maKM = "KM001"; // Mặc định nếu chưa có dữ liệu
        try {
            java.sql.Connection con = ConnectDB.getInstance().getConnection();
            String sql = "SELECT MAX(maKM) FROM KhuyenMai";
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet rs = stm.executeQuery(sql);

            if (rs.next()) {
                String maxMa = rs.getString(1);
                if (maxMa != null) {
                    // Tách phần số ra (Ví dụ: KM005 -> 005)
                    int soHienTai = Integer.parseInt(maxMa.substring(2));
                    // Tăng lên 1 và format lại thành chuỗi (006 -> KM006)
                    maKM = String.format("KM%03d", soHienTai + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maKM;
    }


}