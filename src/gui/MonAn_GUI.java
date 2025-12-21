package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL; // Import thêm URL
import java.util.List;
import dao.MonAn_DAO;
import entity.MonAn;
import entity.QuanLy;

public class MonAn_GUI extends JPanel {
    private JTextField txtMaMon, txtTenMon, txtGiaBan, txtSearch;
    private JComboBox<String> cboLoaiMon, cboFilterLoai, cboFilterGia;
    private JButton btnThemMon, btnChinhSua, btnChooseFile, btnXoaMon, btnXoaTrang;
    private JLabel lblImage;
    private JTable table;
    private DefaultTableModel tableModel;
    private File selectedImageFile;

    private final MonAn_DAO monAnDAO; 
    private List<MonAn> currentMonAnList;

    public MonAn_GUI() {
        monAnDAO = new MonAn_DAO();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 222, 173));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Quản lý món ăn", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);

        // Form panel (left side)
        JPanel formPanel = createFormPanel();
        
        // Image panel (right side)
        JPanel imagePanel = createImagePanel();

        // Top panel combining form and image
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        topPanel.setOpaque(false);
        topPanel.add(formPanel);
        topPanel.add(imagePanel);

        // Search and filter panel
        JPanel searchPanel = createSearchPanel();
        ((JButton) searchPanel.getComponent(1)).addActionListener(e -> searchMonAn());
        
        // Gán Listener cho ComboBox LỌC
        cboFilterLoai.addActionListener(e -> filterMonAn());
        cboFilterGia.addActionListener(e -> filterMonAn()); 

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Add all panels
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(searchPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Load data from CSDL khi khởi tạo
        loadDataToTable();
        
        // Hoãn việc gọi clearForm() cho đến khi components có kích thước
        SwingUtilities.invokeLater(() -> {
            clearForm(); 
        });
    }

// ---------------------------------------------------------------------------------------------------
// PHƯƠNG THỨC KHỞI TẠO PANEL
// ---------------------------------------------------------------------------------------------------

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 239, 213));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Mã món
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblMaMon = new JLabel("Mã món:");
        lblMaMon.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblMaMon, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtMaMon = new JTextField();
        txtMaMon.setBackground(new Color(200, 200, 200));
        txtMaMon.setPreferredSize(new Dimension(250, 35));
        // Luôn khóa Mã món vì nó sẽ được phát sinh tự động
        txtMaMon.setEditable(false); 
        panel.add(txtMaMon, gbc);

        // Tên món
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblTenMon = new JLabel("Tên món:");
        lblTenMon.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTenMon, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtTenMon = new JTextField();
        txtTenMon.setPreferredSize(new Dimension(250, 35));
        panel.add(txtTenMon, gbc);

        // Loại món
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblLoaiMon = new JLabel("Loại món:");
        lblLoaiMon.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblLoaiMon, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cboLoaiMon = new JComboBox<>(new String[]{"Món chính", "Món phụ", "Thức uống", "Tráng miệng"});
        cboLoaiMon.setPreferredSize(new Dimension(250, 35));
        panel.add(cboLoaiMon, gbc);

        // Giá bán
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblGiaBan = new JLabel("Giá bán:");
        lblGiaBan.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblGiaBan, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtGiaBan = new JTextField();
        txtGiaBan.setPreferredSize(new Dimension(250, 35));
        panel.add(txtGiaBan, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        btnThemMon = new JButton("Thêm món");
        btnThemMon.setBackground(new Color(144, 238, 144));
        btnThemMon.setPreferredSize(new Dimension(100, 40));
        btnThemMon.setFont(new Font("Arial", Font.BOLD, 13));
        btnThemMon.addActionListener(e -> addFood());

        btnChinhSua = new JButton("Chỉnh sửa");
        btnChinhSua.setBackground(new Color(255, 255, 0));
        btnChinhSua.setPreferredSize(new Dimension(100, 40));
        btnChinhSua.setFont(new Font("Arial", Font.BOLD, 13));
        btnChinhSua.addActionListener(e -> editFood());
        
        btnXoaMon = new JButton("Xóa món");
        btnXoaMon.setBackground(new Color(255, 99, 71));
        btnXoaMon.setPreferredSize(new Dimension(100, 40));
        btnXoaMon.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaMon.addActionListener(e -> deleteFood());

        btnXoaTrang = new JButton("Xóa trắng");
        btnXoaTrang.setBackground(Color.WHITE);
        btnXoaTrang.setPreferredSize(new Dimension(100, 40));
        btnXoaTrang.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaTrang.addActionListener(e -> clearForm());

        buttonPanel.add(btnThemMon);
        buttonPanel.add(btnChinhSua);
        buttonPanel.add(btnXoaMon);
        buttonPanel.add(btnXoaTrang);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(230, 180));
        lblImage.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Ban đầu, đặt kích thước mặc định
        // createPlaceholderImage được tích hợp trong loadImageIcon khi null
        lblImage.setIcon(loadImageIcon("default.png")); 

        btnChooseFile = new JButton("Choose file");
        btnChooseFile.addActionListener(e -> chooseImage());

        panel.add(lblImage, BorderLayout.CENTER);
        panel.add(btnChooseFile, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        
        JButton btnSearch = new JButton("🔍");
        btnSearch.setPreferredSize(new Dimension(50, 35));

        cboFilterLoai = new JComboBox<>(new String[]{"Loại món", "Món chính", "Món phụ" ,"Thức uống", "Tráng miệng"});
        cboFilterLoai.setPreferredSize(new Dimension(150, 35));

        cboFilterGia = new JComboBox<>(new String[]{"Giá", "Thấp đến cao", "Cao đến thấp"});
        cboFilterGia.setPreferredSize(new Dimension(150, 35));
        
        panel.add(txtSearch);
        panel.add(btnSearch);
        panel.add(cboFilterLoai);
        panel.add(cboFilterGia);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {"Mã món", "Tên món", "Loại món", "Giá"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(255, 239, 213));
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    loadSelectedRow();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

// PHƯƠNG THỨC XỬ LÝ DỮ LIỆU

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        try {
            currentMonAnList = monAnDAO.docTuBang();
            
            for (MonAn mon : currentMonAnList) {
                Object[] row = new Object[]{
                    mon.getMaMonAn(),
                    mon.getTenMonAn(),
                    mon.getLoaiMonAn(),
                    String.format("%,.0f VND", mon.getGiaMonAn()) 
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu món ăn: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addFood() {
        // 1. Lấy dữ liệu từ form
        String maMon = txtMaMon.getText().trim();
        String tenMon = txtTenMon.getText().trim();
        String loaiMon = cboLoaiMon.getSelectedItem().toString();
        String giaBanStr = txtGiaBan.getText().trim();
        
        // --- VALIDATION CƠ BẢN ---
        if (maMon.isEmpty() || maMon.equals("LỖI MÃ")) {
            JOptionPane.showMessageDialog(this, "Không thể phát sinh Mã món. Vui lòng kiểm tra kết nối CSDL.", "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tenMon.isEmpty() || giaBanStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin (Tên và Giá bán)!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedImageFile == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hình ảnh cho món ăn!", "Thiếu hình ảnh", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double giaBan;
        try {
            giaBan = Double.parseDouble(giaBanStr);
            if (giaBan <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số dương hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra trùng tên
        boolean isDuplicateName = currentMonAnList.stream()
                .anyMatch(mon -> mon.getTenMonAn().equalsIgnoreCase(tenMon));

        if (isDuplicateName) {
            JOptionPane.showMessageDialog(this, "Tên món ăn '" + tenMon + "' đã tồn tại! Vui lòng đặt tên khác.", "Trùng tên món", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- THỰC HIỆN THÊM MÓN ---
        QuanLy ql = new QuanLy("QL001"); 
        
        // Lấy tên file từ file ảnh đã chọn
        String hinhAnh = selectedImageFile.getName(); 

        MonAn newMon = new MonAn(maMon, tenMon, loaiMon, giaBan, hinhAnh, ql);

        try {
            if (monAnDAO.themMonAn(newMon)) {
                JOptionPane.showMessageDialog(this, "Thêm món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable(); 
                clearForm();      
            } else {
                JOptionPane.showMessageDialog(this, "Thêm món thất bại. Có thể Mã món đã tồn tại hoặc lỗi CSDL.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm món: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editFood() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món để chỉnh sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maMon = txtMaMon.getText().trim();
        String tenMon = txtTenMon.getText().trim();
        String loaiMon = cboLoaiMon.getSelectedItem().toString();
        String giaBanStr = txtGiaBan.getText().trim();

        if (tenMon.isEmpty() || giaBanStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double giaBan;
        try {
            giaBan = Double.parseDouble(giaBanStr);
            if (giaBan <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số dương hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tìm đối tượng MonAn đầy đủ trong list đã load 
        MonAn updatedMon = currentMonAnList.stream()
            .filter(m -> m.getMaMonAn().equals(maMon))
            .findFirst()
            .orElse(new MonAn(maMon)); 
        
        try {
            updatedMon.setTenMonAn(tenMon);
            updatedMon.setLoaiMonAn(loaiMon);
            updatedMon.setGiaMonAn(giaBan);
            if (selectedImageFile != null) {
                 // Cập nhật tên file mới vào đối tượng
                 updatedMon.setHinhAnh(selectedImageFile.getName()); 
            }
            // Ngược lại, nếu selectedImageFile == null, nó sẽ giữ nguyên tên ảnh cũ đã load từ CSDL

            if (monAnDAO.chinhSuaMonAn(updatedMon)) {
                JOptionPane.showMessageDialog(this, "Cập nhật món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật món thất bại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật món: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteFood() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maMon = tableModel.getValueAt(selectedRow, 0).toString();
        String tenMon = tableModel.getValueAt(selectedRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa món " + tenMon + " (Mã: " + maMon + ")?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (monAnDAO.xoaMonAn(maMon)) {
                    JOptionPane.showMessageDialog(this, "Xóa món thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDataToTable();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Xóa món thất bại. Có thể món ăn này đang được sử dụng trong Hóa Đơn (lỗi khóa ngoại).", 
                        "Lỗi CSDL", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa món: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void loadSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String maMon = tableModel.getValueAt(selectedRow, 0).toString();
            
            MonAn selectedMon = currentMonAnList.stream()
                .filter(m -> m.getMaMonAn().equals(maMon))
                .findFirst()
                .orElse(null);

            if (selectedMon != null) {
                txtMaMon.setText(selectedMon.getMaMonAn());
                txtTenMon.setText(selectedMon.getTenMonAn());
                cboLoaiMon.setSelectedItem(selectedMon.getLoaiMonAn());
                txtGiaBan.setText(String.format("%.0f", selectedMon.getGiaMonAn()));
                
                txtMaMon.setEditable(false); 
                
                // --- QUAN TRỌNG: TẢI ẢNH BẰNG getResource ---
                lblImage.setIcon(loadImageIcon(selectedMon.getHinhAnh())); 
                selectedImageFile = null; 
            }
        }
    }
    
    // PHƯƠNG THỨC XÓA TRẮNG (CLEAR FORM) VÀ PHÁT SINH MÃ
    private void clearForm() {
        txtTenMon.setText("");
        txtGiaBan.setText("");
        cboLoaiMon.setSelectedIndex(0);
        table.clearSelection();
        
        lblImage.setIcon(loadImageIcon("default.png")); 
        selectedImageFile = null;
        
        // TỰ ĐỘNG PHÁT SINH MÃ MÓN MỚI
        try {
            String newMa = monAnDAO.generateNewMaMon();
            txtMaMon.setText(newMa);
        } catch (Exception e) {
            txtMaMon.setText("LỖI MÃ");
        }
        
        txtMaMon.setEditable(false); 
    }

    private void searchMonAn() {
        String query = txtSearch.getText().trim();
        
        if (query.isEmpty()) {
            loadDataToTable();
            return;
        }
        
        tableModel.setRowCount(0);
        try {
            currentMonAnList = monAnDAO.timTheoTen(query);
            for (MonAn mon : currentMonAnList) {
                Object[] row = new Object[]{
                    mon.getMaMonAn(),
                    mon.getTenMonAn(),
                    mon.getLoaiMonAn(),
                    String.format("%,.0f VND", mon.getGiaMonAn()) 
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // PHƯƠNG THỨC LỌC DỮ LIỆU (THEO LOẠI VÀ GIÁ)
    private void filterMonAn() {
        String loaiMon = cboFilterLoai.getSelectedItem().toString();
        String thuTuGia = "ASC"; // Mặc định: Thấp đến cao

        if (loaiMon.equals("Loại món")) {
            loaiMon = "Tất cả";
        }
        
        String filterGia = cboFilterGia.getSelectedItem().toString();
        if (filterGia.equals("Cao đến thấp")) {
            thuTuGia = "DESC";
        } 

        tableModel.setRowCount(0);
        try {
            currentMonAnList = monAnDAO.locMonAn(loaiMon, thuTuGia);
            for (MonAn mon : currentMonAnList) {
                Object[] row = new Object[]{
                    mon.getMaMonAn(),
                    mon.getTenMonAn(),
                    mon.getLoaiMonAn(),
                    String.format("%,.0f VND", mon.getGiaMonAn()) 
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

// ---------------------------------------------------------------------------------------------------
// PHƯƠNG THỨC TIỆN ÍCH
// ---------------------------------------------------------------------------------------------------
    private void chooseImage() {
        // Mở file chooser
        JFileChooser fileChooser = new JFileChooser();
        
        // Cố gắng mở tại thư mục src/image nếu đang ở môi trường dev để tiện chọn
        File devDir = new File("src/image");
        if(devDir.exists()) fileChooser.setCurrentDirectory(devDir);
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") 
                    || f.getName().toLowerCase().endsWith(".png")
                    || f.getName().toLowerCase().endsWith(".jpeg");
            }
            public String getDescription() {
                return "Image Files (*.jpg, *.png, *.jpeg)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            // Load ảnh từ file thực tế vừa chọn để preview
            ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(lblImage.getWidth(), lblImage.getHeight(), Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
        }
    }

    private Image createPlaceholderImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            width = 230; 
            height = 180;
        }
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.GRAY);
        g2d.drawString("No Image", width/2 - 25, height/2);
        g2d.dispose();
        return img;
    }
    
    /**
     * --- HÀM QUAN TRỌNG ĐÃ ĐƯỢC SỬA ---
     * Tải ảnh từ ClassPath (trong file JAR hoặc src folder)
     * Thay thế cho cách dùng new File("src/image/...") bị lỗi
     */
    private ImageIcon loadImageIcon(String fileName) {
        int width = lblImage.getWidth();
        int height = lblImage.getHeight();
        if (width == 0) width = 230; // Kích thước dự phòng
        if (height == 0) height = 180;

        if (fileName == null || fileName.isEmpty()) {
            fileName = "default.png";
        }
        
        // Sử dụng getResource để lấy đường dẫn URL bên trong JAR/Classpath
        URL imgURL = getClass().getResource("/image/" + fileName);
        
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            // Nếu không tìm thấy ảnh (hoặc ảnh default cũng mất), vẽ placeholder
            return new ImageIcon(createPlaceholderImage(width, height));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý món ăn");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MonAn_GUI());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}