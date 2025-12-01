package gui;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
// ------------------------------------

import dao.BanDat_DAO;
import dao.Ban_DAO;
import dao.CTHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.MonAn_DAO;
import entity.Ban;
import entity.CT_HoaDon;
import entity.HoaDon;
import entity.MonAn;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;


import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class ThanhToan_Gui extends JPanel {
    private NumberFormat dinhDangTien;
    
    
    private Map<String, Integer> gioHangXacNhan; 
    private Map<String, Integer> bangGia;      
    private double tongTienHoaDonBanDau;              

    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;
    private JTextField txtKhachDua;
    private JLabel lblTongTienPhaiThu;
    private JLabel lblTienThua;
    private JLabel lblTongTienHoaDon; 
    private JLabel lblGiamGiaHienTai; 
    private JLabel lblQR; 
    private JPanel pnlGoiYContainer; 

    private double soTienKhachDua = 0;
    private String hinhThucThanhToan = "Tiền mặt"; 
    private JButton btnTienMat, btnBank, btnMoMo; 
    
    private double giamGia = 0; 
    private double tienCoc = 0; 
    private double tongTienSauGiamGia = 0; 
    
    private String maBan;
    private NhanVien nhanVienHienTai;
    private String maHoaDon;
    private LocalTime gioVao;
    private LocalTime gioRa;
    
    public ThanhToan_Gui(Map<String, Integer> gioHangXacNhan, Map<String, Integer> bangGia, double tongTienHoaDon, double tienCoc2, String maBan, NhanVien nhanVien, LocalTime gioVao) {
    	this.maHoaDon = new HoaDon_DAO().layMaHDTiepTheo();
    	this.gioHangXacNhan = new LinkedHashMap<>(gioHangXacNhan);
    	this.bangGia = new LinkedHashMap<>(bangGia);
        this.tongTienHoaDonBanDau = tongTienHoaDon;
        this.tongTienSauGiamGia = tongTienHoaDon; 
        this.tienCoc = tienCoc2;
        this.maBan = maBan;
        this.nhanVienHienTai = nhanVien;
        this.gioVao = gioVao;             
        this.gioRa = LocalTime.now();     
        dinhDangTien = NumberFormat.getInstance(new Locale("vi", "VN"));

        setLayout(new BorderLayout());
        setBackground(new Color(255, 235, 205));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. HEADER
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(255, 218, 185));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lblBack = new JLabel("←");
        lblBack.setFont(new Font("Arial", Font.BOLD, 20));
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((JFrame) SwingUtilities.getWindowAncestor(ThanhToan_Gui.this)).dispose();
            }
        });
        
        tieuDe.add(lblBack, BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Thanh toán", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        tieuDe.add(lblTitle, BorderLayout.CENTER);
        add(tieuDe, BorderLayout.NORTH);

        // 2. KHU VỰC CHÍNH (Chia thành trái và phải)
        JPanel pnlChinh = new JPanel(new GridLayout(1, 2, 10, 10));
        pnlChinh.setBackground(new Color(245, 245, 245));
        pnlChinh.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlTrai = taoKhuVucNhapTien();
        JPanel pnlPhai = taoKhuVucHoaDon();

        pnlChinh.add(pnlTrai);
        pnlChinh.add(pnlPhai);

        add(pnlChinh, BorderLayout.CENTER);
        
        // Khởi tạo trạng thái ban đầu
        hienThiHoaDon();
        capNhatTongTien(); 
        capNhatTrangThaiNutThanhToan(); 
        taoVaHienThiQRCode(); 
    }
    
    // ----------------------------------------------------------------------
    // --- PHƯƠNG THỨC TÍNH TOÁN VÀ CẬP NHẬT TRẠNG THÁI ---
    // ----------------------------------------------------------------------
    
    private void capNhatTongTien() {
    	
    	
        this.tongTienSauGiamGia = this.tongTienHoaDonBanDau - this.giamGia - this.tienCoc;
        
        if (this.tongTienSauGiamGia < 0) {
            this.tongTienSauGiamGia = 0; 
        }
        
        capNhatTongKet();
        capNhatTienThua();
        taoVaHienThiQRCode(); 
        
        if (pnlGoiYContainer != null) {
            pnlGoiYContainer.removeAll();
            pnlGoiYContainer.add(capNhatKhuVucGoiY(), BorderLayout.CENTER);
            pnlGoiYContainer.revalidate();
            pnlGoiYContainer.repaint();
        }
    }

    private double[] tinhToanGoiY(double soTienCanThu) {
        if (soTienCanThu <= 0) {
            return new double[]{0, 50000, 100000, 200000, 500000, 1000000};
        }

        Set<Double> goiYSet = new LinkedHashSet<>();
        goiYSet.add(soTienCanThu); 
        
        long[] menhGia = {1000L, 5000L, 10000L, 50000L, 100000L};
        
        for (long mg : menhGia) {
            if (mg * 5 < soTienCanThu && mg < 50000) continue; 
            
            double nextChuc = Math.ceil(soTienCanThu / mg) * mg;
            
            if (nextChuc > soTienCanThu && nextChuc <= soTienCanThu + 500000) {
                goiYSet.add(nextChuc);
            }
        }
        
        if (soTienCanThu < 200000) goiYSet.add(200000.0);
        if (soTienCanThu < 500000) goiYSet.add(500000.0);
        goiYSet.add(1000000.0);
        
        List<Double> list = new ArrayList<>(goiYSet);
        list.sort(Comparator.naturalOrder());
        
        List<Double> finalGoiY = new ArrayList<>();
        for (Double tien : list) {
            if (tien >= soTienCanThu && finalGoiY.size() < 6) { 
                finalGoiY.add(tien);
            }
        }
        
        return finalGoiY.stream().mapToDouble(Double::doubleValue).toArray();
    }
   
    // Giao diện
   
    private JPanel taoKhuVucNhapTien() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlTop.setBackground(Color.WHITE);
        
        // --- Cột trái: Nhập số tiền khách đưa (Bàn phím mới) ---
        JPanel pnlNhapTien = new JPanel(new BorderLayout(5, 5));
        pnlNhapTien.setBackground(Color.WHITE);
        pnlNhapTien.add(new JLabel("Nhập số tiền khách đưa", SwingConstants.CENTER), BorderLayout.NORTH);
        
        txtKhachDua = new JTextField(dinhDangTien.format(0));
        txtKhachDua.setFont(new Font("Arial", Font.BOLD, 28));
        txtKhachDua.setHorizontalAlignment(JTextField.RIGHT);
        txtKhachDua.setEditable(false);
        txtKhachDua.setBackground(new Color(240, 240, 240));
        pnlNhapTien.add(txtKhachDua, BorderLayout.CENTER);
        
        // Bàn phím số mới (4x4)
        JPanel pnlPhim = new JPanel(new GridLayout(4, 4, 8, 8));
        pnlPhim.setBackground(Color.WHITE);
        // Layout 4x4: 7, 8, 9, X; 4, 5, 6, AC; 1, 2, 3, 000; 0, ., ?, ?
        String[] nutPhim = {"7", "8", "9", "X", "4", "5", "6", "AC", "1", "2", "3", "000", "0", "."};
        ActionListener phimListener = e -> xuLyNhapSo(e.getActionCommand());
        
        for (String text : nutPhim) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setFocusPainted(false);
            
            if (text.equals("X") || text.equals("AC")) {
                btn.setBackground(new Color(220, 53, 69)); // Đỏ
                btn.setForeground(Color.WHITE);
            } else if (text.equals("000")) {
                btn.setBackground(new Color(173, 216, 230)); // Xanh nhạt
            } else {
                btn.setBackground(new Color(230, 230, 230));
            }
            
            btn.addActionListener(phimListener);
            pnlPhim.add(btn);
        }
        
        // Thêm nút trống cho đủ 16 ô (4x4)
        for(int i = pnlPhim.getComponentCount(); i < 16; i++) {
            pnlPhim.add(new JLabel());
        }
        
        pnlNhapTien.add(pnlPhim, BorderLayout.SOUTH);
        
        // --- Cột phải: Hình thức thanh toán & Giảm giá ---
        JPanel pnlHinhThucWrapper = new JPanel(new GridLayout(2, 1, 0, 15));
        pnlHinhThucWrapper.setBackground(Color.WHITE);
        
        // Khu vực Hình thức thanh toán
        JPanel pnlHinhThucNut = new JPanel(new GridLayout(3, 1, 0, 10));
        pnlHinhThucNut.setBackground(Color.WHITE);
        pnlHinhThucNut.setBorder(BorderFactory.createTitledBorder("Hình thức thanh toán"));

        btnTienMat = new JButton("Tiền mặt");
        btnBank = new JButton("<html><center>BANK<br>TRANSFER</center></html>");
        btnMoMo = new JButton("MoMo");
        
        // Sử dụng hàm chonHinhThucThanhToan (1 tham số) để reset/set tự động
        btnTienMat.addActionListener(e -> chonHinhThucThanhToan("Tiền mặt"));
        btnBank.addActionListener(e -> chonHinhThucThanhToan("BANK"));
        btnMoMo.addActionListener(e -> chonHinhThucThanhToan("MoMo"));
        
        pnlHinhThucNut.add(btnTienMat);
        pnlHinhThucNut.add(btnBank);
        pnlHinhThucNut.add(btnMoMo);
        
        // Khu vực Giảm giá
        JPanel pnlGiamGia = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlGiamGia.setBackground(Color.WHITE);
        pnlGiamGia.setBorder(BorderFactory.createTitledBorder("Giảm giá"));
        
        JButton btnChietKhau = new JButton("Chiết khấu trực tiếp");
        btnChietKhau.setBackground(new Color(255, 193, 7));
        btnChietKhau.setFocusPainted(false);
        btnChietKhau.addActionListener(e -> xuLyGiamGia()); 
        
        JButton btnKhuyenMai = new JButton("Khuyến mãi");
        btnKhuyenMai.setBackground(new Color(255, 193, 7));
        btnKhuyenMai.setFocusPainted(false);
        
        pnlGiamGia.add(btnChietKhau);
        pnlGiamGia.add(btnKhuyenMai);
        
        pnlHinhThucWrapper.add(pnlHinhThucNut);
        pnlHinhThucWrapper.add(pnlGiamGia);
        
        pnlTop.add(pnlNhapTien);
        pnlTop.add(pnlHinhThucWrapper); 

        panel.add(pnlTop, BorderLayout.CENTER);
        
        // --- Gợi ý tiền mặt (Phần dưới) ---
        JPanel pnlGoiY = new JPanel(new BorderLayout(5, 5));
        pnlGoiY.setBackground(Color.WHITE);
        pnlGoiY.add(new JLabel("Gợi ý tiền mặt", SwingConstants.LEFT), BorderLayout.NORTH);
        
        pnlGoiYContainer = new JPanel(new BorderLayout());
        pnlGoiYContainer.setBackground(Color.WHITE);
        pnlGoiYContainer.add(capNhatKhuVucGoiY(), BorderLayout.CENTER); 
        
        pnlGoiY.add(pnlGoiYContainer, BorderLayout.CENTER);
        panel.add(pnlGoiY, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel capNhatKhuVucGoiY() {
        double soTienCanThu = this.tongTienSauGiamGia; 
        double[] goiY = tinhToanGoiY(soTienCanThu);
        
        JPanel pnlNutGoiY = new JPanel(new GridLayout(2, 3, 8, 8)); 
        pnlNutGoiY.setBackground(Color.WHITE);

        for (double soTien : goiY) {
            JButton btnGoiY = new JButton(dinhDangTien.format(soTien));
            btnGoiY.setFont(new Font("Arial", Font.BOLD, 12));
            btnGoiY.setBackground(new Color(152, 251, 152));
            btnGoiY.setFocusPainted(false);
            btnGoiY.addActionListener(e -> xuLyGoiY(soTien));
            pnlNutGoiY.add(btnGoiY);
        }
        
        while (pnlNutGoiY.getComponentCount() < 6) { 
            pnlNutGoiY.add(new JLabel());
        }
        
        return pnlNutGoiY;
    }

    private JPanel taoKhuVucHoaDon() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Tiêu đề
        JLabel lblTitle = new JLabel("HOÁ ĐƠN THANH TOÁN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setBackground(Color.WHITE);

        // Thông tin chung
        JPanel pnlThongTin = new JPanel(new GridLayout(3, 2, 0, 5));
        pnlThongTin.setBackground(Color.WHITE);
        pnlThongTin.add(new JLabel("Mã HĐ: " + maHoaDon));
        pnlThongTin.add(new JLabel("Thu ngân: " + nhanVienHienTai.getHoTen()));
        pnlThongTin.add(new JLabel("Bàn: "+ maBan));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        pnlThongTin.add(new JLabel("Ngày: " + LocalDate.now().format(df)));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
        pnlThongTin.add(new JLabel("Giờ vào: " + gioVao.format(f)));
        pnlThongTin.add(new JLabel("Giờ ra: " + gioRa.format(f)));
        pnlCenter.add(pnlThongTin);
        pnlCenter.add(Box.createVerticalStrut(10));

        // Bảng hóa đơn
        String[] cot = {"STT", "Tên món", "SL", "Đơn giá", "Thành tiền"};
        modelHoaDon = new DefaultTableModel(cot, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHoaDon = new JTable(modelHoaDon);
        tblHoaDon.setRowHeight(25);
        
        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(JLabel.RIGHT);
        tblHoaDon.getColumnModel().getColumn(3).setCellRenderer(canPhai);
        tblHoaDon.getColumnModel().getColumn(4).setCellRenderer(canPhai);
        tblHoaDon.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblHoaDon.getColumnModel().getColumn(1).setPreferredWidth(150);

        JScrollPane cuon = new JScrollPane(tblHoaDon);
        cuon.setPreferredSize(new Dimension(0, 180));
        pnlCenter.add(cuon);
        pnlCenter.add(Box.createVerticalStrut(10));
        
        // --- KHU VỰC HIỂN THỊ MÃ QR ĐỘNG ---
        JPanel pnlQR = new JPanel(new BorderLayout());
        pnlQR.setBackground(Color.WHITE);
        pnlQR.setBorder(BorderFactory.createTitledBorder("Thanh toán QR Code"));
        
        lblQR = new JLabel("Mã QR sẽ hiển thị ở đây", SwingConstants.CENTER);
        lblQR.setFont(new Font("Arial", Font.ITALIC, 12));
        lblQR.setPreferredSize(new Dimension(150, 150)); 
        pnlQR.add(lblQR, BorderLayout.CENTER);
        
        pnlCenter.add(pnlQR);
        pnlCenter.add(Box.createVerticalStrut(10));
        // --- KẾT THÚC KHU VỰC QR ---
        
        // Tổng kết
        pnlCenter.add(taoKhuVucTongKet());
        pnlCenter.add(Box.createVerticalGlue());
        
        // Nút Thanh toán
        JPanel pnlNut = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlNut.setBackground(Color.WHITE);
        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(220, 53, 69));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.addActionListener(e -> ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose()); 
        
        JButton btnXacNhan = new JButton("Xác nhận và in hóa đơn");
        btnXacNhan.setBackground(new Color(40, 167, 69));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.addActionListener(e -> {
        	this.gioRa = LocalTime.now(); 
        	inHoaDonTrucTiep(); //in máy in
            inHoaDon();       // lưu DB (giữ nguyên của bạn)
        });

        pnlNut.add(btnHuy);
        pnlNut.add(btnXacNhan);
        panel.add(pnlNut, BorderLayout.SOUTH);

        panel.add(pnlCenter, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoKhuVucTongKet() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 0, 5));
        panel.setBackground(Color.WHITE);
        
        // Dòng 1: Thành tiền (Giá trị ban đầu)
        panel.add(new JLabel("Thành tiền:", SwingConstants.LEFT));
        panel.add(new JLabel(dinhDangTien.format(tongTienHoaDonBanDau) + " VND", SwingConstants.RIGHT));
        
        // Dòng 2: Giảm giá (Giá trị đã giảm)
        panel.add(new JLabel("Giảm giá:", SwingConstants.LEFT));
        lblGiamGiaHienTai = new JLabel(dinhDangTien.format(giamGia) + " VND", SwingConstants.RIGHT); 
        lblGiamGiaHienTai.setForeground(new Color(220, 53, 69)); 
        panel.add(lblGiamGiaHienTai);
        
        // Dòng 3: Trừ cọc
        panel.add(new JLabel("Trừ cọc:", SwingConstants.LEFT));
        panel.add(new JLabel(dinhDangTien.format(tienCoc) + " VND", SwingConstants.RIGHT));
        
        // Dòng 4: Tổng tiền (Sau giảm giá và trừ cọc)
        panel.add(new JLabel("Tổng tiền:", SwingConstants.LEFT));
        lblTongTienHoaDon = new JLabel(dinhDangTien.format(tongTienSauGiamGia) + " VND", SwingConstants.RIGHT);
        lblTongTienHoaDon.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTongTienHoaDon);
        
        panel.add(new JSeparator());
        panel.add(new JSeparator());
        
        // Dòng 6: Cần phải thu
        panel.add(new JLabel("Số tiền cần phải thu", SwingConstants.LEFT));
        lblTongTienPhaiThu = new JLabel(dinhDangTien.format(tongTienSauGiamGia) + " VND", SwingConstants.RIGHT);
        lblTongTienPhaiThu.setForeground(new Color(220, 53, 69));
        lblTongTienPhaiThu.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTongTienPhaiThu);
        
        // Dòng 7: Tiền thừa
        panel.add(new JLabel("Tiền thừa", SwingConstants.LEFT));
        lblTienThua = new JLabel(dinhDangTien.format(soTienKhachDua - tongTienSauGiamGia) + " VND", SwingConstants.RIGHT);
        lblTienThua.setFont(new Font("Arial", Font.BOLD, 14));
        lblTienThua.setForeground(new Color(40, 167, 69));
        panel.add(lblTienThua);
        
        return panel;
    }
    
    // xử lý sự kiện
   
    private void xuLyGiamGia() {
        String input = JOptionPane.showInputDialog(this, 
            "Nhập giá trị giảm giá (VND hoặc %):\nVí dụ: 50000 hoặc 10%", 
            "Chiết khấu trực tiếp", JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) {
            return; 
        }

        input = input.trim().toUpperCase().replace(",", ".");
        double giaTriNhap;

        try {
            if (input.endsWith("%")) {
                String phanTramStr = input.substring(0, input.length() - 1);
                giaTriNhap = Double.parseDouble(phanTramStr);
                
                if (giaTriNhap < 0 || giaTriNhap > 100) {
                    throw new IllegalArgumentException("Phần trăm phải từ 0 đến 100.");
                }
                
                this.giamGia = tongTienHoaDonBanDau * (giaTriNhap / 100.0);
                
            } else {
                giaTriNhap = Double.parseDouble(input);
                
                if (giaTriNhap < 0 || giaTriNhap > tongTienHoaDonBanDau) {
                    throw new IllegalArgumentException("Giảm giá không hợp lệ.");
                }
                
                this.giamGia = giaTriNhap;
            }
            
            capNhatTongTien();
            
            // Nếu không phải Tiền mặt, tự động cập nhật số tiền khách đưa
            if (!hinhThucThanhToan.equals("Tiền mặt")) {
                soTienKhachDua = tongTienSauGiamGia;
                txtKhachDua.setText(dinhDangTien.format(soTienKhachDua));
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị nhập không hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
             JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // OVERLOAD 1: Dùng cho các nút Tiền mặt/Bank/MoMo (Sẽ reset tiền mặt về 0)
    private void chonHinhThucThanhToan(String hinhThucMoi) {
        chonHinhThucThanhToan(hinhThucMoi, true); 
    }
    
    private void chonHinhThucThanhToan(String hinhThucMoi, boolean resetTienDua) {
        this.hinhThucThanhToan = hinhThucMoi;
        
        if (!hinhThucMoi.equals("Tiền mặt")) {
            // Luôn tự động điền tổng tiền khi chọn BANK/MoMo
            soTienKhachDua = tongTienSauGiamGia; 
            txtKhachDua.setText(dinhDangTien.format(soTienKhachDua));
        } else if (resetTienDua) { // Chỉ reset về 0 khi bấm nút "Tiền mặt" trực tiếp
             soTienKhachDua = 0;
             txtKhachDua.setText(dinhDangTien.format(0));
        }
        // Nếu resetTienDua=false (tức là được gọi từ nút gợi ý) thì giữ nguyên soTienKhachDua
        
        capNhatTienThua();
        capNhatTrangThaiNutThanhToan();
        taoVaHienThiQRCode(); 
    }
    
    private void xuLyNhapSo(String phim) {
        String hienTai = txtKhachDua.getText().replace(".", ""); 
        
        if (hienTai.isEmpty() || hienTai.equals("0")) {
            hienTai = "0";
        }

        switch (phim) {
            case "X": 
                if (hienTai.length() > 1) {
                    hienTai = hienTai.substring(0, hienTai.length() - 1);
                } else {
                    hienTai = "0";
                }
                break;
            case "AC": 
                hienTai = "0";
                break;
            case "000":
                if (!hienTai.equals("0")) {
                    hienTai += "000";
                }
                break;
            case ".": 
                break;
            default: 
                if (hienTai.equals("0")) {
                    hienTai = phim;
                } else {
                    hienTai += phim;
                }
                break;
        }

        try {
            soTienKhachDua = Long.parseLong(hienTai);
            if (soTienKhachDua > 999999999999L) { 
                soTienKhachDua = 999999999999L;
            }
            txtKhachDua.setText(dinhDangTien.format(soTienKhachDua));
        } catch (NumberFormatException ex) {
            soTienKhachDua = 0;
            txtKhachDua.setText("Lỗi số");
        }
        
        // Nếu đang không ở Tiền mặt, phải chuyển về Tiền mặt (và KHÔNG reset số tiền vừa nhập)
        if (!hinhThucThanhToan.equals("Tiền mặt")) {
            // Bấm nút số là hành động nhập tiền mặt, nên chuyển về Tiền mặt và KHÔNG reset
            chonHinhThucThanhToan("Tiền mặt", false); 
        } else {
            capNhatTienThua();
        }
    }
    
    // PHƯƠNG THỨC ĐÃ SỬA LỖI: Gọi hàm chonHinhThucThanhToan mới với resetTienDua = false
    private void xuLyGoiY(double soTien) {
        soTienKhachDua = soTien;
        txtKhachDua.setText(dinhDangTien.format(soTienKhachDua));
        // Đặt hình thức là Tiền mặt, nhưng KHÔNG reset số tiền khách đưa (giữ lại giá trị gợi ý)
        chonHinhThucThanhToan("Tiền mặt", false); 
    }
    
 // Trong ThanhToan_Gui.java

 // Trong ThanhToan_Gui.java

 // Trong ThanhToan_Gui.java

    private void inHoaDon() {

        // 1. Kiểm tra tiền khách đưa (chỉ áp dụng với Tiền mặt)
        if (hinhThucThanhToan.equals("Tiền mặt") && soTienKhachDua < tongTienSauGiamGia) {
            JOptionPane.showMessageDialog(this,
                    "Số tiền khách đưa không đủ!",
                    "Lỗi Thanh Toán",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Lưu hóa đơn xuống DB
        try {
            HoaDon_DAO hdDAO = new HoaDon_DAO();
            CTHoaDon_DAO ctDAO = new CTHoaDon_DAO();
            MonAn_DAO monDAO = new MonAn_DAO();

            String maHD = hdDAO.layMaHDTiepTheo();
            this.maHoaDon = maHD;
            HoaDon hd = new HoaDon(maHD);

            hd.setNgayLap(LocalDateTime.now());
            hd.setNhanVien(nhanVienHienTai);
            hd.setBan(new Ban(maBan));
            hd.setBanDat(null);
            hd.setKhuyenMai(null);
            hd.setTheThanhVien(null);

            ArrayList<CT_HoaDon> dsCT = new ArrayList<>();

            for (Map.Entry<String, Integer> item : gioHangXacNhan.entrySet()) {

                String tenMon = item.getKey();
                int soLuong = item.getValue();

                MonAn mon = monDAO.getMonAnTheoTen(tenMon);

                if (mon == null) {
                    JOptionPane.showMessageDialog(this,
                            "Không tìm thấy món ăn trong DB: " + tenMon,
                            "Lỗi dữ liệu",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CT_HoaDon ct = new CT_HoaDon(hd, mon, soLuong);
                dsCT.add(ct);
            }

            hd.setDanhSachChiTietHoaDon(dsCT);

            boolean ok = hdDAO.themHoaDon(hd);

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "❌ Lưu hóa đơn thất bại!",
                        "Lỗi database",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu hóa đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Thông báo đơn giản (KHÔNG PDF)
        String thongBao = String.format(
                "Thanh toán thành công!\n\n" +
                "Tổng tiền HĐ: %s VND\n" +
                "Giảm giá: %s VND\n" +
                "Phải thu: %s VND\n" +
                "Hình thức: %s\n" +
                "Khách đưa: %s VND\n" +
                "Tiền thừa: %s VND\n",
                dinhDangTien.format(tongTienHoaDonBanDau),
                dinhDangTien.format(giamGia),
                dinhDangTien.format(tongTienSauGiamGia),
                hinhThucThanhToan,
                dinhDangTien.format(soTienKhachDua),
                dinhDangTien.format(soTienKhachDua - tongTienSauGiamGia)
        );

        JOptionPane.showMessageDialog(this, thongBao, "Hóa đơn", JOptionPane.INFORMATION_MESSAGE);

        // Đóng cửa sổ
        ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose();
    }


    

    // cập nhật giao  diện
  
    private void hienThiHoaDon() {
        modelHoaDon.setRowCount(0);
        int stt = 1;
        for (Map.Entry<String, Integer> e : gioHangXacNhan.entrySet()) {
            String ten = e.getKey();
            int sl = e.getValue();
            if (bangGia.containsKey(ten)) {
                int gia = bangGia.get(ten);
                long thanhTien = (long)gia * sl;
                modelHoaDon.addRow(new Object[]{stt++, ten, sl, dinhDangTien.format(gia), dinhDangTien.format(thanhTien)});
            }
        }
    }
    
    private void capNhatTongKet() {
        lblTongTienHoaDon.setText(dinhDangTien.format(tongTienSauGiamGia) + " VND");
        lblGiamGiaHienTai.setText(dinhDangTien.format(giamGia) + " VND");
        
        capNhatTrangThaiNutThanhToan();
    }
 // ===============================================
 // IN HÓA ĐƠN TRỰC TIẾP RA MÁY IN (POS BILL)
 // ===============================================


    private void inHoaDonTrucTiep() {
        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPrintable(new Printable() {

            @Override
            public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) g;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                g2.setFont(new Font("Arial", Font.PLAIN, 11));

                int y = 20;

                // ====== TIÊU ĐỀ ======
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString("HOÁ ĐƠN THANH TOÁN", 140, y);
                y += 25;

                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.drawString("Mã HĐ: " + maHoaDon, 10, y);
                g2.drawString("Thu ngân: " + nhanVienHienTai.getHoTen(), 200, y);
                y += 15;

                g2.drawString("Bàn: " + maBan, 10, y);
                g2.drawString("Ngày: " + java.time.LocalTime.now().withNano(0), 200, y);
                y += 15;

                DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");

                g2.drawString("Giờ vào: " + gioVao.format(f), 10, y);
                g2.drawString("Giờ ra: " + gioRa.format(f), 200, y);
                y += 20;

                g2.drawLine(10, y, 380, y);
                y += 12;

                // ====== HEADER BẢNG ======
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("STT", 10, y);
                g2.drawString("Tên món", 45, y);
                g2.drawString("SL", 180, y);
                g2.drawString("Đơn giá", 215, y);
                g2.drawString("Thành tiền", 300, y);
                y += 12;

                g2.drawLine(10, y, 380, y);
                y += 14;

                // ====== IN DANH SÁCH MÓN ======
                g2.setFont(new Font("Arial", Font.PLAIN, 11));

                int stt = 1;
                for (Map.Entry<String, Integer> e : gioHangXacNhan.entrySet()) {

                    String ten = e.getKey();
                    int sl = e.getValue();
                    long gia = bangGia.get(ten);
                    long thanhTien = gia * sl;

                    g2.drawString(String.valueOf(stt++), 10, y);
                    g2.drawString(ten, 45, y);
                    g2.drawString(String.valueOf(sl), 185, y);
                    g2.drawString(dinhDangTien.format(gia), 225, y);
                    g2.drawString(dinhDangTien.format(thanhTien), 310, y);

                    y += 18;
                }

                g2.drawLine(10, y, 380, y);
                y += 20;

                // ====== TỔNG KẾT ======
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("Thành tiền:", 10, y);
                g2.drawString(dinhDangTien.format(tongTienHoaDonBanDau), 300, y);
                y += 18;

                g2.drawString("Giảm giá:", 10, y);
                g2.drawString(dinhDangTien.format(giamGia), 300, y);
                y += 18;

                g2.drawString("Trừ cọc:", 10, y);
                g2.drawString(dinhDangTien.format(tienCoc), 300, y);
                y += 18;

                g2.drawString("Tổng tiền:", 10, y);
                g2.drawString(dinhDangTien.format(tongTienSauGiamGia), 300, y);
                y += 22;

                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("Khách đưa:", 10, y);
                g2.drawString(dinhDangTien.format(soTienKhachDua), 300, y);
                y += 18;

                g2.drawString("Tiền thừa:", 10, y);
                g2.drawString(dinhDangTien.format(soTienKhachDua - tongTienSauGiamGia), 300, y);
                y += 25;

                // ====== CHÂN BILL ======
                g2.setFont(new Font("Arial", Font.ITALIC, 12));
                g2.drawString("=== Cảm ơn quý khách! ===", 110, y);

                return PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try { job.print(); }
            catch (PrinterException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi in hóa đơn: " + ex.getMessage());
            }
        }
    }



    private void capNhatTrangThaiNutThanhToan() {
        btnTienMat.setBackground(new Color(230, 230, 230));
        btnBank.setBackground(new Color(230, 230, 230));
        btnMoMo.setBackground(new Color(230, 230, 230));
        
        btnTienMat.setForeground(Color.BLACK);
        btnBank.setForeground(Color.BLACK);
        btnMoMo.setForeground(Color.BLACK);
        
        if (hinhThucThanhToan.equals("Tiền mặt")) {
            btnTienMat.setBackground(new Color(40, 167, 69)); 
            btnTienMat.setForeground(Color.WHITE);
        } else if (hinhThucThanhToan.equals("BANK")) {
            btnBank.setBackground(new Color(0, 123, 255)); 
            btnBank.setForeground(Color.WHITE);
        } else if (hinhThucThanhToan.equals("MoMo")) {
            btnMoMo.setBackground(new Color(220, 53, 69)); 
            btnMoMo.setForeground(Color.WHITE);
        }
        
        lblTongTienPhaiThu.setText(dinhDangTien.format(tongTienSauGiamGia) + " VND (" + hinhThucThanhToan + ")");
    }

    private void capNhatTienThua() {
        double tienThua = soTienKhachDua - tongTienSauGiamGia;
        
        if (tienThua < 0) {
            lblTienThua.setText(dinhDangTien.format(Math.abs(tienThua)) + " VND (Thiếu)");
            lblTienThua.setForeground(new Color(220, 53, 69));
        } else {
            lblTienThua.setText(dinhDangTien.format(tienThua) + " VND");
            lblTienThua.setForeground(new Color(40, 167, 69));
        }
    }
    
   // tạo qr
    private void taoVaHienThiQRCode() {
        String maNganHang = "VCB"; 
        String soTaiKhoan = "102875143321"; 
        long soTienCanThanhToan = (long) Math.round(this.tongTienSauGiamGia);
        
        // Dữ liệu giả lập chuẩn VietQR (chỉ ví dụ đơn giản)
        String dataQR = String.format("STK:%s, BANK:%s, AMOUNT:%d, DESC:ThanhToan", 
                                      soTaiKhoan, maNganHang, soTienCanThanhToan);

        int size = 150;
        
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(dataQR, BarcodeFormat.QR_CODE, size, size, hints);

            
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Hiển thị ảnh QR
            lblQR.setIcon(new ImageIcon(image));
            lblQR.setText(""); 
            
        } catch (WriterException e) {
            lblQR.setIcon(null);
            lblQR.setText("Lỗi tạo QR Code: " + e.getMessage());
        } catch (NoClassDefFoundError | Exception e) {
             // Lỗi này thường xảy ra nếu chưa thêm đủ thư viện JAR vào Build Path.
             lblQR.setIcon(null);
             lblQR.setText("LỖI: Thiếu file Zxing JAR!");
             System.err.println("Lỗi QR Code: Vui lòng kiểm tra đã thêm core.jar và javase.jar chưa.");
        }
    }
}