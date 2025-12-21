package entity;

import java.util.Date;
import java.util.Objects;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class NhanVien {

    private String maNV;
    private String hoTen;
    private String CCCD;
    private String soDienThoai;
    private String email;
    private Boolean gioiTinh;
    private Date ngaySinh;  
    private String trangThai;
    private QuanLy quanLy; // SỬA: Thay String maQL bằng thực thể QuanLy

    public NhanVien() {
        
    }

    public NhanVien(String ma) {
        this.maNV = ma;
    }

    public NhanVien(String maNV, String hoTen, String cCCD, String soDienThoai, String email, Boolean gioiTinh,
                    Date ngaySinh, String trangThai, QuanLy quanLy) { // SỬA: Tham số QuanLy
        
        // Gọi setter để áp dụng validation
        setMaNV(maNV);
        setHoTen(hoTen);
        setCCCD(cCCD);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setGioiTinh(gioiTinh);
        setNgaySinh(ngaySinh);
        setTrangThai(trangThai);
        setQuanLy(quanLy); // SỬA: Gọi setQuanLy
    }

    // --- CÁC HÀM GETTER/SETTER ĐÃ ĐƯỢC CẬP NHẬT ---

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống.");
        }
        if (!maNV.matches("^(NV|QL)\\d{3}$")) {
            throw new IllegalArgumentException("Mã nhân viên phải có dạng NVxxx (ví dụ: NV001).");
        }
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

   
    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        this.hoTen = hoTen;
    }

    public String getCCCD() {
        return CCCD;
    }

    public void setCCCD(String cCCD) {
        if (cCCD == null || cCCD.trim().isEmpty()) {
            throw new IllegalArgumentException("CCCD không được để trống.");
        }
        if (!cCCD.matches("^[0-9]{12}$")) {
             throw new IllegalArgumentException("CCCD phải là 12 chữ số.");
        }
        this.CCCD = cCCD;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

   
    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (!soDienThoai.matches("^(09|03)[0-9]{8}$")) {
            throw new IllegalArgumentException("Số điện thoại phải bắt đầu bằng 09 hoặc 03 và có 10 chữ số.");
        }
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        // Cho phép email null hoặc rỗng nếu nghiệp vụ cho phép
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[a-zA-Z0-9._%+-]+@qlnh\\.vn$")) {
                throw new IllegalArgumentException("Email phải có dạng [tên]@qlnh.vn.");
            }
        }
        this.email = email;
    }

    public Boolean getGioiTinh() {
        return gioiTinh;
    }

   
    public void setGioiTinh(Boolean gioiTinh) {
        if (gioiTinh == null) {
            throw new IllegalArgumentException("Giới tính không được để trống (phải chọn Nam hoặc Nữ).");
        }
        this.gioiTinh = gioiTinh;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        if (ngaySinh == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống.");
        }
        
        // Chuyển đổi java.util.Date sang java.time.LocalDate
        LocalDate birthDate = ngaySinh.toInstant()
                                       .atZone(ZoneId.systemDefault())
                                       .toLocalDate();
        LocalDate today = LocalDate.now();
        
        // Kiểm tra tuổi
        if (Period.between(birthDate, today).getYears() < 18) {
            throw new IllegalArgumentException("Nhân viên phải trên 18 tuổi.");
        }
        
        this.ngaySinh = ngaySinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

   
    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống.");
        }
        this.trangThai = trangThai;
    }
    
 
    public QuanLy getQuanLy() {
        return quanLy;
    }

   
    public void setQuanLy(QuanLy quanLy) {
        
        this.quanLy = quanLy;
    }

   
    @Override
    public String toString() {
        return "NhanVien [maNV=" + maNV + ", hoTen=" + hoTen + ", CCCD=" + CCCD + ", soDienThoai=" + soDienThoai
                + ", email=" + email + ", gioiTinh=" + gioiTinh + ", ngaySinh=" + ngaySinh + ", trangThai=" + trangThai
                + ", quanLy=" + (quanLy != null ? quanLy.getMaQL() : "null") + "]"; // Lấy mã QL từ đối tượng
    }

  
    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NhanVien other = (NhanVien) obj;
        return Objects.equals(maNV, other.maNV);
    }
}