﻿CREATE DATABASE QLNH;
GO

USE QLNH;
GO

-- Xóa các bảng nếu đã tồn tại (Đảm bảo thứ tự xóa để tránh lỗi khóa ngoại)
IF OBJECT_ID('dbo.BAOCAO', 'U') IS NOT NULL DROP TABLE dbo.BAOCAO;
IF OBJECT_ID('dbo.CT_HOADON', 'U') IS NOT NULL DROP TABLE dbo.CT_HOADON;
IF OBJECT_ID('dbo.HOADON', 'U') IS NOT NULL DROP TABLE dbo.HOADON;
IF OBJECT_ID('dbo.CT_BANDAT', 'U') IS NOT NULL DROP TABLE dbo.CT_BANDAT; -- Thêm CT_BANDAT
IF OBJECT_ID('dbo.BANDAT', 'U') IS NOT NULL DROP TABLE dbo.BANDAT;
IF OBJECT_ID('dbo.KHUYENMAI', 'U') IS NOT NULL DROP TABLE dbo.KHUYENMAI;
IF OBJECT_ID('dbo.MONAN', 'U') IS NOT NULL DROP TABLE dbo.MONAN;
IF OBJECT_ID('dbo.THETHANHVIEN', 'U') IS NOT NULL DROP TABLE dbo.THETHANHVIEN;
IF OBJECT_ID('dbo.KHACHHANG', 'U') IS NOT NULL DROP TABLE dbo.KHACHHANG;
IF OBJECT_ID('dbo.BAN', 'U') IS NOT NULL DROP TABLE dbo.BAN;
IF OBJECT_ID('dbo.TAIKHOAN', 'U') IS NOT NULL DROP TABLE dbo.TAIKHOAN;
IF OBJECT_ID('dbo.NHANVIEN', 'U') IS NOT NULL DROP TABLE dbo.NHANVIEN;
IF OBJECT_ID('dbo.QUANLY', 'U') IS NOT NULL DROP TABLE dbo.QUANLY;
GO

------------------------------------
-- 1. QUANLY
------------------------------------
CREATE TABLE QUANLY (
    maQL NVARCHAR(10) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    soDienThoai NVARCHAR(15) UNIQUE NOT NULL,
    email NVARCHAR(50),
    ngaySinh DATE,
    CCCD NVARCHAR(20) UNIQUE,
    gioiTinh BIT NOT NULL, -- 0: Nam, 1: Nữ
    trangThai NVARCHAR(20) NOT NULL 
);

------------------------------------
-- 2. NHANVIEN (ĐÃ BỎ CỘT chucVu)
------------------------------------
CREATE TABLE NHANVIEN (
    maNV NVARCHAR(10) PRIMARY KEY,
    hoTen NVARCHAR(50) NOT NULL,
    CCCD NVARCHAR(20) UNIQUE,
    soDienThoai NVARCHAR(15) UNIQUE NULL, -- Sửa: Cho phép UNIQUE NULL
    email NVARCHAR(50),
    ngaySinh DATE,
    gioiTinh BIT NOT NULL, -- 0: Nam, 1: Nữ
    trangThai NVARCHAR(20) NOT NULL,
    
    maQL NVARCHAR(10), -- Quản lý phụ trách trực tiếp
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL) 
);

------------------------------------
-- 3. TAIKHOAN
------------------------------------
CREATE TABLE TAIKHOAN (
    tenDangNhap NVARCHAR(50) PRIMARY KEY,
    matKhau NVARCHAR(50) NOT NULL,
    vaiTro NVARCHAR(20) NOT NULL, 
    trangThai NVARCHAR(20) NOT NULL,
    
    maNV NVARCHAR(10) UNIQUE, 
    maQL NVARCHAR(10) UNIQUE, 
    
    FOREIGN KEY (maNV) REFERENCES NHANVIEN(maNV),
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL)
);

------------------------------------
-- 4. KHACHHANG
------------------------------------
CREATE TABLE KHACHHANG (
    maKH NVARCHAR(10) PRIMARY KEY,
    hoTenKH NVARCHAR(50) NOT NULL,
    soDienThoai NVARCHAR(15) UNIQUE NULL, -- Sửa: Cho phép UNIQUE NULL
    email NVARCHAR(50), 
    gioiTinh BIT NOT NULL -- 0: Nam, 1: Nữ
);

------------------------------------
-- 5. THETHANHVIEN
------------------------------------
CREATE TABLE THETHANHVIEN (
    maThe NVARCHAR(10) PRIMARY KEY,
    maKH NVARCHAR(10) UNIQUE NOT NULL,
    diemTichLuy INT NOT NULL CHECK (diemTichLuy >= 0),
    loaiHang NVARCHAR(50) NOT NULL DEFAULT N'Bạc', 
    FOREIGN KEY (maKH) REFERENCES KHACHHANG(maKH)
);

------------------------------------
-- 6. BAN
------------------------------------
CREATE TABLE BAN (
    maBan NVARCHAR(10) PRIMARY KEY,
    loaiBan NVARCHAR(50) NOT NULL,   
    soGhe INT NOT NULL CHECK (soGhe > 0), 
    khuVuc NVARCHAR(50) NOT NULL,     
    trangThai NVARCHAR(20) NOT NULL   
);

------------------------------------
-- 7. BANDAT
------------------------------------
CREATE TABLE BANDAT (
    maDatBan NVARCHAR(10) PRIMARY KEY,
    maKH NVARCHAR(10) NOT NULL,
    maBan NVARCHAR(10) NOT NULL,
    
    ngayDat DATE NOT NULL,
    gioDat TIME NOT NULL, 
    ghiChu NVARCHAR(255),
    
    soLuongKhach INT NOT NULL CHECK (soLuongKhach > 0),
    tienCoc FLOAT NOT NULL CHECK (tienCoc >= 0),
    trangThai NVARCHAR(20) NOT NULL, 
    gioCheckIn TIME NULL, 
    
    FOREIGN KEY (maKH) REFERENCES KHACHHANG(maKH),
    FOREIGN KEY (maBan) REFERENCES BAN(maBan)
);

------------------------------------
-- 8. KHUYENMAI
------------------------------------
CREATE TABLE KHUYENMAI (
    maKM NVARCHAR(10) PRIMARY KEY,
    tenKM NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(100),
    phanTramGiam FLOAT NOT NULL CHECK (phanTramGiam >= 0 AND phanTramGiam <= 1),
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    
    maQL NVARCHAR(10) NOT NULL, 
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL),
    CHECK (ngayKetThuc >= ngayBatDau)
);

------------------------------------
-- 9. MONAN
------------------------------------
CREATE TABLE MONAN (
    maMon NVARCHAR(10) PRIMARY KEY,
    tenMon NVARCHAR(50) NOT NULL,
    loaiMon NVARCHAR(50) NOT NULL,
    giaMon FLOAT NOT NULL CHECK (giaMon > 0),
    hinhAnh NVARCHAR(255),
    
    maQL NVARCHAR(10) NOT NULL, 
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL) 
);

-- 9B. CT_BANDAT (Bảng này bị thiếu trong file SQL của bạn)
CREATE TABLE CT_BANDAT (
    maBan NVARCHAR(10) NOT NULL, -- SỬA: Đổi từ maDatBan thành maBan
    maMon NVARCHAR(10) NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    
    PRIMARY KEY (maBan, maMon), -- Khóa chính hỗn hợp
    FOREIGN KEY (maBan) REFERENCES BAN(maBan), -- SỬA: Khóa ngoại đến BAN
    FOREIGN KEY (maMon) REFERENCES MONAN(maMon)
);
GO
------------------------------------
-- 10. HOADON
------------------------------------
CREATE TABLE HOADON (
    maHD NVARCHAR(10) PRIMARY KEY,
    maThe NVARCHAR(10), 
    maNV NVARCHAR(10) NOT NULL, 
    maBan NVARCHAR(10) NOT NULL,
    -- 💡 SỬA: Bỏ UNIQUE constraint để cho phép nhiều hóa đơn không đặt bàn (NULL)
    maDatBan NVARCHAR(10) NULL, 
    maKM NVARCHAR(10),
    ngayLap DATETIME NOT NULL,
    tongTien FLOAT NOT NULL CHECK (tongTien >= 0),
    trangThaiThanhToan NVARCHAR(20) NOT NULL, 
    
    FOREIGN KEY (maThe) REFERENCES THETHANHVIEN(maThe),
    FOREIGN KEY (maNV) REFERENCES NHANVIEN(maNV),
    FOREIGN KEY (maBan) REFERENCES BAN(maBan),
    FOREIGN KEY (maDatBan) REFERENCES BANDAT(maDatBan),
    FOREIGN KEY (maKM) REFERENCES KHUYENMAI(maKM)
);

------------------------------------
-- 11. CT_HOADON
------------------------------------
CREATE TABLE CT_HOADON (
    maHD NVARCHAR(10),
    maMon NVARCHAR(10),
    soLuong INT NOT NULL CHECK (soLuong > 0),
    thanhTien FLOAT NOT NULL CHECK (thanhTien >= 0),
    
    PRIMARY KEY (maHD, maMon),
    FOREIGN KEY (maHD) REFERENCES HOADON(maHD),
    FOREIGN KEY (maMon) REFERENCES MONAN(maMon)
);

------------------------------------
-- 12. BAOCAO
------------------------------------
CREATE TABLE BAOCAO (
    maBC NVARCHAR(10) PRIMARY KEY,
    ngayLap DATE NOT NULL,
    thoiGianTu DATE NOT NULL,
    thoiGianDen DATE NOT NULL,
    doanhThu FLOAT NOT NULL,
    
    maQL NVARCHAR(10) NOT NULL, 
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL)
);
GO

---
-- Dữ liệu mẫu (Đã sửa lỗi)

-- 1. QUANLY
INSERT INTO QUANLY (maQL, hoTen, soDienThoai, email, ngaySinh, CCCD, gioiTinh, trangThai) VALUES
('QL001', N'Trần Đại Hiệp', '0901234567', 'hiep.tran@qlnh.vn', '1985-01-01', '001122334455', 0, N'Đang làm'),
('QL002', N'Lê Thị Mai', '0919876543', 'mai.le@qlnh.vn', '1990-05-10', '001122334466', 1, N'Đang làm');

-- 2. NHANVIEN (Đã sửa lỗi thiếu GioiTinh)
INSERT INTO NHANVIEN (maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL) VALUES
('NV001', N'Nguyễn Văn An', '001122334477', '0922345678', 'an.nguyen@qlnh.vn', '1995-05-20', 0, N'Đang làm', 'QL001'),
('NV002', N'Phạm Thu Hà', '001122334488', '0933456789', 'ha.pham@qlnh.vn', '1998-08-15', 1, N'Đang làm', 'QL002');
-- 💡 SỬA: Thêm NV003 để HOADON có thể tham chiếu
INSERT INTO NHANVIEN (maNV, hoTen, CCCD, soDienThoai, email, ngaySinh, gioiTinh, trangThai, maQL) VALUES
('NV003', N'Lê Văn Ba', '001122334499', '0944556677', 'ba.le@qlnh.vn', '1999-01-01', 0, N'Đang làm', 'QL001');

-- 3. TAIKHOAN
INSERT INTO TAIKHOAN (tenDangNhap, matKhau, vaiTro, trangThai, maNV, maQL) VALUES
('tranhiep', '123456', N'Quản Lý', N'Hoạt động', NULL, 'QL001'),
('nvan', '123456', N'Nhân Viên', N'Hoạt động', 'NV001', NULL);

-- 4. KHACHHANG (Sửa lỗi trùng SĐT)
INSERT INTO KHACHHANG (maKH, hoTenKH, soDienThoai, email, gioiTinh) VALUES
('KH001', N'Phan Đình Quang', '0812345678', 'quang.phan@gmail.com', 0),
('KH002', N'Ngô Tố Uyên', '0898765432', 'uyen.ngo@gmail.com', 1),
('KH003', N'Lê Văn Nguyên', '0898765433', 'nguyen.ngo@gmail.com', 0); -- Sửa SĐT

-- 5. THETHANHVIEN (Sửa lỗi thiếu giá trị và thêm TV002, TV003)
INSERT INTO THETHANHVIEN (maThe, maKH, diemTichLuy, loaiHang) VALUES
('TV001', 'KH001', 500, N'Vàng'),
('TV002', 'KH002', 450, N'Bạc'),
('TV003', 'KH003', 600, N'Vàng');

-- 6. BAN
INSERT INTO BAN (maBan, loaiBan, soGhe, khuVuc, trangThai) VALUES
('B001', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),
('B002', N'Bàn vừa', 4, N'Tầng 1', N'Đang sử dụng'),
('B003', N'Phòng VIP', 10, N'Tầng 2', N'Đã đặt');

-- 7. BANDAT
INSERT INTO BANDAT (maDatBan, maKH, maBan, ngayDat, gioDat, ghiChu, soLuongKhach, tienCoc, trangThai, gioCheckIn) VALUES
('DB001', 'KH001', 'B003', '2025-10-27', '18:30:00', N'Kỷ niệm ngày cưới', 5, 200000, N'Đã nhận',NULL);

-- 8. KHUYENMAI (Sửa lỗi cú pháp)
INSERT INTO KHUYENMAI (maKM, tenKM, moTa, phanTramGiam, ngayBatDau, ngayKetThuc, maQL) VALUES
('KM001', N'Giảm giá mùa hè', N'giảm 15%', 0.15, '2025-06-01', '2025-08-30', 'QL001'),
('KM002', N'Giảm giá lễ', N'giảm 20%', 0.2, '2025-06-01', '2025-12-30', 'QL001');

-- 9. MONAN
INSERT INTO MONAN (maMon, tenMon, loaiMon, giaMon, hinhAnh, maQL) VALUES
('MA001', N'Lẩu Hải Sản', N'Món Chính', 299000, 'lau_haisan.jpg', 'QL001'),
('MA002', N'Bò Né', N'Món Chính', 85000, 'bo_ne.jpg', 'QL001'),
('MA003', N'Trà Đào', N'Thức Uống', 40000, 'tra_dao.jpg', 'QL002'),
('MA004', N'Nước Cam Tươi', N'Thức Uống', 45000, 'nuoc_cam.jpg', 'QL002'),
('MA005', N'Salad Trộn', N'Món Phụ', 65000, 'salad_tron.jpg', 'QL002'),
('MA006', N'Gà Quay Mật Ong', N'Món Chính', 180000, 'ga_quay.jpg', 'QL001'),
('MA007', N'Rau Muống Xào Tỏi', N'Món Phụ', 45000, 'rau_muong.jpg', 'QL002'),
('MA008', N'Cà Phê Đen', N'Thức Uống', 35000, 'cf_den.jpg', 'QL002'),
('MA009', N'Kem Vani', N'Tráng Miệng', 30000, 'kem_vani.jpg', 'QL001'),
('MA010', N'Bún Chả Hà Nội', N'Món Chính', 70000, 'bun_cha.jpg', 'QL001'),
('MA011', N'Khoai Tây Chiên', N'Món Phụ', 50000, 'khoai_tay.jpg', 'QL001'),
('MA012', N'Soda Chanh', N'Thức Uống', 42000, 'soda_chanh.jpg', 'QL002'),
('MA013', N'Chè Ba Màu', N'Tráng Miệng', 35000, 'che_3mau.jpg', 'QL002'),
('MA014', N'Mì Ý Sốt Cà Chua', N'Món Chính', 95000, 'mi_y.jpg', 'QL001'),
('MA015', N'Bánh Mì Bơ Tỏi', N'Món Phụ', 35000, 'banh_mi_boi.jpg', 'QL001'),
('MA016', N'Sinh Tố Bơ', N'Thức Uống', 55000, 'sinhto_bo.jpg', 'QL001'),
('MA017', N'Bánh Flan', N'Tráng Miệng', 25000, 'banh_flan.jpg', 'QL001'),
('MA018', N'Tôm Hấp Bia', N'Món Chính', 250000, 'tom_hap_bia.jpg', 'QL001'),
('MA019', N'Đậu Hũ Tứ Xuyên', N'Món Phụ', 75000, 'dau_hu_sx.jpg', 'QL002'),
('MA020', N'Nước Dừa Tươi', N'Thức Uống', 50000, 'nuoc_dua.jpg', 'QL002'),
('MA021', N'Trái Cây Thập Cẩm', N'Tráng Miệng', 60000, 'trai_cay.jpg', 'QL002'),
('MA022', N'Phở Bò Đặc Biệt', N'Món Chính', 80000, 'pho_bo.jpg', 'QL001'),
('MA023', N'Cơm Chiên Hải Sản', N'Món Chính', 90000, 'com_chien_hs.jpg', 'QL001'),
('MA024', N'Trà Sữa Trân Châu', N'Thức Uống', 50000, 'ts_tranchau.jpg', 'QL002'),
('MA025', N'Gỏi Cuốn Tôm Thịt', N'Món Phụ', 60000, 'goi_cuon.jpg', 'QL001'),
('MA026', N'Lẩu Cá Kèo', N'Món Chính', 320000, 'lau_cakeo.jpg', 'QL002'),
('MA027', N'Bánh Gato Kem', N'Tráng Miệng', 45000, 'banh_gato.jpg', 'QL001'),
('MA028', N'Nước Ép Dứa', N'Thức Uống', 48000, 'nuoc_ep_dua.jpg', 'QL002'),
('MA029', N'Mực Chiên Giòn', N'Món Phụ', 110000, 'muc_chien.jpg', 'QL001'),
('MA030', N'Cà Ri Gà', N'Món Chính', 150000, 'ca_ri_ga.jpg', 'QL002'),
('MA031', N'Sữa Chua Dẻo', N'Tráng Miệng', 30000, 'sua_chua.jpg', 'QL001'),
('MA032', N'Bia Tiger Lon', N'Thức Uống', 35000, 'bia_tiger.jpg', 'QL002'),
('MA033', N'Nem Nướng Nha Trang', N'Món Phụ', 85000, 'nem_nuong.jpg', 'QL001'),
('MA034', N'Vịt Quay Bắc Kinh', N'Món Chính', 450000, 'vit_quay.jpg', 'QL002'),
('MA035', N'Chè Khoai Môn', N'Tráng Miệng', 40000, 'che_khoaimon.jpg', 'QL001'),
('MA036', N'Coca Cola Lon', N'Thức Uống', 30000, 'coca_cola.jpg', 'QL002'),
('MA037', N'Bánh Xèo', N'Món Phụ', 70000, 'banh_xeo.jpg', 'QL001'),
('MA038', N'Bò Lúc Lắc', N'Món Chính', 165000, 'bo_luc_lac.jpg', 'QL002'),
('MA039', N'Pudding Trái Cây', N'Tráng Miệng', 50000, 'pudding.jpg', 'QL001'),
('MA040', N'Rượu Vang Đỏ (Chai)', N'Thức Uống', 550000, 'ruou_vang.jpg', 'QL002'),
('MA041', N'Cháo Lòng', N'Món Phụ', 60000, 'chao_long.jpg', 'QL001'),
('MA042', N'Cá Hồi Nướng Sốt Chanh Dây', N'Món Chính', 280000, 'cahoi_nuong.jpg', 'QL002');

-- 10. HOADON (Sửa lỗi cú pháp và logic)
INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) VALUES
('HD001', 'TV001', 'NV001', 'B002', 'DB001', 'KM001', GETDATE(), 288150, N'Đã thanh toán');

-- 💡 SỬA LỖI: Thêm 4 hóa đơn mới
-- HD002 (Bàn B003, NV002, Thẻ TV002, KM002) - Tổng: 315000 (Chưa KM: 390000)
INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) VALUES
('HD002', 'TV002', 'NV002', 'B003', null, 'KM002', DATEADD(DAY, -1, GETDATE()), 312000, N'Đã thanh toán');

-- HD003 (Bàn B001, NV003, Không thẻ, Không KM) - Tổng: 479000
INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) VALUES
('HD003', NULL, 'NV003', 'B001', NULL, NULL, DATEADD(DAY, -3, GETDATE()), 479000, N'Đã thanh toán');

-- HD004 (Bàn B001, NV001, Thẻ TV003, Không KM) - Tổng: 120000
INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) VALUES
('HD004', 'TV003', 'NV001', 'B001', NULL, NULL, DATEADD(DAY, -7, GETDATE()), 120000, N'Đã thanh toán');

-- HD005 (Bàn B002, NV002, Không thẻ, KM001) - Tổng: 100000 (Chưa KM: 100000) - KM ko áp dụng
INSERT INTO HOADON (maHD, maThe, maNV, maBan, maDatBan, maKM, ngayLap, tongTien, trangThaiThanhToan) VALUES
('HD005', NULL, 'NV002', 'B002', NULL, 'KM001', DATEADD(DAY, -10, GETDATE()), 100000, N'Đã thanh toán');

-- 11. CT_HOADON (Sửa lỗi logic)
INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES
('HD001', 'MA001', 1, 299000), 
('HD001', 'MA003', 1, 40000); 

-- HD002 (Bò Né * 2 + Salad * 1 + Rau xào * 1 = 85*2 + 65 + 45 = 170+65+45 = 280k) -> Lỗi dữ liệu mẫu
-- Sửa: Bò Né * 2 (170k) + Gà Quay * 1 (180k) + Kem * 1 (30k) = 380k
INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES
('HD002', 'MA002', 2, 170000),
('HD002', 'MA006', 1, 180000),
('HD002', 'MA009', 1, 30000);
-- Tổng HD002 (380k * 0.8 (KM 20%) = 304000)
UPDATE HOADON SET tongTien = 304000 WHERE maHD = 'HD002';

-- HD003 (Lẩu * 1 + Gà Quay * 1 = 299 + 180 = 479k)
INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES
('HD003', 'MA001', 1, 299000),
('HD003', 'MA006', 1, 180000);

-- HD004 (Trà Đào * 3 = 120k)
INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES
('HD004', 'MA003', 3, 120000);

-- HD005 (Bò Né * 1 + Kem * 1 + Bún Chả * 1 = 85 + 30 + 70 = 185k) -> Lỗi dữ liệu mẫu
-- Sửa: Khoai tây * 2 = 100k
INSERT INTO CT_HOADON (maHD, maMon, soLuong, thanhTien) VALUES
('HD005', 'MA011', 2, 100000);
-- Tổng HD005 (100k * 1.0 (KM ko áp dụng) = 100k)
UPDATE HOADON SET tongTien = 100000 WHERE maHD = 'HD005';


-- 12. BAOCAO
INSERT INTO BAOCAO (maBC, ngayLap, thoiGianTu, thoiGianDen, doanhThu, maQL) VALUES 
('BC001', GETDATE(), '2025-10-01', '2025-10-31', 288150, 'QL001'); 
GO

-- Bắt đầu chèn dữ liệu BAN
INSERT INTO dbo.BAN (maBan, loaiBan, soGhe, khuVuc, trangThai) VALUES
-- 10 Bàn nhỏ (2-4 chỗ) ở Tầng 1
('B004', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B005', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B006', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B007', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B008', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B009', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B010', N'Bàn nhỏ', 4, N'Tầng 1', N'Trống'),
('B011', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),
('B012', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),
('B013', N'Bàn nhỏ', 2, N'Tầng 1', N'Trống'),

-- 6 Bàn vừa (4-6 chỗ) ở Tầng 2
('B014', N'Bàn vừa', 6, N'Tầng 2', N'Trống'),
('B015', N'Bàn vừa', 6, N'Tầng 2', N'Trống'),
('B016', N'Bàn vừa', 6, N'Tầng 2', N'Trống'),
('B017', N'Bàn vừa', 4, N'Tầng 2', N'Trống'),
('B018', N'Bàn vừa', 4, N'Tầng 2', N'Trống'),
('B019', N'Bàn vừa', 4, N'Tầng 2', N'Trống'),

-- 2 Bàn lớn (8-10 chỗ) ở Tầng 3
('B020', N'Bàn lớn', 8, N'Tầng 3', N'Trống'),
('B021', N'Bàn lớn', 8, N'Tầng 3', N'Trống'),

-- 2 Phòng VIP (12-15 chỗ)
('VIP2', N'Phòng VIP', 15, N'Tầng 3', N'Trống'),
('VIP3', N'Phòng VIP', 15, N'Tầng 3', N'Trống');
GO