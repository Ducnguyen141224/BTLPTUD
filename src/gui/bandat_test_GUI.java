//package gui;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.*;
//import java.sql.SQLException;
//
//import com.toedter.calendar.JDateChooser;
//import entity.Ban;
//import entity.BanDat;
//import entity.KhachHang;
//import dao.Ban_DAO; 
//import dao.BanDat_DAO;
//import dao.KhachHang_DAO;
//import connectDB.ConnectDB;
//
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.ArrayList;
//import java.util.Locale;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.ZoneId;
//
//public class BanDat_GUI extends JPanel {
//    
//    private JTextField txtMaDatBan, txtTenKhachHang, txtSoDienThoai, txtSoNguoi, txtTienCoc; 
//    private JDateChooser dcNgayDat;
//    private JComboBox<String> cboGioDat, cboKhuVuc; 
//    private JTextArea txtGhiChu;
//    
//    private JButton btnDatBan, btnLamMoi, btnGoiMon; 
//    
//    private JPanel pnlBanCards; // Panel chứa các card bàn
//    private Ban banDangChon = null; // Bàn được chọn
//    
//    private Ban_DAO banDAO = new Ban_DAO();
//    private BanDat_DAO banDatDAO = new BanDat_DAO();
//    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
//
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//    private DecimalFormat currencyFormat = new DecimalFormat("#,###");
//
//    private ArrayList<Ban> danhSachBanHienTai = new ArrayList<>();
//    private DataRefreshListener refreshListener;
//    
//    // Màu sắc theme
//    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
//    private final Color COLOR_SECONDARY = new Color(255, 152, 0);
//    private final Color COLOR_ACCENT = new Color(33, 150, 243);
//    private final Color COLOR_BG = new Color(250, 250, 250);
//    private final Color COLOR_CARD_BG = Color.WHITE;
//    
//    public BanDat_GUI() {
//        setLayout(new BorderLayout(0, 0));
//        setBackground(COLOR_BG);
//        
//        // Header với gradient
//        JPanel headerPanel = createHeaderPanel();
//        add(headerPanel, BorderLayout.NORTH);
//        
//        // Main content
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        splitPane.setDividerLocation(750);
//        splitPane.setDividerSize(8);
//        splitPane.setBorder(null);
//        
//        // Left - Danh sách bàn dạng card
//        JPanel leftPanel = createBanCardsPanel();
//        
//        // Right - Form đặt bàn
//        JPanel rightPanel = createFormPanel();
//        
//        splitPane.setLeftComponent(leftPanel);
//        splitPane.setRightComponent(rightPanel);
//        
//        add(splitPane, BorderLayout.CENTER);
//        
//        // Load dữ liệu
//        loadBanCards(banDAO.getAllBan());
//        lamMoiForm();
//        addEventListeners();
//    }
//    
//    private JPanel createHeaderPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setPreferredSize(new Dimension(0, 80));
//        panel.setBackground(new Color(76, 175, 80));
//        panel.setBorder(new EmptyBorder(15, 30, 15, 30));
//        
//        // Title
//        JLabel lblTitle = new JLabel("🍽️ ĐẶT BÀN");
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
//        lblTitle.setForeground(Color.WHITE);
//        
//        // Nút Gọi món
//        btnGoiMon = new JButton("🍜 Gọi Món");
//        btnGoiMon.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnGoiMon.setPreferredSize(new Dimension(140, 45));
//        btnGoiMon.setBackground(COLOR_SECONDARY);
//        btnGoiMon.setForeground(Color.WHITE);
//        btnGoiMon.setFocusPainted(false);
//        btnGoiMon.setBorderPainted(false);
//        btnGoiMon.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        
//        panel.add(lblTitle, BorderLayout.WEST);
//        panel.add(btnGoiMon, BorderLayout.EAST);
//        
//        return panel;
//    }
//    
//    private JPanel createBanCardsPanel() {
//        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
//        mainPanel.setBackground(COLOR_BG);
//        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 10));
//        
//        // Filter panel
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
//        filterPanel.setBackground(COLOR_BG);
//        
//        JLabel lblFilter = new JLabel("Khu vực:");
//        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        
//        cboKhuVuc = new JComboBox<>(new String[]{"Tất cả", "Tầng 1", "Tầng 2", "Tầng 3"});
//        cboKhuVuc.setPreferredSize(new Dimension(150, 35));
//        cboKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        
//        filterPanel.add(lblFilter);
//        filterPanel.add(cboKhuVuc);
//        
//        mainPanel.add(filterPanel, BorderLayout.NORTH);
//        
//        // Cards container với scroll
//        pnlBanCards = new JPanel();
//        pnlBanCards.setLayout(new GridLayout(0, 3, 15, 15)); // 3 cột
//        pnlBanCards.setBackground(COLOR_BG);
//        
//        JScrollPane scrollPane = new JScrollPane(pnlBanCards);
//        scrollPane.setBorder(null);
//        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//        
//        mainPanel.add(scrollPane, BorderLayout.CENTER);
//        
//        return mainPanel;
//    }
//    
//    private JPanel createBanCard(Ban ban) {
//        JPanel card = new JPanel();
//        card.setLayout(new BorderLayout(10, 10));
//        card.setBackground(COLOR_CARD_BG);
//        card.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true),
//            new EmptyBorder(15, 15, 15, 15)
//        ));
//        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        card.setPreferredSize(new Dimension(220, 180));
//        
//        // Icon bàn
//        JLabel lblIcon = new JLabel(getTableIcon(ban.getLoaiBan()), SwingConstants.CENTER);
//        
//        // Thông tin bàn
//        JPanel infoPanel = new JPanel();
//        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
//        infoPanel.setOpaque(false);
//        
//        JLabel lblMaBan = new JLabel(ban.getMaBan());
//        lblMaBan.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        lblMaBan.setAlignmentX(Component.CENTER_ALIGNMENT);
//        
//        JLabel lblLoaiBan = new JLabel(ban.getLoaiBan());
//        lblLoaiBan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lblLoaiBan.setForeground(new Color(100, 100, 100));
//        lblLoaiBan.setAlignmentX(Component.CENTER_ALIGNMENT);
//        
//        JLabel lblSoGhe = new JLabel("👥 " + ban.getSoGhe() + " chỗ");
//        lblSoGhe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lblSoGhe.setAlignmentX(Component.CENTER_ALIGNMENT);
//        
//        // Trạng thái
//        JLabel lblTrangThai = new JLabel(ban.getTrangThai());
//        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        lblTrangThai.setAlignmentX(Component.CENTER_ALIGNMENT);
//        lblTrangThai.setOpaque(true);
//        lblTrangThai.setBorder(new EmptyBorder(5, 15, 5, 15));
//        
//        // Màu theo trạng thái
//        if ("Trống".equals(ban.getTrangThai())) {
//            lblTrangThai.setBackground(new Color(200, 255, 200));
//            lblTrangThai.setForeground(new Color(0, 120, 0));
//            card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(76, 175, 80), 2, true),
//                new EmptyBorder(15, 15, 15, 15)
//            ));
//        } else if ("Đã đặt".equals(ban.getTrangThai())) {
//            lblTrangThai.setBackground(new Color(255, 200, 200));
//            lblTrangThai.setForeground(new Color(180, 0, 0));
//        } else {
//            lblTrangThai.setBackground(new Color(255, 255, 150));
//            lblTrangThai.setForeground(new Color(150, 100, 0));
//        }
//        
//        infoPanel.add(lblMaBan);
//        infoPanel.add(Box.createVerticalStrut(5));
//        infoPanel.add(lblLoaiBan);
//        infoPanel.add(Box.createVerticalStrut(3));
//        infoPanel.add(lblSoGhe);
//        infoPanel.add(Box.createVerticalStrut(8));
//        infoPanel.add(lblTrangThai);
//        
//        card.add(lblIcon, BorderLayout.NORTH);
//        card.add(infoPanel, BorderLayout.CENTER);
//        
//        // Click event
//        card.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                chonBan(ban, card);
//            }
//            
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                card.setBackground(new Color(245, 245, 255));
//            }
//            
//            @Override
//            public void mouseExited(MouseEvent e) {
//                if (banDangChon == null || !banDangChon.getMaBan().equals(ban.getMaBan())) {
//                    card.setBackground(COLOR_CARD_BG);
//                }
//            }
//        });
//        
//        return card;
//    }
//    
//    private String getTableIcon(String loaiBan) {
//        // Sử dụng emoji hoặc text làm icon
//        switch (loaiBan) {
//            case "Bàn nhỏ": return "🪑";
//            case "Bàn vừa": return "🍽️";
//            case "Bàn lớn": return "🍴";
//            case "Phòng VIP": return "👑";
//            default: return "🪑";
//        }
//    }
//    
//    private void chonBan(Ban ban, JPanel card) {
//        // --- ĐOẠN SỬA ĐỔI: Bỏ phần if check chặn chọn bàn ---
//        
//        // Bỏ chọn card cũ (nếu có)
//        if (banDangChon != null) {
//            // Tìm component cũ để reset màu (Duyệt qua danh sách các card)
//            // Lưu ý: card cũ có thể không còn là đối tượng 'card' hiện tại
//            // Cách đơn giản nhất là reset màu tất cả hoặc lưu tham chiếu card cũ
//            for (Component comp : pnlBanCards.getComponents()) {
//                if (comp instanceof JPanel) {
//                    comp.setBackground(COLOR_CARD_BG);
//                    // Reset viền nếu cần
//                    ((JPanel) comp).setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true),
//                        new EmptyBorder(15, 15, 15, 15)
//                    ));
//                }
//            }
//        }
//        
//        // Chọn card mới
//        banDangChon = ban;
//        
//        // Highlight card được chọn
//        card.setBackground(new Color(200, 230, 255)); // Màu xanh nhạt để biết đang chọn
//        card.setBorder(BorderFactory.createCompoundBorder(
//             BorderFactory.createLineBorder(COLOR_PRIMARY, 2, true),
//             new EmptyBorder(15, 15, 15, 15)
//        ));
//        
//        // Debug: In ra console để kiểm tra
//        System.out.println("Đã chọn bàn: " + ban.getMaBan() + " - Trạng thái: " + ban.getTrangThai());
//    }    
//    private JPanel createFormPanel() {
//        JPanel panel = new JPanel();
//        panel.setLayout(new GridBagLayout());
//        panel.setBackground(COLOR_CARD_BG);
//        panel.setBorder(BorderFactory.createCompoundBorder(
//            new EmptyBorder(20, 10, 20, 20),
//            BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                "📋 Thông Tin Đặt Bàn",
//                TitledBorder.LEFT,
//                TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 16),
//                COLOR_PRIMARY
//            )
//        ));
//        
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(8, 10, 8, 10);
//        gbc.weightx = 1.0;
//        
//        // Fields
//        txtMaDatBan = createStyledTextField();
//        txtMaDatBan.setEditable(false);
//        txtMaDatBan.setBackground(new Color(240, 240, 240));
//        
//        txtTenKhachHang = createStyledTextField();
//        txtSoDienThoai = createStyledTextField();
//        txtSoNguoi = createStyledTextField();
//        txtTienCoc = createStyledTextField();
//        txtTienCoc.setText("0");
//        
//        dcNgayDat = new JDateChooser();
//        dcNgayDat.setDateFormatString("dd/MM/yyyy");
//        dcNgayDat.setPreferredSize(new Dimension(200, 38));
//        dcNgayDat.setMinSelectableDate(new Date());
//        dcNgayDat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        
//        String[] gioList = new String[32];
//        for (int i = 0; i < 32; i++) {
//            int hour = i / 2 + 9;
//            int minute = (i % 2) * 30;
//            if (hour <= 24) {
//                gioList[i] = String.format("%02d:%02d", hour == 24 ? 0 : hour, minute);
//            }
//        }
//        cboGioDat = new JComboBox<>(gioList);
//        cboGioDat.setPreferredSize(new Dimension(200, 38));
//        cboGioDat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        
//        txtGhiChu = new JTextArea(3, 20);
//        txtGhiChu.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(200, 200, 200)),
//            new EmptyBorder(8, 8, 8, 8)
//        ));
//        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        txtGhiChu.setLineWrap(true);
//        txtGhiChu.setWrapStyleWord(true);
//        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
//        scrollGhiChu.setPreferredSize(new Dimension(300, 80));
//        
//        // Add to panel
//        int row = 0;
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Mã đặt bàn", txtMaDatBan), gbc);
//        
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Tên khách hàng *", txtTenKhachHang), gbc);
//        
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Số điện thoại *", txtSoDienThoai), gbc);
//        
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Số người *", txtSoNguoi), gbc);
//        
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Ngày đặt *", dcNgayDat), gbc);
//        
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Giờ đặt *", cboGioDat), gbc);
//        
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Tiền cọc (VNĐ)", txtTienCoc), gbc);
//        
//        gbc.gridy = row++;
//        panel.add(createFieldPanel("Ghi chú", scrollGhiChu), gbc);
//        
//        // Buttons
//        gbc.gridy = row++;
//        gbc.insets = new Insets(20, 10, 10, 10);
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
//        buttonPanel.setOpaque(false);
//        
//        btnDatBan = createStyledButton("✅ Đặt Bàn", COLOR_PRIMARY, 140);
//        btnLamMoi = createStyledButton("🔄 Làm Mới", new Color(158, 158, 158), 140);
//        
//        buttonPanel.add(btnDatBan);
//        buttonPanel.add(btnLamMoi);
//        
//        panel.add(buttonPanel, gbc);
//        
//        return panel;
//    }
//    
//    private JPanel createFieldPanel(String label, JComponent component) {
//        JPanel panel = new JPanel(new BorderLayout(5, 5));
//        panel.setOpaque(false);
//        
//        JLabel lbl = new JLabel(label);
//        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        lbl.setForeground(new Color(60, 60, 60));
//        
//        panel.add(lbl, BorderLayout.NORTH);
//        panel.add(component, BorderLayout.CENTER);
//        
//        return panel;
//    }
//    
//    private JTextField createStyledTextField() {
//        JTextField textField = new JTextField();
//        textField.setPreferredSize(new Dimension(200, 38));
//        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        textField.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(200, 200, 200)),
//            new EmptyBorder(5, 10, 5, 10)
//        ));
//        return textField;
//    }
//    
//    private JButton createStyledButton(String text, Color bgColor, int width) {
//        JButton button = new JButton(text);
//        button.setPreferredSize(new Dimension(width, 42));
//        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        button.setBackground(bgColor);
//        button.setForeground(Color.WHITE);
//        button.setFocusPainted(false);
//        button.setBorderPainted(false);
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        
//        button.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) {
//                button.setBackground(bgColor.darker());
//            }
//            public void mouseExited(MouseEvent e) {
//                button.setBackground(bgColor);
//            }
//        });
//        
//        return button;
//    }
//    
//    private void loadBanCards(ArrayList<Ban> dsBan) {
//        pnlBanCards.removeAll();
//        danhSachBanHienTai.clear();
//        
//        for (Ban ban : dsBan) {
//            pnlBanCards.add(createBanCard(ban));
//            danhSachBanHienTai.add(ban);
//        }
//        
//        pnlBanCards.revalidate();
//        pnlBanCards.repaint();
//    }
//    
//    private void addEventListeners() {
//        cboKhuVuc.addActionListener(e -> locBanTheoKhuVuc());
//        
//        btnDatBan.addActionListener(e -> datBanMoi());
//        btnLamMoi.addActionListener(e -> lamMoiForm());
//        btnGoiMon.addActionListener(e -> moGiaoDienGoiMon());
//    }
//    
//    private void locBanTheoKhuVuc() {
//        String khuVucChon = (String) cboKhuVuc.getSelectedItem();
//        
//        if ("Tất cả".equals(khuVucChon)) {
//            loadBanCards(banDAO.getAllBan());
//            return;
//        }
//        
//        ArrayList<Ban> dsBanLoc = banDAO.getFilteredBan("khuVuc", khuVucChon);
//        loadBanCards(dsBanLoc);
//    }
//    
//    private void datBanMoi() {
//        try {
//            // 1. Validate chọn bàn
//            if (banDangChon == null) {
//                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước!", "Chưa chọn bàn", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            // 2. Validate trạng thái (Ngăn đặt chồng lên bàn đang có khách ngồi ăn)
//  
//            if (!"Trống".equals(banDangChon.getTrangThai())) {
//                JOptionPane.showMessageDialog(this, "Bàn này đang bận/đã đặt. Vui lòng chọn bàn khác.", "Bàn bận", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            
//            // 3. Tạo Entity
//            String maDatBanMoi = banDatDAO.generateNewMaDatBan();
//            BanDat banDat = validateAndCreateBanDat(maDatBanMoi); // Hàm validate giữ nguyên
//            
//            // Xử lý khách hàng
//            KhachHang kh = khachHangDAO.themHoacLayKhachHang(banDat.getKhachHang());
//            banDat.setKhachHang(kh);
//            
//            // 4. LƯU VÀO CSDL (Quan trọng)
//            if (banDatDAO.addBanDat(banDat)) {
//                JOptionPane.showMessageDialog(this, 
//                    "Đặt bàn thành công! \nMã đơn: " + maDatBanMoi + "\n(Thông tin đã chuyển qua Danh Sách Đặt Bàn)", 
//                    "Thành công", 
//                    JOptionPane.INFORMATION_MESSAGE);
//                
//          
//                
//                lamMoiForm();
//              
//                if (refreshListener != null) {
//                    refreshListener.onDataChanged(); 
//                }
//                
//            } else {
//                JOptionPane.showMessageDialog(this, "Lỗi thêm vào CSDL", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
//        }
//    }
//    
//    private BanDat validateAndCreateBanDat(String maDatBanHienTai) throws Exception {
//
//        // --- 1. Validation Cơ bản ---
//        if (txtTenKhachHang.getText().trim().isEmpty() || 
//            txtSoDienThoai.getText().trim().isEmpty() ||
//            txtSoNguoi.getText().trim().isEmpty() ||
//            dcNgayDat.getDate() == null) {
//            throw new Exception("Vui lòng điền đầy đủ thông tin bắt buộc (*)");
//        }
//
//        // --- 2. Validation SĐT ---
//        String sdt = txtSoDienThoai.getText().trim();
//        if (!sdt.matches("^0\\d{9}$")) {
//            throw new Exception("Số điện thoại không hợp lệ! (10 số, bắt đầu bằng 0)");
//        }
//
//        // --- 3. Validation Số người ---
//        int soNguoi;
//        try {
//            soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
//            if (soNguoi <= 0) {
//                throw new Exception("Số lượng khách phải lớn hơn 0.");
//            }
//        } catch (NumberFormatException e) {
//            throw new Exception("Số lượng khách không hợp lệ!");
//        }
//
//        // --- 4. Validation Tiền cọc ---
//        double tienCoc = 0;
//        try {
//            String tienCocStr = txtTienCoc.getText().trim().replaceAll("[^0-9]", "");
//            if (!tienCocStr.isEmpty()) {
//                tienCoc = Double.parseDouble(tienCocStr);
//            }
//            if (tienCoc < 0) throw new NumberFormatException();
//        } catch (NumberFormatException e) {
//            throw new Exception("Tiền cọc không hợp lệ!");
//        }
//
//        // --- 5. Validation Bàn được chọn ---
//        if (banDangChon == null) {
//            throw new Exception("Vui lòng chọn bàn từ danh sách!");
//        }
//
//        Ban banDuocChon = banDAO.getBanById(banDangChon.getMaBan());
//        LocalDate ngayDat = dcNgayDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalTime gioDat = LocalTime.parse(cboGioDat.getSelectedItem().toString());
//        String trangThai = "Đã đặt";
//        String ghiChu = txtGhiChu.getText();
//
//        // --- 6. Chuẩn bị Khách hàng ---
//        String tenKH = txtTenKhachHang.getText().trim();
//        KhachHang khachHang = khachHangDAO.timKhachHangTheoSDT(sdt);
//
//        if (khachHang == null) {
//            khachHang = new KhachHang(null, tenKH, sdt, "", false);
//        } else {
//            khachHang.setHoTenKH(tenKH);
//        }
//
//        // --- 7. Tạo đối tượng BanDat ---
//
//        // ⭐⭐ QUAN TRỌNG: thêm tham số giờCheckIn = null (để đồng nhất với constructor mới)
//        BanDat banDat = new BanDat(
//            maDatBanHienTai,
//            khachHang,
//            banDuocChon,
//            ngayDat,
//            gioDat,
//            soNguoi,
//            tienCoc,
//            trangThai,
//            ghiChu,
//            null   // <-- ⭐ GIỜ CHECK IN => LUÔN NULL KHI ĐẶT TRƯỚC
//        );
//
//        return banDat;
//    }
//
//    
//    private void lamMoiForm() {
//        txtMaDatBan.setText(banDatDAO.generateNewMaDatBan());
//        txtTenKhachHang.setText("");
//        txtSoDienThoai.setText("");
//        txtSoNguoi.setText("");
//        txtTienCoc.setText("0");
//        dcNgayDat.setDate(new Date());
//        cboGioDat.setSelectedIndex(0);
//        txtGhiChu.setText("");
//        
//        banDangChon = null;
//        
//        // Bỏ highlight các card
//        for (Component comp : pnlBanCards.getComponents()) {
//            if (comp instanceof JPanel) {
//                comp.setBackground(COLOR_CARD_BG);
//            }
//        }
//    }
//    
// // Thay thế hàm moGiaoDienGoiMon() cũ bằng hàm này
//    private void moGiaoDienGoiMon() {
//
//        // 1. Kiểm tra đã chọn bàn chưa
//        if (banDangChon == null) {
//            JOptionPane.showMessageDialog(this,
//                    "Vui lòng chọn một bàn trước khi gọi món!",
//                    "Chưa chọn bàn",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        String maBan = banDangChon.getMaBan();
//        String trangThai = banDangChon.getTrangThai();
//
//        try {
//
//            // ⭐ LẤY THÔNG TIN ĐẶT BÀN ĐANG SỬ DỤNG (CÓ GIỜ CHECKIN)
//            BanDat banDatHienTai = banDatDAO.getBanDatDangSuDung(maBan);
//
//            if (banDatHienTai != null) {
//                System.out.println("--- BÀN ĐANG SỬ DỤNG ---");
//                System.out.println("Mã đặt bàn: " + banDatHienTai.getMaDatBan());
//                System.out.println("Giờ check-in: " + banDatHienTai.getGioCheckIn());
//            }
//
//            // ============================
//            // ⭐ TRƯỜNG HỢP BÀN TRỐNG
//            // ============================
//            if ("Trống".equals(trangThai)) {
//
//                int confirm = JOptionPane.showConfirmDialog(this,
//                        "Bàn " + maBan + " đang trống.\n"
//                        + "Bạn có muốn mở bàn và bắt đầu gọi món không?",
//                        "Xác nhận mở bàn",
//                        JOptionPane.YES_NO_OPTION);
//
//                if (confirm == JOptionPane.YES_OPTION) {
//
//                    // 1. Tạo mã đặt bàn mới
//                    String maDatBanMoi = banDatDAO.generateNewMaDatBan();
//
//                    // 2. Tạo khách hàng mặc định
//                    KhachHang kh = new KhachHang(null, "Khách lẻ", "0000000000", "", false);
//                    kh = khachHangDAO.themHoacLayKhachHang(kh);
//
//                    // 3. Chuẩn bị đối tượng bàn
//                    Ban banObj = banDAO.getBanById(maBan);
//                    LocalTime gioVao = LocalTime.now();
//
//                    // 4. Tạo bản ghi đặt bàn TRỰC TIẾP (không kiểm tra giờ)
//                    BanDat bdMoi = new BanDat(
//                            maDatBanMoi,
//                            kh,
//                            banObj,
//                            LocalDate.now(),
//                            LocalTime.now(),
//                            1,
//                            0,
//                            "Đang sử dụng",
//                            "Khách vào trực tiếp",
//                            gioVao
//                    );
//
//                    // 5. THÊM VÀO DB (KHÔNG KIỂM TRA TRÙNG GIỜ)
//                    banDatDAO.addBanDatTrucTiep(bdMoi);
//
//                    // 6. Cập nhật giờ checkin trong DB
//                    banDatDAO.updateGioCheckIn(maDatBanMoi, gioVao);
//
//                    // 7. Cập nhật trạng thái bàn
//                    capNhatTrangThaiBan(maBan, "Đang sử dụng");
//
//                    // 8. Mở giao diện gọi món
//                    moCuaSoGoiMon(maBan);
//                }
//
//                return;
//            }
//
//            // ============================
//            // ⭐ BÀN ĐÃ ĐẶT hoặc ĐANG SỬ DỤNG
//            // ============================
//            if ("Đã đặt".equals(trangThai) || "Đang sử dụng".equals(trangThai)) {
//                moCuaSoGoiMon(maBan);
//                return;
//            }
//
//            JOptionPane.showMessageDialog(this,
//                    "Không thể gọi món cho bàn có trạng thái: " + trangThai,
//                    "Thông báo",
//                    JOptionPane.WARNING_MESSAGE);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi xử lý gọi món: " + e.getMessage());
//        }
//    }
//
//
//    // Hàm phụ trợ để mở JFrame Gọi Món (Tách ra cho gọn)
//    private void moCuaSoGoiMon(String maBan) {
//        // Lấy cửa sổ cha hiện tại
//        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//        
//        if (parentFrame != null) {
//            parentFrame.setVisible(false); // Ẩn màn hình đặt bàn
//            
//            // Tạo frame gọi món mới
//            JFrame goiMonFrame = new JFrame("Gọi Món - Bàn " + maBan);
//            goiMonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            goiMonFrame.setSize(1200, 800);
//            goiMonFrame.setLocationRelativeTo(null);
//            
//            try {
//                // Khởi tạo giao diện gọi món (Giả định bạn đã có class GoiMon_GUI)
//                // Lưu ý: Class GoiMon_GUI phải có constructor nhận vào mã bàn
//                GoiMon_GUI goiMonPanel = new GoiMon_GUI(maBan);
//                goiMonFrame.setContentPane(goiMonPanel);
//                
//                // Sự kiện khi đóng form gọi món -> Hiện lại form đặt bàn
//                goiMonFrame.addWindowListener(new WindowAdapter() {
//                    @Override
//                    public void windowClosed(WindowEvent e) {
//                        parentFrame.setVisible(true);
//                        parentFrame.toFront();
//                        // Refresh lại danh sách bàn để cập nhật trạng thái mới
//                        loadBanCards(banDAO.getAllBan());
//                    }
//                });
//                
//                goiMonFrame.setVisible(true);
//                
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Lỗi mở giao diện gọi món: " + e.getMessage());
//                parentFrame.setVisible(true);
//            }
//        }
//    }
//
//    // Hàm cập nhật trạng thái bàn xuống CSDL và giao diện
//    private void capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
//        try {
//            if (banDAO.updateTrangThaiBan(maBan, trangThaiMoi)) {
//                // Cập nhật đối tượng hiện tại
//                if (banDangChon != null && banDangChon.getMaBan().equals(maBan)) {
//                    banDangChon.setTrangThai(trangThaiMoi);
//                }
//                // Tải lại giao diện
//                loadBanCards(banDAO.getAllBan());
//            } else {
//                JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái bàn!");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    
//    // Listener methods
//    public void setDataRefreshListener(DataRefreshListener listener) {
//        this.refreshListener = listener;
//    }
//    
//    public void refreshData() {
//        loadBanCards(banDAO.getAllBan());
//    }
//    
//    public static void main(String[] args) throws SQLException {
//        ConnectDB.getInstance().connect();
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Đặt Bàn");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(1400, 900);
//            frame.add(new BanDat_GUI());
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//        });
//    }
//}