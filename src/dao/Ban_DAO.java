package dao;

import connectDB.ConnectDB;
import entity.Ban;
import java.sql.*;
import java.util.ArrayList;

public class Ban_DAO {

    // Phương thức chuyển đổi ResultSet thành đối tượng Ban
    private Ban createBanFromResultSet(ResultSet rs) throws SQLException {
        String maBan = rs.getString("maBan");
        String loaiBan = rs.getString("loaiBan");
        int soGhe = rs.getInt("soGhe");
        String khuVuc = rs.getString("khuVuc");
        String trangThai = rs.getString("trangThai");
        return new Ban(maBan, loaiBan, soGhe, khuVuc, trangThai);
    }
    
    
    public ArrayList<Ban> getAllBan() {
        ArrayList<Ban> dsBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection(); // Lấy kết nối Singleton
        String sql = "SELECT maBan, loaiBan, soGhe, khuVuc, trangThai FROM BAN";
        
        try (PreparedStatement ps = con.prepareStatement(sql); // Statement và ResultSet tự đóng
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                dsBan.add(createBanFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsBan;
    }
    
    
    public Ban getBanById(String maBan) {
        Ban ban = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT maBan, loaiBan, soGhe, khuVuc, trangThai FROM BAN WHERE maBan = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ban = createBanFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm bàn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return ban;
    }

   
    public boolean updateTrangThaiBan(String maBan, String trangThaiMoi) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE BAN SET trangThai = ? WHERE maBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maBan);
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }
    
   
    public boolean updateMaDatBanHienTai(String maBan, String maDatBan) {
        String sql = "UPDATE BAN SET maDatBanHienTai = ? WHERE maBan = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDatBan);
            ps.setString(2, maBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    
    public boolean addBan(Ban ban) {
        Connection con = ConnectDB.getConnection();
        String sql = "INSERT INTO BAN (maBan, loaiBan, soGhe, khuVuc, trangThai) VALUES (?, ?, ?, ?, ?)";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ban.getMaBan());
            ps.setString(2, ban.getLoaiBan());
            ps.setInt(3, ban.getSoGhe());
            ps.setString(4, ban.getKhuVuc());
            ps.setString(5, ban.getTrangThai());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

   
    public boolean updateBan(Ban ban) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE BAN SET loaiBan = ?, soGhe = ?, khuVuc = ?, trangThai = ? WHERE maBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ban.getLoaiBan());
            ps.setInt(2, ban.getSoGhe());
            ps.setString(3, ban.getKhuVuc());
            ps.setString(4, ban.getTrangThai());
            ps.setString(5, ban.getMaBan());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }

   
    public boolean deleteBan(String maBan) {
        Connection con = ConnectDB.getConnection();
        String sql = "DELETE FROM BAN WHERE maBan = ?";
        int n = 0;
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            n = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return n > 0;
    }
    
  
    public ArrayList<Ban> getFilteredBan(String filterType, String filterValue) {
        ArrayList<Ban> dsBan = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        
        String sql = "SELECT maBan, loaiBan, soGhe, khuVuc, trangThai FROM BAN WHERE " + filterType + " = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Đặt giá trị cho tham số ?
            ps.setString(1, filterValue);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsBan.add(createBanFromResultSet(rs)); 
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsBan;
    }
    public String getTrangThaiBan(String maBan) {
        String trangThai = null;
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT trangThai FROM BAN WHERE maBan = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    trangThai = rs.getString("trangThai");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy trạng thái bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return trangThai;
    }
    public boolean capNhatTrangThaiBan(String maBan, String trangThai) {
        Connection con = ConnectDB.getConnection();
        String sql = "UPDATE BAN SET trangThai = ? WHERE maBan = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maBan);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái bàn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
   

}