﻿CREATE DATABASE QLNH
GO

USE QLNH
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
    
    maNV NVARCHAR(10) NULL, 
    maQL NVARCHAR(10), 
    
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
    giaMon DECIMAL(18,2) NOT NULL CHECK (giaMon > 0),
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
    tongTien DECIMAL(18,2) NOT NULL CHECK (tongTien >= 0),
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
    thanhTien DECIMAL(18,2) NOT NULL CHECK (thanhTien >= 0),
    
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
    doanhThu DECIMAL(18,2) NOT NULL,
    
    maQL NVARCHAR(10) NOT NULL, 
    FOREIGN KEY (maQL) REFERENCES QUANLY(maQL)
);
GO

-- INSERT QUANLY (1)
INSERT INTO QUANLY (maQL,hoTen,soDienThoai,email,ngaySinh,CCCD,gioiTinh,trangThai) VALUES
('QL001', N'Nguyễn Hoàng Long', '0909123456', 'nguyenhoanglong@qlnh.vn', '1982-03-15', '012345678901', 0, N'Đang làm');
GO

-- INSERT NHANVIEN (10)
INSERT INTO NHANVIEN (maNV,hoTen,CCCD,soDienThoai,email,ngaySinh,gioiTinh,trangThai,maQL) VALUES
('NV001', N'Trần Minh Tài', '111111111001', '0911000001', 'tranminhtai@qlnh.vn', '1996-01-10', 0, N'Đang làm', 'QL001'),
('NV002', N'Nguyễn Thị Thu', '111111111002', '0911000002', 'nguyenthithu@qlnh.vn', '1997-02-12', 1, N'Đang làm', 'QL001'),
('NV003', N'Lê Quốc Bảo', '111111111003', '0911000003', 'lequocbao@qlnh.vn', '1995-05-20', 0, N'Đang làm', 'QL001'),
('NV004', N'Phạm Ngọc Hân', '111111111004', '0911000004', 'phamngochan@qlnh.vn', '1998-07-08', 1, N'Đang làm', 'QL001'),
('NV005', N'Võ Thanh Nam', '111111111005', '0911000005', 'vothanhnam@qlnh.vn', '1996-09-14', 0, N'Đang làm', 'QL001'),
('NV006', N'Đặng Mỹ Linh', '111111111006', '0911000006', 'dangmylinh@qlnh.vn', '1999-11-02', 1, N'Đang làm', 'QL001'),
('NV007', N'Hoàng Văn Đức', '111111111007', '0911000007', 'hoangvanduc@qlnh.vn', '1994-03-30', 0, N'Đang làm', 'QL001'),
('NV008', N'Nguyễn Phương Anh', '111111111008', '0911000008', 'nguyenphuonganh@qlnh.vn', '1997-06-18', 1, N'Đang làm', 'QL001'),
('NV009', N'Bùi Tuấn Kiệt', '111111111009', '0911000009', 'buituankiet@qlnh.vn', '1995-12-01', 0, N'Đang làm', 'QL001'),
('NV010', N'Trần Thị Mai', '111111111010', '0911000010', 'tranthimai@qlnh.vn', '1998-04-25', 1, N'Đang làm', 'QL001');
GO

-- INSERT TAIKHOAN (QL + 10 NV) (username = maNV/maQL)
INSERT INTO TAIKHOAN (tenDangNhap,matKhau,vaiTro,trangThai,maNV,maQL) VALUES
('QL001','123456',N'Quản Lý',N'Hoạt động',NULL,'QL001'),
('NV001','123456',N'Nhân Viên',N'Hoạt động','NV001','QL001'),
('NV002','123456',N'Nhân Viên',N'Hoạt động','NV002','QL001'),
('NV003','123456',N'Nhân Viên',N'Hoạt động','NV003','QL001'),
('NV004','123456',N'Nhân Viên',N'Hoạt động','NV004','QL001'),
('NV005','123456',N'Nhân Viên',N'Hoạt động','NV005','QL001'),
('NV006','123456',N'Nhân Viên',N'Hoạt động','NV006','QL001'),
('NV007','123456',N'Nhân Viên',N'Hoạt động','NV007','QL001'),
('NV008','123456',N'Nhân Viên',N'Hoạt động','NV008','QL001'),
('NV009','123456',N'Nhân Viên',N'Hoạt động','NV009','QL001'),
('NV010','123456',N'Nhân Viên',N'Hoạt động','NV010','QL001');
GO

-- INSERT KHACHHANG (60)
INSERT INTO KHACHHANG (maKH,hoTenKH,soDienThoai,email,gioiTinh) VALUES
('KH001', N'Phạm Đình Quang', '0812345671', 'pham_dinh_quang@gmail.com', 0),
('KH002', N'Ngô Tố Uyên', '0812345672', 'ngo_to_uyen@gmail.com', 1),
('KH003', N'Lê Văn Nguyên', '0812345673', 'le_van_nguyen@gmail.com', 0),
('KH004', N'Trần Ngọc Bích', '0812345674', 'tran_ngoc_bich@gmail.com', 1),
('KH005', N'Nguyễn Quốc Huy', '0812345675', 'nguyen_quoc_huy@gmail.com', 0),
('KH006', N'Phạm Minh Châu', '0812345676', 'pham_minh_chau@gmail.com', 1),
('KH007', N'Lê Thị Hồng', '0812345677', 'le_thi_hong@gmail.com', 0),
('KH008', N'Võ Anh Tuấn', '0812345678', 'vo_anh_tuan@gmail.com', 1),
('KH009', N'Đặng Gia Bảo', '0812345679', 'dang_gia_bao@gmail.com', 0),
('KH010', N'Bùi Thanh Tâm', '0812345680', 'bui_thanh_tam@gmail.com', 1),
('KH011', N'Nguyễn Thảo My', '0812345681', 'nguyen_thao_my@gmail.com', 0),
('KH012', N'Trần Đức Anh', '0812345682', 'tran_duc_anh@gmail.com', 1),
('KH013', N'Lê Minh Khang', '0812345683', 'le_minh_khang@gmail.com', 0),
('KH014', N'Phạm Thu Trang', '0812345684', 'pham_thu_trang@gmail.com', 1),
('KH015', N'Nguyễn Quỳnh Anh', '0812345685', 'nguyen_quynh_anh@gmail.com', 0),
('KH016', N'Vũ Hải Nam', '0812345686', 'vu_hai_nam@gmail.com', 1),
('KH017', N'Hoàng Nhật Minh', '0812345687', 'hoang_nhat_minh@gmail.com', 0),
('KH018', N'Nguyễn Khánh Linh', '0812345688', 'nguyen_khanh_linh@gmail.com', 1),
('KH019', N'Trần Phương Thảo', '0812345689', 'tran_phuong_thao@gmail.com', 0),
('KH020', N'Đỗ Thành Đạt', '0812345690', 'do_thanh_dat@gmail.com', 1),
('KH021', N'Lý Minh Trí', '0812345691', 'ly_minh_tri@gmail.com', 0),
('KH022', N'Nguyễn Đình Sơn', '0812345692', 'nguyen_dinh_son@gmail.com', 1),
('KH023', N'Trần Bảo Ngọc', '0812345693', 'tran_bao_ngoc@gmail.com', 0),
('KH024', N'Phạm Gia Hân', '0812345694', 'pham_gia_han@gmail.com', 1),
('KH025', N'Võ Mỹ Duyên', '0812345695', 'vo_my_duyen@gmail.com', 0),
('KH026', N'Lê Quốc Việt', '0812345696', 'le_quoc_viet@gmail.com', 1),
('KH027', N'Nguyễn Trọng Nghĩa', '0812345697', 'nguyen_trong_nghia@gmail.com', 0),
('KH028', N'Trần Thanh Vy', '0812345698', 'tran_thanh_vy@gmail.com', 1),
('KH029', N'Bùi Minh Hoàng', '0812345699', 'bui_minh_hoang@gmail.com', 0),
('KH030', N'Đặng Thị Mai', '0812345700', 'dang_thi_mai@gmail.com', 1),
('KH031', N'Hoàng Tuấn Kiệt', '0812345701', 'hoang_tuan_kiet@gmail.com', 0),
('KH032', N'Nguyễn Minh Tâm', '0812345702', 'nguyen_minh_tam@gmail.com', 1),
('KH033', N'Phạm Quốc Thịnh', '0812345703', 'pham_quoc_thinh@gmail.com', 0),
('KH034', N'Lê Thùy Dung', '0812345704', 'le_thuy_dung@gmail.com', 1),
('KH035', N'Trần Thị Kim Ngân', '0812345705', 'tran_thi_kim_ngan@gmail.com', 0),
('KH036', N'Nguyễn Hoàng Yến', '0812345706', 'nguyen_hoang_yen@gmail.com', 1),
('KH037', N'Bùi Quốc Khánh', '0812345707', 'bui_quoc_khanh@gmail.com', 0),
('KH038', N'Võ Thanh Hà', '0812345708', 'vo_thanh_ha@gmail.com', 1),
('KH039', N'Đặng Đức Long', '0812345709', 'dang_duc_long@gmail.com', 0),
('KH040', N'Hoàng Mỹ Anh', '0812345710', 'hoang_my_anh@gmail.com', 1),
('KH041', N'Nguyễn Thanh Bình', '0812345711', 'nguyen_thanh_binh@gmail.com', 0),
('KH042', N'Trần Quốc Bảo', '0812345712', 'tran_quoc_bao@gmail.com', 1),
('KH043', N'Lê Minh Đức', '0812345713', 'le_minh_duc@gmail.com', 0),
('KH044', N'Phạm Thuỳ Linh', '0812345714', 'pham_thuy_linh@gmail.com', 1),
('KH045', N'Nguyễn Văn Khôi', '0812345715', 'nguyen_van_khoi@gmail.com', 0),
('KH046', N'Trần Anh Khoa', '0812345716', 'tran_anh_khoa@gmail.com', 1),
('KH047', N'Bùi Gia Huy', '0812345717', 'bui_gia_huy@gmail.com', 0),
('KH048', N'Vũ Thuỳ Chi', '0812345718', 'vu_thuy_chi@gmail.com', 1),
('KH049', N'Đỗ Minh Quân', '0812345719', 'do_minh_quan@gmail.com', 0),
('KH050', N'Lý Gia An', '0812345720', 'ly_gia_an@gmail.com', 1),
('KH051', N'Nguyễn Đức Tín', '0812345721', 'nguyen_duc_tin@gmail.com', 0),
('KH052', N'Trần Thảo Nhi', '0812345722', 'tran_thao_nhi@gmail.com', 1),
('KH053', N'Phạm Thanh Tùng', '0812345723', 'pham_thanh_tung@gmail.com', 0),
('KH054', N'Lê Bảo Châu', '0812345724', 'le_bao_chau@gmail.com', 1),
('KH055', N'Võ Quốc Hưng', '0812345725', 'vo_quoc_hung@gmail.com', 0),
('KH056', N'Đặng Minh Anh', '0812345726', 'dang_minh_anh@gmail.com', 1),
('KH057', N'Hoàng Gia Bảo', '0812345727', 'hoang_gia_bao@gmail.com', 0),
('KH058', N'Nguyễn Trâm Anh', '0812345728', 'nguyen_tram_anh@gmail.com', 1),
('KH059', N'Trần Gia Hân', '0812345729', 'tran_gia_han@gmail.com', 0),
('KH060', N'Bùi Nhật Linh', '0812345730', 'bui_nhat_linh@gmail.com', 1);
GO

-- INSERT THETHANHVIEN (20)
INSERT INTO THETHANHVIEN (maThe, maKH, diemTichLuy, loaiHang)
SELECT
    'TV' + RIGHT('000' + CAST(ROW_NUMBER() OVER (ORDER BY maKH) AS VARCHAR), 3),
    maKH,
    diem,
    CASE
        WHEN diem >= 250 THEN N'Kim cương'
        WHEN diem >= 100 THEN N'Vàng'
        ELSE N'Bạc'
    END
FROM (
    SELECT maKH,
           ABS(CHECKSUM(NEWID())) % 1200 AS diem
    FROM KHACHHANG
) AS X;
GO

-- INSERT BAN (30)
INSERT INTO BAN (maBan,loaiBan,soGhe,khuVuc,trangThai) VALUES
('B001', N'Bàn 2 người', 2, N'Tầng 1', N'Trống'),
('B002', N'Bàn 4 người', 4, N'Tầng 1', N'Trống'),
('B003', N'Bàn 2 người', 2, N'Tầng 1', N'Trống'),
('B004', N'Bàn 4 người', 4, N'Tầng 1', N'Trống'),
('B005', N'Bàn 2 người', 2, N'Tầng 1', N'Trống'),
('B006', N'Bàn 4 người', 4, N'Tầng 1', N'Trống'),
('B007', N'Bàn 2 người', 2, N'Tầng 1', N'Trống'),
('B008', N'Bàn 4 người', 4, N'Tầng 1', N'Trống'),
('B009', N'Bàn 2 người', 2, N'Tầng 1', N'Trống'),
('B010', N'Bàn 4 người', 4, N'Tầng 1', N'Trống'),
('B011', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B012', N'Bàn 8 người', 8, N'Tầng 2', N'Trống'),
('B013', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B014', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B015', N'Bàn 8 người', 8, N'Tầng 2', N'Trống'),
('B016', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B017', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B018', N'Bàn 8 người', 8, N'Tầng 2', N'Trống'),
('B019', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B020', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B021', N'Bàn 8 người', 8, N'Tầng 2', N'Trống'),
('B022', N'Bàn 6 người', 6, N'Tầng 2', N'Trống'),
('B023', N'Phòng VIP', 12, N'Tầng 3', N'Trống'),
('B024', N'Phòng VIP', 12, N'Tầng 3', N'Trống'),
('B025', N'Phòng VIP', 12, N'Tầng 3', N'Trống'),
('B026', N'Phòng VIP', 12, N'Tầng 3', N'Trống'),
('B027', N'Phòng VIP', 12, N'Tầng 3', N'Trống'),
('B028', N'Phòng VIP', 12, N'Tầng 3', N'Trống'),
('B029', N'Bàn 10 người', 10, N'Tầng 3', N'Trống'),
('B030', N'Bàn 10 người', 10, N'Tầng 3', N'Trống');
GO

-- INSERT KHUYENMAI (2)
INSERT INTO KHUYENMAI (maKM,tenKM,moTa,phanTramGiam,ngayBatDau,ngayKetThuc,maQL) VALUES
('KM001', N'Giảm giá mùa hè', N'Giảm 15% cho hóa đơn từ 300k', 0.15, '2025-06-01', '2025-08-31', 'QL001'),
('KM002', N'Ưu đãi cuối tuần', N'Giảm 10% vào T7-CN', 0.1, '2025-01-01', '2025-12-31', 'QL001');
GO

-- INSERT MONAN (110) (hinhAnh NOT NULL, theo ten mon)
INSERT INTO MONAN (maMon,tenMon,loaiMon,giaMon,hinhAnh,maQL) VALUES
('MA001', N'Lẩu Hải Sản', N'Món Chính', 250000, 'lau_hai_san.jpg', 'QL001'),
('MA002', N'Lẩu Thái', N'Món Chính', 150000, 'lau_thai.jpg', 'QL001'),
('MA003', N'Lẩu Gà Lá É', N'Món Chính', 279000, 'lau_ga_la_e.jpg', 'QL001'),
('MA004', N'Lẩu Cá Kèo', N'Món Chính', 320000, 'lau_ca_keo.jpg', 'QL001'),
('MA005', N'Lẩu Bò Nhúng Dấm', N'Món Chính', 165000, 'lau_bo_nhung_dam.jpg', 'QL001'),
('MA006', N'Phở Bò Đặc Biệt', N'Món Chính', 85000, 'pho_bo_dac_biet.jpg', 'QL001'),
('MA007', N'Bún Chả Hà Nội', N'Món Chính', 120000, 'bun_cha_ha_noi.jpg', 'QL001'),
('MA008', N'Bún Bò Huế', N'Món Chính', 250000, 'bun_bo_hue.jpg', 'QL001'),
('MA009', N'Mì Quảng', N'Món Chính', 180000, 'mi_quang.jpg', 'QL001'),
('MA010', N'Cơm Tấm Sườn Bì Chả', N'Món Chính', 165000, 'com_tam_suon_bi_cha.jpg', 'QL001'),
('MA011', N'Cơm Gà Xối Mỡ', N'Món Chính', 120000, 'com_ga_xoi_mo.jpg', 'QL001'),
('MA012', N'Cơm Chiên Hải Sản', N'Món Chính', 150000, 'com_chien_hai_san.jpg', 'QL001'),
('MA013', N'Cơm Chiên Dương Châu', N'Món Chính', 180000, 'com_chien_duong_chau.jpg', 'QL001'),
('MA014', N'Cơm Niêu Cá Kho Tộ', N'Món Chính', 95000, 'com_nieu_ca_kho_to.jpg', 'QL001'),
('MA015', N'Bò Lúc Lắc', N'Món Chính', 95000, 'bo_luc_lac.jpg', 'QL001'),
('MA016', N'Bò Né', N'Món Chính', 250000, 'bo_ne.jpg', 'QL001'),
('MA017', N'Gà Quay Mật Ong', N'Món Chính', 95000, 'ga_quay_mat_ong.jpg', 'QL001'),
('MA018', N'Gà Chiên Nước Mắm', N'Món Chính', 180000, 'ga_chien_nuoc_mam.jpg', 'QL001'),
('MA019', N'Gà Nướng Muối Ớt', N'Món Chính', 180000, 'ga_nuong_muoi_ot.jpg', 'QL001'),
('MA020', N'Sườn Nướng BBQ', N'Món Chính', 320000, 'suon_nuong_bbq.jpg', 'QL001'),
('MA021', N'Cá Hồi Nướng Sốt Chanh Dây', N'Món Chính', 165000, 'ca_hoi_nuong_sot_chanh_day.jpg', 'QL001'),
('MA022', N'Cá Diêu Hồng Chiên Xù', N'Món Chính', 85000, 'ca_dieu_hong_chien_xu.jpg', 'QL001'),
('MA023', N'Tôm Hấp Bia', N'Món Chính', 279000, 'tom_hap_bia.jpg', 'QL001'),
('MA024', N'Tôm Nướng Muối Ớt', N'Món Chính', 299000, 'tom_nuong_muoi_ot.jpg', 'QL001'),
('MA025', N'Mực Nướng Sa Tế', N'Món Chính', 95000, 'muc_nuong_sa_te.jpg', 'QL001'),
('MA026', N'Mực Chiên Giòn', N'Món Chính', 250000, 'muc_chien_gion.jpg', 'QL001'),
('MA027', N'Bạch Tuộc Nướng', N'Món Chính', 95000, 'bach_tuoc_nuong.jpg', 'QL001'),
('MA028', N'Vịt Quay Bắc Kinh', N'Món Chính', 299000, 'vit_quay_bac_kinh.jpg', 'QL001'),
('MA029', N'Cà Ri Gà', N'Món Chính', 165000, 'ca_ri_ga.jpg', 'QL001'),
('MA030', N'Bò Kho Bánh Mì', N'Món Chính', 450000, 'bo_kho_banh_mi.jpg', 'QL001'),
('MA031', N'Súp Hải Sản', N'Món Chính', 320000, 'sup_hai_san.jpg', 'QL001'),
('MA032', N'Súp Bào Ngư', N'Món Chính', 180000, 'sup_bao_ngu.jpg', 'QL001'),
('MA033', N'Miến Xào Cua', N'Món Chính', 320000, 'mien_xao_cua.jpg', 'QL001'),
('MA034', N'Mì Ý Sốt Cà Chua', N'Món Chính', 150000, 'mi_y_sot_ca_chua.jpg', 'QL001'),
('MA035', N'Mì Ý Sốt Kem', N'Món Chính', 95000, 'mi_y_sot_kem.jpg', 'QL001'),
('MA036', N'Pizza Hải Sản', N'Món Chính', 85000, 'pizza_hai_san.jpg', 'QL001'),
('MA037', N'Pizza Pepperoni', N'Món Chính', 450000, 'pizza_pepperoni.jpg', 'QL001'),
('MA038', N'Bánh Xèo', N'Món Chính', 150000, 'banh_xeo.jpg', 'QL001'),
('MA039', N'Bánh Canh Cua', N'Món Chính', 165000, 'banh_canh_cua.jpg', 'QL001'),
('MA040', N'Cháo Hải Sản', N'Món Chính', 95000, 'chao_hai_san.jpg', 'QL001'),
('MA041', N'Khoai Tây Chiên', N'Món Phụ', 60000, 'khoai_tay_chien.jpg', 'QL001'),
('MA042', N'Khoai Lang Chiên', N'Món Phụ', 45000, 'khoai_lang_chien.jpg', 'QL001'),
('MA043', N'Rau Muống Xào Tỏi', N'Món Phụ', 75000, 'rau_muong_xao_toi.jpg', 'QL001'),
('MA044', N'Cải Thìa Xào Dầu Hào', N'Món Phụ', 65000, 'cai_thia_xao_dau_hao.jpg', 'QL001'),
('MA045', N'Đậu Hũ Tứ Xuyên', N'Món Phụ', 85000, 'dau_hu_tu_xuyen.jpg', 'QL001'),
('MA046', N'Đậu Hũ Chiên Sả Ớt', N'Món Phụ', 70000, 'dau_hu_chien_sa_ot.jpg', 'QL001'),
('MA047', N'Salad Trộn', N'Món Phụ', 50000, 'salad_tron.jpg', 'QL001'),
('MA048', N'Salad Cá Ngừ', N'Món Phụ', 70000, 'salad_ca_ngu.jpg', 'QL001'),
('MA049', N'Gỏi Cuốn Tôm Thịt', N'Món Phụ', 70000, 'goi_cuon_tom_thit.jpg', 'QL001'),
('MA050', N'Chả Giò', N'Món Phụ', 60000, 'cha_gio.jpg', 'QL001'),
('MA051', N'Nem Nướng Nha Trang', N'Món Phụ', 65000, 'nem_nuong_nha_trang.jpg', 'QL001'),
('MA052', N'Gà Rán Giòn', N'Món Phụ', 45000, 'ga_ran_gion.jpg', 'QL001'),
('MA053', N'Cánh Gà Chiên', N'Món Phụ', 50000, 'canh_ga_chien.jpg', 'QL001'),
('MA054', N'Bánh Mì Bơ Tỏi', N'Món Phụ', 110000, 'banh_mi_bo_toi.jpg', 'QL001'),
('MA055', N'Bánh Bao Chiên', N'Món Phụ', 60000, 'banh_bao_chien.jpg', 'QL001'),
('MA056', N'Bắp Xào', N'Món Phụ', 50000, 'bap_xao.jpg', 'QL001'),
('MA057', N'Trứng Chiên Hành', N'Món Phụ', 85000, 'trung_chien_hanh.jpg', 'QL001'),
('MA058', N'Canh Chua Cá', N'Món Phụ', 75000, 'canh_chua_ca.jpg', 'QL001'),
('MA059', N'Canh Rong Biển', N'Món Phụ', 65000, 'canh_rong_bien.jpg', 'QL001'),
('MA060', N'Kim Chi', N'Món Phụ', 110000, 'kim_chi.jpg', 'QL001'),
('MA061', N'Dưa Muối', N'Món Phụ', 60000, 'dua_muoi.jpg', 'QL001'),
('MA062', N'Bánh Tráng Trộn', N'Món Phụ', 70000, 'banh_trang_tron.jpg', 'QL001'),
('MA063', N'Gỏi Tai Heo', N'Món Phụ', 35000, 'goi_tai_heo.jpg', 'QL001'),
('MA064', N'Ốc Xào Sả Ớt', N'Món Phụ', 60000, 'oc_xao_sa_ot.jpg', 'QL001'),
('MA065', N'Sò Điệp Nướng Mỡ Hành', N'Món Phụ', 35000, 'so_diep_nuong_mo_hanh.jpg', 'QL001'),
('MA066', N'Hàu Nướng Phô Mai', N'Món Phụ', 70000, 'hau_nuong_pho_mai.jpg', 'QL001'),
('MA067', N'Súp Lơ Xào Tỏi', N'Món Phụ', 75000, 'sup_lo_xao_toi.jpg', 'QL001'),
('MA068', N'Nấm Xào', N'Món Phụ', 65000, 'nam_xao.jpg', 'QL001'),
('MA069', N'Rau Củ Luộc', N'Món Phụ', 45000, 'rau_cu_luoc.jpg', 'QL001'),
('MA070', N'Xôi Chiên', N'Món Phụ', 60000, 'xoi_chien.jpg', 'QL001'),
('MA071', N'Trà Đào', N'Thức Uống', 150000, 'tra_dao.jpg', 'QL001'),
('MA072', N'Trà Chanh', N'Thức Uống', 50000, 'tra_chanh.jpg', 'QL001'),
('MA073', N'Trà Tắc', N'Thức Uống', 40000, 'tra_tac.jpg', 'QL001'),
('MA074', N'Trà Sữa Trân Châu', N'Thức Uống', 65000, 'tra_sua_tran_chau.jpg', 'QL001'),
('MA075', N'Trà Sữa Matcha', N'Thức Uống', 55000, 'tra_sua_matcha.jpg', 'QL001'),
('MA076', N'Soda Chanh', N'Thức Uống', 65000, 'soda_chanh.jpg', 'QL001'),
('MA077', N'Soda Việt Quất', N'Thức Uống', 35000, 'soda_viet_quat.jpg', 'QL001'),
('MA078', N'Nước Cam Vắt', N'Thức Uống', 45000, 'nuoc_cam_vat.jpg', 'QL001'),
('MA079', N'Nước Ép Dứa', N'Thức Uống', 35000, 'nuoc_ep_dua.jpg', 'QL001'),
('MA080', N'Nước Ép Ổi', N'Thức Uống', 40000, 'nuoc_ep_oi.jpg', 'QL001'),
('MA081', N'Nước Ép Cà Rốt', N'Thức Uống', 120000, 'nuoc_ep_ca_rot.jpg', 'QL001'),
('MA082', N'Sinh Tố Bơ', N'Thức Uống', 120000, 'sinh_to_bo.jpg', 'QL001'),
('MA083', N'Sinh Tố Dâu', N'Thức Uống', 45000, 'sinh_to_dau.jpg', 'QL001'),
('MA084', N'Sinh Tố Xoài', N'Thức Uống', 150000, 'sinh_to_xoai.jpg', 'QL001'),
('MA085', N'Nước Dừa Tươi', N'Thức Uống', 55000, 'nuoc_dua_tuoi.jpg', 'QL001'),
('MA086', N'Cà Phê Đen', N'Thức Uống', 150000, 'ca_phe_den.jpg', 'QL001'),
('MA087', N'Cà Phê Sữa', N'Thức Uống', 55000, 'ca_phe_sua.jpg', 'QL001'),
('MA088', N'Bạc Xỉu', N'Thức Uống', 50000, 'bac_xiu.jpg', 'QL001'),
('MA089', N'Latte', N'Thức Uống', 40000, 'latte.jpg', 'QL001'),
('MA090', N'Cappuccino', N'Thức Uống', 35000, 'cappuccino.jpg', 'QL001'),
('MA091', N'Coca Cola Lon', N'Thức Uống', 120000, 'coca_cola_lon.jpg', 'QL001'),
('MA092', N'Pepsi Lon', N'Thức Uống', 65000, 'pepsi_lon.jpg', 'QL001'),
('MA093', N'Sprite Lon', N'Thức Uống', 30000, 'sprite_lon.jpg', 'QL001'),
('MA094', N'Bia Tiger Lon', N'Thức Uống', 25000, 'bia_tiger_lon.jpg', 'QL001'),
('MA095', N'Bia Heineken Lon', N'Thức Uống', 30000, 'bia_heineken_lon.jpg', 'QL001'),
('MA096', N'Bánh Flan', N'Tráng Miệng', 30000, 'banh_flan.jpg', 'QL001'),
('MA097', N'Kem Vani', N'Tráng Miệng', 50000, 'kem_vani.jpg', 'QL001'),
('MA098', N'Kem Socola', N'Tráng Miệng', 30000, 'kem_socola.jpg', 'QL001'),
('MA099', N'Pudding Trái Cây', N'Tráng Miệng', 60000, 'pudding_trai_cay.jpg', 'QL001'),
('MA100', N'Chè Ba Màu', N'Tráng Miệng', 50000, 'che_ba_mau.jpg', 'QL001'),
('MA101', N'Chè Khúc Bạch', N'Tráng Miệng', 40000, 'che_khuc_bach.jpg', 'QL001'),
('MA102', N'Chè Khoai Môn', N'Tráng Miệng', 45000, 'che_khoai_mon.jpg', 'QL001'),
('MA103', N'Sữa Chua Dẻo', N'Tráng Miệng', 25000, 'sua_chua_deo.jpg', 'QL001'),
('MA104', N'Trái Cây Thập Cẩm', N'Tráng Miệng', 40000, 'trai_cay_thap_cam.jpg', 'QL001'),
('MA105', N'Bánh Gato Kem', N'Tráng Miệng', 40000, 'banh_gato_kem.jpg', 'QL001'),
('MA106', N'Bánh Tiramisu', N'Tráng Miệng', 45000, 'banh_tiramisu.jpg', 'QL001'),
('MA107', N'Bánh Brownie', N'Tráng Miệng', 40000, 'banh_brownie.jpg', 'QL001'),
('MA108', N'Bánh Mochi', N'Tráng Miệng', 45000, 'banh_mochi.jpg', 'QL001'),
('MA109', N'Bánh Cheese Cake', N'Tráng Miệng', 35000, 'banh_cheese_cake.jpg', 'QL001'),
('MA110', N'Rau Câu Dừa', N'Tráng Miệng', 45000, 'rau_cau_dua.jpg', 'QL001');
GO

INSERT INTO CT_BANDAT (maBan, maMon, soLuong) VALUES
('B001','MA001',1),
('B001','MA023',1),
('B001','MA045',1),
('B001','MA067',1),
('B006','MA012',2),
('B006','MA034',2),
('B006','MA056',2),
('B006','MA078',2),
('B011','MA001',3),
('B011','MA023',3),
('B011','MA045',3),
('B011','MA089',3),
('B016','MA012',1),
('B016','MA034',1),
('B016','MA056',1),
('B016','MA100',1),
('B021','MA001',2),
('B021','MA023',2),
('B021','MA045',2),
('B021','MA067',2),
('B026','MA012',3),
('B026','MA034',3),
('B026','MA056',3),
('B026','MA078',3);
GO


-- INSERT BANDAT (50) (đặt bàn thực tế năm 2025)
INSERT INTO BANDAT (maDatBan,maKH,maBan,ngayDat,gioDat,ghiChu,soLuongKhach,tienCoc,trangThai,gioCheckIn) VALUES
('DB001','KH001','B004','2025-01-03','17:30:00',N'Kỷ niệm',3,200000,N'Đã nhận',NULL),
('DB002','KH002','B007','2025-01-05','17:30:00',N'Liên hoan công ty',4,300000,N'Đã nhận',NULL),
('DB003','KH003','B010','2025-01-07','19:30:00',N'Họp lớp',5,400000,N'Đã nhận',NULL),
('DB004','KH004','B013','2025-01-09','18:30:00',N'Gặp mặt gia đình',6,500000,N'Đã nhận',NULL),
('DB005','KH005','B016','2025-02-11','18:30:00',N'Tiếp khách',7,100000,N'Đã nhận',NULL),
('DB006','KH006','B019','2025-02-13','17:30:00',N'Đặt bàn cuối tuần',8,200000,N'Đã nhận',NULL),
('DB007','KH007','B022','2025-02-15','18:30:00',N'Tiệc nhỏ',9,300000,N'Đã hủy',NULL),
('DB008','KH008','B025','2025-02-17','19:00:00',N'Sinh nhật',10,400000,N'Đã nhận',NULL),
('DB009','KH009','B028','2025-03-19','18:00:00',N'Kỷ niệm',11,500000,N'Đã nhận',NULL),
('DB010','KH010','B001','2025-03-21','19:00:00',N'Liên hoan công ty',2,100000,N'Đã nhận',NULL),
('DB011','KH011','B004','2025-03-23','17:30:00',N'Họp lớp',3,200000,N'Đã nhận',NULL),
('DB012','KH012','B007','2025-03-01','18:30:00',N'Gặp mặt gia đình',4,300000,N'Đã nhận',NULL),
('DB013','KH013','B010','2025-04-03','19:30:00',N'Tiếp khách',5,400000,N'Đã nhận',NULL),
('DB014','KH014','B013','2025-04-05','18:00:00',N'Đặt bàn cuối tuần',6,500000,N'Đã hủy',NULL),
('DB015','KH015','B016','2025-04-07','19:30:00',N'Tiệc nhỏ',7,100000,N'Đã nhận',NULL),
('DB016','KH016','B019','2025-04-09','17:30:00',N'Sinh nhật',8,200000,N'Đã nhận',NULL),
('DB017','KH017','B022','2025-05-11','18:30:00',N'Kỷ niệm',9,300000,N'Đã nhận',NULL),
('DB018','KH018','B025','2025-05-13','19:30:00',N'Liên hoan công ty',10,400000,N'Đã nhận',NULL),
('DB019','KH019','B028','2025-05-15','19:30:00',N'Họp lớp',11,500000,N'Đã nhận',NULL),
('DB020','KH020','B001','2025-05-17','18:00:00',N'Gặp mặt gia đình',2,100000,N'Đã nhận',NULL),
('DB021','KH021','B004','2025-06-19','18:00:00',N'Tiếp khách',3,200000,N'Đã hủy',NULL),
('DB022','KH022','B007','2025-06-21','18:30:00',N'Đặt bàn cuối tuần',4,300000,N'Đã nhận',NULL),
('DB023','KH023','B010','2025-06-23','18:00:00',N'Tiệc nhỏ',5,400000,N'Đã nhận',NULL),
('DB024','KH024','B013','2025-06-01','19:30:00',N'Sinh nhật',6,500000,N'Đã nhận',NULL),
('DB025','KH025','B016','2025-07-03','19:30:00',N'Kỷ niệm',7,100000,N'Đã nhận',NULL),
('DB026','KH026','B019','2025-07-05','17:30:00',N'Liên hoan công ty',8,200000,N'Đã nhận',NULL),
('DB027','KH027','B022','2025-07-07','19:30:00',N'Họp lớp',9,300000,N'Đã nhận',NULL),
('DB028','KH028','B025','2025-07-09','18:30:00',N'Gặp mặt gia đình',10,400000,N'Đã hủy',NULL),
('DB029','KH029','B028','2025-08-11','19:00:00',N'Tiếp khách',11,500000,N'Đã nhận',NULL),
('DB030','KH030','B001','2025-08-13','17:30:00',N'Đặt bàn cuối tuần',2,100000,N'Đã nhận',NULL),
('DB031','KH031','B004','2025-08-15','17:30:00',N'Tiệc nhỏ',3,200000,N'Đã nhận',NULL),
('DB032','KH032','B007','2025-08-17','18:30:00',N'Sinh nhật',4,300000,N'Đã nhận',NULL),
('DB033','KH033','B010','2025-09-19','18:30:00',N'Kỷ niệm',5,400000,N'Đã nhận',NULL),
('DB034','KH034','B013','2025-09-21','18:00:00',N'Liên hoan công ty',6,500000,N'Đã nhận',NULL),
('DB035','KH035','B016','2025-09-23','17:30:00',N'Họp lớp',7,100000,N'Đã hủy',NULL),
('DB036','KH036','B019','2025-09-01','18:00:00',N'Gặp mặt gia đình',8,200000,N'Đã nhận',NULL),
('DB037','KH037','B022','2025-10-03','19:30:00',N'Tiếp khách',9,300000,N'Đã nhận',NULL),
('DB038','KH038','B025','2025-10-05','17:30:00',N'Đặt bàn cuối tuần',10,400000,N'Đã nhận',NULL),
('DB039','KH039','B028','2025-10-07','17:30:00',N'Tiệc nhỏ',11,500000,N'Đã nhận',NULL),
('DB040','KH040','B001','2025-10-09','19:00:00',N'Sinh nhật',2,100000,N'Đã nhận',NULL),
('DB041','KH041','B004','2025-11-11','17:30:00',N'Kỷ niệm',3,200000,N'Đã nhận',NULL),
('DB042','KH042','B007','2025-11-13','19:30:00',N'Liên hoan công ty',4,300000,N'Đã hủy',NULL),
('DB043','KH043','B010','2025-11-15','18:00:00',N'Họp lớp',5,400000,N'Đã nhận',NULL),
('DB044','KH044','B013','2025-11-17','18:00:00',N'Gặp mặt gia đình',6,500000,N'Đã nhận',NULL),
('DB045','KH045','B016','2025-12-19','19:00:00',N'Tiếp khách',7,100000,N'Đã nhận',NULL),
('DB046','KH046','B019','2025-12-21','19:30:00',N'Đặt bàn cuối tuần',8,200000,N'Đã nhận',NULL),
('DB047','KH047','B022','2025-12-23','18:00:00',N'Tiệc nhỏ',9,300000,N'Đã nhận',NULL),
('DB048','KH048','B025','2025-12-01','18:30:00',N'Sinh nhật',10,400000,N'Đã nhận',NULL),
('DB049','KH049','B028','2025-01-03','19:30:00',N'Kỷ niệm',11,500000,N'Đã hủy',NULL),
('DB050','KH050','B001','2025-01-05','19:30:00',N'Liên hoan công ty',2,100000,N'Đã nhận',NULL);
GO


-- UPDATE: mỗi khách = 1 thẻ thành viên, KHÔNG khách vãng lai
INSERT INTO HOADON (maHD,maThe,maNV,maBan,maDatBan,maKM,ngayLap,tongTien,trangThaiThanhToan) VALUES
('HD001','TV002','NV002','B002',NULL,NULL,'2025-01-02 18:00:00',730000,N'Đã thanh toán'),
('HD002','TV003','NV003','B003','DB003',NULL,'2025-01-05 18:00:00',300000,N'Đã thanh toán'),
('HD003','TV004','NV004','B004',NULL,NULL,'2025-01-08 20:00:00',420000,N'Đã thanh toán'),
('HD004','TV005','NV005','B005','DB005',NULL,'2025-01-11 18:15:00',560000,N'Đã thanh toán'),
('HD005','TV006','NV006','B006',NULL,NULL,'2025-01-14 11:00:00',1040000,N'Đã thanh toán'),
('HD006','TV007','NV007','B007','DB007',NULL,'2025-01-17 12:15:00',150000,N'Đã thanh toán'),
('HD007','TV008','NV008','B008',NULL,NULL,'2025-01-20 20:00:00',480000,N'Đã thanh toán'),
('HD008','TV009','NV009','B009','DB009',NULL,'2025-01-23 18:30:00',225000,N'Đã thanh toán'),
('HD009','TV010','NV010','B010',NULL,NULL,'2025-01-26 18:15:00',270000,N'Đã thanh toán'),
('HD010','TV011','NV001','B011','DB011',NULL,'2025-01-28 18:15:00',405000,N'Đã thanh toán'),
('HD011','TV012','NV002','B012',NULL,NULL,'2025-02-02 19:00:00',565000,N'Đã thanh toán'),
('HD012','TV013','NV003','B013','DB013',NULL,'2025-02-05 11:15:00',490000,N'Đã thanh toán'),
('HD013','TV014','NV004','B014',NULL,NULL,'2025-02-08 19:15:00',220000,N'Đã thanh toán'),
('HD014','TV015','NV005','B015','DB015',NULL,'2025-02-11 19:00:00',370000,N'Đã thanh toán'),
('HD015','TV016','NV006','B016',NULL,NULL,'2025-02-14 18:00:00',390000,N'Đã thanh toán'),
('HD016','TV017','NV007','B017','DB017',NULL,'2025-02-17 18:30:00',550000,N'Đã thanh toán'),
('HD017','TV018','NV008','B018',NULL,NULL,'2025-02-20 20:00:00',330000,N'Đã thanh toán'),
('HD018','TV019','NV009','B019','DB019',NULL,'2025-02-23 20:15:00',650000,N'Đã thanh toán'),
('HD019','TV020','NV010','B020',NULL,NULL,'2025-02-26 11:00:00',360000,N'Đã thanh toán'),
('HD020','TV021','NV001','B021','DB021',NULL,'2025-03-02 11:15:00',280000,N'Đã thanh toán'),
('HD021','TV022','NV002','B022',NULL,NULL,'2025-03-05 12:00:00',730000,N'Đã thanh toán'),
('HD022','TV023','NV003','B023','DB023',NULL,'2025-03-08 12:15:00',545000,N'Đã thanh toán'),
('HD023','TV024','NV004','B024',NULL,NULL,'2025-03-11 20:30:00',530000,N'Đã thanh toán'),
('HD024','TV025','NV005','B025','DB025',NULL,'2025-03-14 19:30:00',560000,N'Đã thanh toán'),
('HD025','TV026','NV006','B026',NULL,NULL,'2025-03-17 12:30:00',865000,N'Đã thanh toán'),
('HD026','TV027','NV007','B027','DB027',NULL,'2025-03-20 20:15:00',300000,N'Đã thanh toán'),
('HD027','TV028','NV008','B028',NULL,NULL,'2025-03-23 12:30:00',810000,N'Đã thanh toán'),
('HD028','TV029','NV009','B029','DB029',NULL,'2025-03-26 11:15:00',700000,N'Đã thanh toán'),
('HD029','TV030','NV010','B030',NULL,NULL,'2025-04-02 20:15:00',305000,N'Đã thanh toán'),
('HD030','TV031','NV001','B001','DB031',NULL,'2025-04-05 19:00:00',1000000,N'Đã thanh toán'),
('HD031','TV032','NV002','B002',NULL,NULL,'2025-04-08 18:00:00',320000,N'Đã thanh toán'),
('HD032','TV033','NV003','B003','DB033',NULL,'2025-04-11 12:00:00',550000,N'Đã thanh toán'),
('HD033','TV034','NV004','B004',NULL,NULL,'2025-04-14 18:00:00',660000,N'Đã thanh toán'),
('HD034','TV035','NV005','B005','DB035',NULL,'2025-04-17 11:15:00',320000,N'Đã thanh toán'),
('HD035','TV036','NV006','B006',NULL,NULL,'2025-04-20 18:30:00',110000,N'Đã thanh toán'),
('HD036','TV037','NV007','B007','DB037',NULL,'2025-04-23 12:15:00',330000,N'Đã thanh toán'),
('HD037','TV038','NV008','B008',NULL,NULL,'2025-04-26 20:15:00',635000,N'Đã thanh toán'),
('HD038','TV039','NV009','B009','DB039',NULL,'2025-05-02 12:00:00',340000,N'Đã thanh toán'),
('HD039','TV040','NV010','B010',NULL,NULL,'2025-05-05 20:00:00',720000,N'Đã thanh toán'),
('HD040','TV041','NV001','B011','DB041',NULL,'2025-05-08 20:00:00',435000,N'Đã thanh toán'),
('HD041','TV042','NV002','B012',NULL,NULL,'2025-05-11 20:15:00',860000,N'Đã thanh toán'),
('HD042','TV043','NV003','B013','DB043',NULL,'2025-05-14 12:30:00',880000,N'Đã thanh toán'),
('HD043','TV044','NV004','B014',NULL,NULL,'2025-05-17 20:30:00',700000,N'Đã thanh toán'),
('HD044','TV045','NV005','B015','DB045',NULL,'2025-05-20 11:15:00',1035000,N'Đã thanh toán'),
('HD045','TV046','NV006','B016',NULL,NULL,'2025-05-23 12:15:00',450000,N'Đã thanh toán'),
('HD046','TV047','NV007','B017','DB047',NULL,'2025-05-26 11:00:00',110000,N'Đã thanh toán'),
('HD047','TV048','NV008','B018',NULL,NULL,'2025-06-02 20:30:00',390000,N'Đã thanh toán'),
('HD048','TV049','NV009','B019','DB049',NULL,'2025-06-05 19:30:00',490000,N'Đã thanh toán'),
('HD049','TV050','NV010','B020',NULL,NULL,'2025-06-08 12:15:00',555000,N'Đã thanh toán'),
('HD050','TV051','NV001','B004','DB001',NULL,'2025-06-11 11:30:00',690000,N'Đã thanh toán'),
('HD051','TV052','NV002','B022',NULL,NULL,'2025-06-14 19:15:00',640000,N'Đã thanh toán'),
('HD052','TV053','NV003','B010','DB003',NULL,'2025-06-17 20:15:00',870000,N'Đã thanh toán'),
('HD053','TV054','NV004','B024',NULL,NULL,'2025-06-20 11:15:00',660000,N'Đã thanh toán'),
('HD054','TV055','NV005','B025','DB005',NULL,'2025-06-23 20:15:00',730000,N'Đã thanh toán'),
('HD055','TV056','NV006','B026',NULL,NULL,'2025-06-26 18:00:00',615000,N'Đã thanh toán'),
('HD056','TV057','NV007','B022','DB007',NULL,'2025-07-02 12:00:00',490000,N'Đã thanh toán'),
('HD057','TV058','NV008','B028',NULL,NULL,'2025-07-05 18:30:00',260000,N'Đã thanh toán'),
('HD058','TV059','NV009','B029','DB009',NULL,'2025-07-08 12:15:00',870000,N'Đã thanh toán'),
('HD059','TV060','NV010','B030',NULL,NULL,'2025-07-11 19:00:00',860000,N'Đã thanh toán'),
('HD060','TV001','NV001','B001','DB011',NULL,'2025-07-14 18:30:00',460000,N'Đã thanh toán'),
('HD061','TV002','NV002','B002',NULL,NULL,'2025-07-17 11:15:00',1130000,N'Đã thanh toán'),
('HD062','TV003','NV003','B003','DB013',NULL,'2025-07-20 20:15:00',760000,N'Đã thanh toán'),
('HD063','TV004','NV004','B004',NULL,NULL,'2025-07-23 20:15:00',680000,N'Đã thanh toán'),
('HD064','TV005','NV005','B005','DB015',NULL,'2025-07-26 20:00:00',670000,N'Đã thanh toán'),
('HD065','TV006','NV006','B006',NULL,NULL,'2025-08-02 11:30:00',500000,N'Đã thanh toán'),
('HD066','TV007','NV007','B007','DB017',NULL,'2025-08-05 11:00:00',780000,N'Đã thanh toán'),
('HD067','TV008','NV008','B008',NULL,NULL,'2025-08-08 12:00:00',290000,N'Đã thanh toán'),
('HD068','TV009','NV009','B009','DB019',NULL,'2025-08-11 12:30:00',260000,N'Đã thanh toán'),
('HD069','TV010','NV010','B010',NULL,NULL,'2025-08-14 12:15:00',660000,N'Đã thanh toán'),
('HD070','TV011','NV001','B004','DB021',NULL,'2025-08-17 18:30:00',430000,N'Đã thanh toán'),
('HD071','TV012','NV002','B012',NULL,NULL,'2025-08-20 20:15:00',400000,N'Đã thanh toán'),
('HD072','TV013','NV003','B013','DB023',NULL,'2025-08-23 18:15:00',1310000,N'Đã thanh toán'),
('HD073','TV014','NV004','B014',NULL,NULL,'2025-08-26 18:00:00',340000,N'Đã thanh toán'),
('HD074','TV015','NV005','B015','DB025',NULL,'2025-09-02 18:00:00',550000,N'Đã thanh toán'),
('HD075','TV016','NV006','B016',NULL,NULL,'2025-09-05 20:30:00',1005000,N'Đã thanh toán'),
('HD076','TV017','NV007','B017','DB027',NULL,'2025-09-08 19:00:00',300000,N'Đã thanh toán'),
('HD077','TV018','NV008','B018',NULL,NULL,'2025-09-11 20:30:00',1030000,N'Đã thanh toán'),
('HD078','TV019','NV009','B019','DB029',NULL,'2025-09-14 11:15:00',300000,N'Đã thanh toán'),
('HD079','TV020','NV010','B020',NULL,NULL,'2025-09-17 11:15:00',500000,N'Đã thanh toán'),
('HD080','TV021','NV001','B021','DB031',NULL,'2025-09-20 11:15:00',280000,N'Đã thanh toán'),
('HD081','TV022','NV002','B022',NULL,NULL,'2025-09-23 12:00:00',1040000,N'Đã thanh toán'),
('HD082','TV023','NV003','B023','DB033',NULL,'2025-09-26 18:00:00',630000,N'Đã thanh toán'),
('HD083','TV024','NV004','B024',NULL,NULL,'2025-10-02 20:30:00',660000,N'Đã thanh toán'),
('HD084','TV025','NV005','B025','DB035',NULL,'2025-10-05 19:00:00',260000,N'Đã thanh toán'),
('HD085','TV026','NV006','B026',NULL,NULL,'2025-10-08 20:30:00',250000,N'Đã thanh toán'),
('HD086','TV027','NV007','B027','DB037',NULL,'2025-10-11 12:30:00',245000,N'Đã thanh toán'),
('HD087','TV028','NV008','B028',NULL,NULL,'2025-10-14 11:15:00',420000,N'Đã thanh toán'),
('HD088','TV029','NV009','B029','DB039',NULL,'2025-10-17 18:15:00',230000,N'Đã thanh toán'),
('HD089','TV030','NV010','B030',NULL,NULL,'2025-10-20 20:30:00',460000,N'Đã thanh toán'),
('HD090','TV031','NV001','B001','DB041',NULL,'2025-10-23 11:15:00',920000,N'Đã thanh toán'),
('HD091','TV032','NV002','B002',NULL,NULL,'2025-10-26 18:00:00',1005000,N'Đã thanh toán'),
('HD092','TV033','NV003','B003','DB043',NULL,'2025-11-02 11:15:00',600000,N'Đã thanh toán'),
('HD093','TV034','NV004','B004',NULL,NULL,'2025-11-05 11:30:00',730000,N'Đã thanh toán'),
('HD094','TV035','NV005','B005','DB045',NULL,'2025-11-08 11:00:00',250000,N'Đã thanh toán'),
('HD095','TV036','NV006','B006',NULL,NULL,'2025-11-11 19:00:00',490000,N'Đã thanh toán'),
('HD096','TV037','NV007','B007','DB047',NULL,'2025-11-14 11:00:00',405000,N'Đã thanh toán'),
('HD097','TV038','NV008','B008',NULL,NULL,'2025-11-17 12:30:00',970000,N'Đã thanh toán'),
('HD098','TV039','NV009','B009','DB049',NULL,'2025-11-20 19:00:00',300000,N'Đã thanh toán'),
('HD099','TV040','NV010','B010',NULL,NULL,'2025-11-23 11:15:00',370000,N'Đã thanh toán'),
('HD100','TV041','NV001','B011','DB001',NULL,'2025-11-26 12:30:00',590000,N'Đã thanh toán'),
('HD101','TV042','NV002','B012',NULL,NULL,'2025-12-02 12:00:00',650000,N'Đã thanh toán'),
('HD102','TV043','NV003','B013','DB003',NULL,'2025-12-05 11:15:00',690000,N'Đã thanh toán'),
('HD103','TV044','NV004','B014',NULL,NULL,'2025-12-08 19:30:00',470000,N'Đã thanh toán'),
('HD104','TV045','NV005','B015','DB005',NULL,'2025-12-11 18:30:00',265000,N'Đã thanh toán'),
('HD105','TV046','NV006','B016',NULL,NULL,'2025-12-14 18:30:00',490000,N'Đã thanh toán'),
('HD106','TV047','NV007','B017','DB007',NULL,'2025-12-17 19:15:00',180000,N'Đã thanh toán'),
('HD107','TV048','NV008','B018',NULL,NULL,'2025-12-20 11:00:00',210000,N'Đã thanh toán'),
('HD108','TV049','NV009','B019','DB009',NULL,'2025-12-23 18:00:00',220000,N'Đã thanh toán'),
('HD109','TV050','NV010','B020',NULL,NULL,'2025-12-26 11:00:00',230000,N'Đã thanh toán'),
('HD110','TV051','NV001','B021','DB011',NULL,'2025-12-28 18:30:00',460000,N'Đã thanh toán');
GO


INSERT INTO CT_HOADON (maHD,maMon,soLuong,thanhTien) VALUES
('HD001','MA041',1,50000),
('HD001','MA058',1,120000),
('HD001','MA051',2,60000),
('HD001','MA052',2,500000),
('HD002','MA033',2,240000),
('HD002','MA028',2,60000),
('HD003','MA024',2,60000),
('HD003','MA070',1,180000),
('HD003','MA027',1,180000),
('HD004','MA011',1,250000),
('HD004','MA105',2,60000),
('HD004','MA036',1,250000),
('HD005','MA074',2,60000),
('HD005','MA083',2,360000),
('HD005','MA044',1,120000),
('HD005','MA030',2,500000),
('HD006','MA041',2,100000),
('HD006','MA102',1,50000),
('HD007','MA013',1,180000),
('HD007','MA070',1,180000),
('HD007','MA079',1,120000),
('HD008','MA104',2,160000),
('HD008','MA032',1,65000),
('HD009','MA071',1,50000),
('HD009','MA010',1,40000),
('HD009','MA094',1,180000),
('HD010','MA097',2,240000),
('HD010','MA102',2,100000),
('HD010','MA046',1,65000),
('HD011','MA065',2,500000),
('HD011','MA100',1,65000),
('HD012','MA086',1,180000),
('HD012','MA023',2,60000),
('HD012','MA100',2,130000),
('HD012','MA020',1,120000),
('HD013','MA066',1,30000),
('HD013','MA107',1,40000),
('HD013','MA078',1,120000),
('HD013','MA038',1,30000),
('HD014','MA106',1,40000),
('HD014','MA080',1,250000),
('HD014','MA103',2,80000),
('HD015','MA069',1,80000),
('HD015','MA021',2,60000),
('HD015','MA007',1,250000),
('HD016','MA058',2,240000),
('HD016','MA104',2,160000),
('HD016','MA056',2,100000),
('HD016','MA071',1,50000),
('HD017','MA108',2,60000),
('HD017','MA044',2,240000),
('HD017','MA022',1,30000),
('HD018','MA054',2,360000),
('HD018','MA074',1,30000),
('HD018','MA003',1,80000),
('HD018','MA008',1,180000),
('HD019','MA107',2,80000),
('HD019','MA036',1,250000),
('HD019','MA051',1,30000),
('HD020','MA063',1,180000),
('HD020','MA001',2,100000),
('HD021','MA084',1,50000),
('HD021','MA057',1,120000),
('HD021','MA088',2,500000),
('HD021','MA082',2,60000),
('HD022','MA062',2,100000),
('HD022','MA029',2,130000),
('HD022','MA092',1,250000),
('HD022','MA053',1,65000),
('HD023','MA098',2,500000),
('HD023','MA066',1,30000),
('HD024','MA099',2,60000),
('HD024','MA102',2,100000),
('HD024','MA027',2,360000),
('HD024','MA040',1,40000),
('HD025','MA090',1,65000),
('HD025','MA095',1,120000),
('HD025','MA060',2,500000),
('HD025','MA077',1,180000),
('HD026','MA033',1,120000),
('HD026','MA055',1,180000),
('HD027','MA088',2,500000),
('HD027','MA051',2,60000),
('HD027','MA092',1,250000),
('HD028','MA094',2,360000),
('HD028','MA006',1,250000),
('HD028','MA068',2,60000),
('HD028','MA012',1,30000),
('HD029','MA100',1,65000),
('HD029','MA079',2,240000),
('HD030','MA109',2,500000),
('HD030','MA049',2,500000),
('HD031','MA042',1,80000),
('HD031','MA057',2,240000),
('HD032','MA016',2,500000),
('HD032','MA056',1,50000),
('HD033','MA038',1,30000),
('HD033','MA036',1,250000),
('HD033','MA032',2,130000),
('HD033','MA049',1,250000),
('HD034','MA081',1,80000),
('HD034','MA078',2,240000),
('HD035','MA023',1,30000),
('HD035','MA037',1,80000),
('HD036','MA040',2,80000),
('HD036','MA075',2,130000),
('HD036','MA097',1,120000),
('HD037','MA046',1,65000),
('HD037','MA063',1,180000),
('HD037','MA054',2,360000),
('HD037','MA110',1,30000),
('HD038','MA104',1,80000),
('HD038','MA014',1,180000),
('HD038','MA004',2,80000),
('HD039','MA098',1,250000),
('HD039','MA093',2,240000),
('HD039','MA084',2,100000),
('HD039','MA018',2,130000),
('HD040','MA087',2,60000),
('HD040','MA046',1,65000),
('HD040','MA098',1,250000),
('HD040','MA068',2,60000),
('HD041','MA058',2,240000),
('HD041','MA045',2,360000),
('HD041','MA040',2,80000),
('HD041','MA070',1,180000),
('HD042','MA049',2,500000),
('HD042','MA027',1,180000),
('HD042','MA072',2,100000),
('HD042','MA001',2,100000),
('HD043','MA092',2,500000),
('HD043','MA040',1,40000),
('HD043','MA090',2,130000),
('HD043','MA022',1,30000),
('HD044','MA050',2,360000),
('HD044','MA075',1,65000),
('HD044','MA055',2,360000),
('HD044','MA052',1,250000),
('HD045','MA084',2,100000),
('HD045','MA038',1,30000),
('HD045','MA081',2,160000),
('HD045','MA003',2,160000),
('HD046','MA099',1,30000),
('HD046','MA010',2,80000),
('HD047','MA103',2,80000),
('HD047','MA091',1,180000),
('HD047','MA053',2,130000),
('HD048','MA063',1,180000),
('HD048','MA022',2,60000),
('HD048','MA060',1,250000),
('HD049','MA076',1,65000),
('HD049','MA055',2,360000),
('HD049','MA009',1,65000),
('HD049','MA046',1,65000),
('HD050','MA091',2,360000),
('HD050','MA021',2,60000),
('HD050','MA089',2,240000),
('HD050','MA012',1,30000),
('HD051','MA027',1,180000),
('HD051','MA031',1,250000),
('HD051','MA043',2,80000),
('HD051','MA035',2,130000),
('HD052','MA072',2,100000),
('HD052','MA095',2,240000),
('HD052','MA007',2,500000),
('HD052','MA022',1,30000),
('HD053','MA072',2,100000),
('HD053','MA052',2,500000),
('HD053','MA023',2,60000),
('HD054','MA029',1,65000),
('HD054','MA034',1,65000),
('HD054','MA079',2,240000),
('HD054','MA091',2,360000),
('HD055','MA098',2,500000),
('HD055','MA032',1,65000),
('HD055','MA101',1,50000),
('HD056','MA094',1,180000),
('HD056','MA022',2,60000),
('HD056','MA075',2,130000),
('HD056','MA057',1,120000),
('HD057','MA100',2,130000),
('HD057','MA018',2,130000),
('HD058','MA097',1,120000),
('HD058','MA052',1,250000),
('HD058','MA031',2,500000),
('HD059','MA014',2,360000),
('HD059','MA030',2,500000),
('HD060','MA013',1,180000),
('HD060','MA024',1,30000),
('HD060','MA006',1,250000),
('HD061','MA005',2,500000),
('HD061','MA064',2,240000),
('HD061','MA091',2,360000),
('HD061','MA068',1,30000),
('HD062','MA089',2,240000),
('HD062','MA023',1,30000),
('HD062','MA013',2,360000),
('HD062','MA029',2,130000),
('HD063','MA097',1,120000),
('HD063','MA022',2,60000),
('HD063','MA030',2,500000),
('HD064','MA075',2,130000),
('HD064','MA050',2,360000),
('HD064','MA028',2,60000),
('HD064','MA058',1,120000),
('HD065','MA011',1,250000),
('HD065','MA006',1,250000),
('HD066','MA041',2,100000),
('HD066','MA050',1,180000),
('HD066','MA109',2,500000),
('HD067','MA106',1,40000),
('HD067','MA098',1,250000),
('HD068','MA050',1,180000),
('HD068','MA019',2,80000),
('HD069','MA017',2,160000),
('HD069','MA011',1,250000),
('HD069','MA060',1,250000),
('HD070','MA008',1,180000),
('HD070','MA068',2,60000),
('HD070','MA108',1,30000),
('HD070','MA017',2,160000),
('HD071','MA025',2,360000),
('HD071','MA004',1,40000),
('HD072','MA036',1,250000),
('HD072','MA088',2,500000),
('HD072','MA105',2,60000),
('HD072','MA109',2,500000),
('HD073','MA035',1,65000),
('HD073','MA034',1,65000),
('HD073','MA083',1,180000),
('HD073','MA082',1,30000),
('HD074','MA055',1,180000),
('HD074','MA078',2,240000),
('HD074','MA090',2,130000),
('HD075','MA026',1,65000),
('HD075','MA092',2,500000),
('HD075','MA069',1,80000),
('HD075','MA055',2,360000),
('HD076','MA013',1,180000),
('HD076','MA020',1,120000),
('HD077','MA110',1,30000),
('HD077','MA006',2,500000),
('HD077','MA007',2,500000),
('HD078','MA041',1,50000),
('HD078','MA006',1,250000),
('HD079','MA086',2,360000),
('HD079','MA017',1,80000),
('HD079','MA051',2,60000),
('HD080','MA033',2,240000),
('HD080','MA103',1,40000),
('HD081','MA005',2,500000),
('HD081','MA050',2,360000),
('HD081','MA008',1,180000),
('HD082','MA102',1,50000),
('HD082','MA049',2,500000),
('HD082','MA104',1,80000),
('HD083','MA108',1,30000),
('HD083','MA032',2,130000),
('HD083','MA065',2,500000),
('HD084','MA101',1,50000),
('HD084','MA051',1,30000),
('HD084','MA075',2,130000),
('HD084','MA062',1,50000),
('HD085','MA096',2,60000),
('HD085','MA021',2,60000),
('HD085','MA026',2,130000),
('HD086','MA053',1,65000),
('HD086','MA045',1,180000),
('HD087','MA039',2,360000),
('HD087','MA105',2,60000),
('HD088','MA041',2,100000),
('HD088','MA046',1,65000),
('HD088','MA035',1,65000),
('HD089','MA041',2,100000),
('HD089','MA094',2,360000),
('HD090','MA009',2,130000),
('HD090','MA058',2,240000),
('HD090','MA036',2,500000),
('HD090','MA062',1,50000),
('HD091','MA103',2,80000),
('HD091','MA008',2,360000),
('HD091','MA018',1,65000),
('HD091','MA007',2,500000),
('HD092','MA103',2,80000),
('HD092','MA083',2,360000),
('HD092','MA048',2,160000),
('HD093','MA044',1,120000),
('HD093','MA069',1,80000),
('HD093','MA065',2,500000),
('HD093','MA022',1,30000),
('HD094','MA018',2,130000),
('HD094','MA015',1,30000),
('HD094','MA024',1,30000),
('HD094','MA099',2,60000),
('HD095','MA014',1,180000),
('HD095','MA027',1,180000),
('HD095','MA034',1,65000),
('HD095','MA009',1,65000),
('HD096','MA056',2,100000),
('HD096','MA003',2,160000),
('HD096','MA076',1,65000),
('HD096','MA048',1,80000),
('HD097','MA064',2,240000),
('HD097','MA031',1,250000),
('HD097','MA055',2,360000),
('HD097','MA058',1,120000),
('HD098','MA053',2,130000),
('HD098','MA026',2,130000),
('HD098','MA002',1,40000),
('HD099','MA079',2,240000),
('HD099','MA066',1,30000),
('HD099','MA102',2,100000),
('HD100','MA001',1,50000),
('HD100','MA025',1,180000),
('HD100','MA039',2,360000),
('HD101','MA096',2,60000),
('HD101','MA041',2,100000),
('HD101','MA100',2,130000),
('HD101','MA070',2,360000),
('HD102','MA039',2,360000),
('HD102','MA017',1,80000),
('HD102','MA065',1,250000),
('HD103','MA082',1,30000),
('HD103','MA002',2,80000),
('HD103','MA055',2,360000),
('HD104','MA037',1,80000),
('HD104','MA085',1,65000),
('HD104','MA097',1,120000),
('HD105','MA050',2,360000),
('HD105','MA035',2,130000),
('HD106','MA082',2,60000),
('HD106','MA096',2,60000),
('HD106','MA110',2,60000),
('HD107','MA103',2,80000),
('HD107','MA015',1,30000),
('HD107','MA062',2,100000),
('HD108','MA003',2,160000),
('HD108','MA023',2,60000),
('HD109','MA076',2,130000),
('HD109','MA101',2,100000),
('HD110','MA066',2,60000),
('HD110','MA037',2,160000),
('HD110','MA095',2,240000);
GO


-- INSERT BAOCAO (12 tháng năm 2025)
INSERT INTO BAOCAO (maBC,ngayLap,thoiGianTu,thoiGianDen,doanhThu,maQL) VALUES
('BC001','2025-01-28','2025-01-01','2025-01-28',2934000,'QL001'),
('BC002','2025-02-28','2025-02-01','2025-02-28',4685000,'QL001'),
('BC003','2025-03-28','2025-03-01','2025-03-28',2654000,'QL001'),
('BC004','2025-04-28','2025-04-01','2025-04-28',4103000,'QL001'),
('BC005','2025-05-28','2025-05-01','2025-05-28',2140000,'QL001'),
('BC006','2025-06-28','2025-06-01','2025-06-28',3189000,'QL001'),
('BC007','2025-07-28','2025-07-01','2025-07-28',2900000,'QL001'),
('BC008','2025-08-28','2025-08-01','2025-08-28',3204000,'QL001'),
('BC009','2025-09-28','2025-09-01','2025-09-28',5157000,'QL001'),
('BC010','2025-10-28','2025-10-01','2025-10-28',2489000,'QL001'),
('BC011','2025-11-28','2025-11-01','2025-11-28',3574000,'QL001'),
('BC012','2025-12-28','2025-12-01','2025-12-28',2500000,'QL001');
GO

-- FIX: Đồng bộ maBan trong HOADON theo BANDAT
UPDATE H
SET H.maBan = B.maBan
FROM HOADON H
JOIN BANDAT B ON H.maDatBan = B.maDatBan
WHERE H.maDatBan IS NOT NULL;
GO
