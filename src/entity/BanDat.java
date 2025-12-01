package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class BanDat {
    private String maDatBan;
    private KhachHang khachHang; 
    private Ban ban;            
    
    private LocalDate ngayDat; 
    private LocalTime gioDat;   
    
    private int soLuongKhach;
    private double tienCoc;
    private String trangThai;
    private String ghiChu;   
    private LocalTime gioCheckIn;
    public LocalTime getGioCheckIn() { return gioCheckIn; }
    public void setGioCheckIn(LocalTime gioCheckIn) { this.gioCheckIn = gioCheckIn; }

    
    public BanDat(String maDatBan, KhachHang khachHang, Ban ban, LocalDate ngayDat, LocalTime gioDat, 
                  int soLuongKhach, double tienCoc, String trangThai, String ghiChu, LocalTime gioCheckIn) {
      
        this.maDatBan = maDatBan;
        this.khachHang = khachHang;
        this.ban = ban;
        this.ngayDat = ngayDat;
        this.gioDat = gioDat;
        this.soLuongKhach = soLuongKhach;
        this.tienCoc = tienCoc;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.gioCheckIn = gioCheckIn;
    }

   
    public BanDat(String maDatBan) {
        this.maDatBan = maDatBan;
    }


   
    
    // Getters
    public String getMaDatBan() { return maDatBan; }
    public KhachHang getKhachHang() { return khachHang; }
    public Ban getBan() { return ban; }
    public LocalDate getNgayDat() { return ngayDat; } // Thêm
    public LocalTime getGioDat() { return gioDat; }   // Thêm
    public int getSoLuongKhach() { return soLuongKhach; }
    public double getTienCoc() { return tienCoc; }
    public String getTrangThai() { return trangThai; }
    public String getGhiChu() { return ghiChu; }     // Thêm

    // Setters (Đã điều chỉnh validation)
    public void setMaDatBan(String maDatBan) {
        if(maDatBan == null || maDatBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đặt bàn không được rỗng.");
        }
        this.maDatBan = maDatBan;
    }
    public void setKhachHang(KhachHang khachHang) {
        if(khachHang == null) {
            throw new IllegalArgumentException("Phải có Khách hàng đặt bàn.");
        }
        this.khachHang = khachHang;
    }
    public void setBan(Ban ban) {
        if(ban == null) {
            throw new IllegalArgumentException("Phải có Bàn được đặt.");
        }
        this.ban = ban;
    }
    public void setNgayDat(LocalDate ngayDat) { // Thêm
        if(ngayDat == null) {
            throw new IllegalArgumentException("Ngày đặt không được rỗng.");
        }
        this.ngayDat = ngayDat;
    }
    public void setGioDat(LocalTime gioDat) { // Thêm
        if(gioDat == null) {
            throw new IllegalArgumentException("Giờ đặt không được rỗng.");
        }
        this.gioDat = gioDat;
    }
    public void setSoLuongKhach(int soLuongKhach) {
        if(soLuongKhach <= 0) {
            throw new IllegalArgumentException("Số lượng khách phải lớn hơn 0.");
        }
        this.soLuongKhach = soLuongKhach;
    }
    public void setTienCoc(double tienCoc) {
        if(tienCoc < 0) {
            throw new IllegalArgumentException("Tiền cọc không được âm.");
        }
        this.tienCoc = tienCoc;
    }
    public void setTrangThai(String trangThai) {
        if(trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được rỗng.");
        }
        this.trangThai = trangThai;
    }
    public void setGhiChu(String ghiChu) { // Thêm
        this.ghiChu = ghiChu;
    }

    // Equals và HashCode (Sử dụng Objects.hash và Objects.equals)
    @Override
    public int hashCode() {
        return Objects.hash(maDatBan);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        BanDat other = (BanDat) obj;
        return Objects.equals(maDatBan, other.maDatBan);
    }

    @Override
    public String toString() {
        return "BanDat [maDatBan=" + maDatBan + ", khachHang=" + khachHang.getMaKH() + ", ban=" + ban.getMaBan()
                + ", ngayDat=" + ngayDat + ", gioDat=" + gioDat + ", soLuongKhach=" + soLuongKhach + ", tienCoc="
                + tienCoc + ", trangThai=" + trangThai + ", ghiChu=" + ghiChu + "]";
    }
}