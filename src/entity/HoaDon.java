package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    private String maHoaDon;
    private LocalDateTime ngayLap;
    private String trangThaiThanhToan; // ✅ Đã thêm thuộc tính này

    // Quan hệ
    private List<CT_HoaDon> danhSachChiTietHoaDon = new ArrayList<>(); 
    private TheThanhVien theThanhVien; 
    private NhanVien nhanVien;       
    private Ban ban;               
    private BanDat banDat;         
    private KhuyenMai khuyenMai;      

    // =========================================================
    // CONSTRUCTORS
    // =========================================================
    
    // Constructor rỗng
    public HoaDon() {
        this.ngayLap = LocalDateTime.now();
        this.trangThaiThanhToan = "Chờ thanh toán";
    }

    // Constructor chỉ có mã (dùng khi tạo mới nhanh)
    public HoaDon(String maHoaDon) {
        setMaHoaDon(maHoaDon);
        this.ngayLap = LocalDateTime.now();
        this.trangThaiThanhToan = "Chờ thanh toán";
    }

    // Constructor đầy đủ
    public HoaDon(String maHoaDon, TheThanhVien theThanhVien, NhanVien nhanVien, 
                  Ban ban, BanDat banDat, KhuyenMai khuyenMai, 
                  LocalDateTime ngayLap, List<CT_HoaDon> danhSachChiTietHoaDon, 
                  String trangThaiThanhToan) {
        setMaHoaDon(maHoaDon);
        setTheThanhVien(theThanhVien);
        setNhanVien(nhanVien);
        setBan(ban);
        setBanDat(banDat);
        setKhuyenMai(khuyenMai);
        setNgayLap(ngayLap);
        setDanhSachChiTietHoaDon(danhSachChiTietHoaDon);
        setTrangThaiThanhToan(trangThaiThanhToan);
    }

    // =========================================================
    // BUSINESS METHODS
    // =========================================================
    
    public double tinhTongTien() {
        if (danhSachChiTietHoaDon == null || danhSachChiTietHoaDon.isEmpty()) {
            return 0.0;
        }

        // 1. Tính tổng tiền món ăn
        double tongChuaGiam = danhSachChiTietHoaDon.stream()
            .mapToDouble(CT_HoaDon::tinhThanhTien)
            .sum();

        // 2. Tính phần trăm giảm giá (xử lý null an toàn)
        double phanTramGiam = 0.0;
        if (khuyenMai != null && khuyenMai.getPhanTramGiam() != null) {
            phanTramGiam = khuyenMai.getPhanTramGiam();
        }
        
        // 3. Trả về kết quả cuối cùng
        return tongChuaGiam * (1 - phanTramGiam);
    }

    // =========================================================
    // GETTERS & SETTERS
    // =========================================================

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã Hóa đơn không được rỗng");
        }
        this.maHoaDon = maHoaDon;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        if (ngayLap == null) {
            this.ngayLap = LocalDateTime.now(); // Mặc định là hiện tại nếu null
        } else {
            this.ngayLap = ngayLap;
        }
    }
    
    // ✅ Getter/Setter cho Trạng Thái Thanh Toán
    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        if (trangThaiThanhToan == null || trangThaiThanhToan.trim().isEmpty()) {
            this.trangThaiThanhToan = "Chờ thanh toán";
        } else {
            this.trangThaiThanhToan = trangThaiThanhToan;
        }
    }

    public List<CT_HoaDon> getDanhSachChiTietHoaDon() {
        return danhSachChiTietHoaDon;
    }

    public void setDanhSachChiTietHoaDon(List<CT_HoaDon> danhSachChiTietHoaDon) {
        this.danhSachChiTietHoaDon = danhSachChiTietHoaDon;
    }

    public TheThanhVien getTheThanhVien() {
        return theThanhVien;
    }

    public void setTheThanhVien(TheThanhVien theThanhVien) {
        this.theThanhVien = theThanhVien;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Hóa đơn phải có Nhân viên lập");
        }
        this.nhanVien = nhanVien;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        if (ban == null) {
            throw new IllegalArgumentException("Hóa đơn phải liên kết với Bàn");
        }
        this.ban = ban;
    }

    public BanDat getBanDat() {
        return banDat;
    }

    public void setBanDat(BanDat banDat) {
        this.banDat = banDat;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }
    
    @Override
    public String toString() {
        return "HoaDon [maHD=" + maHoaDon + ", ngayLap=" + ngayLap + ", tongTien=" + tinhTongTien() + "]";
    }
}