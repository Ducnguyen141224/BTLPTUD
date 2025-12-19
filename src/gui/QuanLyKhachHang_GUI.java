package gui;

import connectDB.ConnectDB;
import dao.KhachHang_DAO;
import dao.TheThanhVien_DAO;
import entity.KhachHang;
import entity.NhanVien; 
import entity.TheThanhVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuanLyKhachHang_GUI extends JPanel {

    private JTextField txtMaKH, txtHoTen, txtSDT, txtEmail, txtTimKiem;
    private JRadioButton radNam, radNu;
    private ButtonGroup nhomGioiTinh;
    private JTextField txtDiem;
    private JTable bangKH;
    private DefaultTableModel modelBangKH;

   
    private KhachHang_DAO khDAO;
    private TheThanhVien_DAO ttvDAO;

    private NhanVien nvDangNhap;

    public QuanLyKhachHang_GUI() {
        khDAO = new KhachHang_DAO();
        ttvDAO = new TheThanhVien_DAO();
       
        nvDangNhap = new NhanVien("NV001"); 
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 218, 170)); // Màu nền be nhạt
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); // Thêm padding

        // --- Tiêu đề ---
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 36));
        lblTieuDe.setForeground(Color.BLACK); // Màu chữ đen
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Padding dưới
        
        // --- Panel Trung tâm (Chứa Form và các nút chức năng) ---
        JPanel pnlGiua = new JPanel(new BorderLayout(10, 10));
        pnlGiua.setBackground(new Color(255, 218, 170)); // Cùng màu nền chính
        JPanel pnlForm = taoPanelForm(); // Hàm tạo form nhập liệu
        pnlGiua.add(pnlForm, BorderLayout.CENTER);
        JPanel pnlNutChucNang = taoPanelNut(); // Hàm tạo các nút Thêm, Xóa, Sửa, Làm mới
        pnlGiua.add(pnlNutChucNang, BorderLayout.SOUTH);
        
        // --- Panel Dưới (Chứa ô tìm kiếm và bảng dữ liệu) ---
        JPanel pnlDuoi = new JPanel(new BorderLayout(15, 15));
        pnlDuoi.setBackground(new Color(255, 218, 170)); // Cùng màu nền chính
        JPanel pnlTimKiem = taoPanelTimKiem(); // Hàm tạo ô tìm kiếm
        pnlDuoi.add(pnlTimKiem, BorderLayout.NORTH);
        JScrollPane cuonBangKH = taoBang(); // Hàm tạo bảng dữ liệu
        pnlDuoi.add(cuonBangKH, BorderLayout.CENTER);
        
        // --- Sắp xếp các Panel chính ---
        // Panel bọc phần trên (Tiêu đề + Form + Nút chức năng)
        JPanel pnlBocTren = new JPanel(new BorderLayout());
        pnlBocTren.setBackground(new Color(255, 218, 170)); // Cùng màu nền chính
        pnlBocTren.add(lblTieuDe, BorderLayout.NORTH);  
        pnlBocTren.add(pnlGiua, BorderLayout.CENTER); 
        
        // Thêm Panel bọc trên vào vị trí NORTH của Layout chính
        add(pnlBocTren, BorderLayout.NORTH);
        // Thêm Panel dưới (Tìm kiếm + Bảng) vào vị trí CENTER
        add(pnlDuoi, BorderLayout.CENTER);
        
        // --- Kết thúc Cài đặt Layout ---

        // Tải dữ liệu ban đầu
        taiDuLieuVaoBang();
        
        // Xóa trắng form và phát sinh mã KH mới
        xoaTrangForm();
    }

    private void taiDuLieuVaoBang() {
        try {
            modelBangKH.setRowCount(0); 
            ArrayList<Object[]> dsKH = khDAO.layTatCaKhachHangChoBang();           
            for (Object[] dong : dsKH) {
                modelBangKH.addRow(dong);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
       
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khách hàng!", "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

  
    private JPanel taoPanelForm() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(new Color(220, 210, 210)); // Màu nền form xám nhạt
        // Thêm viền và padding cho form
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Viền xám
            BorderFactory.createEmptyBorder(25, 30, 25, 30) // Padding
        ));
        GridBagConstraints gbc = new GridBagConstraints(); // Dùng để sắp xếp vị trí component
        gbc.insets = new Insets(10, 15, 10, 15); // Khoảng cách giữa các component
        gbc.fill = GridBagConstraints.HORIZONTAL; // Kéo dãn component theo chiều ngang

        // --- Hàng 1: Mã KH, Họ tên ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2; gbc.anchor = GridBagConstraints.WEST; // Căn lề trái cho label
        pnl.add(taoNhan("Mã khách hàng"), gbc); // Thêm label Mã KH
        gbc.gridx = 1; gbc.weightx = 0.8; // TextField chiếm nhiều không gian hơn
        txtMaKH = taoTextFieldDinhDang(); // Tạo ô nhập Mã KH
        
        // Khóa ô Mã KH vì nó được phát sinh tự động
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(new Color(230, 230, 230)); // Đặt nền xám để biểu thị bị khóa
        
        pnl.add(txtMaKH, gbc); // Thêm ô nhập Mã KH
        gbc.gridx = 2; gbc.weightx = 0.2; // Quay lại label
        pnl.add(taoNhan("Họ tên"), gbc); // Thêm label Họ tên
        gbc.gridx = 3; gbc.weightx = 0.8; // TextField
        txtHoTen = taoTextFieldDinhDang(); // Tạo ô nhập Họ tên
        pnl.add(txtHoTen, gbc); // Thêm ô nhập Họ tên

        // --- Hàng 2: Số điện thoại, Email ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        pnl.add(taoNhan("Số điện thoại"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtSDT = taoTextFieldDinhDang();
        pnl.add(txtSDT, gbc);
        gbc.gridx = 2; gbc.weightx = 0.2;
        pnl.add(taoNhan("Email"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtEmail = taoTextFieldDinhDang();
        pnl.add(txtEmail, gbc);

        // --- Hàng 3: Giới tính, Điểm tích lũy ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        pnl.add(taoNhan("Giới tính"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        // Panel riêng cho các RadioButton giới tính
        JPanel pnlGioiTinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlGioiTinh.setBackground(new Color(220, 210, 210)); // Cùng màu nền form
        radNam = new JRadioButton("Nam"); // RadioButton Nam
        radNu = new JRadioButton("Nữ");   // RadioButton Nữ
        // Đặt màu nền và font cho RadioButton
        radNam.setBackground(new Color(220, 210, 210));
        radNu.setBackground(new Color(220, 210, 210));
        radNam.setFont(new Font("Arial", Font.PLAIN, 14));
        radNu.setFont(new Font("Arial", Font.PLAIN, 14));
        // Tạo nhóm để chỉ chọn được 1 trong 2
        nhomGioiTinh = new ButtonGroup();
        nhomGioiTinh.add(radNam);
        nhomGioiTinh.add(radNu);
        // Thêm RadioButton vào panel giới tính
        pnlGioiTinh.add(radNam);
        pnlGioiTinh.add(radNu);
        pnl.add(pnlGioiTinh, gbc); // Thêm panel giới tính vào form chính

        gbc.gridx = 2; gbc.weightx = 0.2;
        pnl.add(taoNhan("Điểm tích lũy"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        txtDiem = taoTextFieldDinhDang();
        txtDiem.setText("0"); // Giá trị mặc định là 0
        pnl.add(txtDiem, gbc);

        return pnl; // Trả về panel form đã tạo
    }

  
    private JPanel taoPanelNut() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15)); // Căn giữa các nút
        pnl.setBackground(new Color(255, 218, 170)); // Cùng màu nền chính

        // Tạo các nút
        JButton btnThem = new JButton("Thêm khách hàng");
        dinhDangNut(btnThem, new Color(76, 175, 80), Color.WHITE); // Màu xanh lá
        btnThem.addActionListener(e -> themKhachHang()); // Gán sự kiện khi nhấn nút Thêm

        JButton btnXoa = new JButton("Xóa khách hàng");
        dinhDangNut(btnXoa, new Color(244, 67, 54), Color.WHITE); // Màu đỏ
        btnXoa.addActionListener(e -> xoaKhachHang()); // Gán sự kiện khi nhấn nút Xóa

        JButton btnSua = new JButton("Chỉnh sửa thông tin");
        dinhDangNut(btnSua, new Color(234, 196, 28), Color.WHITE); // Màu vàng
        btnSua.addActionListener(e -> suaKhachHang()); // Gán sự kiện khi nhấn nút Sửa

        JButton btnLamMoi = new JButton("Làm mới");
        dinhDangNut(btnLamMoi, new Color(158, 158, 158), Color.WHITE); // Màu xám
        btnLamMoi.addActionListener(e -> xoaTrangForm()); // Gán sự kiện khi nhấn nút Làm mới

        // Thêm các nút vào panel
        pnl.add(btnThem);
        pnl.add(btnXoa);
        pnl.add(btnSua);
        pnl.add(btnLamMoi);

        return pnl; // Trả về panel chứa các nút
    }

   
    private void dinhDangNut(JButton nut, Color mauNen, Color mauChu) {
        nut.setBackground(mauNen); // Đặt màu nền
        nut.setForeground(mauChu); // Đặt màu chữ
        nut.setFocusPainted(false); // Bỏ viền focus khi nhấn
        nut.setFont(new Font("Arial", Font.BOLD, 14)); // Đặt font chữ
        nut.setPreferredSize(new Dimension(230, 45)); // Đặt kích thước cố định
        nut.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ khi di chuột vào
        // Thêm viền và padding cho nút
        nut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(mauNen.darker(), 2), // Viền đậm hơn màu nền
            BorderFactory.createEmptyBorder(8, 20, 8, 20) // Padding trong nút
        ));
    }

  
    private JLabel taoNhan(String chuỗiNhan) {
        JLabel nhan = new JLabel(chuỗiNhan);
        nhan.setFont(new Font("Arial", Font.BOLD, 14)); // Font chữ
        nhan.setForeground(Color.BLACK); // Màu chữ đen
        return nhan;
    }

    
    private JTextField taoTextFieldDinhDang() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14)); // Font chữ
        textField.setBackground(Color.WHITE); // Nền trắng
        textField.setPreferredSize(new Dimension(0, 35)); // Chiều cao 35px
        // Thêm viền và padding
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Viền xám nhạt
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding trong ô nhập
        ));
        return textField;
    }


    private JPanel taoPanelTimKiem() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); // Căn lề trái
        pnl.setBackground(new Color(255, 218, 170)); // Cùng màu nền chính

        // Ô nhập từ khóa tìm kiếm
        txtTimKiem = new JTextField(35); // Độ rộng khoảng 35 ký tự
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 14));
        // Thêm viền và padding
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Nút Tìm kiếm
        JButton btnTim = new JButton("Tìm Kiếm");
        btnTim.setFocusPainted(false); // Bỏ viền focus
        btnTim.setFont(new Font("Arial", Font.BOLD, 16));
        btnTim.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Con trỏ tay
        btnTim.setPreferredSize(new Dimension(150, 35)); // Kích thước nút
        btnTim.addActionListener(e -> timKiemKhachHang()); // Gán sự kiện tìm kiếm
        btnTim.setBackground(new Color(33, 150, 243)); // Màu xanh dương
        btnTim.setForeground(Color.WHITE); // Chữ trắng

        // Label hướng dẫn
        JLabel lblTim = new JLabel("Tìm khách hàng:");
        lblTim.setFont(new Font("Arial", Font.BOLD, 13));
        lblTim.setForeground(new Color(100, 100, 100)); // Màu xám

        // Thêm các component vào panel
        pnl.add(lblTim);
        pnl.add(txtTimKiem);
        pnl.add(btnTim);

        return pnl; // Trả về panel tìm kiếm
    }

    /**
     * Tạo JScrollPane chứa bảng dữ liệu khách hàng.
     * @return JScrollPane chứa JTable.
     */
    private JScrollPane taoBang() {
        // Định nghĩa tên các cột
        String[] cot = {"Mã", "Tên khách hàng", "Thẻ thành viên", "Số điện thoại", "Giới tính", "Email"};
        // Tạo table model, không cho phép sửa trực tiếp trên bảng
        modelBangKH = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        // Tạo JTable từ model
        bangKH = new JTable(modelBangKH);
        bangKH.setRowHeight(40); // Chiều cao mỗi dòng
        bangKH.setFont(new Font("Arial", Font.PLAIN, 13)); // Font chữ nội dung bảng
        bangKH.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ cho chọn 1 dòng
        // Đặt màu nền và chữ khi chọn dòng
        bangKH.setSelectionBackground(new Color(255, 200, 150)); // Màu cam nhạt
        bangKH.setSelectionForeground(Color.BLACK); // Chữ đen
        bangKH.setGridColor(new Color(200, 200, 200)); // Màu đường kẻ
        bangKH.setShowGrid(true); // Hiển thị đường kẻ
        bangKH.setIntercellSpacing(new Dimension(1, 1)); // Khoảng cách giữa các ô
        // Thêm sự kiện: Khi click vào 1 dòng, hiển thị thông tin lên form
        bangKH.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Chỉ xử lý khi click 1 lần
                    hienThiDongDaChon(); // Gọi hàm hiển thị thông tin
                }
            }
        });

        // Định dạng tiêu đề bảng
        JTableHeader tieuDeBang = bangKH.getTableHeader();
        tieuDeBang.setBackground(new Color(255, 178, 102)); // Màu cam đậm
        tieuDeBang.setForeground(Color.BLACK); // Chữ đen
        tieuDeBang.setFont(new Font("Arial", Font.BOLD, 14)); // Font chữ tiêu đề
        tieuDeBang.setPreferredSize(new Dimension(tieuDeBang.getWidth(), 45)); // Chiều cao tiêu đề
        tieuDeBang.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 100), 2)); // Viền tiêu đề

        // Đặt bảng vào JScrollPane để có thanh cuộn
        JScrollPane cuon = new JScrollPane(bangKH);
        cuon.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 100), 2)); // Viền cho ScrollPane

        return cuon; // Trả về ScrollPane chứa bảng
    }

    /**
     * Xử lý sự kiện thêm khách hàng mới.
     * Kiểm tra dữ liệu -> Gọi DAO -> Cập nhật giao diện.
     */
    private void themKhachHang() {
        // Kiểm tra dữ liệu nhập có hợp lệ không
        if (!kiemTraDuLieuNhap()) {
            return; // Dừng nếu dữ liệu không hợp lệ
        }

        // Lấy mã KH và SĐT từ form để hiển thị trong thông báo lỗi nếu cần
        String maKH = txtMaKH.getText().trim(); 
        String sdt = txtSDT.getText().trim();     

        try {
            // Lấy các thông tin khác từ form
            String hoTen = txtHoTen.getText().trim();
            String email = txtEmail.getText().trim();
            boolean gioiTinh = radNam.isSelected(); // true là Nam, false là Nữ (cần thống nhất với DB/Entity)

            // Tạo đối tượng KhachHang từ dữ liệu form
            KhachHang kh = new KhachHang(maKH, hoTen, sdt, email, gioiTinh);

            // Gọi DAO để thêm khách hàng (có thể ném SQLException)
            if (khDAO.themKhachHang(kh)) {
                // Nếu thêm KH thành công, tiến hành thêm thẻ thành viên
                int diem = chuyenDoiDiem(); // Lấy điểm từ form (có thể ném IllegalArgumentException)
                String maTheMoi = ttvDAO.phatSinhMaThe(); // Phát sinh mã thẻ mới (có thể ném SQLException)
                // Tạo đối tượng TheThanhVien
                TheThanhVien ttv = new TheThanhVien(maTheMoi, kh, diem); 
                // Gọi DAO để thêm thẻ thành viên (có thể ném SQLException)
                ttvDAO.themTheThanhVien(ttv); 

                // Nếu cả hai thao tác thêm KH và thêm Thẻ đều thành công
                taiDuLieuVaoBang(); // Tải lại dữ liệu mới vào bảng
                xoaTrangForm();     // Xóa trắng form và phát sinh mã KH mới
                // Hiển thị thông báo thành công
                JOptionPane.showMessageDialog(this, "Đã thêm khách hàng và thẻ thành viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Trường hợp hiếm gặp: DAO trả về false mà không ném lỗi
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại (DAO trả về false)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            // --- Xử lý các lỗi SQL cụ thể ---
            String thongBaoLoi = e.getMessage();

            if (thongBaoLoi.contains("PRIMARY KEY") && thongBaoLoi.contains(maKH)) {
                // Lỗi trùng Mã Khách Hàng (PK)
                JOptionPane.showMessageDialog(this,
                    "Lỗi: Mã khách hàng '" + maKH + "' đã tồn tại.\n" +
                    "Vui lòng nhấn 'Làm mới' để lấy mã mới.",
                    "Lỗi Trùng Mã Khách Hàng",
                    JOptionPane.ERROR_MESSAGE);
            } else if (thongBaoLoi.contains("UNIQUE") && thongBaoLoi.contains(sdt)) {
                // Lỗi trùng Số Điện Thoại (UQ constraint trong DB)
                JOptionPane.showMessageDialog(this,
                    "Lỗi: Số điện thoại '" + sdt + "' đã được đăng ký cho khách hàng khác.",
                    "Lỗi Trùng Số Điện Thoại",
                    JOptionPane.ERROR_MESSAGE);
            } else if (thongBaoLoi.contains("PRIMARY KEY") && thongBaoLoi.contains("THETHANHVIEN")) {
                 // Lỗi trùng Mã Thẻ Thành Viên khi thêm thẻ
                 JOptionPane.showMessageDialog(this,
                    "Lỗi: Không thể tạo thẻ thành viên, mã thẻ tự động phát sinh bị trùng.\n" +
                    "Vui lòng thử lại.",
                    "Lỗi Tạo Mã Thẻ",
                    JOptionPane.ERROR_MESSAGE);
            }
             else {
                // Các lỗi SQL khác không xác định
                e.printStackTrace(); // In lỗi ra console để debug
                JOptionPane.showMessageDialog(this,
                    "Lỗi Cơ sở dữ liệu khi thêm khách hàng hoặc thẻ thành viên:\n" + thongBaoLoi,
                    "Lỗi Cơ Sở Dữ Liệu",
                    JOptionPane.ERROR_MESSAGE);
            }
            // --- Kết thúc xử lý lỗi SQL ---
        } catch (IllegalArgumentException e) {
             // Lỗi do dữ liệu nhập không hợp lệ (từ kiemTraDuLieuNhap hoặc chuyenDoiDiem)
             JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi Dữ Liệu Nhập", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Bắt các lỗi không mong muốn khác
            ex.printStackTrace(); // In lỗi ra console để debug
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Xử lý sự kiện xóa khách hàng đang được chọn.
     * Xác nhận -> Gọi DAO -> Cập nhật giao diện.
     */
    private void xoaKhachHang() {
        int hangDaChon = bangKH.getSelectedRow(); // Lấy chỉ số dòng đang chọn
        // Kiểm tra xem có dòng nào được chọn không
        if (hangDaChon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Chưa chọn khách hàng", JOptionPane.WARNING_MESSAGE);
            return; // Dừng nếu chưa chọn
        }
        // Lấy mã KH từ dòng đã chọn
        String maKH = modelBangKH.getValueAt(hangDaChon, 0).toString();
        // Hiển thị hộp thoại xác nhận xóa
        int xacNhan = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa khách hàng '" + maKH + "'?\n(Thao tác này cũng sẽ xóa thẻ thành viên nếu có)",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        // Nếu người dùng chọn YES
        if (xacNhan == JOptionPane.YES_OPTION) {
            try {
                // Phải xóa thẻ thành viên trước (do ràng buộc khóa ngoại)
                ttvDAO.xoaTheThanhVienTheoMaKH(maKH); 
                // Sau đó mới xóa khách hàng
                if (khDAO.xoaKhachHang(maKH)) {
                    taiDuLieuVaoBang(); // Tải lại bảng
                    xoaTrangForm();     // Xóa trắng form
                    JOptionPane.showMessageDialog(this, "Đã xóa khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Trường hợp hiếm gặp: DAO trả về false mà không ném lỗi
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại (DAO trả về false)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                // Xử lý lỗi SQL (ví dụ: không xóa được do KH còn liên kết ở bảng khác chưa xử lý)
                e.printStackTrace(); // In lỗi ra console
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng hoặc thẻ thành viên:\n" + e.getMessage(), "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý sự kiện sửa thông tin khách hàng đang được chọn.
     * Kiểm tra dữ liệu -> Gọi DAO -> Cập nhật giao diện.
     */
    private void suaKhachHang() {
        int hangDaChon = bangKH.getSelectedRow(); // Lấy chỉ số dòng đang chọn
        // Kiểm tra xem có dòng nào được chọn không
        if (hangDaChon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa thông tin!", "Chưa chọn khách hàng", JOptionPane.WARNING_MESSAGE);
            return; // Dừng nếu chưa chọn
        }

        // Kiểm tra dữ liệu nhập trên form có hợp lệ không
        if (kiemTraDuLieuNhap()) { 
            try {
                // Lấy mã KH từ ô text (đã bị khóa, không thể sửa)
                String maKH = txtMaKH.getText().trim();
                // Lấy thông tin đã sửa từ form
                String hoTen = txtHoTen.getText().trim();
                String sdt = txtSDT.getText().trim();
                String email = txtEmail.getText().trim();
                boolean gioiTinh = radNam.isSelected(); // true là Nam
                int diemMoi = chuyenDoiDiem(); // Lấy điểm đã sửa

                // Tạo đối tượng KhachHang với thông tin mới
                KhachHang khMoi = new KhachHang(maKH, hoTen, sdt, email, gioiTinh);
                
                // Gọi DAO để cập nhật thông tin khách hàng (không cập nhật mã KH)
                boolean capNhatThanhCong = khDAO.capNhatKhachHang(khMoi);

                // Nếu cập nhật thông tin KH thành công
                if (capNhatThanhCong) {
                    // Cập nhật điểm và hạng của thẻ thành viên
                    TheThanhVien ttv = ttvDAO.layTheTheoMaKH(maKH); // Lấy thẻ hiện tại theo mã KH

                    if (ttv == null) {
                        // Nếu khách hàng chưa có thẻ, tạo thẻ mới
                        String maTheMoi = ttvDAO.phatSinhMaThe();
                        TheThanhVien ttvMoi = new TheThanhVien(maTheMoi, khMoi, diemMoi);
                        ttvDAO.themTheThanhVien(ttvMoi); // Thêm thẻ mới vào CSDL
                    } else {
                        // Nếu khách hàng đã có thẻ, cập nhật điểm (Entity tự tính lại hạng)
                        ttv.setDiemTichLuy(diemMoi); 
                        ttvDAO.capNhatTheThanhVien(ttv); // Cập nhật thẻ vào CSDL
                    }

                    taiDuLieuVaoBang(); // Tải lại dữ liệu vào bảng
                    JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Lỗi khi cập nhật KH (có thể do lỗi kết nối hoặc logic DAO)
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException | IllegalArgumentException e) { // Bắt cả lỗi SQL và lỗi dữ liệu nhập (điểm)
                e.printStackTrace(); // In lỗi ra console
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khách hàng hoặc thẻ thành viên:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Bắt các lỗi khác
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Hiển thị thông tin của dòng đang được chọn trong bảng lên form nhập liệu.
     */
    private void hienThiDongDaChon() {
        int hangDaChon = bangKH.getSelectedRow(); // Lấy chỉ số dòng đang chọn
        if (hangDaChon != -1) { // Nếu có dòng được chọn
            try {
                // Lấy mã KH từ bảng
                String maKH = modelBangKH.getValueAt(hangDaChon, 0).toString();
                // Gọi DAO để lấy đối tượng KhachHang đầy đủ (đảm bảo dữ liệu mới nhất)
                KhachHang kh = khDAO.layKhachHangTheoMa(maKH); 

                if (kh == null) {
                    // Trường hợp hiếm gặp: Dữ liệu trên bảng có nhưng không có trong DB
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết của khách hàng này trong Cơ sở dữ liệu!", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Điền thông tin lên các ô nhập liệu
                txtMaKH.setText(kh.getMaKH()); // Mã KH sẽ hiển thị trên ô đã bị khóa
                txtHoTen.setText(kh.getHoTenKH());
                txtSDT.setText(kh.getSoDienThoai());
                txtEmail.setText(kh.getEmail());
                
                // Chọn RadioButton giới tính tương ứng
                if (kh.isGioiTinh()) { // Giả định true = Nam
                    radNam.setSelected(true);
                } else {
                    radNu.setSelected(true);
                }

                // Lấy thông tin thẻ thành viên
                TheThanhVien ttv = ttvDAO.layTheTheoMaKH(maKH);
                if (ttv != null) {
                    // Nếu có thẻ, hiển thị điểm
                    txtDiem.setText(String.valueOf(ttv.getDiemTichLuy()));
                } else {
                    // Nếu chưa có thẻ, hiển thị điểm là 0
                    txtDiem.setText("0"); 
                }

            } catch (SQLException e) {
                e.printStackTrace(); // In lỗi ra console
                JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết khách hàng từ Cơ sở dữ liệu!", "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý sự kiện tìm kiếm khách hàng theo mã KH nhập vào ô tìm kiếm.
     */
    /**
     * Xử lý sự kiện tìm kiếm khách hàng theo từ khóa (Mã, Tên hoặc SĐT).
     */
    private void timKiemKhachHang() {
        String tuKhoa = txtTimKiem.getText().trim();

        // 1. Nếu ô tìm kiếm rỗng -> Tải lại toàn bộ bảng
        if (tuKhoa.isEmpty()) {
            taiDuLieuVaoBang();
            return;
        }

        // 2. Gọi DAO để tìm kiếm
        // Lưu ý: Cần đảm bảo bạn đã thêm hàm timKhachHangTongHop vào DAO như hướng dẫn ở trên
        ArrayList<Object[]> dsKetQua = khDAO.timKhachHangTongHop(tuKhoa);

        // 3. Cập nhật bảng
        modelBangKH.setRowCount(0); // Xóa dữ liệu cũ

        if (!dsKetQua.isEmpty()) {
            // Nếu tìm thấy
            for (Object[] row : dsKetQua) {
                modelBangKH.addRow(row);
            }
            
            // Chọn dòng đầu tiên và hiển thị thông tin
            bangKH.setRowSelectionInterval(0, 0);
            hienThiDongDaChon();
            
            JOptionPane.showMessageDialog(this, 
                "Tìm thấy " + dsKetQua.size() + " kết quả phù hợp.", 
                "Kết quả tìm kiếm", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Nếu không tìm thấy
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy khách hàng nào với từ khóa: '" + tuKhoa + "'.", 
                "Không tìm thấy", 
                JOptionPane.WARNING_MESSAGE);
            
            // Tải lại bảng và xóa trắng form
            taiDuLieuVaoBang();
            xoaTrangForm();
        }
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu nhập trên form.
     * Sử dụng validation từ Entity và kiểm tra thêm các ràng buộc GUI.
     * @return true nếu dữ liệu hợp lệ, false nếu không hợp lệ.
     */
    private boolean kiemTraDuLieuNhap() {
        try {
           
            new KhachHang(
                txtMaKH.getText().trim(),     // Mã KH
                txtHoTen.getText().trim(),    // Họ tên
                txtSDT.getText().trim(),      // Số điện thoại
                txtEmail.getText().trim(),    // Email
                radNam.isSelected()           // Giới tính (true = Nam)
            );

        
            if (!radNam.isSelected() && !radNu.isSelected()) {
                throw new IllegalArgumentException("Vui lòng chọn giới tính (Nam hoặc Nữ)!");
            }
            chuyenDoiDiem(); 
            return true;

        } catch (IllegalArgumentException e) {
         
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi Dữ Liệu Nhập", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private int chuyenDoiDiem() throws IllegalArgumentException {
        try {
            int diem = Integer.parseInt(txtDiem.getText().trim()); 
            if (diem < 0) { 
                 throw new IllegalArgumentException("Điểm tích lũy không được là số âm.");
            }
            return diem; 
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Điểm tích lũy phải là một con số nguyên hợp lệ.");
        }
    }

    private void xoaTrangForm() {
        try {
            txtMaKH.setText(khDAO.phatSinhMaKH()); 
        } catch (Exception e) {
            txtMaKH.setText("LoiMaKH"); 
            JOptionPane.showMessageDialog(this, "Lỗi phát sinh mã khách hàng mới: " + e.getMessage(), "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }

        txtHoTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        nhomGioiTinh.clearSelection(); // Bỏ chọn giới tính
        txtDiem.setText("0"); // Đặt lại điểm về 0

        bangKH.clearSelection();          
    }


//    public static void main(String[] args) {
//        ConnectDB.connect();
//
//     
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Quản lý khách hàng - Phiên bản cuối");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát khi đóng cửa sổ
//
//            QuanLyKhachHang_GUI panel = new QuanLyKhachHang_GUI(); // Tạo panel quản lý KH
//
//            frame.add(panel); // Thêm panel vào frame
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Mở full màn hình
//            frame.setVisible(true); // Hiển thị frame
//        });
//    }
}