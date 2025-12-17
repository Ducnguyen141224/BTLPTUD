package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import dao.TaiKhoan_DAO;
import entity.TaiKhoan;



public class DangNhap_GUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	JLabel lblten, lblMatKhau;
	JTextField txtTenDN;
	JPasswordField txtMatKhau;
	JButton btnDangNhap, btnQMK;
	public static TaiKhoan taiKhoanDangNhap;
	public DangNhap_GUI() {
		
		setTitle("Đăng nhập");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Hình nền
		ImageIcon bgIcon = new ImageIcon("src/image/login.png");
		Image img = bgIcon.getImage().getScaledInstance(500, 400, Image.SCALE_SMOOTH);
		JLabel nen = new JLabel(new ImageIcon(img));
		nen.setLayout(new BorderLayout());
		setContentPane(nen);

		Font font = new Font("Arial", Font.BOLD, 16);

		// Panel tiêu đề
		JPanel pTieuDe = new JPanel();
		pTieuDe.setLayout(new GridBagLayout());
		JLabel lblTieuDe = new JLabel("Đăng nhập");
		lblTieuDe.setFont(new Font("Arial", Font.BOLD, 30));
		lblTieuDe.setForeground(Color.WHITE);
		pTieuDe.add(lblTieuDe);
		pTieuDe.setPreferredSize(new Dimension(400, 100));

		// Các dòng nhập liệu và nút
		Box pTkhoan, pMkhau, pNut;
		Box pBody = Box.createVerticalBox();

		pBody.add(pTkhoan = Box.createHorizontalBox());
		pTkhoan.add(lblten = new JLabel("Tên đăng nhập:"));
		lblten.setFont(font);
		lblten.setForeground(Color.WHITE);
		pTkhoan.add(Box.createHorizontalStrut(20));
		txtTenDN = new JTextField();
		pTkhoan.add(txtTenDN);

		pBody.add(Box.createVerticalStrut(5));
		pBody.add(pMkhau = Box.createHorizontalBox());
		pMkhau.add(lblMatKhau = new JLabel("Mật khẩu:"));
		lblMatKhau.setForeground(Color.WHITE);
		lblMatKhau.setFont(font);
		pMkhau.add(Box.createHorizontalStrut(20));
		txtMatKhau = new JPasswordField();
		pMkhau.add(txtMatKhau);

		pBody.add(Box.createVerticalStrut(30));
		pBody.add(pNut = Box.createHorizontalBox());
		pNut.add(btnDangNhap = new JButton("Đăng nhập"));
		pNut.add(Box.createHorizontalStrut(10));
		pNut.add(btnQMK = new JButton("Quên mật khẩu"));

		lblMatKhau.setPreferredSize(lblten.getPreferredSize());
		txtTenDN.setPreferredSize(new Dimension(200, 25));
		txtMatKhau.setPreferredSize(new Dimension(200, 25));

		btnDangNhap.setBackground(new Color(133, 72, 54));
		btnDangNhap.setForeground(Color.WHITE);
		btnDangNhap.setFocusPainted(false);
		btnDangNhap.setBorderPainted(false);
		btnDangNhap.setOpaque(true);

		btnQMK.setBackground(new Color(255, 178, 44));
		btnQMK.setForeground(Color.WHITE);
		btnQMK.setFocusPainted(false);
		btnQMK.setBorderPainted(false);
		btnQMK.setOpaque(true);

		JPanel pnlNoiDung = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlNoiDung.add(pBody);

		btnDangNhap.addActionListener(this);
		btnQMK.addActionListener(this);

		add(pTieuDe, BorderLayout.NORTH);
		add(pnlNoiDung, BorderLayout.CENTER);

		// Đặt nền trong suốt cho các box
		pTieuDe.setOpaque(false);
		pnlNoiDung.setOpaque(false);
		pBody.setOpaque(false);
		pTkhoan.setOpaque(false);
		pMkhau.setOpaque(false);
		pNut.setOpaque(false);

		setSize(500, 400);
		setResizable(false);
		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    Object o = e.getSource();

	    if (o == btnDangNhap) {
	        TaiKhoan_DAO dao = new TaiKhoan_DAO();
	        String userInput = txtTenDN.getText();
	        String passInput = new String(txtMatKhau.getPassword());

	        // Nếu người dùng nhập thêm khoảng trắng đầu/cuối → báo lỗi
	        if (!userInput.equals(userInput.trim())) {
	            JOptionPane.showMessageDialog(this,
	                "Tên đăng nhập hoặc mật khẩu không đúng!",
	                "Lỗi", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        if (!passInput.equals(passInput.trim())) {
	            JOptionPane.showMessageDialog(this,
	            	"Tên đăng nhập hoặc mật khẩu không đúng!",
	                "Lỗi", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        // Dùng chuỗi nguyên bản (không trim) để đăng nhập
	        TaiKhoan tk = dao.dangNhap(userInput, passInput);

	        if (tk != null) {
	            DangNhap_GUI.taiKhoanDangNhap = tk;   // 🔥 LƯU TÀI KHOẢN ĐĂNG NHẬP

	            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
	            new TrangChinh_Form();
	            this.dispose();
	        } else {
	            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản đã khóa!", 
	                                          "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
	        }
	    }

//
	    if (o == btnQMK){
	    	 new QuenMatKhau_GUI();
	    }
	}
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        new DangNhap_GUI().setVisible(true);
	    });
	}

	
}
