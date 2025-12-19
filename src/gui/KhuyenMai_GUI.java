package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JTextField txtMaKM, txtTenKM, txtMoTa, txtPhanTramGiam;
    private JDateChooser dcNgayBD, dcNgayKT; 
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnTim, btnXoaBoLoc; 
    private JComboBox<String> cboTrangThai; 
    private KhuyenMai_DAO khuyenMai_DAO;
    private static final DateTimeFormatter DATE_FORMATTER_GUI = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int TEXTFIELD_HEIGHT = 40; 

    public KhuyenMai_GUI() {
        // 1. KẾT NỐI CSDL
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        khuyenMai_DAO = new KhuyenMai_DAO();
        
        // 2. THIẾT LẬP LAYOUT CHÍNH
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 239, 213)); 
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 3. HEADER & FORM
        JPanel pnlTopContainer = new JPanel();
        pnlTopContainer.setLayout(new BoxLayout(pnlTopContainer, BoxLayout.Y_AXIS));
        pnlTopContainer.setBackground(getBackground());
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("Quản Lý Khuyến Mãi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnlTopContainer.add(lblTitle);
        
        // Thanh tìm kiếm
        pnlTopContainer.add(createSearchAndFilterPanel());
        pnlTopContainer.add(Box.createVerticalStrut(15));
        
        // Form nhập liệu
        pnlTopContainer.add(createInputFormPanel());

        add(pnlTopContainer, BorderLayout.NORTH);

        // 4. BẢNG DỮ LIỆU
        String[] cols = {"Mã", "Tên khuyến mãi", "Mô tả", "Giá trị", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); 
        add(scroll, BorderLayout.CENTER);
        
        // 5. KHỞI TẠO DỮ LIỆU & SỰ KIỆN
        loadDataToTable();
        addTableClickListener();
        addActionListener(); 
        
        // ⭐ QUAN TRỌNG: Gọi hàm này để tự động điền mã KM mới ngay khi mở form
        xoaTrangForm();
    }
    
    // =========================================================================
    // PHẦN 1: TẠO GIAO DIỆN (UI COMPONENTS)
    // =========================================================================
    
    private JPanel createSearchAndFilterPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        pnl.setBackground(getBackground()); 
        pnl.setMaximumSize(new Dimension(1000, 50));
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(250, 35)); 
        styleTextField(txtTimKiem);
        
        btnTim = new JButton("Tìm");
        btnTim.setPreferredSize(new Dimension(80, 35));
        btnTim.setBackground(Color.WHITE);
        
        cboTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc"});
        cboTrangThai.setPreferredSize(new Dimension(150, 35));
        
        btnXoaBoLoc = new JButton("Làm mới");
        btnXoaBoLoc.setPreferredSize(new Dimension(100, 35));
        btnXoaBoLoc.setBackground(Color.WHITE);

        pnl.add(txtTimKiem);
        pnl.add(btnTim);
        pnl.add(cboTrangThai);
        pnl.add(btnXoaBoLoc);
        
        return pnl;
    }
    
    private JPanel createInputFormPanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE); 
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        pnl.setMaximumSize(new Dimension(1000, 450));
        
        // --- Panel GridBagLayout cho các trường nhập ---
        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // Khởi tạo fields
        txtMaKM = createSizedTextField(TEXTFIELD_HEIGHT);
        // ⭐ CẤU HÌNH CHO MÃ TỰ ĐỘNG: KHÔNG CHO SỬA
        txtMaKM.setEditable(false);
        txtMaKM.setBackground(new Color(230, 230, 230)); // Màu xám nhạt
        txtMaKM.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtTenKM = createSizedTextField(TEXTFIELD_HEIGHT);
        txtMoTa = createSizedTextField(TEXTFIELD_HEIGHT); 
        txtPhanTramGiam = createSizedTextField(TEXTFIELD_HEIGHT);
        dcNgayBD = createDateChooser(TEXTFIELD_HEIGHT);
        dcNgayKT = createDateChooser(TEXTFIELD_HEIGHT);

        // Add fields
        addField(pnlFields, gbc, "Mã khuyến mãi (Auto)", txtMaKM, 0, 0);
        addField(pnlFields, gbc, "Tên khuyến mãi", txtTenKM, 1, 0);
        
        addField(pnlFields, gbc, "Mô tả", txtMoTa, 0, 1);
        addField(pnlFields, gbc, "Phần trăm giảm (VD: 0.1 = 10%)", txtPhanTramGiam, 1, 1);

        addField(pnlFields, gbc, "Ngày bắt đầu", dcNgayBD, 0, 2);
        addField(pnlFields, gbc, "Ngày kết thúc", dcNgayKT, 1, 2);

        pnl.add(pnlFields, BorderLayout.CENTER);
        
        // --- Panel Nút chức năng ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        pnlButtons.setBackground(Color.WHITE);

        btnThem = new JButton("Thêm khuyến mãi"); 
        btnXoa = new JButton("Xóa"); 
        btnSua = new JButton("Sửa"); 

        styleButton(btnThem, new Color(39, 174, 96), 180); 
        styleButton(btnXoa, new Color(231, 76, 60), 100); 
        styleButton(btnSua, new Color(243, 156, 18), 100); 

        pnlButtons.add(btnThem);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnSua);

        pnl.add(pnlButtons, BorderLayout.SOUTH);
        
        return pnl;
    }
    
    // --- Các hàm Style hỗ trợ ---
    
    private JTextField createSizedTextField(int height) {
        JTextField textField = new JTextField(15);
        textField.setPreferredSize(new Dimension(200, height));
        return textField;
    }
    
    private JDateChooser createDateChooser(int height) {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy"); 
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.setPreferredSize(new Dimension(200, height));
        return dateChooser;
    }

    private void styleTextField(JTextField tf) {
        tf.setBorder(BorderFactory.createCompoundBorder(
            tf.getBorder(), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5))); 
    }
    
    private void addField(JPanel parent, GridBagConstraints gbc, String labelText, JComponent component, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        
        JPanel pnlField = new JPanel(new BorderLayout());
        pnlField.setBackground(parent.getBackground());
        pnlField.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(), 
            labelText, 
            TitledBorder.LEADING, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.PLAIN, 12), 
            Color.BLACK)); 
            
        pnlField.add(component, BorderLayout.CENTER);
        parent.add(pnlField, gbc);
    }

    private void styleButton(JButton btn, Color color, int width) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(width, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }
    
    // =========================================================================
    // PHẦN 2: LOGIC XỬ LÝ DỮ LIỆU & SỰ KIỆN
    // =========================================================================

    public void loadDataToTable(ArrayList<KhuyenMai> danhSachKM) {
        model.setRowCount(0); 
        
        if (danhSachKM == null) {
            danhSachKM = khuyenMai_DAO.getAllKhuyenMai();
        }
        
        for (KhuyenMai km : danhSachKM) {
            String trangThai = getTrangThai(km.getNgayBatDau(), km.getNgayKetThuc());
            
            String ngayBDStr = km.getNgayBatDau().format(DATE_FORMATTER_GUI);
            String ngayKTStr = km.getNgayKetThuc().format(DATE_FORMATTER_GUI);
            
            Object[] rowData = {
                km.getMaKM(),
                km.getTenKM(),
                (km.getMoTa() != null) ? km.getMoTa() : "---", 
                String.format("%.0f%%", km.getPhanTramGiam() * 100), 
                ngayBDStr,
                ngayKTStr,
                trangThai
            };
            model.addRow(rowData);
        }
    }
    
    public void loadDataToTable() {
        loadDataToTable(null);
    }
    
    private String getTrangThai(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        LocalDate homNay = LocalDate.now();
        if (homNay.isBefore(ngayBatDau)) {
            return "Sắp diễn ra";
        } else if (homNay.isAfter(ngayKetThuc)) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }
    
    private void addTableClickListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                hienThiChiTietKhuyenMaiLenForm();
            }
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
            
            // Xử lý ngày
            String ngayBDStr = model.getValueAt(selectedRow, 4).toString();
            String ngayKTStr = model.getValueAt(selectedRow, 5).toString();

            LocalDate ngayBD = LocalDate.parse(ngayBDStr, DATE_FORMATTER_GUI);
            LocalDate ngayKT = LocalDate.parse(ngayKTStr, DATE_FORMATTER_GUI);
            
            dcNgayBD.setDate(Date.from(ngayBD.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            dcNgayKT.setDate(Date.from(ngayKT.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            
            // Khi chọn dòng, khóa nút Thêm, mở nút Sửa/Xóa
            btnThem.setEnabled(false);
            btnSua.setEnabled(true);
            btnXoa.setEnabled(true);
        }
    }
    
    private void addActionListener() {
        btnThem.addActionListener(e -> themKhuyenMai());
        btnSua.addActionListener(e -> suaKhuyenMai());
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        btnTim.addActionListener(e -> timKhuyenMai());
        
        btnXoaBoLoc.addActionListener(e -> lamMoiFormVaBang());
        cboTrangThai.addActionListener(e -> locKhuyenMaiTheoTrangThai());
    }
    
    private void lamMoiFormVaBang() {
        xoaTrangForm();
        txtTimKiem.setText(""); 
        if (cboTrangThai != null) {
            cboTrangThai.setSelectedIndex(0); 
        }
        loadDataToTable(); 
    }

    /**
     * Hàm xóa trắng form và TẠO MÃ TỰ ĐỘNG
     */
    private void xoaTrangForm() {
        // ⭐ TỰ ĐỘNG LẤY MÃ MỚI TỪ DAO
        String maMoi = khuyenMai_DAO.phatSinhMaKM();
        txtMaKM.setText(maMoi);
        
        txtTenKM.setText("");
        txtMoTa.setText("");
        txtPhanTramGiam.setText("");
        
        dcNgayBD.setDate(null);
        dcNgayKT.setDate(null);
        
        // Reset trạng thái nút
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
            if (currentTrangThai.equals(selectedTrangThai)) {
                filteredList.add(km);
            }
        }
        loadDataToTable(filteredList);
        // Lưu ý: Không gọi xoaTrangForm ở đây để tránh mất mã mới đang hiển thị
    }

    // =========================================================================
    // PHẦN 3: LOGIC CRUD (THÊM, XÓA, SỬA)
    // =========================================================================
    
    private KhuyenMai layDuLieuTuForm() throws Exception {
        String ma = txtMaKM.getText().trim();
        String ten = txtTenKM.getText().trim();
        String moTa = txtMoTa.getText().trim();
        double giaTri;
        LocalDate ngayBD, ngayKT;

        if (ma.isEmpty() || ten.isEmpty() || txtPhanTramGiam.getText().trim().isEmpty()) {
             throw new Exception("Vui lòng điền đầy đủ các trường bắt buộc.");
        }
        Date dateBD = dcNgayBD.getDate();
        Date dateKT = dcNgayKT.getDate();
        
        if (dateBD == null || dateKT == null) {
            throw new Exception("Vui lòng chọn Ngày bắt đầu và Ngày kết thúc.");
        }
        
        ngayBD = dateBD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        ngayKT = dateKT.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        try {
             giaTri = Double.parseDouble(txtPhanTramGiam.getText().trim());
             if (giaTri < 0 || giaTri > 1) {
                 throw new Exception("Giá trị giảm phải từ 0 đến 1 (ví dụ 0.1).");
             }
        } catch (NumberFormatException e) {
             throw new Exception("Giá trị giảm không hợp lệ.");
        }

        if (ngayBD.isAfter(ngayKT)) {
            throw new Exception("Ngày bắt đầu không thể sau Ngày kết thúc.");
        }

        return new KhuyenMai(ma, ten, moTa, giaTri, ngayBD, ngayKT);
    }

    private void themKhuyenMai() {
        try {
            KhuyenMai km = layDuLieuTuForm();
            // Mặc dù mã tự động, kiểm tra trùng vẫn tốt
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
                    JOptionPane.showMessageDialog(this, "Xóa thất bại (Có thể do ràng buộc dữ liệu).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void timKhuyenMai() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadDataToTable();
            return;
        }

        ArrayList<KhuyenMai> danhSachKM = khuyenMai_DAO.timKhuyenMaiTheoTen(keyword);
        
        if (danhSachKM.isEmpty()) {
            KhuyenMai km = khuyenMai_DAO.timKhuyenMaiTheoMa(keyword);
            if (km != null) {
                danhSachKM.add(km);
            }
        }
        
        loadDataToTable(danhSachKM);
        
        if (danhSachKM.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        cboTrangThai.setSelectedIndex(0); 
    }
}