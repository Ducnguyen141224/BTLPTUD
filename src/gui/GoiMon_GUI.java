package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// Import các lớp cần thiết để làm việc với CSDL
import connectDB.ConnectDB;
import dao.MonAn_DAO;
import dao.TheThanhVien_DAO;
import dao.BanDat_DAO;
import dao.Ban_DAO;
import dao.CT_BanDat_DAO;
import dao.KhachHang_DAO;
import entity.MonAn;
import entity.CTBanDat;
import entity.KhachHang;
import entity.Ban;
import entity.BanDat;
 import entity.NhanVien;
import entity.TheThanhVien;
import gui.ThanhToan_Gui; 
 import gui.DangNhap_GUI; 


public class GoiMon_GUI extends JPanel {
    private NumberFormat dinhDangTien;
    
    private String loaiMonDangChon = "Tất cả"; 

    private JPanel pnlDanhSachMon;
    private JTextField txtTimKiem;
    private JTable tblMonDangGoi;
    private DefaultTableModel modelMonDangGoi;
    private JLabel lblTongTienDangGoi;
    private Map<String, Integer> gioHang;
    private Map<String, Integer> bangGia;
    private double tongTien = 0;
    private JLabel lblTongTien;
    private JTable tblMonDaGoi;
    private DefaultTableModel modelMonDaGoi;
    private Map<String, Integer> gioHangXacNhan;
    private double tongTienHoaDon = 0;
    private JTextArea txtGhiChu;
    private JLabel lblTieuDeDangGoi;
    private JLabel lblTieuDeDaGoi;
    private MonAn_DAO monAn_DAO;
    private CT_BanDat_DAO ctBanDat_DAO;
    private List<MonAn> danhSachMonAn;
    private List<MonAn> danhSachMonAnHienThi;
    private String maBanHienTai;
    private LocalTime gioVao;
    private TheThanhVien theThanhVienDangChon = null;
    private BanDat_DAO banDatDAO = new BanDat_DAO();
    private static final String[] COT_DANG_GOI = {"STT", "Tên món", "SL", "Giá", "Thao tác"};
    private static final String[] COT_DA_GOI = {"STT", "Tên món", "SL", "Giá"};

    public GoiMon_GUI(String maBan) throws SQLException {
        this.maBanHienTai = maBan;
        BanDat bd = banDatDAO.getBanDatDangSuDung(maBan);
        if (bd != null && bd.getGioCheckIn() != null) {
            this.gioVao = bd.getGioCheckIn();
        } else {
            this.gioVao = LocalTime.now();
        }
        
        dinhDangTien = NumberFormat.getInstance(new Locale("vi", "VN"));

        gioHang = new LinkedHashMap<>();
        bangGia = new HashMap<>();
        gioHangXacNhan = new LinkedHashMap<>();

        modelMonDangGoi = new DefaultTableModel(COT_DANG_GOI, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 2 || c == 4; } 
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class; 
                return super.getColumnClass(columnIndex);
            }
        };
        modelMonDaGoi = new DefaultTableModel(COT_DA_GOI, 0) {
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        
        lblTongTienDangGoi = new JLabel(dinhDangTien.format(0) + " VND");
        lblTieuDeDangGoi = new JLabel("Món đang gọi (0)");
        lblTongTien = new JLabel(dinhDangTien.format(0) + " VND");
        lblTieuDeDaGoi = new JLabel("Món đã gọi (0)"); 


        monAn_DAO = new MonAn_DAO();
        ctBanDat_DAO = new CT_BanDat_DAO(); 
        ConnectDB.getConnection(); 
        
        taiDuLieuMonAn();
        khoiTaoGia(); 
        taiDuLieuDaGoi(); 

        setLayout(new BorderLayout());
        setBackground(new Color(255, 235, 205));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(255, 218, 185));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lblBack = new JLabel("←");
        lblBack.setFont(new Font("Arial", Font.BOLD, 20));
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(GoiMon_GUI.this);
                if (parent != null) {
                    ConnectDB.disconnect(); 
                    parent.dispose();
                }
            }
        });
        
        tieuDe.add(lblBack, BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Gọi món - Bàn: " + maBanHienTai, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        tieuDe.add(lblTitle, BorderLayout.CENTER);
        add(tieuDe, BorderLayout.NORTH);

        JPanel pnlChinh = new JPanel(new BorderLayout(12, 12));
        pnlChinh.setBackground(new Color(245, 245, 245));
        pnlChinh.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlTrai = taoKhuVucMenu();
        JPanel pnlPhai = taoKhuVucGioHang();

        pnlChinh.add(pnlTrai, BorderLayout.CENTER);
        pnlChinh.add(pnlPhai, BorderLayout.EAST);

        add(pnlChinh, BorderLayout.CENTER);
    }



    public void setMaBanHienTai(String maBan) {
        this.maBanHienTai = maBan;
        
        gioHangXacNhan.clear();
        gioHang.clear();
        
        taiDuLieuDaGoi();
        
        capNhatBangGio();
        txtGhiChu.setText("");
        
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComps = panel.getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getText().startsWith("Gọi món - Bàn:")) {
                            label.setText("Gọi món - Bàn: " + maBanHienTai);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public String getMaBanHienTai() {
        return maBanHienTai;
    }

    private void taiDuLieuMonAn() {
        try {
            danhSachMonAn = monAn_DAO.layTatCaMonAn();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách món ăn từ CSDL: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            danhSachMonAn = new ArrayList<>(); 
        }
        danhSachMonAnHienThi = new ArrayList<>(danhSachMonAn);
    }

    private void khoiTaoGia() {
        bangGia.clear();
        if (danhSachMonAn != null) {
            for (MonAn mon : danhSachMonAn) {
                bangGia.put(mon.getTenMonAn(), (int)mon.getGiaMonAn());
            }
        }
    }

    private void taiDuLieuDaGoi() {
        Map<String, Integer> data = ctBanDat_DAO.layCTBan(maBanHienTai);
        
        gioHangXacNhan.clear();
        if (data != null) {
            gioHangXacNhan.putAll(data);
        }
        capNhatBangDaGoi();
    }

    private void locMonNavbar(String loaiChon) {
        if (loaiChon != null) {
            this.loaiMonDangChon = loaiChon; 
        }
        
        danhSachMonAnHienThi.clear();
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        
        for (MonAn mon : danhSachMonAn) {
            boolean thoaManLoai = "Tất cả".equals(loaiMonDangChon) || mon.getLoaiMonAn().equals(loaiMonDangChon);
            boolean thoaManTimKiem = mon.getTenMonAn().toLowerCase().contains(tuKhoa);
            
            if (thoaManLoai && thoaManTimKiem) {
                danhSachMonAnHienThi.add(mon);
            }
        }
        hienThiMonAn(danhSachMonAnHienThi);
    }

    private void timMon() {
        locMonNavbar(null); 
    }

    private void themVaoGio(String ten, int gia, int sl) {
        if (gioHang.containsKey(ten))
            gioHang.put(ten, gioHang.get(ten) + sl);
        else
            gioHang.put(ten, sl);
        capNhatBangGio();
        JOptionPane.showMessageDialog(this, "Đã thêm " + sl + " " + ten + " vào giỏ hàng");
    }
    private void capNhatChiTietTongTien() {
        tongTien = 0;
        for (Map.Entry<String, Integer> e : gioHang.entrySet()) {
            String ten = e.getKey();
            int sl = e.getValue();
            int gia = bangGia.getOrDefault(ten, 0);
            tongTien += (long) gia * sl;
        }
        lblTongTienDangGoi.setText(dinhDangTien.format(tongTien) + " VND");
        lblTieuDeDangGoi.setText("Món đang gọi (" + gioHang.size() + ")");
    }
    private void capNhatBangGio() {
        modelMonDangGoi.setRowCount(0); // Hàm này chỉ dùng khi thêm/xóa món
        int stt = 1;
        
        for (Map.Entry<String, Integer> e : gioHang.entrySet()) {
            String ten = e.getKey();
            int sl = e.getValue();
            int gia = bangGia.getOrDefault(ten, 0);
            
            // Cột 2 là số lượng, cột 3 là thành tiền
            modelMonDangGoi.addRow(new Object[]{stt++, ten, sl, dinhDangTien.format((long)gia * sl) + " VND", "Xóa"});
        }
        capNhatChiTietTongTien(); // Gọi hàm tính tổng
    }

    private void xoaKhoiGio(String ten) {
        gioHang.remove(ten);
        capNhatBangGio();
    }
    
    private void huyDon() {
        if (gioHang.isEmpty()) {
              JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng, không có gì để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
              return;
        }

        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy đơn đang gọi?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gioHang.clear();
            capNhatBangGio();
            JOptionPane.showMessageDialog(this, "Đơn hàng đang gọi đã được hủy.");
        }
    }

    private void xacNhanDon() {
        if (gioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng, không có gì để xác nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Map<String, MonAn> mapTenToMon = new HashMap<>();
            for(MonAn mon : danhSachMonAn) {
                mapTenToMon.put(mon.getTenMonAn(), mon);
            }
            for (Map.Entry<String, Integer> entry : gioHang.entrySet()) {
                gioHangXacNhan.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }

            ctBanDat_DAO.xoaTatCaCTBan(maBanHienTai); 
            Ban banDatGoc = new Ban(maBanHienTai);           
            for (Map.Entry<String, Integer> entry : gioHangXacNhan.entrySet()) {
                MonAn mon = mapTenToMon.get(entry.getKey());
                if (mon != null) {
                    CTBanDat ct = new CTBanDat(banDatGoc, mon, entry.getValue());
                    ctBanDat_DAO.themCTBan(ct);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu chi tiết đơn hàng vào CSDL: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        gioHang.clear();
        capNhatBangGio(); 
        
        capNhatBangDaGoi(); 
        txtGhiChu.setText("");

        JOptionPane.showMessageDialog(this, "Đã xác nhận đơn hàng! Các món đã được lưu vào CSDL.");
    }

    public void lamMoiSauThanhToan() {
        try {
            ctBanDat_DAO.xoaTatCaCTBan(maBanHienTai); 
            Ban_DAO banDAO_temp = new Ban_DAO(); 
            if (banDAO_temp.updateTrangThaiBan(maBanHienTai, "Trống")) {
            } else {
                 JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái bàn về Trống.", "Lỗi CSDL", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL khi làm mới sau thanh toán: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
        
        gioHangXacNhan.clear(); 
        tongTienHoaDon = 0;
        modelMonDaGoi.setRowCount(0); 
        lblTongTien.setText(dinhDangTien.format(tongTienHoaDon) + " VND"); 
        lblTieuDeDaGoi.setText("Món đã gọi (0)"); 
        gioHang.clear(); 
        capNhatBangGio(); 
        txtGhiChu.setText("");
        JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán và bàn [" + maBanHienTai + "] đã được dọn!", "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void thanhToan() {
        if (gioHangXacNhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có món nào được xác nhận để thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        if (parentFrame == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy cửa sổ cha.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ============================
        // LẤY KHÁCH HÀNG + THẺ TV (Giữ nguyên logic cũ của bạn)
        // ============================
        KhachHang kh = null;
        this.theThanhVienDangChon = null;

        KhachHang_DAO khDAO = new KhachHang_DAO();
        TheThanhVien_DAO ttvDAO = new TheThanhVien_DAO();

        BanDat bd = banDatDAO.getBanDatDangSuDung(maBanHienTai);

        try {
            boolean laKhachTrucTiep = (bd == null) || "Khách vào trực tiếp".equalsIgnoreCase(bd.getGhiChu());

            if (!laKhachTrucTiep && bd.getKhachHang() != null) {
                kh = bd.getKhachHang();
                this.theThanhVienDangChon = ttvDAO.layTheTheoMaKH(kh.getMaKH());

                if (this.theThanhVienDangChon == null) {
                    String maTheMoi = ttvDAO.phatSinhMaThe();
                    ttvDAO.themTheThanhVien(new TheThanhVien(maTheMoi, new KhachHang(kh.getMaKH()), 0, "Bạc"));
                    this.theThanhVienDangChon = ttvDAO.layTheTheoMaThe(maTheMoi);
                }
            } else {
                String sdt = JOptionPane.showInputDialog(this, "Nhập SĐT để tích điểm (bỏ trống nếu bỏ qua):", "Xác thực tích điểm", JOptionPane.QUESTION_MESSAGE);
                if (sdt != null && !sdt.trim().isEmpty()) {
                    sdt = sdt.trim();
                    kh = khDAO.timKhachHangTheoSDT(sdt);
                    if (kh == null) {
                        String ten = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:", "Khách mới", JOptionPane.QUESTION_MESSAGE);
                        if (ten == null || ten.trim().isEmpty()) ten = "Khách mới";
                        KhachHang khMoi = new KhachHang(null, ten, sdt, "", false);
                        khMoi = khDAO.themHoacLayKhachHang(khMoi);
                        String maTheMoi = ttvDAO.phatSinhMaThe();
                        ttvDAO.themTheThanhVien(new TheThanhVien(maTheMoi, new KhachHang(khMoi.getMaKH()), 0, "Bạc"));
                        this.theThanhVienDangChon = ttvDAO.layTheTheoMaThe(maTheMoi);
                        kh = khMoi;
                    } else {
                        this.theThanhVienDangChon = ttvDAO.layTheTheoMaKH(kh.getMaKH());
                        if (this.theThanhVienDangChon == null) {
                            String maTheMoi = ttvDAO.phatSinhMaThe();
                            ttvDAO.themTheThanhVien(new TheThanhVien(maTheMoi, new KhachHang(kh.getMaKH()), 0, "Bạc"));
                            this.theThanhVienDangChon = ttvDAO.layTheTheoMaThe(maTheMoi);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xử lý khách hàng/thẻ TV:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ============================
        // TẠO CỬA SỔ THANH TOÁN
        // ============================
        parentFrame.setVisible(false);
        GoiMon_GUI currentGui = this; 
        double tienCoc = 0;
        try {
            tienCoc = banDatDAO.getTienCocByActiveMaBan(this.maBanHienTai);            
        } catch (Exception e_sql) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải tiền cọc: " + e_sql.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
      
        JFrame thanhToanFrame = new JFrame("Thanh toán");
        thanhToanFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        thanhToanFrame.setSize(1000, 750);
        thanhToanFrame.setLocationRelativeTo(null);
        
        try {
             // Tạo panel thanh toán
             ThanhToan_Gui thanhToanPanel = new ThanhToan_Gui(
                 gioHangXacNhan, 
                 bangGia, 
                 tinhTongTienGioHang(),
                 tienCoc,
                 maBanHienTai,
                 DangNhap_GUI.taiKhoanDangNhap.getNhanVien(), 
                 this.gioVao,
                 this.theThanhVienDangChon
             );
             thanhToanFrame.setContentPane(thanhToanPanel);

             // --- SỬA LỖI Ở ĐÂY: DI CHUYỂN SỰ KIỆN WINDOW CLOSED VÀO ĐÂY ---
             // Để có thể truy cập biến thanhToanPanel và kiểm tra trạng thái
             thanhToanFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    parentFrame.setVisible(true);
                    parentFrame.toFront();
                    
                    // CHỈ DỌN BÀN NẾU ĐÃ THANH TOÁN XONG
                    if (thanhToanPanel.isDaThanhToanXong()) {
                        currentGui.lamMoiSauThanhToan(); 
                    }
                }
            });

        } catch (Exception e) {
              JOptionPane.showMessageDialog(this, "Lỗi khi khởi tạo màn hình Thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
              parentFrame.setVisible(true); 
              return;
        }

        thanhToanFrame.setVisible(true);
    }

    private void capNhatBangDaGoi() {
        modelMonDaGoi.setRowCount(0);
        int stt = 1;
        tongTienHoaDon = 0;
        
        for (Map.Entry<String, Integer> e : gioHangXacNhan.entrySet()) {
            String ten = e.getKey();
            int sl = e.getValue();
            if (bangGia.containsKey(ten)) {
                int gia = bangGia.get(ten);
                long thanhTien = (long)gia * sl;
                
                modelMonDaGoi.addRow(new Object[]{stt++, ten, sl, dinhDangTien.format(thanhTien) + " VND"});
                tongTienHoaDon += thanhTien;
            }
        }
        lblTieuDeDaGoi.setText("Món đã gọi (" + gioHangXacNhan.size() + ")");
        lblTongTien.setText(dinhDangTien.format(tongTienHoaDon) + " VND");
    }
    
    // START: Sửa Navbar
    private JPanel taoKhuVucMenu() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JPanel pnlLoaiMonNavbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlLoaiMonNavbar.setBackground(Color.WHITE);
        pnlLoaiMonNavbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); 

        List<String> loaiMonCSDL = new ArrayList<>();
        try {
            loaiMonCSDL = monAn_DAO.layDanhSachLoaiMon();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> dsLoaiMoi = new ArrayList<>();
        dsLoaiMoi.add("Tất cả");
        dsLoaiMoi.addAll(loaiMonCSDL);
        
        List<JButton> listButtons = new ArrayList<>();
        
        for (String loai : dsLoaiMoi) {
            JButton btnLoai = new JButton(loai);
            btnLoai.setFont(new Font("Arial", Font.BOLD, 12));
            btnLoai.setFocusPainted(false);
            btnLoai.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            btnLoai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (loai.equals("Tất cả")) {
                btnLoai.setBackground(new Color(40, 167, 69)); 
                btnLoai.setForeground(Color.WHITE);
            } else {
                btnLoai.setBackground(new Color(240, 240, 240)); 
                btnLoai.setForeground(Color.BLACK);
            }
            
            btnLoai.addActionListener(e -> {
                for(JButton btn : listButtons) {
                    if (btn == btnLoai) {
                        btn.setBackground(new Color(40, 167, 69)); 
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(new Color(240, 240, 240)); 
                        btn.setForeground(Color.BLACK);
                    }
                }
                locMonNavbar(loai); 
            });
            
            listButtons.add(btnLoai);
            pnlLoaiMonNavbar.add(btnLoai);
        }
        
        JPanel pnlTim = new JPanel(new BorderLayout(4, 0));
        pnlTim.setBackground(Color.WHITE);
        pnlTim.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        pnlTim.setPreferredSize(new Dimension(300, 34));
        txtTimKiem = new JTextField();
        txtTimKiem.setBorder(BorderFactory.createEmptyBorder(4,8,4,4));
        JButton btnTim = new JButton("🔍");
        btnTim.setBorder(BorderFactory.createEmptyBorder());
        btnTim.setFocusPainted(false);
        btnTim.setBackground(Color.WHITE);
        btnTim.addActionListener(e -> timMon());

        pnlTim.add(txtTimKiem, BorderLayout.CENTER);
        pnlTim.add(btnTim, BorderLayout.EAST);
        
        JPanel pnlTop = new JPanel(new BorderLayout(8, 0));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(pnlLoaiMonNavbar, BorderLayout.WEST);
        pnlTop.add(pnlTim, BorderLayout.EAST);
        
        pnlDanhSachMon = new JPanel(new GridLayout(0, 3, 10, 12));
        pnlDanhSachMon.setBackground(Color.WHITE);
        pnlDanhSachMon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        hienThiMonAn(danhSachMonAnHienThi); 

        JScrollPane cuon = new JScrollPane(pnlDanhSachMon);
        cuon.setBorder(null);
        cuon.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(pnlTop, BorderLayout.NORTH);
        panel.add(cuon, BorderLayout.CENTER);
        return panel;
    }
    // END: Sửa Navbar
    
    private void hienThiMonAn(List<MonAn> ds) {
        pnlDanhSachMon.removeAll(); 
        pnlDanhSachMon.setLayout(new GridLayout(0, 3, 10, 12)); 
        
        if (ds != null && !ds.isEmpty()) {
            for (MonAn mon : ds) {
                themMon(mon.getTenMonAn(), (int)mon.getGiaMonAn(), mon.getHinhAnh());
            }
        } else {
            pnlDanhSachMon.setLayout(new BorderLayout());
            pnlDanhSachMon.add(new JLabel("Không tìm thấy món ăn nào.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        pnlDanhSachMon.revalidate();
        pnlDanhSachMon.repaint();
    }
    
    //
    private void themMon(String ten, int gia, String imagePath) { 
        JPanel item = new JPanel(new BorderLayout(6,6));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(8,8,8,8)
        ));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblAnh = new JLabel(); 
        lblAnh.setPreferredSize(new Dimension(110, 90));
        lblAnh.setOpaque(true);
        lblAnh.setBackground(new Color(250,250,250));
        lblAnh.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        if (imagePath == null || imagePath.trim().isEmpty()) {
            lblAnh.setText("Ảnh CSDL (null)");
            imagePath = "default.png"; 
        }
        
        String internalImagePath = "/image/" + imagePath.trim();
        
        try {
            URL imageUrl = getClass().getResource(internalImagePath);
            
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image originalImage = originalIcon.getImage();
                Image scaledImage = originalImage.getScaledInstance(90, 80, Image.SCALE_SMOOTH); 
                lblAnh.setIcon(new ImageIcon(scaledImage));
                lblAnh.setText(""); 
            } else {
                lblAnh.setText("<html>Lỗi: Không tìm thấy<br>" + internalImagePath + "</html>"); 
                lblAnh.setFont(new Font("Arial", Font.PLAIN, 9));
            }
        } catch (Exception e) {
            lblAnh.setText("Lỗi Tải Ảnh");
            e.printStackTrace();
        }
        JLabel lblTen = new JLabel(ten, SwingConstants.CENTER);
        lblTen.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel lblGia = new JLabel(dinhDangTien.format(gia) + " VND", SwingConstants.CENTER);
        lblGia.setFont(new Font("Arial", Font.PLAIN, 12));
        lblGia.setForeground(new Color(220, 53, 69));

        JPanel pnlDuoi = new JPanel(new BorderLayout(6,6));
        pnlDuoi.setBackground(Color.WHITE);

        SpinnerNumberModel spModel = new SpinnerNumberModel(1, 1, 99, 1);
        JSpinner spSoLuong = new JSpinner(spModel);
        spSoLuong.setPreferredSize(new Dimension(60, 30));
        ((JSpinner.DefaultEditor) spSoLuong.getEditor()).getTextField().setFont(new Font("Arial", Font.BOLD, 12));

        JButton btnChon = new JButton("Chọn");
        btnChon.setBackground(new Color(40, 167, 69));
        btnChon.setForeground(Color.WHITE);
        btnChon.setFocusPainted(false);
        btnChon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChon.addActionListener(e -> {
            int sl = (Integer) spSoLuong.getValue();
            themVaoGio(ten, gia, sl);
        });

        pnlDuoi.add(spSoLuong, BorderLayout.WEST);
        pnlDuoi.add(btnChon, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.add(lblTen);
        info.add(Box.createVerticalStrut(6));
        info.add(lblGia);
        info.add(Box.createVerticalStrut(8));
        info.add(pnlDuoi);

        item.add(lblAnh, BorderLayout.NORTH);
        item.add(info, BorderLayout.CENTER);

        pnlDanhSachMon.add(item);
    }

    // START: Sửa phần giỏ hàng
    private JPanel taoKhuVucGioHang() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(420, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));

        panel.add(lblTieuDeDangGoi);
        panel.add(Box.createVerticalStrut(8));
        tblMonDangGoi = new JTable(modelMonDangGoi);
        tblMonDangGoi.setRowHeight(42);
        tblMonDangGoi.setFont(new Font("Arial", Font.PLAIN, 12));
        tblMonDangGoi.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(JLabel.CENTER);
        
        tblMonDangGoi.getColumnModel().getColumn(0).setCellRenderer(canGiua);
        tblMonDangGoi.getColumnModel().getColumn(1).setCellRenderer(canGiua);
        tblMonDangGoi.getColumnModel().getColumn(3).setCellRenderer(canGiua);

        // Áp dụng SpinnerCellRenderer và SpinnerCellEditor cho cột Số Lượng (index 2)
        tblMonDangGoi.getColumnModel().getColumn(2).setCellRenderer(new SpinnerCellRenderer());
        tblMonDangGoi.getColumnModel().getColumn(2).setCellEditor(new SpinnerCellEditor());
        
        tblMonDangGoi.getColumnModel().getColumn(0).setPreferredWidth(30);  
        tblMonDangGoi.getColumnModel().getColumn(1).setPreferredWidth(120); 
        tblMonDangGoi.getColumnModel().getColumn(2).setPreferredWidth(70);  
        tblMonDangGoi.getColumnModel().getColumn(3).setPreferredWidth(90);  
        tblMonDangGoi.getColumnModel().getColumn(4).setPreferredWidth(60);  

        tblMonDangGoi.getColumnModel().getColumn(4).setCellRenderer(new NutRenderer("Xóa", new Color(220,53,69)));
        tblMonDangGoi.getColumnModel().getColumn(4).setCellEditor(new NutEditor(new JCheckBox(), "Xóa", new Color(220,53,69), (row) -> {
            int modelRow = tblMonDangGoi.convertRowIndexToModel(row);
            if (modelRow >= 0 && modelRow < modelMonDangGoi.getRowCount()) {
                String ten = (String) modelMonDangGoi.getValueAt(modelRow, 1);
                xoaKhoiGio(ten);
            }
        }));

        JScrollPane cuon = new JScrollPane(tblMonDangGoi);
        cuon.setPreferredSize(new Dimension(0, 190));
        panel.add(cuon);
        panel.add(Box.createVerticalStrut(8));
        JPanel pnlSubTotal = new JPanel(new BorderLayout());
        pnlSubTotal.setBackground(Color.WHITE);
        JLabel lblTxtSubTotal = new JLabel("Tổng tiền tạm tính");
        lblTxtSubTotal.setFont(new Font("Arial", Font.ITALIC, 13));
        pnlSubTotal.add(lblTxtSubTotal, BorderLayout.WEST);
        pnlSubTotal.add(lblTongTienDangGoi, BorderLayout.EAST);
        
        lblTongTienDangGoi.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTienDangGoi.setForeground(new Color(255, 140, 0));
        
        panel.add(pnlSubTotal);
        panel.add(Box.createVerticalStrut(10));
        JLabel lblGhiChu = new JLabel("Ghi chú");
        lblGhiChu.setFont(new Font("Arial", Font.BOLD, 13));
        txtGhiChu = new JTextArea(4, 20);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        JScrollPane cuonGhiChu = new JScrollPane(txtGhiChu);
        cuonGhiChu.setPreferredSize(new Dimension(0, 70));

        panel.add(lblGhiChu);
        panel.add(Box.createVerticalStrut(6));
        panel.add(cuonGhiChu);
        panel.add(Box.createVerticalStrut(10));

        JPanel pnlNut = new JPanel(new BorderLayout(8,8));
        pnlNut.setBackground(Color.WHITE);
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.setBackground(new Color(40,167,69));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Arial", Font.BOLD, 14));
        btnXacNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXacNhan.addActionListener(e -> xacNhanDon());

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(220,53,69));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFont(new Font("Arial", Font.BOLD, 14));
        btnHuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> huyDon());

        pnlNut.add(btnXacNhan, BorderLayout.CENTER);
        pnlNut.add(btnHuy, BorderLayout.EAST);

        panel.add(pnlNut);
        panel.add(Box.createVerticalStrut(12));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(8));

        panel.add(lblTieuDeDaGoi);
        panel.add(Box.createVerticalStrut(8));

        tblMonDaGoi = new JTable(modelMonDaGoi);
        tblMonDaGoi.setRowHeight(36);
        tblMonDaGoi.setFont(new Font("Arial", Font.PLAIN, 12));
        tblMonDaGoi.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        DefaultTableCellRenderer canGiua2 = new DefaultTableCellRenderer();
        canGiua2.setHorizontalAlignment(JLabel.CENTER);
        tblMonDaGoi.getColumnModel().getColumn(0).setCellRenderer(canGiua2);
        tblMonDaGoi.getColumnModel().getColumn(1).setCellRenderer(canGiua2);
        tblMonDaGoi.getColumnModel().getColumn(2).setCellRenderer(canGiua2);

        DefaultTableCellRenderer canPhai2 = new DefaultTableCellRenderer();
        canPhai2.setHorizontalAlignment(JLabel.RIGHT);
        tblMonDaGoi.getColumnModel().getColumn(3).setCellRenderer(canPhai2);

        JScrollPane cuon2 = new JScrollPane(tblMonDaGoi);
        cuon2.setPreferredSize(new Dimension(0, 140));
        panel.add(cuon2);
        panel.add(Box.createVerticalStrut(8));

        JPanel pnlTong = new JPanel(new BorderLayout());
        pnlTong.setBackground(Color.WHITE);
        JLabel lblTxtTong = new JLabel("Tổng tiền hóa đơn");
        lblTxtTong.setFont(new Font("Arial", Font.BOLD, 14));
        
        pnlTong.add(lblTxtTong, BorderLayout.WEST);
        pnlTong.add(lblTongTien, BorderLayout.EAST);
        
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(220,53,69));
        
        panel.add(pnlTong);
        panel.add(Box.createVerticalStrut(10));

        JButton btnThanhToan = new JButton("Thanh toán");
        btnThanhToan.setBackground(new Color(0, 123, 255));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
        btnThanhToan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThanhToan.addActionListener(e -> thanhToan()); 
        panel.add(btnThanhToan);

        return panel;
    }
    private double tinhTongTienGioHang() {
        double tong = 0;
        for (Map.Entry<String, Integer> entry : gioHangXacNhan.entrySet()) {
            String ten = entry.getKey();
            int sl = entry.getValue();
            int gia = bangGia.getOrDefault(ten, 0);
            tong += gia * sl;
        }
        return tong;
    }

    // END: Sửa phần giỏ hàng
    

    class NutRenderer extends JButton implements TableCellRenderer {
        public NutRenderer(String text, Color color) {
            setText(text);
            setOpaque(true);
            setBackground(color);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class NutEditor extends DefaultCellEditor {
        private JButton btn;
        private ActionListener listener;

        public NutEditor(JCheckBox checkBox, String text, Color color, java.util.function.Consumer<Integer> onClick) {
            super(checkBox);
            btn = new JButton(text);
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            listener = e -> onClick.accept(tblMonDangGoi.getSelectedRow());
            btn.addActionListener(listener);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return btn;
        }

        public Object getCellEditorValue() {
            return null;
        }
    }
    
    // START: Thêm lớp SpinnerCellRenderer
    class SpinnerCellRenderer extends JSpinner implements TableCellRenderer {

        public SpinnerCellRenderer() {
            setModel(new SpinnerNumberModel(1, 1, 999, 1));
            JFormattedTextField textField = ((JSpinner.DefaultEditor) this.getEditor()).getTextField();
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.setEditable(false); 
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Integer) {
                setValue(value);
            }
            if (isSelected) {
                setBorder(BorderFactory.createLineBorder(table.getSelectionBackground(), 2));
            } else {
                setBorder(BorderFactory.createLineBorder(table.getBackground(), 2));
            }
            return this;
        }
    }
    // END: Thêm lớp SpinnerCellRenderer
    
    // START: Thêm lớp SpinnerCellEditor

    class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner;
        private JPanel editorComponent;

        public SpinnerCellEditor() {
            spinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
            // Khi spinner thay đổi, dừng edit ngay lập tức để lưu giá trị
            spinner.addChangeListener(e -> {
                // Dùng invokeLater để tránh xung đột luồng
                SwingUtilities.invokeLater(this::stopCellEditing);
            });

            JPanel pnl = new JPanel(new BorderLayout());
            pnl.add(spinner, BorderLayout.CENTER);
            editorComponent = pnl;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Integer) {
                spinner.setValue(value);
            }
            return editorComponent;
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
        
        @Override
        public boolean stopCellEditing() {
            try {
                int row = tblMonDangGoi.getEditingRow();
                // ⭐ QUAN TRỌNG: Kiểm tra dòng hợp lệ để tránh lỗi IndexOutOfBounds
                if (row >= 0 && row < tblMonDangGoi.getRowCount()) {
                    String tenMon = (String) modelMonDangGoi.getValueAt(row, 1);
                    int newValue = (Integer) spinner.getValue(); 

                    if (newValue <= 0) { 
                        // Trường hợp đặc biệt: Xóa món -> Lúc này mới cần load lại bảng
                        xoaKhoiGio(tenMon);
                    } else {
                        // 1. Cập nhật dữ liệu ngầm (Map)
                        gioHang.put(tenMon, newValue); 
                        
                        // 2. Tính lại thành tiền của dòng này
                        int gia = bangGia.getOrDefault(tenMon, 0);
                        long thanhTien = (long) gia * newValue;
                        
                        // 3. Cập nhật trực tiếp lên giao diện (Ô Thành tiền - Cột 3)
                        // LƯU Ý: Không được gọi capNhatBangGio() ở đây vì nó sẽ xóa dòng gây lỗi
                        modelMonDangGoi.setValueAt(dinhDangTien.format(thanhTien) + " VND", row, 3);
                        
                        // 4. Chỉ tính lại tổng tiền (Không vẽ lại bảng)
                        capNhatChiTietTongTien();
                    }
                }
            } catch (Exception e) {
                // Bắt lỗi để không crash chương trình nếu thao tác quá nhanh
                return false;
            }
            return super.stopCellEditing();
        }
    }
    // END: Thêm lớp SpinnerCellEditor
}