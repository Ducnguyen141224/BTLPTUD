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
        String trangThai = rs.getString("trangThaiThanhToan");

        Timestamp ngayLapTs = rs.getTimestamp("ngayLap");
        LocalDateTime ngayLap = ngayLapTs != null ? ngayLapTs.toLocalDateTime() : LocalDateTime.now();

        HoaDon hd = new HoaDon(maHD);
        hd.setNgayLap(ngayLap);
        hd.setTrangThaiThanhToan(trangThai);

        // ============= SỬA CHỖ #1: Load đầy đủ Thẻ thành viên =============
        if (maThe != null) {
            TheThanhVien_DAO ttvDAO = new TheThanhVien_DAO();
            hd.setTheThanhVien(ttvDAO.layTheTheoMaThe(maThe));
        } else {
            hd.setTheThanhVien(null);
        }

        hd.setNhanVien(new NhanVien(maNV));
        hd.setBan(new Ban(maBan));
        hd.setBanDat(maDatBan != null ? new BanDat(maDatBan) : null);
        hd.setKhuyenMai(maKM != null ? new KhuyenMai(maKM) : null);

        return hd;
    }

    // =========================================================
    // THÊM HÓA ĐƠN + CHI TIẾT
    // =========================================================
    public boolean themHoaDon(HoaDon hd) {
        Connection con = null;
        boolean success = false;

        String sqlHD = "INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            double tongSauGiam = 0;
            double tongGoc = 0;
            double tongGiam = 0;

            try (PreparedStatement psHD = con.prepareStatement(sqlHD)) {

                // ========== GHI THÔNG TIN CƠ BẢN ==========
                psHD.setString(1, hd.getMaHoaDon());
                psHD.setString(2, hd.getTheThanhVien() != null ? hd.getTheThanhVien().getMaThe() : null);
                psHD.setString(3, hd.getNhanVien().getMaNV());
                psHD.setString(4, hd.getBan().getMaBan());
                psHD.setString(5, hd.getBanDat() != null ? hd.getBanDat().getMaDatBan() : null);
                psHD.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
                psHD.setTimestamp(7, Timestamp.valueOf(hd.getNgayLap()));

                // ========== TÍNH TIỀN ==========
                tongGoc = hd.tinhTongTien();       
                tongGiam = hd.tinhTongGiamGia();   

                tongSauGiam = tongGoc - tongGiam;
                if (tongSauGiam < 0) tongSauGiam = 0;

                psHD.setDouble(8, tongSauGiam);
                psHD.setString(9, "Chờ thanh toán");

                if (psHD.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // ========== THÊM CHI TIẾT HÓA ĐƠN ==========
            if (hd.getDanhSachChiTietHoaDon() != null) {
                for (CT_HoaDon ct : hd.getDanhSachChiTietHoaDon()) {
                    if (!ctHoaDonDAO.themCTHoaDon(ct, con)) {
                        con.rollback();
                        return false;
                    }
                }
            }

            // ========== CỘNG ĐIỂM TÍCH LŨY ==========
            if (hd.getTheThanhVien() != null) {
                TheThanhVien_DAO ttvDAO = new TheThanhVien_DAO();
                TheThanhVien the = ttvDAO.layTheTheoMaThe(hd.getTheThanhVien().getMaThe());

                if (the != null) {
                    int diemCong = (int) (tongSauGiam / 100000);
                    if (diemCong > 0) {
                        ttvDAO.congDiem(the.getMaThe(), diemCong);
                        ttvDAO.capNhatLoaiHangTheoDiem(the.getMaThe());
                    }
                }
            }

            con.commit();
            success = true;

        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) try { con.rollback(); } catch (SQLException ignored) {}
        } finally {
            if (con != null) try { con.close(); } catch (SQLException ignored) {}
        }

        return success;
    }

    // =========================================================
    // LẤY HÓA ĐƠN CHƯA THANH TOÁN THEO BÀN
    // =========================================================
    public HoaDon layHoaDonChuaThanhToanTheoBan(String maBan) {
        HoaDon hd = null;

        String sql = "SELECT TOP 1 * FROM HOADON WHERE maBan = ? AND trangThaiThanhToan = N'Chờ thanh toán' ORDER BY ngayLap DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maBan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hd = createHoaDonFromResultSet(rs);

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
    // LẤY MÃ HÓA ĐƠN TIẾP THEO
    // =========================================================
    public String layMaHDTiepTheo() {
        String sql = "SELECT MAX(CAST(SUBSTRING(maHD, 3, LEN(maHD)) AS INT)) AS MaxNum FROM HOADON";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int max = rs.getInt("MaxNum");
                return String.format("HD%03d", max + 1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "HD001";
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
                ds.add(hd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // =========================================================
    // LẤY TỔNG TIỀN THEO MÃ HÓA ĐƠN
    // =========================================================
    public double layTongTienTheoMaHD(String maHD) {
        double tongTien = -1.0;
        String sql = "SELECT tongTien FROM HOADON WHERE maHD = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tongTien = rs.getDouble("tongTien");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tongTien;
    }

    // =========================================================
    // HÀM TÍNH GIẢM GIÁ THEO LOẠI THẺ
    // =========================================================
    private double tinhGiamGiaTheoLoaiHang(String loaiHang) {
        if (loaiHang == null) return 0.10;
        if (loaiHang.equals("Kim cương")) return 0.15;
        if (loaiHang.equals("Vàng")) return 0.12;
        return 0.10;
    }
}
