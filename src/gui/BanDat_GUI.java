package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.SQLException;

import com.toedter.calendar.JDateChooser;
import entity.Ban;
import entity.BanDat;
import entity.KhachHang;
import dao.Ban_DAO; 
import dao.BanDat_DAO;
import dao.KhachHang_DAO;
import connectDB.ConnectDB;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class BanDat_GUI extends JPanel {
    
    private JTextField txtMaDatBan, txtTenKhachHang, txtSoDienThoai, txtSoNguoi, txtTienCoc; 
    private JDateChooser dcNgayDat;
    private JComboBox<String> cboGioDat, cboKhuVuc; 
    private JTextArea txtGhiChu;
    
    private JButton btnDatBan, btnLamMoi, btnGoiMon, btnTimBan;; 
    
    private JPanel pnlBanCards; // Panel chứa các card bàn
    private Ban banDangChon = null; // Bàn được chọn
    
    private Ban_DAO banDAO = new Ban_DAO();
    private BanDat_DAO banDatDAO = new BanDat_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat currencyFormat = new DecimalFormat("#,###");

    private ArrayList<Ban> danhSachBanHienTai = new ArrayList<>();
    private DataRefreshListener refreshListener;
    
    // Màu sắc theme
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(255, 152, 0);
    private final Color COLOR_ACCENT = new Color(33, 150, 243);
    private final Color COLOR_BG = new Color(250, 250, 250);
    private final Color COLOR_CARD_BG = Color.WHITE;
    
    public BanDat_GUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BG);
        
        // Header với gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(750);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        
        // Left - Danh sách bàn dạng card
        JPanel leftPanel = createBanCardsPanel();
        
        // Right - Form đặt bàn
        JPanel rightPanel = createFormPanel();
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Load dữ liệu
        loadBanCards(banDAO.getAllBan());
        lamMoiForm();
        addEventListeners();
    }
    
    // --- HÀM HỖ TRỢ LẤY ICON ---
    private ImageIcon getIcon(String tenFile, int width, int height) {
        try {
            URL url = getClass().getResource("/image/" + tenFile);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBackground(new Color(76, 175, 80));
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        // Title - Sửa icon
        JLabel lblTitle = new JLabel("ĐẶT BÀN");
        ImageIcon iconTitle = getIcon("reservation.png", 40, 40);
        if (iconTitle != null) lblTitle.setIcon(iconTitle);
        else lblTitle.setText("🍽️ ĐẶT BÀN");
        
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        
        // Nút Gọi món - Sửa icon
        btnGoiMon = new JButton("Gọi Món");
        ImageIcon iconGoiMon = getIcon("menu.png", 24, 24);
        if (iconGoiMon != null) btnGoiMon.setIcon(iconGoiMon);
        
        btnGoiMon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGoiMon.setPreferredSize(new Dimension(140, 45));
        btnGoiMon.setBackground(COLOR_SECONDARY);
        btnGoiMon.setForeground(Color.WHITE);
        btnGoiMon.setFocusPainted(false);
        btnGoiMon.setBorderPainted(false);
        btnGoiMon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(btnGoiMon, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createBanCardsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 10)); 
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(COLOR_BG);
        
        JLabel lblFilter = new JLabel("Khu vực:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        cboKhuVuc = new JComboBox<>(new String[]{"Tất cả", "Tầng 1", "Tầng 2", "Tầng 3"});
        cboKhuVuc.setPreferredSize(new Dimension(150, 35));
        cboKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        filterPanel.add(lblFilter);
        filterPanel.add(cboKhuVuc);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Cards container với scroll
        pnlBanCards = new JPanel();
        pnlBanCards.setLayout(new GridLayout(0, 3, 15, 15)); // 3 cột
        pnlBanCards.setBackground(COLOR_BG);
        
        JScrollPane scrollPane = new JScrollPane(pnlBanCards);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createBanCard(Ban ban) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(220, 180));
        
        // 1. XỬ LÝ MÀU SẮC & TRẠNG THÁI
        String trangThai = ban.getTrangThai().trim(); // Xóa khoảng trắng thừa
        Color mauNen;
        Color mauVien;
        Color mauChu;
        String textTrangThai;

        // Logic màu sắc: Trống (Xanh) - Đang dùng (Vàng) - Đã đặt (Đỏ)
        if (trangThai.equalsIgnoreCase("Trống")) {
            // MÀU XANH LÁ
            mauNen = new Color(225, 255, 225); 
            mauVien = new Color(40, 167, 69);  
            mauChu = new Color(0, 100, 0);
            textTrangThai = "TRỐNG";
            
        } else if (trangThai.equalsIgnoreCase("Đang sử dụng")) {
            // --- SỬA THÀNH MÀU VÀNG ---
            mauNen = new Color(255, 253, 230); // Vàng kem nhạt
            mauVien = new Color(255, 193, 7);  // Vàng cam đậm
            mauChu = new Color(150, 100, 0);   // Chữ màu nâu đất
            textTrangThai = "ĐANG SỬ DỤNG";
            
        } else if (trangThai.equalsIgnoreCase("Đã đặt")) {
            // --- SỬA THÀNH MÀU ĐỎ ---
            mauNen = new Color(255, 235, 235); // Đỏ hồng nhạt
            mauVien = new Color(220, 53, 69);  // Đỏ đậm
            mauChu = new Color(150, 0, 0);     // Chữ đỏ đậm
            textTrangThai = "ĐÃ ĐẶT";
            
        } else {
            // Mặc định (Xám)
            mauNen = Color.WHITE;
            mauVien = Color.GRAY;
            mauChu = Color.BLACK;
            textTrangThai = trangThai.toUpperCase();
        }

        card.setBackground(mauNen);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(mauVien, 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // 2. PHẦN TRÊN: TÊN BÀN + ICON
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false); // Để lộ màu nền của card
        
        JLabel lblMaBan = new JLabel(ban.getMaBan());
        lblMaBan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMaBan.setForeground(mauChu);
        
        // Icon bàn
        JLabel lblIcon = new JLabel();
        ImageIcon iconBan = getTableIcon(ban.getLoaiBan());
        if (iconBan != null) lblIcon.setIcon(iconBan);
        
        pnlTop.add(lblMaBan, BorderLayout.WEST);
        pnlTop.add(lblIcon, BorderLayout.EAST);

        // 3. PHẦN GIỮA: THÔNG TIN CHI TIẾT
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblLoai = new JLabel("Loại: " + ban.getLoaiBan());
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoai.setForeground(Color.DARK_GRAY); // Thêm màu chữ cho dễ đọc trên nền vàng
        
        JLabel lblGhe = new JLabel("Số ghế: " + ban.getSoGhe());
        lblGhe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGhe.setForeground(Color.DARK_GRAY);
        ImageIcon iconUser = getIcon("user.png", 14, 14);
        if(iconUser != null) lblGhe.setIcon(iconUser);

        pnlCenter.add(lblLoai);
        pnlCenter.add(Box.createVerticalStrut(5));
        pnlCenter.add(lblGhe);

        // 4. PHẦN DƯỚI: TRẠNG THÁI TO RÕ
        JLabel lblStatusText = new JLabel(textTrangThai, SwingConstants.CENTER);
        lblStatusText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatusText.setForeground(Color.WHITE);
        lblStatusText.setOpaque(true);
        lblStatusText.setBackground(mauVien); // Nền chữ giống màu viền
        lblStatusText.setBorder(new EmptyBorder(5, 0, 5, 0)); // Padding cho chữ

        // 5. RÁP CÁC PHẦN VÀO CARD
        card.add(pnlTop, BorderLayout.NORTH);
        card.add(pnlCenter, BorderLayout.CENTER);
        card.add(lblStatusText, BorderLayout.SOUTH);

        // 6. XỬ LÝ SỰ KIỆN CLICK
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chonBan(ban, card);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                // Khi hover thì làm đậm màu viền hơn chút
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(mauVien.darker(), 3, true),
                    new EmptyBorder(9, 9, 9, 9)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Trả về trạng thái bình thường (trừ khi đang được chọn)
                if (banDangChon == null || !banDangChon.getMaBan().equals(ban.getMaBan())) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(mauVien, 2, true),
                        new EmptyBorder(10, 10, 10, 10)
                    ));
                }
            }
        });

        return card;
    }
    
    // --- SỬA HÀM LẤY ICON BÀN ---
    private ImageIcon getTableIcon(String loaiBan) {
        String fileName = "table_small.png"; // Mặc định
        switch (loaiBan) {
            case "Bàn 2 người": fileName = "table_small.png"; break;
            case "Bàn 4 người": fileName = "table_medium.png"; break;
            case "Bàn 6 người": fileName = "table_medium.png"; break;
            case "Bàn 8 người": fileName = "table_large.png"; break;
            case "Bàn 10 người": fileName = "table_large.png"; break;
            case "Phòng VIP": fileName = "table_vip.png"; break;
        }
        // Thử load ảnh 64x64 cho rõ
        return getIcon(fileName, 64, 64);
    }
    
  

 private void chonBan(Ban ban, JPanel card) {
     
     // ============================
     // BƯỚC 1: BỎ CHỌN CARD CŨ VÀ RESET VỀ MÀU THEO TRẠNG THÁI
     // ============================
     if (banDangChon != null) {
         for (int i = 0; i < pnlBanCards.getComponentCount(); i++) {
             Component comp = pnlBanCards.getComponent(i);
             if (comp instanceof JPanel) {
                 JPanel oldCard = (JPanel) comp;
                 
                 // Lấy bàn tương ứng với card này
                 if (i < danhSachBanHienTai.size()) {
                     Ban banCuaCard = danhSachBanHienTai.get(i);
                     String trangThai = banCuaCard.getTrangThai().trim();
                     
                     // ⭐⭐⭐ XÁC ĐỊNH MÀU NỀN THEO TRẠNG THÁI
                     Color mauNen;
                     Color mauVien;
                     
                     if (trangThai.equalsIgnoreCase("Trống")) {
                         mauNen = new Color(225, 255, 225);  // Xanh lá nhạt
                         mauVien = new Color(40, 167, 69);   // Xanh lá đậm
                     } else if (trangThai.equalsIgnoreCase("Đang sử dụng")) {
                         mauNen = new Color(255, 253, 230);  // Vàng nhạt
                         mauVien = new Color(255, 193, 7);   // Vàng đậm
                     } else if (trangThai.equalsIgnoreCase("Đã đặt")) {
                         mauNen = new Color(255, 235, 235);  // Đỏ nhạt
                         mauVien = new Color(220, 53, 69);   // Đỏ đậm
                     } else {
                         mauNen = Color.WHITE;               // Mặc định
                         mauVien = Color.GRAY;
                     }
                     
                     // Reset về màu theo trạng thái
                     oldCard.setBackground(mauNen);
                     oldCard.setBorder(BorderFactory.createCompoundBorder(
                         BorderFactory.createLineBorder(mauVien, 2, true),
                         new EmptyBorder(10, 10, 10, 10)
                     ));
                 }
             }
         }
     }
     
     // ============================
     // BƯỚC 2: CHỌN CARD MỚI VÀ HIGHLIGHT
     // ============================
     banDangChon = ban;
     
     // Highlight card được chọn bằng viền xanh dương đậm
     card.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(COLOR_ACCENT, 4, true), // Viền dày 4px
          new EmptyBorder(8, 8, 8, 8) // Giảm padding để bù cho viền dày hơn
     ));
     
     // Có thể thêm hiệu ứng làm sáng nhẹ (tùy chọn)
      Color mauHienTai = card.getBackground();
      card.setBackground(new Color(	102, 204, 255));
     
     
 }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(20, 10, 20, 20),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Thông Tin Đặt Bàn", // Bỏ icon trong title border vì thường lỗi font
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                COLOR_PRIMARY
            )
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;
        
        // Fields
        txtMaDatBan = createStyledTextField();
        txtMaDatBan.setEditable(false);
        txtMaDatBan.setBackground(new Color(240, 240, 240));
        
        txtTenKhachHang = createStyledTextField();
        txtSoDienThoai = createStyledTextField();
        txtSoNguoi = createStyledTextField();
        txtTienCoc = createStyledTextField();
        txtTienCoc.setText("0");
        
        dcNgayDat = new JDateChooser();
        dcNgayDat.setDateFormatString("dd/MM/yyyy");
        dcNgayDat.setPreferredSize(new Dimension(200, 38));
        dcNgayDat.setMinSelectableDate(new Date());
        dcNgayDat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        String[] gioList = new String[32];
        for (int i = 0; i < 32; i++) {
            int hour = i / 2 + 9;
            int minute = (i % 2) * 30;
            if (hour <= 24) {
                gioList[i] = String.format("%02d:%02d", hour == 24 ? 0 : hour, minute);
            }
        }
        cboGioDat = new JComboBox<>(gioList);
        cboGioDat.setPreferredSize(new Dimension(200, 38));
        cboGioDat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 8, 8, 8)
        ));
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setPreferredSize(new Dimension(300, 80));
        
        // Add to panel
        int row = 0;
        gbc.gridy = row++;
        panel.add(createFieldPanel("Mã đặt bàn", txtMaDatBan), gbc);
        
        gbc.gridy = row++;
        panel.add(createFieldPanel("Tên khách hàng *", txtTenKhachHang), gbc);
        
        gbc.gridy = row++;
        panel.add(createFieldPanel("Số điện thoại *", txtSoDienThoai), gbc);
        
        gbc.gridy = row++;
        panel.add(createFieldPanel("Số người *", txtSoNguoi), gbc);
        
        gbc.gridy = row++;
        panel.add(createFieldPanel("Ngày đặt *", dcNgayDat), gbc);
        
        gbc.gridy = row++;
        panel.add(createFieldPanel("Giờ đặt *", cboGioDat), gbc);
        
        gbc.gridy = row++;
        panel.add(createFieldPanel("Tiền cọc (VNĐ)", txtTienCoc), gbc);
        
        gbc.gridy = row++;
        panel.add(createFieldPanel("Ghi chú", scrollGhiChu), gbc);
        
        // Buttons
        gbc.gridy = row++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        // --- SỬA ICON CÁC NÚT ---
        btnDatBan = createStyledButton("Đặt Bàn", COLOR_PRIMARY, 140);
      

        btnLamMoi = createStyledButton("Làm Mới", new Color(158, 158, 158), 140);
       

        btnTimBan = createStyledButton("Tìm Bàn", COLOR_ACCENT, 120);
      
        buttonPanel.add(btnDatBan);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnTimBan);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createFieldPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 60, 60));
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 38));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    private JButton createStyledButton(String text, Color bgColor, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 42));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadBanCards(ArrayList<Ban> dsBan) {
        pnlBanCards.removeAll();
        danhSachBanHienTai.clear();
        
        for (Ban ban : dsBan) {
            pnlBanCards.add(createBanCard(ban));
            danhSachBanHienTai.add(ban);
        }
        
        pnlBanCards.revalidate();
        pnlBanCards.repaint();
    }
    
    private void addEventListeners() {
        cboKhuVuc.addActionListener(e -> locBanTheoKhuVuc());
        
        btnDatBan.addActionListener(e -> datBanMoi());
        btnLamMoi.addActionListener(e -> lamMoiForm());
        btnGoiMon.addActionListener(e -> moGiaoDienGoiMon());
        btnTimBan.addActionListener(e -> timBanPhuHop());
    }
    
    private void locBanTheoKhuVuc() {
        String khuVucChon = (String) cboKhuVuc.getSelectedItem();
        
        if ("Tất cả".equals(khuVucChon)) {
            loadBanCards(banDAO.getAllBan());
            return;
        }
        
        ArrayList<Ban> dsBanLoc = banDAO.getFilteredBan("khuVuc", khuVucChon);
        loadBanCards(dsBanLoc);
    }
    
    private void datBanMoi() {
        try {
            // 1. Validate chọn bàn
            if (banDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước!", "Chưa chọn bàn", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Validate trạng thái
            if (!"Trống".equals(banDangChon.getTrangThai())) {
                JOptionPane.showMessageDialog(this, "Bàn này đang bận/đã đặt. Vui lòng chọn bàn khác.", "Bàn bận", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 3. Tạo Entity BanDat
            String maDatBanMoi = banDatDAO.generateNewMaDatBan();
            BanDat banDat = validateAndCreateBanDat(maDatBanMoi); 
            
            // Xử lý khách hàng
            KhachHang kh = khachHangDAO.themHoacLayKhachHang(banDat.getKhachHang());
            banDat.setKhachHang(kh);
            
            // 4. LƯU VÀO CSDL
            if (banDatDAO.addBanDat(banDat)) {
                
                // CẬP NHẬT TRẠNG THÁI BÀN THÀNH 'Đã đặt'
                if (banDAO.updateTrangThaiBan(banDangChon.getMaBan(), "Đã đặt")) {
                    
                    // ⭐⭐⭐ QUAN TRỌNG: Cập nhật trạng thái của đối tượng banDangChon
                    banDangChon.setTrangThai("Đã đặt");
                    
                    JOptionPane.showMessageDialog(this, 
                        "Đặt bàn thành công! Mã: " + maDatBanMoi, 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Reset form và load lại danh sách bàn
                    lamMoiForm();
                    
                    // Thông báo cho các tab khác (nếu có)
                    if (refreshListener != null) {
                        refreshListener.onDataChanged(); 
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Lưu đặt bàn thành công nhưng lỗi cập nhật trạng thái bàn!");
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm vào CSDL", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    private BanDat validateAndCreateBanDat(String maDatBanHienTai) throws Exception {

        // --- 1. Validation Cơ bản ---
        if (txtTenKhachHang.getText().trim().isEmpty() || 
            txtSoDienThoai.getText().trim().isEmpty() ||
            txtSoNguoi.getText().trim().isEmpty() ||
            dcNgayDat.getDate() == null) {
            throw new Exception("Vui lòng điền đầy đủ thông tin bắt buộc (*)");
        }

        // --- 2. Validation SĐT ---
        String sdt = txtSoDienThoai.getText().trim();
        if (!sdt.matches("^0\\d{9}$")) {
            throw new Exception("Số điện thoại không hợp lệ! (10 số, bắt đầu bằng 0)");
        }

        // --- 3. Validation Số người ---
        int soNguoi;
        try {
            soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
            if (soNguoi <= 0) {
                throw new Exception("Số lượng khách phải lớn hơn 0.");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Số lượng khách không hợp lệ!");
        }

        // --- 4. Validation Tiền cọc ---
        double tienCoc = 0;
        try {
            String tienCocStr = txtTienCoc.getText().trim().replaceAll("[^0-9]", "");
            if (!tienCocStr.isEmpty()) {
                tienCoc = Double.parseDouble(tienCocStr);
            }
            if (tienCoc < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new Exception("Tiền cọc không hợp lệ!");
        }

        // --- 5. Validation Bàn được chọn ---
        if (banDangChon == null) {
            throw new Exception("Vui lòng chọn bàn từ danh sách!");
        }

        Ban banDuocChon = banDAO.getBanById(banDangChon.getMaBan());
        LocalDate ngayDat = dcNgayDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime gioDat = LocalTime.parse(cboGioDat.getSelectedItem().toString());
        String trangThai = "Đã đặt";
        String ghiChu = txtGhiChu.getText();

        // --- 6. Chuẩn bị Khách hàng ---
        String tenKH = txtTenKhachHang.getText().trim();
        KhachHang khachHang = khachHangDAO.timKhachHangTheoSDT(sdt);

        if (khachHang == null) {
            khachHang = new KhachHang(null, tenKH, sdt, "", false);
        } else {
            khachHang.setHoTenKH(tenKH);
        }

        // --- 7. Tạo đối tượng BanDat ---

        // ⭐⭐ QUAN TRỌNG: thêm tham số giờCheckIn = null (để đồng nhất với constructor mới)
        BanDat banDat = new BanDat(
            maDatBanHienTai,
            khachHang,
            banDuocChon,
            ngayDat,
            gioDat,
            soNguoi,
            tienCoc,
            trangThai,
            ghiChu,
            null   // <-- ⭐ GIỜ CHECK IN => LUÔN NULL KHI ĐẶT TRƯỚC
        );

        return banDat;
    }

    
    private void lamMoiForm() {
        txtMaDatBan.setText(banDatDAO.generateNewMaDatBan());
        txtTenKhachHang.setText("");
        txtSoDienThoai.setText("");
        txtSoNguoi.setText("");
        txtTienCoc.setText("0");
        dcNgayDat.setDate(new Date());
        cboGioDat.setSelectedIndex(0);
        txtGhiChu.setText("");
        
        banDangChon = null;
        
        // Reset combobox khu vực về Tất cả
        cboKhuVuc.setSelectedIndex(0);
        
        // ⭐ QUAN TRỌNG: Load lại toàn bộ bàn với trạng thái mới nhất từ DB
        ArrayList<Ban> dsBanMoi = banDAO.getAllBan();
        loadBanCards(dsBanMoi);
      
    }
    
    private void moGiaoDienGoiMon() {

        // 1. Kiểm tra đã chọn bàn chưa
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một bàn trước khi gọi món!",
                    "Chưa chọn bàn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maBan = banDangChon.getMaBan();
        String trangThai = banDangChon.getTrangThai();

        try {
            // ============================
            // TRƯỜNG HỢP 1: BÀN TRỐNG -> MỞ BÀN MỚI
            // ============================
            if ("Trống".equals(trangThai)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bàn " + maBan + " đang trống. Mở bàn để gọi món?",
                        "Xác nhận mở bàn",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Tạo dữ liệu mở bàn trực tiếp
                    String maDatBanMoi = banDatDAO.generateNewMaDatBan();
                    KhachHang kh = new KhachHang(null, "Khách lẻ", "0000000000", "", false);
                    kh = khachHangDAO.themHoacLayKhachHang(kh);
                    Ban banObj = banDAO.getBanById(maBan);
                    LocalTime gioVao = LocalTime.now();

                    BanDat bdMoi = new BanDat(maDatBanMoi, kh, banObj, LocalDate.now(), LocalTime.now(), 1, 0, "Đang sử dụng", "Khách vào trực tiếp", gioVao);

                    // Thêm vào BanDat
                    if(banDatDAO.addBanDatTrucTiep(bdMoi)) {
                        banDatDAO.updateGioCheckIn(maDatBanMoi, gioVao);
                        
                        // --- QUAN TRỌNG: CẬP NHẬT TRẠNG THÁI BÀN ---
                        if(banDAO.updateTrangThaiBan(maBan, "Đang sử dụng")) {
                            
                            // 1. Cập nhật đối tượng hiện tại
                            banDangChon.setTrangThai("Đang sử dụng");
                            
                            // 2. Load lại toàn bộ giao diện để bàn đổi màu Đỏ/Vàng
                            // (Phải load lại từ DB để đảm bảo đồng bộ nhất)
                            ArrayList<Ban> dsBanMoi = banDAO.getAllBan();
                            loadBanCards(dsBanMoi);
                            
                            // 3. Mở cửa sổ gọi món
                            moCuaSoGoiMon(maBan);
                            
                        } else {
                            JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái bàn!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi tạo phiếu đặt bàn!");
                    }
                }
                return;
            }

            // ============================
            // TRƯỜNG HỢP 2: BÀN ĐÃ ĐẶT -> CHUYỂN SANG ĐANG SỬ DỤNG
            // ============================
            if ("Đã đặt".equals(trangThai)) {
                // Nếu bàn "Đã đặt" (khách đặt trước giờ mới đến) -> Chuyển sang "Đang sử dụng"
                if(banDAO.updateTrangThaiBan(maBan, "Đang sử dụng")) {
                     
                     // Cập nhật trạng thái
                     banDangChon.setTrangThai("Đang sử dụng");
                     
                     // Load lại giao diện ngay lập tức
                     ArrayList<Ban> dsBanMoi = banDAO.getAllBan();
                     loadBanCards(dsBanMoi);
                     
                     moCuaSoGoiMon(maBan);
                }
                return;
            }
            
            // ============================
            // TRƯỜNG HỢP 3: BÀN ĐANG SỬ DỤNG
            // ============================
            if ("Đang sử dụng".equals(trangThai)) {
                moCuaSoGoiMon(maBan);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xử lý: " + e.getMessage());
        }
    }
    // Hàm phụ trợ để mở JFrame Gọi Món (Tách ra cho gọn)
    private void moCuaSoGoiMon(String maBan) {
        // Lấy cửa sổ cha hiện tại
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        if (parentFrame != null) {
//            parentFrame.setVisible(false); // Ẩn màn hình đặt bàn
            
            // Tạo frame gọi món mới
            JFrame goiMonFrame = new JFrame("Gọi Món - Bàn " + maBan);
            goiMonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            goiMonFrame.setSize(1200, 800);
            goiMonFrame.setLocationRelativeTo(null);
            
            try {
                // Khởi tạo giao diện gọi món (Giả định bạn đã có class GoiMon_GUI)
                // Lưu ý: Class GoiMon_GUI phải có constructor nhận vào mã bàn
                GoiMon_GUI goiMonPanel = new GoiMon_GUI(maBan);
                goiMonFrame.setContentPane(goiMonPanel);
                
                // Sự kiện khi đóng form gọi món -> Hiện lại form đặt bàn
                goiMonFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        parentFrame.setVisible(true);
                        parentFrame.toFront();
                        // Refresh lại danh sách bàn để cập nhật trạng thái mới
                        loadBanCards(banDAO.getAllBan());
                    }
                });
                
                goiMonFrame.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi mở giao diện gọi món: " + e.getMessage());
                parentFrame.setVisible(true);
            }
        }
    }

    // Hàm cập nhật trạng thái bàn xuống CSDL và giao diện
    private void capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
        try {
            if (banDAO.updateTrangThaiBan(maBan, trangThaiMoi)) {
                // Cập nhật đối tượng hiện tại
                if (banDangChon != null && banDangChon.getMaBan().equals(maBan)) {
                    banDangChon.setTrangThai(trangThaiMoi);
                }
                // Tải lại giao diện
                loadBanCards(banDAO.getAllBan());
            } else {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái bàn!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 // --- HÀM TÌM BÀN (ĐÃ NÂNG CẤP) ---
    private void timBanPhuHop() {
        String soNguoiStr = txtSoNguoi.getText().trim();
        if (soNguoiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số người cần tìm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtSoNguoi.requestFocus();
            return;
        }

        int soNguoi = 0;
        try {
            soNguoi = Integer.parseInt(soNguoiStr);
            if(soNguoi <= 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số người không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy tất cả bàn
        ArrayList<Ban> allBan = banDAO.getAllBan();
        ArrayList<Ban> result = new ArrayList<>();

        // --- CẤU HÌNH ĐỘ LỆCH GHẾ ---
        // Cho phép dư tối đa bao nhiêu ghế. 
        // Ví dụ: Khách 2 người. 
        // Nếu MAX_DU_THUA = 4 -> Gợi ý bàn 2, 4, 6. (Bàn 8, 10 sẽ bị loại)
        int MAX_DU_THUA = 4; 

        for (Ban b : allBan) {
            int soGhe = b.getSoGhe();
            
            // Logic lọc nâng cao:
            // 1. Trạng thái phải là Trống
            // 2. Số ghế phải ĐỦ (soGhe >= soNguoi)
            // 3. Số ghế KHÔNG QUÁ DƯ THỪA (soGhe <= soNguoi + MAX_DU_THUA)
            boolean dkTrangThai = "Trống".equals(b.getTrangThai());
            boolean dkDuCho = soGhe >= soNguoi;
            boolean dkKhongLangPhi = soGhe <= (soNguoi + MAX_DU_THUA);
            
            // Nếu bạn muốn tìm chính xác hơn nữa, có thể bỏ comment dòng dưới để lọc theo khu vực đang chọn
            // String khuVucHienTai = cboKhuVuc.getSelectedItem().toString();
            // boolean dkKhuVuc = "Tất cả".equals(khuVucHienTai) || b.getKhuVuc().equals(khuVucHienTai);

            if (dkTrangThai && dkDuCho && dkKhongLangPhi) {
                result.add(b);
            }
        }
        
        // --- SẮP XẾP KẾT QUẢ ---
        // Sắp xếp để các bàn vừa vặn nhất (số ghế nhỏ nhất) hiện lên đầu danh sách
        result.sort((b1, b2) -> Integer.compare(b1.getSoGhe(), b2.getSoGhe()));

        // --- HIỂN THỊ ---
        if (result.isEmpty()) {
            // Nếu lọc quá chặt không ra bàn nào, thử tìm các bàn lớn hơn nữa (Fallback)
            // Để tránh trường hợp khách 6 người mà chỉ còn bàn 12 người, nếu lọc chặt quá sẽ báo không tìm thấy.
             int confirm = JOptionPane.showConfirmDialog(this, 
                "Không tìm thấy bàn vừa vặn (dư < " + MAX_DU_THUA + " ghế).\nBạn có muốn xem các bàn lớn hơn không?",
                "Gợi ý mở rộng",
                JOptionPane.YES_NO_OPTION);
             
             if (confirm == JOptionPane.YES_OPTION) {
                 // Tìm lại nhưng bỏ điều kiện dkKhongLangPhi
                 result.clear();
                 for (Ban b : allBan) {
                     if ("Trống".equals(b.getTrangThai()) && b.getSoGhe() >= soNguoi) {
                         result.add(b);
                     }
                 }
                 result.sort((b1, b2) -> Integer.compare(b1.getSoGhe(), b2.getSoGhe()));
                 loadBanCards(result);
             }
        } else {
            loadBanCards(result); // Hiển thị danh sách đã lọc
            JOptionPane.showMessageDialog(this, 
                "Tìm thấy " + result.size() + " bàn phù hợp (Dư tối đa " + MAX_DU_THUA + " ghế).", 
                "Kết quả", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Listener methods
    public void setDataRefreshListener(DataRefreshListener listener) {
        this.refreshListener = listener;
    }
    
    public void refreshData() {
        loadBanCards(banDAO.getAllBan());
    }
    
    public static void main(String[] args) throws SQLException {
        ConnectDB.getInstance().connect();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Đặt Bàn");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);
            frame.add(new BanDat_GUI());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}