package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import com.toedter.calendar.JDateChooser;

import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import connectDB.ConnectDB;

public class KhuyenMai_GUI extends JPanel {

    private static final long serialVersionUID = 1L;
    
    // Components
    private JTextField txtMaKM, txtTenKM, txtMoTa, txtPhanTramGiam;
    private JDateChooser dcNgayBD, dcNgayKT;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnXoaBoLoc, btnTim;
    private JComboBox<String> cboTrangThai; // Bỏ cboLoai
    
    private KhuyenMai_DAO khuyenMai_DAO;
    private static final DateTimeFormatter DATE_FORMATTER_GUI = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // COLORS
    private final Color BG_COLOR = new Color(255, 220, 180);
    private final Color PANEL_COLOR = Color.WHITE;
    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);   // Xanh lá
    private final Color BTN_DELETE_COLOR = new Color(231, 76, 60); // Đỏ
    private final Color BTN_EDIT_COLOR = new Color(241, 196, 15);  // Vàng
    private final Color BTN_SEARCH_COLOR = new Color(52, 152, 219); // Xanh biển
    private final Color TEXT_COLOR = new Color(50, 50, 50);

    public KhuyenMai_GUI() {
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        khuyenMai_DAO = new KhuyenMai_DAO();

        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(10, 20, 10, 20)); 

        // --- HEADER ---
        JLabel lblTitle = new JLabel("Quản Lý Khuyến Mãi", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- CENTER PANEL ---
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setOpaque(false);

        pnlContent.add(createSearchFilterBar());
        pnlContent.add(Box.createVerticalStrut(10));
        pnlContent.add(createFormSection());
        pnlContent.add(Box.createVerticalStrut(10));
        pnlContent.add(createTableSection());

        JScrollPane mainScroll = new JScrollPane(pnlContent);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.setOpaque(false);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16); 

        add(mainScroll, BorderLayout.CENTER);

        loadDataToTable();
        addTableClickListener();
        addActionListener();
        xoaTrangForm();
    }

    private JPanel createSearchFilterBar() {
        JPanel pnl = new JPanel(new BorderLayout(15, 0));
        pnl.setOpaque(false);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 

        // --- KHỐI TÌM KIẾM ---
        JPanel pnlSearch = new RoundedPanel(15, Color.WHITE);
        pnlSearch.setLayout(new BorderLayout(5, 0)); 
        pnlSearch.setBorder(new EmptyBorder(5, 10, 5, 5)); 
        
        txtTimKiem = new JTextField();
        txtTimKiem.setBorder(null); 
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPlaceholder(txtTimKiem, "Tìm theo mã hoặc tên...");
        
        // Tạo nút Tìm màu xanh biển
        btnTim = createRoundedButton("Tìm", BTN_SEARCH_COLOR, Color.WHITE);
        btnTim.setPreferredSize(new Dimension(80, 35)); 
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlSearch.add(txtTimKiem, BorderLayout.CENTER);
        pnlSearch.add(btnTim, BorderLayout.EAST);
        
        // --- KHỐI BỘ LỌC ---
        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilters.setOpaque(false);

        cboTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc"});
        cboTrangThai.setPreferredSize(new Dimension(160, 45));
        cboTrangThai.setBackground(Color.WHITE);
        cboTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnXoaBoLoc = createRoundedButton("Làm mới", Color.WHITE, Color.BLACK);
        btnXoaBoLoc.setPreferredSize(new Dimension(100, 45));

        pnlFilters.add(cboTrangThai);
        pnlFilters.add(btnXoaBoLoc);

        pnl.add(pnlSearch, BorderLayout.CENTER);
        pnl.add(pnlFilters, BorderLayout.EAST);

        return pnl;
    }

    private JPanel createFormSection() {
        JPanel pnlForm = new RoundedPanel(20, PANEL_COLOR);
        pnlForm.setLayout(new GridBagLayout());
        pnlForm.setBorder(new EmptyBorder(20, 30, 30, 30)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        addLabel(pnlForm, "Mã khuyến mãi", 0, 0, gbc);
        addLabel(pnlForm, "Tên khuyến mãi", 1, 0, gbc);

        txtMaKM = createStyledTextField();
        txtMaKM.setEditable(false);
        txtMaKM.setBackground(new Color(240, 240, 240));
        addComponent(pnlForm, txtMaKM, 0, 1, gbc);

        txtTenKM = createStyledTextField();
        addComponent(pnlForm, txtTenKM, 1, 1, gbc);

        addLabel(pnlForm, "Mô tả", 0, 2, gbc);
        addLabel(pnlForm, "Phần trăm giảm (VD: 10 hoặc 0.1)", 1, 2, gbc);

        txtMoTa = createStyledTextField();
        addComponent(pnlForm, txtMoTa, 0, 3, gbc);

        txtPhanTramGiam = createStyledTextField();
        addComponent(pnlForm, txtPhanTramGiam, 1, 3, gbc);

        addLabel(pnlForm, "Ngày bắt đầu", 0, 4, gbc);
        addLabel(pnlForm, "Ngày kết thúc", 1, 4, gbc);

        dcNgayBD = createStyledDateChooser();
        addComponent(pnlForm, dcNgayBD, 0, 5, gbc);

        dcNgayKT = createStyledDateChooser();
        addComponent(pnlForm, dcNgayKT, 1, 5, gbc);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlButtons.setOpaque(false);
        
        btnThem = createRoundedButton("Thêm khuyến mãi", BTN_ADD_COLOR, Color.WHITE);
        btnThem.setPreferredSize(new Dimension(200, 45));
        btnThem.setIcon(createIcon("image/add.png"));

        btnXoa = createRoundedButton("Xóa", BTN_DELETE_COLOR, Color.WHITE);
        btnXoa.setPreferredSize(new Dimension(120, 45));
        btnXoa.setIcon(createIcon("image/delete.png"));

        btnSua = createRoundedButton("Sửa", BTN_EDIT_COLOR, Color.WHITE);
        btnSua.setPreferredSize(new Dimension(120, 45));
        btnSua.setIcon(createIcon("image/edit.png"));

        pnlButtons.add(btnThem);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnSua);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.weighty = 1.0; 
        gbc.anchor = GridBagConstraints.PAGE_END; 
        gbc.insets = new Insets(25, 0, 5, 0); 
        pnlForm.add(pnlButtons, gbc);

        return pnlForm;
    }

    private JPanel createTableSection() {
        JPanel pnlTable = new RoundedPanel(20, PANEL_COLOR);
        pnlTable.setLayout(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        pnlTable.setPreferredSize(new Dimension(800, 300)); 

        String[] cols = {"Mã", "Tên khuyến mãi", "Mô tả", "Giá trị", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder()); 
        scroll.getViewport().setBackground(Color.WHITE);

        pnlTable.add(scroll, BorderLayout.CENTER);
        return pnlTable;
    }

    private ImageIcon createIcon(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    private void addLabel(JPanel panel, String text, int x, int y, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_COLOR);
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.weighty = 0; 
        panel.add(lbl, gbc);
    }

    private void addComponent(JPanel panel, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.weighty = 0; 
        panel.add(comp, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 40));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(new CompoundBorder(
            new RoundedBorder(10, new Color(200, 200, 200)), 
            new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JDateChooser createStyledDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.setPreferredSize(new Dimension(0, 40));
        dc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField editor = ((JTextField) dc.getDateEditor().getUiComponent());
        editor.setBorder(new CompoundBorder(
            new RoundedBorder(10, new Color(200, 200, 200)), 
            new EmptyBorder(5, 10, 5, 10)
        ));
        return dc;
    }

    private JButton createRoundedButton(String text, Color bg, Color textCol) {
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
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(textCol);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIconTextGap(10); 
        return btn;
    }

    private void setPlaceholder(JTextField tf, String text) {
        tf.setText(text);
        tf.setForeground(Color.GRAY);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(text)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(text);
                    tf.setForeground(Color.GRAY);
                }
            }
        });
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

    class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
    }

    public void loadDataToTable(ArrayList<KhuyenMai> dsHienThi) {
        // 1. Xóa sạch dữ liệu cũ trên bảng
        model.setRowCount(0); 
        
        // 2. Kiểm tra: Nếu danh sách truyền vào là null thì mới load tất cả
        // (Khi tìm kiếm, dsHienThi sẽ KHÔNG null, nên nó sẽ bỏ qua dòng này)
        if (dsHienThi == null) {
            dsHienThi = khuyenMai_DAO.getAllKhuyenMai();
        }
        
        // 3. Đổ dữ liệu từ danh sách vào bảng
        for (KhuyenMai km : dsHienThi) {
            String trangThai = getTrangThai(km.getNgayBatDau(), km.getNgayKetThuc());
            String ngayBDStr = km.getNgayBatDau().format(DATE_FORMATTER_GUI);
            String ngayKTStr = km.getNgayKetThuc().format(DATE_FORMATTER_GUI);
            
            // Format phần trăm
            String phanTramStr = String.format("%.0f%%", km.getPhanTramGiam() * 100);

            model.addRow(new Object[]{
                km.getMaKM(), 
                km.getTenKM(), 
                (km.getMoTa() != null) ? km.getMoTa() : "---",
                phanTramStr, 
                ngayBDStr, 
                ngayKTStr, 
                trangThai
            });
        }
    }
    public void loadDataToTable() { loadDataToTable(null); }

    private String getTrangThai(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        LocalDate homNay = LocalDate.now();
        if (homNay.isBefore(ngayBatDau)) return "Sắp diễn ra";
        else if (homNay.isAfter(ngayKetThuc)) return "Đã kết thúc";
        else return "Đang diễn ra";
    }

    private void addTableClickListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) hienThiChiTietKhuyenMaiLenForm();
        });
    }

    private void hienThiChiTietKhuyenMaiLenForm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtMaKM.setText(model.getValueAt(selectedRow, 0).toString());
            txtTenKM.setText(model.getValueAt(selectedRow, 1).toString());
            txtMoTa.setText(model.getValueAt(selectedRow, 2).toString());
            
            String giaTriStr = model.getValueAt(selectedRow, 3).toString().replace("%", "");
            double phanTram = Double.parseDouble(giaTriStr) / 100.0;
            txtPhanTramGiam.setText(String.valueOf(phanTram));
            
            String ngayBDStr = model.getValueAt(selectedRow, 4).toString();
            String ngayKTStr = model.getValueAt(selectedRow, 5).toString();
            LocalDate ngayBD = LocalDate.parse(ngayBDStr, DATE_FORMATTER_GUI);
            LocalDate ngayKT = LocalDate.parse(ngayKTStr, DATE_FORMATTER_GUI);
            
            dcNgayBD.setDate(Date.from(ngayBD.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            dcNgayKT.setDate(Date.from(ngayKT.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            
            btnThem.setEnabled(false);
            btnSua.setEnabled(true);
            btnXoa.setEnabled(true);
        }
    }

    private void addActionListener() {
        btnThem.addActionListener(e -> themKhuyenMai());
        btnSua.addActionListener(e -> suaKhuyenMai());
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        btnXoaBoLoc.addActionListener(e -> lamMoiFormVaBang());
        cboTrangThai.addActionListener(e -> locKhuyenMaiTheoTrangThai());
        
        // ⭐ SỬA LẠI: Gán sự kiện tìm kiếm chính xác
        
            btnTim.addActionListener(e -> timKhuyenMai());
        
        txtTimKiem.addActionListener(e -> timKhuyenMai());
    }

    private void lamMoiFormVaBang() {
        xoaTrangForm();
        txtTimKiem.setText("Tìm theo mã hoặc tên...");
        txtTimKiem.setForeground(Color.GRAY);
        if (cboTrangThai != null) cboTrangThai.setSelectedIndex(0);
        loadDataToTable();
    }

    private void xoaTrangForm() {
        try {
            String maMoi = khuyenMai_DAO.phatSinhMaKM();
            txtMaKM.setText((maMoi != null && !maMoi.isEmpty()) ? maMoi : "KM001");
        } catch (Exception e) {
            txtMaKM.setText("KM001");
        }
        
        txtTenKM.setText("");
        txtMoTa.setText("");
        txtPhanTramGiam.setText("");
        dcNgayBD.setDate(null);
        dcNgayKT.setDate(null);
        
        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        table.clearSelection();
    }

    private void locKhuyenMaiTheoTrangThai() {
        String selectedTrangThai = (String) cboTrangThai.getSelectedItem();
        if (selectedTrangThai == null || selectedTrangThai.equals("Tất cả trạng thái")) {
            loadDataToTable();
            return;
        }
        ArrayList<KhuyenMai> allKM = khuyenMai_DAO.getAllKhuyenMai();
        ArrayList<KhuyenMai> filteredList = new ArrayList<>();
        for (KhuyenMai km : allKM) {
            String currentTrangThai = getTrangThai(km.getNgayBatDau(), km.getNgayKetThuc());
            if (currentTrangThai.equals(selectedTrangThai)) filteredList.add(km);
        }
        loadDataToTable(filteredList);
    }

    private KhuyenMai layDuLieuTuForm() throws Exception {
        String ma = txtMaKM.getText().trim();
        String ten = txtTenKM.getText().trim();
        String moTa = txtMoTa.getText().trim();
        double giaTri;
        LocalDate ngayBD, ngayKT;

        if (ma.isEmpty() || ten.isEmpty() || txtPhanTramGiam.getText().trim().isEmpty()) 
             throw new Exception("Vui lòng điền đầy đủ các trường bắt buộc.");
        
        Date dateBD = dcNgayBD.getDate();
        Date dateKT = dcNgayKT.getDate();
        if (dateBD == null || dateKT == null) throw new Exception("Vui lòng chọn Ngày bắt đầu và Ngày kết thúc.");
        
        ngayBD = dateBD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        ngayKT = dateKT.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        try {
             giaTri = Double.parseDouble(txtPhanTramGiam.getText().trim());
             if (giaTri > 1) {
                 giaTri = giaTri / 100.0;
             }
             if (giaTri < 0) throw new Exception("Giá trị giảm không được âm.");
        } catch (NumberFormatException e) {
             throw new Exception("Giá trị giảm không hợp lệ. Vui lòng nhập số.");
        }

        if (ngayBD.isAfter(ngayKT)) throw new Exception("Ngày bắt đầu không thể sau Ngày kết thúc.");

        return new KhuyenMai(ma, ten, moTa, giaTri, ngayBD, ngayKT);
    }

    private void themKhuyenMai() {
        try {
            KhuyenMai km = layDuLieuTuForm();
            if (khuyenMai_DAO.timKhuyenMaiTheoMa(km.getMaKM()) != null) {
                JOptionPane.showMessageDialog(this, "Mã khuyến mãi đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (khuyenMai_DAO.themKhuyenMai(km)) {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!");
                lamMoiFormVaBang();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhuyenMai() {
        try {
            KhuyenMai km = layDuLieuTuForm();
            if (khuyenMai_DAO.suaKhuyenMai(km)) {
                JOptionPane.showMessageDialog(this, "Sửa khuyến mãi thành công!");
                lamMoiFormVaBang();
            } else {
                JOptionPane.showMessageDialog(this, "Sửa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhuyenMai() {
        String maKM = txtMaKM.getText().trim();
        if (maKM.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khuyến mãi: " + maKM + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (khuyenMai_DAO.xoaKhuyenMai(maKM)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    lamMoiFormVaBang();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void timKhuyenMai() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty() || keyword.equals("Tìm theo mã hoặc tên...")) {
            loadDataToTable(null); // Truyền null để load lại tất cả
            return;
        }

        // Tìm kiếm
        ArrayList<KhuyenMai> ketQuaTimKiem = khuyenMai_DAO.timKhuyenMaiTheoTen(keyword);
        
        // Tìm thêm theo mã nếu cần
        KhuyenMai kmTheoMa = khuyenMai_DAO.timKhuyenMaiTheoMa(keyword);
        if (kmTheoMa != null) {
            // Kiểm tra trùng lặp
            boolean exists = false;
            for (KhuyenMai k : ketQuaTimKiem) {
                if (k.getMaKM().equals(kmTheoMa.getMaKM())) {
                    exists = true; 
                    break;
                }
            }
            if (!exists) ketQuaTimKiem.add(kmTheoMa);
        }

        System.out.println("Số lượng kết quả tìm thấy: " + ketQuaTimKiem.size());

        // --- ĐÂY LÀ DÒNG QUAN TRỌNG NHẤT ---
        // Phải truyền biến 'ketQuaTimKiem' vào hàm load
        loadDataToTable(ketQuaTimKiem); 
        
        if (ketQuaTimKiem.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}