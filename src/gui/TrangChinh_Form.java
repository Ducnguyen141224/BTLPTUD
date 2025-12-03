package gui;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;


import connectDB.ConnectDB;


public class TrangChinh_Form extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private CardLayout cardLayout;
    private JPanel pnlContent;
    private JButton btndangxuat;
    
    public TrangChinh_Form() {
        setTitle("Nhà hàng TripleND");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(247, 247, 247));
        
        // --- Menu trái ---
        JPanel pTrai = new JPanel();
        pTrai.setBackground(new Color(160, 134, 121));
        pTrai.setPreferredSize(new Dimension(220, getHeight()));
        pTrai.setLayout(new BoxLayout(pTrai, BoxLayout.Y_AXIS));
        pTrai.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        
        // Logo
        ImageIcon logoIcon = new ImageIcon("src/image/logo.png");
        JLabel lblLogo = new JLabel(logoIcon);
        Image scaledImage = logoIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        lblLogo.setIcon(scaledIcon);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
        pTrai.add(lblLogo);
        
        // --- Nút điều hướng (menu bên trái) ---
        String[] btnChucnang = {
            "Dashboard",
            "Quản lý bàn đặt",
            "Quản lý món ăn",
            "Quản lý KH",
            "Quản lý nhân viên",
            "Quản lý hóa đơn",
            "Báo cáo",
            "Khuyến mãi",
        };

        String[] imgPaths = {
            "src/image/dashboard.png",
            "src/image/ban.png",
            "src/image/monan.png",
            "src/image/khachhang.png",
            "src/image/nhanvien.png",
            "src/image/nhanvien.png",
            "src/image/baocao.png",
            "src/image/khuyenmai.png",
        };

        // Màu nền chính
        Color colorNen = new Color(255, 216, 164);
        Color colorNhat = new Color(255, 231, 188);
        Color colorDam = new Color(255, 178, 44);
        Font fontMenu = new Font("Segoe UI", Font.BOLD, 14);

        pTrai.setBackground(colorNen);

        // --- Content phải (CardLayout) ---
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);

        // 💡 KHỞI TẠO CÁC PANEL CÓ KHẢ NĂNG LỖI TRONG TRY-CATCH
        try {
            // Thêm Trang chủ (không lỗi CSDL)
            pnlContent.add(new TrangChu_GUI(), "Dashboard");

            // 💡 SỬA: TẠO LIÊN KẾT GIỮA BanDat_GUI và DanhSachBanDat_GUI
            BanDat_GUI pnlDatBan = new BanDat_GUI();
            DanhSachBanDat_GUI pnlDanhSach = new DanhSachBanDat_GUI();

            // 💡 Gửi tham chiếu của pnlDanhSach cho pnlDatBan
            pnlDatBan.setDataRefreshListener(new DataRefreshListener() {
                @Override
                public void onDataChanged() {
                    pnlDanhSach.refreshData(); // Gọi hàm tải lại của pnlDanhSach
                }
            });
            
            // 💡 Gửi tham chiếu của pnlDatBan cho pnlDanhSach
            pnlDanhSach.setDataRefreshListener(new DataRefreshListener() {
                @Override
                public void onDataChanged() {
                    pnlDatBan.refreshData(); // Gọi hàm tải lại của pnlDatBan
                }
            });

            // Thêm 2 panel với TÊN KEY ĐÃ ĐỊNH NGHĨA
            pnlContent.add(pnlDatBan, "PANEL_DAT_BAN");
            pnlContent.add(pnlDanhSach, "PANEL_DS_DAT_BAN");
            
            // Thêm các panel còn lại
            pnlContent.add(new Dashboard_GUI(), "Dashboard");
            pnlContent.add(new MonAn_GUI(), "Quản lý món ăn");
            pnlContent.add(new QuanLyKhachHang_GUI(), "Quản lý KH");
            pnlContent.add(new NhanVien_GUI(), "Quản lý nhân viên");
            pnlContent.add(new BaoCao_GUI(), "Báo cáo");
            pnlContent.add(new KhuyenMai_GUI(), "Khuyến mãi");
            pnlContent.add(new HoaDon_GUI(), "Quản lý hóa đơn");

        } catch (Exception e) {
             // Bắt các lỗi khác (ví dụ: NullPointerException nếu DAO chưa khởi tạo)
            e.printStackTrace();
             JOptionPane.showMessageDialog(this, 
                "Lỗi khởi tạo giao diện: " + e.getMessage(), 
                "Lỗi Khởi Tạo", 
                JOptionPane.ERROR_MESSAGE);
        }

        // --- 💡 SỬA: Vòng lặp FOR để tạo nút menu ---
        for (int i = 0; i < btnChucnang.length; i++) {
            String label = btnChucnang[i];
            
            // Sử dụng hàm helper để tạo nút đồng bộ
            JButton btn = createMenuButton(label, imgPaths[i], colorNhat, fontMenu);

            // Xử lý sự kiện
            if (label.equals("Quản lý bàn đặt")) {
                // Đây là nút đặc biệt, tạo JPopupMenu
                btn.setText(label + "   ▼"); // Thêm mũi tên
                
                JPopupMenu popupMenu = createBanDatPopupMenu(colorNhat, colorDam, fontMenu);
                
                // Hành động cho nút chính: Hiển thị popup
                btn.addActionListener(e -> {
                    popupMenu.show(btn, 0, btn.getHeight());
                });
            } else {
                // Đây là các nút bình thường
                btn.addActionListener(e -> cardLayout.show(pnlContent, label));
            }

            pTrai.add(btn);
            pTrai.add(Box.createRigidArea(new Dimension(0, 15))); // khoảng cách
        }

        
        btndangxuat = createMenuButton("Đăng xuất", "src/image/dangxuat.png", colorNhat, fontMenu);
        btndangxuat.addActionListener(this);
        pTrai.add(btndangxuat);
        
        add(pTrai, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);
        setVisible(true);
    }
    
    /**
     * Hàm helper để tạo nút menu (Tái sử dụng)
     */
    private JButton createMenuButton(String text, String iconPath, Color bgColor, Font font) {
        JButton btn = new JButton(text);
        
        ImageIcon icon = new ImageIcon(iconPath);
        Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(img));
        
        btn.setFont(font);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(bgColor);
        btn.setForeground(Color.BLACK);
        
        return btn;
    }
    
    /**
     * Hàm helper tạo JPopupMenu cho Quản lý bàn đặt
     */
    private JPopupMenu createBanDatPopupMenu(Color colorNhat, Color colorDam, Font fontMenu) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(colorNhat);
        popupMenu.setBorder(BorderFactory.createLineBorder(colorDam, 1));
        
        JMenuItem itemFormDat = new JMenuItem("   📝 Form Đặt Bàn");
        JMenuItem itemDSDat = new JMenuItem("   📋 Danh Sách Đặt Bàn");
        
        JMenuItem[] items = {itemFormDat, itemDSDat};
        
        for (JMenuItem item : items) {
            item.setFont(fontMenu);
            item.setBackground(colorNhat);
            item.setPreferredSize(new Dimension(218, 40)); // Đảm bảo chiều rộng khớp
            item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            // Hiệu ứng hover
            item.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    item.setBackground(colorDam);
                }
                public void mouseExited(MouseEvent e) {
                    item.setBackground(colorNhat);
                }
            });
        }
        
        // Hành động cho nút "Form Đặt Bàn"
        itemFormDat.addActionListener(e -> {
            cardLayout.show(pnlContent, "PANEL_DAT_BAN");
        });
        
        // Hành động cho nút "Danh Sách Đặt Bàn"
        itemDSDat.addActionListener(e -> {
            cardLayout.show(pnlContent, "PANEL_DS_DAT_BAN");
        });

        popupMenu.add(itemFormDat);
        popupMenu.add(itemDSDat);

        return popupMenu;
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o.equals(btndangxuat)) {
             int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
             if (confirm == JOptionPane.YES_OPTION) {
                dispose();
               
                new DangNhap_GUI().setVisible(true); 
             }
        }
    }
    
   
    public static void main(String[] args) {
        ConnectDB.getInstance().connect();
        
        SwingUtilities.invokeLater(() -> {
            new TrangChinh_Form().setVisible(true);
        });
    }
    
}