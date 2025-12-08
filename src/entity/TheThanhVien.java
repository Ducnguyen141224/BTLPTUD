package entity;

import java.util.Objects;

public class TheThanhVien {
    private String maThe;
    private KhachHang khachHang;
    private int diemTichLuy;
    private String loaiHang;

    public static String tinhLoaiHang(int diem) {
        if (diem >= 250)
            return "Kim cương";
        else if (diem >= 100)
            return "Vàng";
        else
            return "Bạc";
    }

    // Constructor dùng khi tạo mới thẻ
    public TheThanhVien(String maThe, KhachHang khachHang, int diemTichLuy) {
        this.maThe = maThe;
        this.khachHang = khachHang;
        this.setDiemTichLuy(diemTichLuy);
    }

    // Constructor dùng khi đọc từ CSDL
    public TheThanhVien(String maThe, KhachHang khachHang, int diemTichLuy, String loaiHangTuCSDL) {
        this.maThe = maThe;
        this.khachHang = khachHang;
        this.diemTichLuy = diemTichLuy;

        // ⭐ SỬA LỖI QUAN TRỌNG: PHẢI SET LOẠI HẠNG THEO DB
        this.loaiHang = loaiHangTuCSDL != null ? loaiHangTuCSDL : tinhLoaiHang(diemTichLuy);
    }

    public TheThanhVien(String maThe) {
        this.maThe = maThe;
    }

    // Getters
    public String getMaThe() { return maThe; }
    public KhachHang getKhachHang() { return khachHang; }
    public int getDiemTichLuy() { return diemTichLuy; }
    public String getLoaiHang() { return loaiHang; }

    // Setters
    public void setMaThe(String maThe) { this.maThe = maThe; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) diemTichLuy = 0;
        this.diemTichLuy = diemTichLuy;

        // Tự cập nhật loại hạng khi thay đổi điểm
        this.loaiHang = tinhLoaiHang(diemTichLuy);
    }

    // ⭐ NÊN THÊM SETTER NẾU DAO CẦN GÁN TRỰC TIẾP
    public void setLoaiHang(String loaiHang) {
        this.loaiHang = loaiHang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TheThanhVien)) return false;
        TheThanhVien that = (TheThanhVien) o;
        return Objects.equals(maThe, that.maThe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maThe);
    }
}
