package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.toedter.calendar.JDateChooser; // Cần thư viện jcalendar

import dao.HoaDon_DAO;
import dao.NhanVien_DAO;
import dao.CTHoaDon_DAO;
import entity.HoaDon;
import entity.NhanVien;
import entity.CT_HoaDon;

public class HoaDon_GUI extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimMaHD;
    private JDateChooser chonNgayTu, chonNgayDen;
    
    private final HoaDon_DAO hoaDon_DAO;
    private final CTHoaDon_DAO ctHoaDon_DAO;
    private List<HoaDon> danhSachGoc = new ArrayList<>();

    // --- CẤU HÌNH MÀU SẮC (TÔNG VÀNG CAM) ---
    private final Color BG_COLOR = new Color(255, 218, 170); // Nền Vàng nhạt
    private final Color PANEL_COLOR = Color.WHITE;           // Nền trắng các khối
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); // Xanh dương (Nút tìm/Link)
    private final Color TEXT_COLOR = new Color(50, 50, 50);  // Màu chữ
    private final Color HEADER_COLOR = new Color(255, 178, 102); // Header bảng màu Cam

    public HoaDon_GUI() {
        this.hoaDon_DAO = new HoaDon_DAO();
        this.ctHoaDon_DAO = new CTHoaDon_DAO();

        setLayout(new BorderLayout(20, 20)); 
        setBackground(BG_COLOR); 
        setBorder(new EmptyBorder(20, 30, 20, 30)); 

        // --- 1. TIÊU ĐỀ ---
        JLabel titleLabel = new JLabel("QUẢN LÝ HÓA ĐƠN", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30)); 
        titleLabel.setForeground(Color.BLACK); 
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- 2. VÙNG TRUNG TÂM ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false); 

        centerPanel.add(taoPanelBoLoc(), BorderLayout.NORTH);
        centerPanel.add(createTableSection(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDataToTable();
    }

    // =====================================================
    // PANEL LỌC
    // =====================================================
    private JPanel taoPanelBoLoc() {
        JPanel panel = new RoundedPanel(15, PANEL_COLOR);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        // --- TÌM MÃ ---
        panel.add(createLabel("Mã HĐ:", fontLabel));
        txtTimMaHD = createStyledTextField(130, fontInput);
        panel.add(txtTimMaHD);

        JButton btnTim = createRoundedButton("Tìm", PRIMARY_COLOR, Color.WHITE);
        btnTim.addActionListener(e -> timTheoMaHD());
        panel.add(btnTim);

        // --- VÁCH NGĂN ---
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 30));
        sep.setForeground(Color.LIGHT_GRAY);
        panel.add(sep);

        // --- LỌC NGÀY ---
        panel.add(createLabel("Từ ngày:", fontLabel));
        chonNgayTu = createStyledDateChooser(fontInput);
        panel.add(chonNgayTu);

        panel.add(createLabel("Đến ngày:", fontLabel));
        chonNgayDen = createStyledDateChooser(fontInput);
        panel.add(chonNgayDen);

        JButton btnLoc = createRoundedButton("Lọc", new Color(46, 204, 113), Color.WHITE); // Xanh lá
        btnLoc.addActionListener(e -> locTheoKhoangNgay());
        panel.add(btnLoc);

        JButton btnReset = createRoundedButton("Làm mới", new Color(149, 165, 166), Color.WHITE); // Xám
        btnReset.addActionListener(e -> resetBang());
        panel.add(btnReset);

        return panel;
    }

    // =====================================================
    // KHU VỰC BẢNG (CÓ CỘT NÚT BẤM)
    // =====================================================
    private JComponent createTableSection() {
        JPanel panel = new RoundedPanel(15, PANEL_COLOR);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Định nghĩa cột (Cột cuối là Thao tác)
        String[] columns = { "Mã hóa đơn", "Mã nhân viên", "Ngày lập", "Tổng tiền", "Thao tác" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return false; // Không cho sửa ô nào để tránh lỗi click
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        table.setSelectionBackground(new Color(255, 229, 204));
        table.setSelectionForeground(Color.BLACK);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_COLOR); 
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        // Căn lề các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT); 

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Tiền căn phải

        // ⭐ 1. CUSTOM RENDERER CHO CỘT "Thao tác" (Cột 4)
        // Tạo giao diện chữ màu xanh, gạch chân giống link
        DefaultTableCellRenderer linkRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setForeground(PRIMARY_COLOR);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setText("<html><u>Xem chi tiết</u></html>");
                return lbl;
            }
        };
        table.getColumnModel().getColumn(4).setCellRenderer(linkRenderer);

        // ⭐ 2. BẮT SỰ KIỆN CLICK CHUỘT TRÊN BẢNG
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Nếu click vào cột thứ 4 (Cột Thao tác)
                if (row >= 0 && col == 4) {
                    String maHD = table.getValueAt(row, 0).toString();
                    xemChiTietHoaDon(maHD); // Gọi hàm hiển thị dialog
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // HÀM HIỂN THỊ CHI TIẾT HÓA ĐƠN (DIALOG)
    // =====================================================
 // =====================================================
    // HÀM HIỂN THỊ CHI TIẾT HÓA ĐƠN (GIAO DIỆN GIỐNG HÓA ĐƠN IN)
    // =====================================================
    private void xemChiTietHoaDon(String maHD) {
        // 1. Lấy thông tin hóa đơn từ CSDL (Header)
        HoaDon hd = hoaDon_DAO.timHoaDonTheoMa(maHD); // Bạn cần đảm bảo DAO có hàm này
        if (hd == null) return;

        // 2. Lấy danh sách chi tiết (Body)
        ArrayList<CT_HoaDon> listCT = ctHoaDon_DAO.layDSCTHoaDonTheoMaHD(maHD);
        if (listCT == null || listCT.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chi tiết hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- TẠO DIALOG ---
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Hóa Đơn", true);
        dialog.setSize(450, 600); // Kích thước khổ giấy in bill
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // --- TẠO PANEL ĐỂ VẼ HÓA ĐƠN ---
        JPanel invoicePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Khử răng cưa cho chữ đẹp
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                int y = 30;
                int startX = 20;
                int rightX = 250; // Cột phải cho header
                int width = getWidth() - 40; // Chiều rộng vùng vẽ
                int colSL = 260; // Vị trí cột SL
                int colDonGia = 300; // Vị trí cột Đơn giá
                int colThanhTien = 380; // Vị trí cột Thành tiền
                NhanVien_DAO nv_dao = new NhanVien_DAO();
                NhanVien nv = nv_dao.timNhanVienTheoMa(hd.getNhanVien().getMaNV());
                // 1. TIÊU ĐỀ
                g2.setFont(new Font("Arial", Font.BOLD, 22));
                String title = "HOÁ ĐƠN THANH TOÁN";
                int titleWidth = g2.getFontMetrics().stringWidth(title);
                g2.drawString(title, (getWidth() - titleWidth) / 2, y);
                y += 30;

                // 2. THÔNG TIN CHUNG
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                g2.drawString("Mã HĐ: " + hd.getMaHoaDon(), startX, y);
                g2.drawString("Thu ngân: " + (nv != null ? nv.getHoTen() : "N/A"), rightX, y);
                y += 20;

                g2.drawString("Bàn: " + (hd.getBan() != null ? hd.getBan().getMaBan() : "Mang về"), startX, y);
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String ngayLap = hd.getNgayLap() != null ? df.format(hd.getNgayLap()) : "";
                g2.drawString("Ngày: " + ngayLap, rightX, y);
                y += 20;

                // Giả lập giờ (vì trong entity HoaDon thường lưu LocalDateTime)
                DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
                String gio = hd.getNgayLap() != null ? tf.format(hd.getNgayLap()) : "";
                g2.drawString("Giờ vào: " + gio, startX, y);
                g2.drawString("Giờ ra: " + gio, rightX, y);
                y += 20;

                // 3. HEADER BẢNG
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(startX, y, startX + width, y);
                y += 20;
                
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("STT", startX, y);
                g2.drawString("Tên món", startX + 40, y);
                g2.drawString("SL", colSL, y);
                g2.drawString("Đơn giá", colDonGia, y);
                g2.drawString("Thành tiền", colThanhTien, y); // Căn phải sau này
                y += 10;
                
                g2.drawLine(startX, y, startX + width, y);
                y += 20;

                // 4. DANH SÁCH MÓN
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
                int stt = 1;
                long tongTienHang = 0;

                for (CT_HoaDon ct : listCT) {
                    String tenMon = ct.getMonAn().getTenMonAn();
                    int sl = ct.getSoLuong();
                    double donGia = ct.getMonAn().getGiaMonAn();
                    double thanhTien = sl * donGia;
                    tongTienHang += thanhTien;

                    g2.drawString(String.valueOf(stt++), startX, y);
                    
                    // Cắt tên món nếu quá dài
                    if (tenMon.length() > 22) tenMon = tenMon.substring(0, 19) + "...";
                    g2.drawString(tenMon, startX + 40, y);
                    
                    g2.drawString(String.valueOf(sl), colSL + 5, y);
                    g2.drawString(fmt.format(donGia), colDonGia, y);
                    
                    // Căn phải thành tiền
                    String ttStr = fmt.format(thanhTien);
                    int ttWidth = g2.getFontMetrics().stringWidth(ttStr);
                    g2.drawString(ttStr, startX + width - ttWidth, y); // Căn sát lề phải

                    y += 25;
                }

                g2.drawLine(startX, y, startX + width, y);
                y += 25;

                // 5. TỔNG KẾT (Căn lề giống hình mẫu)
                int labelX = startX;
                int valueX = startX + width; // Căn phải giá trị

                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("Thành tiền:", labelX, y);
                String tongStr = fmt.format(tongTienHang);
                g2.drawString(tongStr, valueX - g2.getFontMetrics().stringWidth(tongStr), y);
                y += 25;

                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("Giảm giá:", labelX, y);
                g2.drawString("0", valueX - g2.getFontMetrics().stringWidth("0"), y); // Demo số 0
                y += 25;

                g2.drawString("Trừ cọc:", labelX, y);
                g2.drawString("0", valueX - g2.getFontMetrics().stringWidth("0"), y);
                y += 25;

                g2.setFont(new Font("Arial", Font.BOLD, 15));
                g2.drawString("Tổng tiền:", labelX, y);
                g2.drawString(tongStr, valueX - g2.getFontMetrics().stringWidth(tongStr), y);
                y += 25;

                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("Khách đưa:", labelX, y);
                // Giả lập khách đưa dư
                String khachDua = fmt.format(tongTienHang); 
                g2.drawString(khachDua, valueX - g2.getFontMetrics().stringWidth(khachDua), y);
                y += 25;

                g2.drawString("Tiền thừa:", labelX, y);
                g2.drawString("0", valueX - g2.getFontMetrics().stringWidth("0"), y);
                y += 40;

                // 6. FOOTER
                g2.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 14));
                String footer = "=== Cảm ơn quý khách! ===";
                g2.drawString(footer, (getWidth() - g2.getFontMetrics().stringWidth(footer)) / 2, y);
                
                // Tự động thay đổi chiều cao panel nếu nội dung dài
                setPreferredSize(new Dimension(450, y + 50));
            }
        };

        invoicePanel.setBackground(Color.WHITE);
        
        // Cho vào ScrollPane để cuộn nếu hóa đơn dài
        JScrollPane scrollPane = new JScrollPane(invoicePanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Nút đóng ở dưới
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Arial", Font.BOLD, 14));
        btnDong.setBackground(new Color(52, 152, 219));
        btnDong.setForeground(Color.WHITE);
        btnDong.setFocusPainted(false);
        btnDong.addActionListener(e -> dialog.dispose());
        
        JPanel pnlBtn = new JPanel();
        pnlBtn.setBackground(Color.WHITE);
        pnlBtn.setBorder(new EmptyBorder(10, 0, 10, 0));
        pnlBtn.add(btnDong);
        
        dialog.add(pnlBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // =====================================================
    // CÁC HÀM HELPER GUI (STYLE)
    // =====================================================
    
    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

    private JTextField createStyledTextField(int width, Font font) {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(width, 35));
        tf.setFont(font);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JDateChooser createStyledDateChooser(Font font) {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.setPreferredSize(new Dimension(150, 35));
        dc.setFont(font);
        JTextField editor = (JTextField) dc.getDateEditor().getUiComponent();
        editor.setFont(font);
        editor.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return dc;
    }

    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }

    // =====================================================
    // LOGIC NGHIỆP VỤ
    // =====================================================
    
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        danhSachGoc = hoaDon_DAO.getAllHoaDon();    
        for (HoaDon hd : danhSachGoc) {
            addRow(hd);
        }
    }

    private void timTheoMaHD() {
        String ma = txtTimMaHD.getText().trim().toLowerCase();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn!");
            return;
        }
        lamMoiBang();
        boolean found = false;
        for (HoaDon hd : danhSachGoc) {
            if (hd.getMaHoaDon().toLowerCase().contains(ma)) {
                addRow(hd);
                found = true;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn với mã: " + ma);
            resetBang();
        }
    }

    private void locTheoKhoangNgay() {
        Date tuNgay = chonNgayTu.getDate();
        Date denNgay = chonNgayDen.getDate();

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc!");
            return;
        }

        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải <= ngày kết thúc!");
            return;
        }
        denNgay = new Date(denNgay.getTime() + 86399999); 

        lamMoiBang();
        boolean found = false;
        for (HoaDon hd : danhSachGoc) {
            if (hd.getNgayLap() == null) continue;
            Date ngayHD = Date.from(hd.getNgayLap().atZone(ZoneId.systemDefault()).toInstant());
            if (!ngayHD.before(tuNgay) && !ngayHD.after(denNgay)) {
                addRow(hd);
                found = true;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn trong khoảng này!");
            resetBang();
        }
    }

    private void addRow(HoaDon hd) {
        double tongtien = hoaDon_DAO.layTongTienTheoMaHD(hd.getMaHoaDon());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        tableModel.addRow(new Object[]{
            hd.getMaHoaDon(),
            (hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : "N/A"),
            (hd.getNgayLap() != null ? dtf.format(hd.getNgayLap()) : ""),
            String.format("%,.0f", tongtien),
            "Xem" // Giá trị giả để hiển thị text "Xem chi tiết"
        });
    }

    private void resetBang() {
        lamMoiBang();
        loadDataToTable();
        txtTimMaHD.setText(""); 
        chonNgayTu.setDate(null);
        chonNgayDen.setDate(null);
    }

    private void lamMoiBang() {
        tableModel.setRowCount(0);
    }
}