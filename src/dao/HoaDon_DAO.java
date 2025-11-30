package dao;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.CT_HoaDon;
import entity.NhanVien;
import entity.Ban;
import entity.KhuyenMai;
import entity.BanDat;
import entity.TheThanhVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    private final CTHoaDon_DAO ctHoaDonDAO = new CTHoaDon_DAO();

    // =========================================================
    // TẠO OBJECT HOÁ ĐƠN TỪ RESULTSET
    // =========================================================
    private HoaDon createHoaDonFromResultSet(ResultSet rs) throws Exception {
        String maHD = rs.getString("maHD");
        String maThe = rs.getString("maThe");
        String maNV = rs.getString("maNV");
        String maBan = rs.getString("maBan");
        String maDatBan = rs.getString("maDatBan");
        String maKM = rs.getString("maKM");
        String trangThai = rs.getString("trangThaiThanhToan"); // Lấy thêm trạng thái

        Timestamp ngayLapTs = rs.getTimestamp("ngayLap");
        LocalDateTime ngayLap = ngayLapTs != null ? ngayLapTs.toLocalDateTime() : LocalDateTime.now();

        HoaDon hd = new HoaDon(maHD);
        hd.setNgayLap(ngayLap);
        hd.setTrangThaiThanhToan(trangThai); // Cần set trạng thái vào Entity

        hd.setTheThanhVien(maThe != null ? new TheThanhVien(maThe) : null);
        hd.setNhanVien(new NhanVien(maNV));
        hd.setBan(new Ban(maBan));
        hd.setBanDat(maDatBan != null ? new BanDat(maDatBan) : null);
        hd.setKhuyenMai(maKM != null ? new KhuyenMai(maKM) : null);

        return hd;
    }

    // =========================================================
    // THÊM HÓA ĐƠN (Gồm cả Transaction thêm CT_HoaDon)
    // =========================================================
    
    public boolean themHoaDon(HoaDon hd) {
        Connection con = null;
        boolean success = false;

        // SỬA LỖI: Format lại câu query cho dễ nhìn và chuẩn xác
        String sqlHD = "INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction

            try (PreparedStatement psHD = con.prepareStatement(sqlHD)) {
                psHD.setString(1, hd.getMaHoaDon());
                // Kiểm tra null an toàn hơn
                psHD.setString(2, (hd.getTheThanhVien() != null) ? hd.getTheThanhVien().getMaThe() : null);
                psHD.setString(3, hd.getNhanVien().getMaNV());
                psHD.setString(4, hd.getBan().getMaBan());
                psHD.setString(5, (hd.getBanDat() != null) ? hd.getBanDat().getMaDatBan() : null);
                psHD.setString(6, (hd.getKhuyenMai() != null) ? hd.getKhuyenMai().getMaKM() : null);
                psHD.setTimestamp(7, Timestamp.valueOf(hd.getNgayLap()));
                psHD.setDouble(8, hd.tinhTongTien());
                psHD.setString(9, "Chờ thanh toán"); // Hardcode trạng thái ban đầu

                if (psHD.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // THÊM CT HOÁ ĐƠN
            if (hd.getDanhSachChiTietHoaDon() != null && !hd.getDanhSachChiTietHoaDon().isEmpty()) {
                for (CT_HoaDon ct : hd.getDanhSachChiTietHoaDon()) {
                    // LƯU Ý QUAN TRỌNG: Hàm themCTHoaDon phải nhận tham số Connection
                    // và KHÔNG ĐƯỢC close connection đó.
                    if (!ctHoaDonDAO.themCTHoaDon(ct, con)) {
                        con.rollback();
                        return false;
                    }
                }
            }

            con.commit(); // Xác nhận Transaction thành công
            success = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Rollback nếu có lỗi SQL
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Chỉ đóng kết nối ở đây
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    // =========================================================
    // LẤY HOÁ ĐƠN CHƯA THANH TOÁN THEO BÀN (Đã sửa logic)
    // =========================================================
    public HoaDon layHoaDonChuaThanhToanTheoBan(String maBan) { // Bỏ tham số maNV vì không dùng
        HoaDon hd = null;

        // SỬA LỖI: Thêm điều kiện trangThaiThanhToan để tránh lấy nhầm hóa đơn cũ
        String sql = "SELECT TOP 1 * FROM HOADON WHERE maBan = ? AND trangThaiThanhToan = N'Chờ thanh toán' ORDER BY ngayLap DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hd = createHoaDonFromResultSet(rs);
                    // Load chi tiết hóa đơn đi kèm
                    ArrayList<CT_HoaDon> dsCT = ctHoaDonDAO.layDSCTHoaDonTheoMaHD(hd.getMaHoaDon());
                    hd.setDanhSachChiTietHoaDon(dsCT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hd;
    }

    // =========================================================
    // LẤY MÃ HOÁ ĐƠN TIẾP THEO
    // =========================================================
    public String layMaHDTiepTheo() {
        // Query này hoạt động tốt với format HD001, HD002...
        // Tuy nhiên cần đảm bảo trong DB không có rác (ví dụ HDABC) nếu không sẽ lỗi cast int
        String sql = "SELECT MAX(CAST(SUBSTRING(maHD, 3, LEN(maHD)) AS INT)) AS MaxNum FROM HOADON";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                // Nếu DB chưa có dữ liệu, rs.getInt trả về 0, maxNum + 1 = 1 -> HD001 (Đúng)
                int maxNum = rs.getInt("MaxNum");
                return String.format("HD%03d", maxNum + 1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "HD001"; // Fallback nếu lỗi
    }

    // =========================================================
    // LẤY TẤT CẢ HÓA ĐƠN
    // =========================================================
    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM HOADON ORDER BY ngayLap DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = createHoaDonFromResultSet(rs);
                // Có thể cân nhắc KHÔNG load chi tiết ở đây nếu danh sách quá dài (Lazy loading)
                // để tăng tốc độ hiển thị danh sách ban đầu.
                // hd.setDanhSachChiTietHoaDon(ctHoaDonDAO.layDSCTHoaDonTheoMaHD(hd.getMaHoaDon()));
                ds.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}