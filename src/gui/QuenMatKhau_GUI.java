package gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL; // Cần import thêm lớp này
import javax.swing.*;

import dao.TaiKhoan_DAO;

public class QuenMatKhau_GUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    JLabel lblTenTK, lblEmail, lblCCCD, lblMatKhauMoi, lblXacNhanMK;
    JTextField txtTenTK, txtEmail, txtCCCD;
    JPasswordField txtMatKhauMoi, txtXacNhanMK;
    JButton btnKiemTra, btnCapNhat, btnThoat;

    public QuenMatKhau_GUI() {
        setTitle("Quên mật khẩu");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // --- PHẦN SỬA ĐỔI HÌNH ẢNH ---
        // Sử dụng getResource để đọc ảnh từ bộ nhớ JAR
        URL url = getClass().getResource("/image/login.png");
        if (url != null) {
            ImageIcon bgIcon = new ImageIcon(url);
            Image img = bgIcon.getImage().getScaledInstance(500, 400, Image.SCALE_SMOOTH);
            JLabel nen = new JLabel(new ImageIcon(img));
            nen.setLayout(new BorderLayout());
            setContentPane(nen);
        } else {
            // Trường hợp không tìm thấy ảnh (phòng lỗi)
            getContentPane().setBackground(new Color(139, 69, 19)); 
        }
        // ------------------------------

        Font font = new Font("Arial", Font.BOLD, 16);

        // Panel tiêu đề
        JPanel pTieuDe = new JPanel();
        pTieuDe.setLayout(new GridBagLayout());
        JLabel lblTieuDe = new JLabel("Quên mật khẩu");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 30));
        lblTieuDe.setForeground(Color.WHITE);
        pTieuDe.add(lblTieuDe);
        pTieuDe.setPreferredSize(new Dimension(400, 100));

        // Box chính
        Box pBody = Box.createVerticalBox();
        
        // Cấu trúc các thành phần Box (Giữ nguyên logic của bạn)
        Box pTenTK = Box.createHorizontalBox();
        lblTenTK = new JLabel("Tên tài khoản:");
        lblTenTK.setFont(font);
        lblTenTK.setForeground(Color.WHITE);
        txtTenTK = new JTextField();
        pTenTK.add(lblTenTK);
        pTenTK.add(Box.createHorizontalStrut(20));
        pTenTK.add(txtTenTK);

        Box pEmail = Box.createHorizontalBox();
        lblEmail = new JLabel("Email:");
        lblEmail.setFont(font);
        lblEmail.setForeground(Color.WHITE);
        txtEmail = new JTextField();
        pEmail.add(lblEmail);
        pEmail.add(Box.createHorizontalStrut(20));
        pEmail.add(txtEmail);

        Box pCCCD = Box.createHorizontalBox();
        lblCCCD = new JLabel("CCCD:");
        lblCCCD.setFont(font);
        lblCCCD.setForeground(Color.WHITE);
        txtCCCD = new JTextField();
        btnKiemTra = new JButton("Kiểm tra");
        pCCCD.add(lblCCCD);
        pCCCD.add(Box.createHorizontalStrut(20));
        pCCCD.add(txtCCCD);
        pCCCD.add(Box.createHorizontalStrut(10));
        pCCCD.add(btnKiemTra);

        Box pMatKhauMoi = Box.createHorizontalBox();
        lblMatKhauMoi = new JLabel("Mật khẩu mới:");
        lblMatKhauMoi.setFont(font);
        lblMatKhauMoi.setForeground(Color.WHITE);
        txtMatKhauMoi = new JPasswordField();
        txtMatKhauMoi.setEnabled(false);
        pMatKhauMoi.add(lblMatKhauMoi);
        pMatKhauMoi.add(Box.createHorizontalStrut(20));
        pMatKhauMoi.add(txtMatKhauMoi);

        Box pXacNhan = Box.createHorizontalBox();
        lblXacNhanMK = new JLabel("Xác nhận MK:");
        lblXacNhanMK.setFont(font);
        lblXacNhanMK.setForeground(Color.WHITE);
        txtXacNhanMK = new JPasswordField();
        txtXacNhanMK.setEnabled(false);
        pXacNhan.add(lblXacNhanMK);
        pXacNhan.add(Box.createHorizontalStrut(20));
        pXacNhan.add(txtXacNhanMK);

        Box pNut = Box.createHorizontalBox();
        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.setEnabled(false);
        btnThoat = new JButton("Thoát");
        pNut.add(btnCapNhat);
        pNut.add(Box.createHorizontalStrut(10));
        pNut.add(btnThoat);

        pBody.add(pTenTK);
        pBody.add(Box.createVerticalStrut(10));
        pBody.add(pEmail);
        pBody.add(Box.createVerticalStrut(10));
        pBody.add(pCCCD);
        pBody.add(Box.createVerticalStrut(20));
        pBody.add(pMatKhauMoi);
        pBody.add(Box.createVerticalStrut(10));
        pBody.add(pXacNhan);
        pBody.add(Box.createVerticalStrut(20));
        pBody.add(pNut);

        Dimension lblSize = lblTenTK.getPreferredSize();
        lblEmail.setPreferredSize(lblSize);
        lblCCCD.setPreferredSize(lblSize);
        lblMatKhauMoi.setPreferredSize(lblSize);
        lblXacNhanMK.setPreferredSize(lblSize);

        txtTenTK.setPreferredSize(new Dimension(200, 25));
        txtEmail.setPreferredSize(new Dimension(200, 25));
        txtCCCD.setPreferredSize(new Dimension(200, 25));
        txtMatKhauMoi.setPreferredSize(new Dimension(200, 25));
        txtXacNhanMK.setPreferredSize(new Dimension(200, 25));

        JPanel pnlNoiDung = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pTieuDe.setOpaque(false);
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.add(pBody);

        add(pTieuDe, BorderLayout.NORTH);
        add(pnlNoiDung, BorderLayout.CENTER);

        btnKiemTra.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnThoat.addActionListener(this);

        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Giữ nguyên logic xử lý sự kiện của bạn
        Object o = e.getSource();
        TaiKhoan_DAO dao = new TaiKhoan_DAO();

        if (o == btnKiemTra) {
            String tenTK = txtTenTK.getText().trim();
            String email = txtEmail.getText().trim();
            String cccd = txtCCCD.getText().trim();

            if (dao.kiemTraThongTin(tenTK, email, cccd)) {
                txtTenTK.setEnabled(false);
                txtEmail.setEnabled(false);
                txtCCCD.setEnabled(false);
                txtMatKhauMoi.setEnabled(true);
                txtXacNhanMK.setEnabled(true);
                btnCapNhat.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Thông tin chính xác. Vui lòng nhập mật khẩu mới.");
            } else {
                JOptionPane.showMessageDialog(this, "Thông tin không chính xác!");
            }
        }

        if (o == btnCapNhat) {
            String matKhauMoi = new String(txtMatKhauMoi.getPassword());
            String xacNhan = new String(txtXacNhanMK.getPassword());

            if (!matKhauMoi.equals(xacNhan)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không khớp!");
                return;
            }

            if (dao.capNhatMatKhau(txtTenTK.getText().trim(), matKhauMoi)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        }

        if (o == btnThoat) {
            dispose();
        }
    }
}