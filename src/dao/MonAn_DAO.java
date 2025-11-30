package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.MonAn;
import entity.QuanLy; // Đã thêm
import connectDB.ConnectDB;

public class MonAn_DAO {
    
    
    
   
    private MonAn createMonAnFromResultSet(ResultSet rs) throws SQLException {
        String maQL = rs.getString("maQL");
        QuanLy quanLy = null;
        
        if (maQL != null && !maQL.trim().isEmpty()) {
            quanLy = new QuanLy(maQL);
        }
        return new MonAn(
            rs.getString("maMon"),
            rs.getString("tenMon"),
            rs.getString("loaiMon"),
            rs.getDouble("giaMon"),
            rs.getString("hinhAnh"),
            quanLy 
        );
    }

    

   
    public List<MonAn> docTuBang() {
        List<MonAn> dsMon = new ArrayList<>();
        String sql = "SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                dsMon.add(createMonAnFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }

    
    // HÊM MÓN ĂN
   
    public boolean themMonAn(MonAn mon) {
        String sql = "INSERT INTO MONAN(maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL) VALUES (?, ?, ?, ?, ?, ?)";
        

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
        
            ps.setString(1, mon.getMaMonAn());
            ps.setString(2, mon.getTenMonAn());
            ps.setString(3, mon.getLoaiMonAn());
            ps.setDouble(4, mon.getGiaMonAn());
            ps.setString(5, mon.getHinhAnh());
            QuanLy ql = mon.getQuanLy();
            if (ql != null && ql.getMaQL() != null && !ql.getMaQL().trim().isEmpty())
                ps.setString(6, ql.getMaQL());
            else
                ps.setNull(6, Types.NVARCHAR); // Cho phép NULL
                
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String generateNewMaMon() {
        String newMa = "MA001"; // Mã mặc định nếu chưa có món nào
        String sql = "SELECT TOP 1 maMon FROM MONAN ORDER BY maMon DESC"; 

        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String lastMa = rs.getString("maMon");
                
               
                String numStr = lastMa.substring(2); 
                int lastNum = Integer.parseInt(numStr);         
                int newNum = lastNum + 1;
                newMa = String.format("MA%03d", newNum); 
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi phát sinh mã món ăn mới: " + e.getMessage());
            e.printStackTrace();
           
            return "ERROR";
        } catch (NumberFormatException e) {
     
             System.err.println("Lỗi định dạng mã món ăn trong CSDL: " + e.getMessage());
             return "MA001"; 
        }
        return newMa;
    }

   
    public boolean chinhSuaMonAn(MonAn mon) {
        String sql = "UPDATE MONAN SET tenMon=?, loaiMon=?, giaMon=?, hinhAnh=?, maQL=? WHERE maMon=?";
        
        // SỬA LỖI: Sử dụng try-with-resources
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, mon.getTenMonAn());
            ps.setString(2, mon.getLoaiMonAn());
            ps.setDouble(3, mon.getGiaMonAn());
            ps.setString(4, mon.getHinhAnh());
            
            // XỬ LÝ QUANLY
            QuanLy ql = mon.getQuanLy();
            if (ql != null && ql.getMaQL() != null && !ql.getMaQL().trim().isEmpty())
                ps.setString(5, ql.getMaQL());
            else
                ps.setNull(5, Types.NVARCHAR);
                
            ps.setString(6, mon.getMaMonAn());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

  
    public List<MonAn> timTheoTen(String tenMon) {
        List<MonAn> ds = new ArrayList<>();
        String sql = "SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN WHERE tenMon LIKE ?";
        
       
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + tenMon + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(createMonAnFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    public boolean xoaMonAn(String maMon) {
        // Truy vấn SQL để xóa món ăn
        String sql = "DELETE FROM MONAN WHERE maMon = ?";
        
        // Sử dụng try-with-resources để đảm bảo Connection và PreparedStatement được đóng
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maMon);
            
            // executeUpdate() trả về số lượng hàng bị ảnh hưởng
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa món ăn (Mã: " + maMon + "):");
            e.printStackTrace();
            return false;
        }
    }
    
    // 🧩 Hàm đóng kết nối (Không cần thiết nếu dùng try-with-resources, nhưng giữ lại nếu các phương thức khác vẫn dùng)
    private void close(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ====================================================================
    // LỌC THEO LOẠI MÓN
    // ====================================================================
    public List<MonAn> locTheoLoai(String loaiMon) {
        List<MonAn> dsMon = new ArrayList<>();
        String sql = "SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN WHERE loaiMon = ?";

        // SỬA LỖI: Sử dụng try-with-resources
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, loaiMon);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsMon.add(createMonAnFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }

    // ====================================================================
    // LỌC + SẮP XẾP THEO GIÁ
    // ====================================================================
    public ArrayList<MonAn> locTheoGia(String thuTu) {
        ArrayList<MonAn> dsMon = new ArrayList<>();
        String sortOrder = thuTu.equalsIgnoreCase("DESC") ? "DESC" : "ASC";
        String sql = "SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN ORDER BY giaMon " + sortOrder;

        // SỬA LỖI: Sử dụng try-with-resources
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dsMon.add(createMonAnFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }

    // ====================================================================
    // LỌC KẾT HỢP (THEO LOẠI + THEO GIÁ)
    // ====================================================================
    public ArrayList<MonAn> locMonAn(String loaiMon, String thuTu) {
        ArrayList<MonAn> dsMon = new ArrayList<>();
        String sql = "SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN";
        
        boolean hasLoaiMonFilter = (loaiMon != null && !loaiMon.equals("Tất cả"));
        
        if (hasLoaiMonFilter) {
            sql += " WHERE loaiMon = ?";
        }
        String sortOrder = thuTu.equalsIgnoreCase("DESC") ? "DESC" : "ASC";
        sql += " ORDER BY giaMon " + sortOrder;

        // SỬA LỖI: Sử dụng try-with-resources
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (hasLoaiMonFilter) {
                ps.setString(1, loaiMon);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsMon.add(createMonAnFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMon;
    }
    
    // HÀM close() ĐÃ BỊ XÓA vì try-with-resources đã đảm bảo việc đóng tài nguyên.
    public List<MonAn> layTatCaMonAn() throws SQLException {
        List<MonAn> danhSachMonAn = new ArrayList<>();
        Connection con = ConnectDB.getConnection(); 
        
        if (con == null) return danhSachMonAn; // Trả về rỗng nếu không có kết nối
        
        // Sử dụng try-with-resources để tự động đóng Statement và ResultSet
        try (Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery("SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN")) {

            while (rs.next()) {
                String maMon = rs.getString("maMon");
                String tenMon = rs.getString("tenMon");
                String loaiMon = rs.getString("loaiMon");
                double giaMon = rs.getDouble("giaMon"); 
                String hinhAnh = rs.getString("hinhAnh");
                String maQL = rs.getString("maQL");
                
                // Khởi tạo đối tượng QuanLy chỉ với mã
                QuanLy quanLy = new QuanLy(maQL); 
                
                MonAn mon = new MonAn(maMon, tenMon, loaiMon, giaMon, hinhAnh, quanLy);
                danhSachMonAn.add(mon);
            }
        } catch (Exception e) {
            System.err.println("Lỗi truy vấn dữ liệu Món ăn:");
            e.printStackTrace();
        } 
        return danhSachMonAn;
    }
    public List<String> layDanhSachLoaiMon() throws SQLException {
        List<String> dsLoai = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        
        if (con == null) return dsLoai;
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT loaiMon FROM MONAN")) {
            
            while(rs.next()) {
                dsLoai.add(rs.getString("loaiMon"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi truy vấn Loại món:");
            e.printStackTrace();
        }
        return dsLoai;
    }
    public MonAn getMonAnById(String maMon) {
        MonAn monAn = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN WHERE maMon = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    monAn = createMonAnFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm món ăn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return monAn;
    }
    public MonAn getMonAnTheoTen(String tenMon) {
        String sql = "SELECT maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL FROM MONAN WHERE tenMon = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenMon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createMonAnFromResultSet(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy món theo tên: " + e.getMessage());
        }
        return null;
    }

}