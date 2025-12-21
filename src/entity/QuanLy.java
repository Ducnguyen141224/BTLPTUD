package entity;

import javax.management.RuntimeErrorException;

public class QuanLy {
    private String maQL;
    private String hoTen;
    private String email;
    private String soDienThoai;

    public QuanLy() {}
    public QuanLy(String maQL) {
        super();
        this.maQL = maQL;
    }

    public QuanLy(String maQL, String hoTen, String email, String soDienThoai) {
        this.maQL = maQL;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
    }

    // Getters
    public String getMaQL() { return maQL; }
    public String getHoTen() { return hoTen; }
    public String getEmail() { return email; }
    public String getSoDienThoai() { return soDienThoai; }

    // Setters
    public void setMaQL(String maQL) {
        if(maQL == null || maQL.trim().length() == 0) throw new RuntimeException("Mã Quản lý không được rỗng");
        this.maQL = maQL;
    }
    public void setHoTen(String hoTen) {
        if(hoTen == null || hoTen.trim().length() == 0) throw new RuntimeException("Họ tên không được rỗng");
        this.hoTen = hoTen;
    }
    public void setEmail(String email) {
        if(email == null || email.trim().length() == 0) throw new RuntimeException("Email không được rỗng");
        this.email = email;
    }
    public void setSoDienThoai(String soDienThoai) {
        if(soDienThoai == null || soDienThoai.trim().length() == 0) throw new RuntimeException("Số điện thoại không được rỗng");
        this.soDienThoai = soDienThoai;
    }

    // Equals và HashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maQL == null) ? 0 : maQL.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuanLy other = (QuanLy) obj;
        if (maQL == null) {
            if (other.maQL != null) return false;
        } else if (!maQL.equals(other.maQL)) return false;
        return true;
    }
}