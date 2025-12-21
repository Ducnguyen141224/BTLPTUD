package gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import dao.TaiKhoan_DAO;
import entity.TaiKhoan;

public class DangNhap_GUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JTextField txtTenDN;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap, btnThoat;
    public static TaiKhoan taiKhoanDangNhap;

   
    private final Color COL_PRIMARY = new Color(139, 69, 19); // Nâu đất (SaddleBrown)
    private final Color COL_SECONDARY = new Color(255, 140, 0); // Cam đậm
    private final Color COL_BG_RIGHT = new Color(255, 250, 240); // Màu kem (FloralWhite)

    public DangNhap_GUI() {
        setTitle("Đăng nhập hệ thống TripleND");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel chính chứa 2 phần: Trái (Ảnh) và Phải (Form)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        setContentPane(mainPanel);

        // --- 1. PHẦN BÊN TRÁI: ẢNH POSTER ---
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBackground(COL_PRIMARY);
        
       
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Lấy ảnh (Sử dụng hàm getIcon chuẩn)
        ImageIcon bannerIcon = getIcon("man_hinh_cho.png", 450, 500); 
        if (bannerIcon != null) {
            lblImage.setIcon(bannerIcon);
        } else {
            lblImage.setText("TRIPLE ND RESTAURANT");
            lblImage.setForeground(Color.WHITE);
            lblImage.setFont(new Font("Segoe UI", Font.BOLD, 24));
        }
        pnlLeft.add(lblImage, BorderLayout.CENTER);


        // --- 2. PHẦN BÊN PHẢI: FORM ĐĂNG NHẬP ---
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBackground(COL_BG_RIGHT);
        pnlRight.setBorder(new EmptyBorder(40, 50, 40, 50)); // Căn lề rộng rãi

        // Tiêu đề
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(COL_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label Welcome
        JLabel lblWelcome = new JLabel("Chào mừng trở lại!");
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblWelcome.setForeground(Color.GRAY);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- SỬA ĐỔI Ở ĐÂY: Dùng JPanel để căn trái nhãn "Tên tài khoản" ---
        JLabel lblUser = new JLabel("Tên tài khoản");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(COL_PRIMARY);
        
        JPanel pnlLblUser = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLblUser.setBackground(COL_BG_RIGHT);
        pnlLblUser.add(lblUser);

        txtTenDN = new JTextField();
        styleTextField(txtTenDN);

        // --- SỬA ĐỔI Ở ĐÂY: Dùng JPanel để căn trái nhãn "Mật khẩu" ---
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(COL_PRIMARY);
        
        JPanel pnlLblPass = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLblPass.setBackground(COL_BG_RIGHT);
        pnlLblPass.add(lblPass);

        txtMatKhau = new JPasswordField();
        styleTextField(txtMatKhau);

        // Panel chứa nút bấm
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlButton.setBackground(COL_BG_RIGHT);
        pnlButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnDangNhap = createStyledButton("Đăng nhập", COL_PRIMARY, Color.WHITE);
        btnThoat = createStyledButton("Thoát", new Color(220, 53, 69), Color.WHITE); // Màu đỏ

        pnlButton.add(btnDangNhap);
        pnlButton.add(btnThoat);

        // Nút Quên mật khẩu (Dạng text link)
        JButton btnQMK = new JButton("Quên mật khẩu?");
        btnQMK.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnQMK.setForeground(COL_SECONDARY);
        btnQMK.setBorderPainted(false);
        btnQMK.setContentAreaFilled(false);
        btnQMK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQMK.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnQMK.addActionListener(e -> new QuenMatKhau_GUI());

        // --- Ráp các thành phần vào Panel Phải ---
        pnlRight.add(Box.createVerticalGlue()); // Đẩy nội dung vào giữa
        pnlRight.add(lblTitle);
        pnlRight.add(Box.createVerticalStrut(5));
        pnlRight.add(lblWelcome);
        pnlRight.add(Box.createVerticalStrut(40));
        
        // Thêm panel chứa nhãn thay vì thêm trực tiếp nhãn
        pnlRight.add(pnlLblUser); 
        pnlRight.add(Box.createVerticalStrut(5));
        pnlRight.add(txtTenDN);
        pnlRight.add(Box.createVerticalStrut(20));
        
        // Thêm panel chứa nhãn thay vì thêm trực tiếp nhãn
        pnlRight.add(pnlLblPass);
        pnlRight.add(Box.createVerticalStrut(5));
        pnlRight.add(txtMatKhau);
        pnlRight.add(Box.createVerticalStrut(30));
        
        pnlRight.add(pnlButton);
        pnlRight.add(Box.createVerticalStrut(15));
        pnlRight.add(btnQMK);
        pnlRight.add(Box.createVerticalGlue());

        // Thêm 2 panel vào màn hình chính
        mainPanel.add(pnlLeft);
        mainPanel.add(pnlRight);

        // Sự kiện
        btnDangNhap.addActionListener(this);
        btnThoat.addActionListener(e -> System.exit(0));
        
        // Xử lý phím Enter để đăng nhập nhanh
        getRootPane().setDefaultButton(btnDangNhap);
    }

    // --- HÀM STYLE GIAO DIỆN ---
    private void styleTextField(JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(100, 40));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txt.setBackground(Color.WHITE);
        // Viền dưới (MatteBorder) tạo cảm giác hiện đại
        txt.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 2, 0, COL_SECONDARY),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hiệu ứng hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // --- HÀM TẢI ẢNH CHUẨN (Resource) ---
    private ImageIcon getIcon(String path, int width, int height) {
        URL imgURL = getClass().getResource("/image/" + path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            // Scale ảnh kiểu 'cover' cho đẹp
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnDangNhap) {
            TaiKhoan_DAO dao = new TaiKhoan_DAO();
            String userInput = txtTenDN.getText();
            String passInput = new String(txtMatKhau.getPassword());

            if (userInput.trim().isEmpty() || passInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra khoảng trắng thừa
            if (!userInput.equals(userInput.trim()) || !passInput.equals(passInput.trim())) {
                JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không được chứa khoảng trắng thừa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TaiKhoan tk = dao.dangNhap(userInput, passInput);

            if (tk != null) {
                DangNhap_GUI.taiKhoanDangNhap = tk;
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Chào mừng", JOptionPane.INFORMATION_MESSAGE);
                new TrangChinh_Form();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DangNhap_GUI().setVisible(true);
        });
    }
}