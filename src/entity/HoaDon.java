package entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.management.RuntimeErrorException;

public class HoaDon {
    private String maHoaDon;
    private LocalDateTime ngayLap;
    
    
    private List<CT_HoaDon> danhSachChiTietHoaDon; 

    // Khóa ngoại
    private TheThanhVien theThanhVien; 
    private NhanVien nhanVien;      
    private Ban ban;              
    private BanDat banDat;        
    private KhuyenMai khuyenMai;     

    public HoaDon(String maHoaDon) {
        super();
        this.maHoaDon = maHoaDon;
    }

    public HoaDon(String maHoaDon, TheThanhVien theThanhVien, NhanVien nhanVien, Ban ban, BanDat banDat, KhuyenMai khuyenMai, LocalDateTime ngayLap, List<CT_HoaDon> danhSachChiTietHoaDon) {
        this.maHoaDon = maHoaDon;
        this.theThanhVien = theThanhVien;
        this.nhanVien = nhanVien;
        this.ban = ban;
        this.banDat = banDat;
        this.khuyenMai = khuyenMai;
        this.ngayLap = ngayLap;
        this.danhSachChiTietHoaDon = danhSachChiTietHoaDon;
    }

    // Getters
    public String getMaHoaDon() { return maHoaDon; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public List<CT_HoaDon> getDanhSachChiTietHoaDon() { return danhSachChiTietHoaDon; }
    public TheThanhVien getTheThanhVien() { return theThanhVien; }
    public NhanVien getNhanVien() { return nhanVien; }
    public Ban getBan() { return ban; }
    public BanDat getBanDat() { return banDat; }
    public KhuyenMai getKhuyenMai() { return khuyenMai; }

  
    public double tinhTongTien() {
        if (danhSachChiTietHoaDon == null || danhSachChiTietHoaDon.isEmpty()) {
            return 0.0;
        }

        double tongChuaGiam = danhSachChiTietHoaDon.stream()
            .mapToDouble(CT_HoaDon::tinhThanhTien)
            .sum();

     
        double phanTramGiam = (khuyenMai != null) ? khuyenMai.getPhanTramGiam() : 0.0;
        
       
        return tongChuaGiam * (1 - phanTramGiam);
    }

    // Setters
    public void setMaHoaDon(String maHoaDon) {
        if(maHoaDon == null || maHoaDon.trim().length() == 0) throw new RuntimeException("Mã Hóa đơn không được rỗng");
        this.maHoaDon = maHoaDon;
    }
    public void setNgayLap(LocalDateTime ngayLap) {
        if(ngayLap == null) throw new RuntimeException("Ngày lập không được rỗng");
        this.ngayLap = ngayLap;
    }
    public void setDanhSachChiTietHoaDon(List<CT_HoaDon> danhSachChiTietHoaDon) { this.danhSachChiTietHoaDon = danhSachChiTietHoaDon; }
    public void setTheThanhVien(TheThanhVien theThanhVien) { this.theThanhVien = theThanhVien; }
    public void setNhanVien(NhanVien nhanVien) {
        if(nhanVien == null) throw new RuntimeException("Hóa đơn phải có Nhân viên lập");
        this.nhanVien = nhanVien;
    }
    public void setBan(Ban ban) {
        if(ban == null) throw new RuntimeException("Hóa đơn phải liên kết với Bàn");
        this.ban = ban;
    }
    public void setBanDat(BanDat banDat) { this.banDat = banDat; }
    public void setKhuyenMai(KhuyenMai khuyenMai) { this.khuyenMai = khuyenMai; }

}