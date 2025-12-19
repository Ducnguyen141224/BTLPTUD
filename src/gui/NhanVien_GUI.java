package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.toedter.calendar.JDateChooser; 

import dao.NhanVien_DAO; 
import entity.NhanVien; 
import entity.QuanLy; 
import connectDB.ConnectDB;

public class NhanVien_GUI extends JPanel {
    
    // Khai báo các component
    private JTextField txtEmployeeId, txtName, txtIdNumber, txtPhone, txtEmail;
    private JDateChooser dateChooserBirthDate;
    private JRadioButton rbMale, rbFemale;
    private JComboBox<String> cbStatus;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    private NhanVien_DAO nhanVien_DAO;

    public NhanVien_GUI() {
        // 1. Kết nối CSDL
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        nhanVien_DAO = new NhanVien_DAO();

        // 2. Thiết lập Layout chính
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 235, 205));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 3. Tiêu đề
        JLabel titleLabel = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(139, 69, 19));
        
        // 4. Tạo các Panel thành phần
        JPanel formPanel = taoPanelForm();
        JPanel searchPanel = taoPanelTimKiem();
        JScrollPane tableScrollPane = taoBang();

        // 5. Sắp xếp bố cục
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(255, 235, 205));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(new Color(255, 235, 205));
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);

        // 6. Khởi tạo dữ liệu ban đầu
        taiDuLieuVaoBang();
        xoaTrangForm(); // Gọi hàm này để tự động sinh mã NV ngay khi mở
    }

    // =========================================================================
    // PHẦN 1: GIAO DIỆN (UI)
    // =========================================================================

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 222, 179));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 180, 140), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Hàng 1: Mã NV & Họ tên ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        panel.add(taoNhan("Mã nhân viên"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.4;
        txtEmployeeId = taoTextFieldDinhDang();
        // ⭐ KHÓA Ô MÃ & ĐỔI MÀU NỀN
        txtEmployeeId.setEditable(false);
        txtEmployeeId.setBackground(new Color(230, 230, 230));
        txtEmployeeId.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(txtEmployeeId, gbc);

        gbc.gridx = 2; gbc.weightx = 0.1;
        panel.add(taoNhan("Họ tên"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.4;
        txtName = taoTextFieldDinhDang();
        panel.add(txtName, gbc);

        // --- Hàng 2: Ngày sinh & CCCD ---
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(taoNhan("Ngày sinh"), gbc);
        
        gbc.gridx = 1;
        dateChooserBirthDate = new JDateChooser();
        dateChooserBirthDate.setDateFormatString("dd/MM/yyyy");
        dateChooserBirthDate.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(dateChooserBirthDate, gbc);

        gbc.gridx = 2;
        panel.add(taoNhan("Số CCCD"), gbc);
        
        gbc.gridx = 3;
        txtIdNumber = taoTextFieldDinhDang();
        panel.add(txtIdNumber, gbc);

        // --- Hàng 3: Giới tính & SĐT ---
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(taoNhan("Giới tính"), gbc);
        
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(new Color(245, 222, 179));
        rbMale = new JRadioButton("Nam");
        rbFemale = new JRadioButton("Nữ");
        rbMale.setBackground(new Color(245, 222, 179));
        rbFemale.setBackground(new Color(245, 222, 179));
        rbMale.setFont(new Font("Arial", Font.PLAIN, 14));
        rbFemale.setFont(new Font("Arial", Font.PLAIN, 14));
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbMale); bg.add(rbFemale);
        rbMale.setSelected(true);
        genderPanel.add(rbMale); genderPanel.add(rbFemale);
        panel.add(genderPanel, gbc);

        gbc.gridx = 2;
        panel.add(taoNhan("Số điện thoại"), gbc);
        
        gbc.gridx = 3;
        txtPhone = taoTextFieldDinhDang();
        panel.add(txtPhone, gbc);

        // --- Hàng 4: Email & Trạng thái ---
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(taoNhan("Email"), gbc);
        
        gbc.gridx = 1;
        txtEmail = taoTextFieldDinhDang();
        panel.add(txtEmail, gbc);

        gbc.gridx = 2;
        panel.add(taoNhan("Trạng thái"), gbc);
        
        gbc.gridx = 3;
        cbStatus = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc", "Tạm nghỉ"});
        cbStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        cbStatus.setBackground(Color.WHITE);
        panel.add(cbStatus, gbc);

        // --- Hàng 5: Nút bấm ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(new Color(245, 222, 179));

        JButton btnAdd = taoNutForm("Thêm", new Color(76, 175, 80), new Color(56, 142, 60));
        btnAdd.addActionListener(e -> themNhanVien());

        JButton btnEdit = taoNutForm("Sửa", new Color(234, 196, 28), new Color(245, 166, 35));
        btnEdit.addActionListener(e -> suaNhanVien());
        
        // ⭐ NÚT LÀM MỚI
        JButton btnReset = taoNutForm("Làm mới", new Color(33, 150, 243), new Color(25, 118, 210));
        btnReset.addActionListener(e -> xoaTrangForm()); // Gọi hàm reset + sinh mã

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnReset);
        
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(255, 235, 205));

        JLabel lblSearch = new JLabel("Tìm kiếm (Mã/Tên):");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 14));
        lblSearch.setForeground(new Color(62, 39, 35));

        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(200, 35));
        
        JButton btnSearch = new JButton("Tìm");
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Arial", Font.BOLD, 14));
        btnSearch.setPreferredSize(new Dimension(100, 35));
        btnSearch.addActionListener(e -> timKiemNhanVien());

        panel.add(lblSearch);
        panel.add(txtSearch);
        panel.add(btnSearch);

        return panel;
    }

    private JScrollPane taoBang() {
        String[] columns = {"Mã NV", "Họ Tên", "Ngày Sinh", "CCCD", "SĐT", "Giới Tính", "Email", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(255, 178, 102));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) hienThiDongDaChon();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140), 2));
        return scrollPane;
    }

    // --- Hàm hỗ trợ giao diện ---
    private JLabel taoNhan(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(62, 39, 35));
        return label;
    }

    private JTextField taoTextFieldDinhDang() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return tf;
    }

    private JButton taoNutForm(String text, Color bg, Color border) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setBorder(BorderFactory.createLineBorder(border, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // PHẦN 2: LOGIC NGHIỆP VỤ (CONTROLLER)
    // =========================================================================

    // ⭐ HÀM XÓA TRẮNG FORM VÀ SINH MÃ MỚI
    private void xoaTrangForm() {
        // Tự động lấy mã mới từ DAO
        String maMoi = nhanVien_DAO.phatSinhMaNV();
        txtEmployeeId.setText(maMoi);
        
        txtName.setText("");
        dateChooserBirthDate.setDate(null);
        txtIdNumber.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        rbMale.setSelected(true);
        cbStatus.setSelectedIndex(0);
        
        table.clearSelection();
    }

    private void taiDuLieuVaoBang() {
        tableModel.setRowCount(0);
        List<NhanVien> list = nhanVien_DAO.layTatCaNhanVien();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (NhanVien nv : list) {
            String gt = (nv.getGioiTinh() != null && nv.getGioiTinh()) ? "Nữ" : "Nam";
            String ns = (nv.getNgaySinh() != null) ? sdf.format(nv.getNgaySinh()) : "";
            
            tableModel.addRow(new Object[]{
                nv.getMaNV(), nv.getHoTen(), ns, nv.getCCCD(), 
                nv.getSoDienThoai(), gt, nv.getEmail(), nv.getTrangThai()
            });
        }
    }

    private void hienThiDongDaChon() {
        int r = table.getSelectedRow();
        if (r != -1) {
            txtEmployeeId.setText(tableModel.getValueAt(r, 0).toString());
            txtName.setText(tableModel.getValueAt(r, 1).toString());
            
            try {
                String ns = tableModel.getValueAt(r, 2).toString();
                if(!ns.isEmpty()) 
                    dateChooserBirthDate.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(ns));
                else 
                    dateChooserBirthDate.setDate(null);
            } catch (ParseException e) { e.printStackTrace(); }

            txtIdNumber.setText(tableModel.getValueAt(r, 3).toString());
            txtPhone.setText(tableModel.getValueAt(r, 4).toString());
            
            String gt = tableModel.getValueAt(r, 5).toString();
            if (gt.equalsIgnoreCase("Nữ")) rbFemale.setSelected(true);
            else rbMale.setSelected(true);

            txtEmail.setText(tableModel.getValueAt(r, 6).toString());
            cbStatus.setSelectedItem(tableModel.getValueAt(r, 7).toString());
        }
    }

    private void themNhanVien() {
        // Mã NV lấy tự động từ ô (đã sinh sẵn)
        String ma = txtEmployeeId.getText();
        String ten = txtName.getText().trim();
        Date ngSinh = dateChooserBirthDate.getDate();
        String cccd = txtIdNumber.getText().trim();
        String sdt = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String tt = cbStatus.getSelectedItem().toString();
        boolean gioiTinh = rbFemale.isSelected(); // Nữ = true

        if (ten.isEmpty() || cccd.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            NhanVien nv = new NhanVien(ma, ten, cccd, sdt, email, gioiTinh, ngSinh, tt, new QuanLy("QL001"));
            nhanVien_DAO.themNhanVien(nv);
            
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            taiDuLieuVaoBang();
            xoaTrangForm(); // Reset form và sinh mã kế tiếp
            
        } catch (SQLException e) {
            xuLyLoiSQL(e, ma, cccd, "Thêm");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void suaNhanVien() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa.");
            return;
        }

        String ma = txtEmployeeId.getText();
        String ten = txtName.getText().trim();
        Date ngSinh = dateChooserBirthDate.getDate();
        String cccd = txtIdNumber.getText().trim();
        String sdt = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String tt = cbStatus.getSelectedItem().toString();
        boolean gioiTinh = rbFemale.isSelected();

        try {
            // Lấy mã QL cũ để giữ nguyên (nếu cần)
            NhanVien old = nhanVien_DAO.timNhanVienTheoMa(ma);
            String maQL = (old != null && old.getQuanLy() != null) ? old.getQuanLy().getMaQL() : "QL001";

            NhanVien nv = new NhanVien(ma, ten, cccd, sdt, email, gioiTinh, ngSinh, tt, new QuanLy(maQL));
            nhanVien_DAO.capNhatNhanVien(nv);
            
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            taiDuLieuVaoBang();
            
        } catch (SQLException e) {
            xuLyLoiSQL(e, ma, cccd, "Sửa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timKiemNhanVien() {
        String key = txtSearch.getText().trim();
        if (key.isEmpty()) {
            taiDuLieuVaoBang();
            return;
        }
        List<NhanVien> list = nhanVien_DAO.timNhanVien(key);
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy!");
        } else {
            for (NhanVien nv : list) {
                String gt = (nv.getGioiTinh() != null && nv.getGioiTinh()) ? "Nữ" : "Nam";
                String ns = (nv.getNgaySinh() != null) ? sdf.format(nv.getNgaySinh()) : "";
                tableModel.addRow(new Object[]{
                    nv.getMaNV(), nv.getHoTen(), ns, nv.getCCCD(), 
                    nv.getSoDienThoai(), gt, nv.getEmail(), nv.getTrangThai()
                });
            }
        }
    }

    private void xuLyLoiSQL(SQLException e, String ma, String cccd, String action) {
        String msg = e.getMessage();
        if (msg.contains("PRIMARY KEY")) 
            JOptionPane.showMessageDialog(this, "Mã nhân viên '" + ma + "' đã tồn tại!", "Trùng Mã", JOptionPane.ERROR_MESSAGE);
        else if (msg.contains("CCCD") || msg.contains(cccd))
            JOptionPane.showMessageDialog(this, "Số CCCD '" + cccd + "' đã tồn tại!", "Trùng CCCD", JOptionPane.ERROR_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, "Lỗi CSDL khi " + action + ": " + msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}