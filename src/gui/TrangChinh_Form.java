package gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

import connectDB.ConnectDB;
import entity.TaiKhoan; // Import entity TaiKhoan

public class TrangChinh_Form extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private CardLayout cardLayout;
    private JPanel pnlContent;
    private JButton btndangxuat;
    private JButton currentBtn = null; 
    
    private Color colorNen = new Color(255, 216, 164);
    private Color colorNhat = new Color(255, 231, 188);
    private Color colorDam = new Color(255, 178, 44);
    private Font fontMenu = new Font("Segoe UI", Font.BOLD, 14);

    public TrangChinh_Form() {
        setTitle("Nhà hàng TripleND - Hệ thống quản lý");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(247, 247, 247));
        
        // --- 1. LẤY QUYỀN TRUY CẬP ---
        String userRole = "Quản lý"; // Mặc định là Quản lý (để test khi chạy trực tiếp)
        String userName = "Admin";
        
        if (DangNhap_GUI.taiKhoanDangNhap != null) {
            // Giả sử class TaiKhoan có hàm getVaiTro() trả về "Quản lý" hoặc "Nhân viên"
            userRole = DangNhap_GUI.taiKhoanDangNhap.getVaiTro(); 
            
            // Lấy tên để hiển thị xin chào (nếu có)
            if(DangNhap_GUI.taiKhoanDangNhap.getNhanVien() != null) {
                userName = DangNhap_GUI.taiKhoanDangNhap.getNhanVien().getHoTen();
            }
        }

        // --- Menu trái ---
        JPanel pTrai = new JPanel();
        pTrai.setBackground(colorNen);
        pTrai.setPreferredSize(new Dimension(220, getHeight()));
        pTrai.setLayout(new BoxLayout(pTrai, BoxLayout.Y_AXIS));
        pTrai.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        
        // Logo
        JLabel lblLogo = new JLabel();
        ImageIcon logoIcon = getIcon("image/logo.png", 200, 150);
        if (logoIcon != null) {
            lblLogo.setIcon(logoIcon);
        } else {
            lblLogo.setText("TRIPLE ND");
            lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        pTrai.add(lblLogo);

        // Hiển thị xin chào & Vai trò
        JLabel lblHello = new JLabel("Xin chào, " + getTenRutGon(userName));
        lblHello.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHello.setAlignmentX(Component.CENTER_ALIGNMENT);
        pTrai.add(lblHello);
        
        JLabel lblRole = new JLabel("(" + userRole + ")");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(Color.RED);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRole.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0)); // Cách menu ra
        pTrai.add(lblRole);

        // --- Content phải (CardLayout) ---
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);

        // Khởi tạo các module (Vẫn load hết để code không bị lỗi NullPointer, nhưng menu sẽ ẩn đi)
        try { pnlContent.add(new Dashboard_GUI(), "Dashboard"); } catch (Exception e) {}

        try {
            BanDat_GUI pnlDatBan = new BanDat_GUI();
            DanhSachBanDat_GUI pnlDanhSach = new DanhSachBanDat_GUI();
            pnlDatBan.setDataRefreshListener(() -> pnlDanhSach.refreshData());
            pnlDanhSach.setDataRefreshListener(() -> pnlDatBan.refreshData());
            pnlContent.add(pnlDatBan, "PANEL_DAT_BAN");
            pnlContent.add(pnlDanhSach, "PANEL_DS_DAT_BAN");
        } catch (Exception e) {}

        addModule(new MonAn_GUI(), "Quản lý món ăn");
        addModule(new QuanLyKhachHang_GUI(), "Quản lý KH");
        addModule(new NhanVien_GUI(), "Quản lý nhân viên");
        addModule(new BaoCao_GUI(), "Báo cáo");
        addModule(new KhuyenMai_GUI(), "Khuyến mãi");
        addModule(new HoaDon_GUI(), "Quản lý hóa đơn");

        // --- Danh sách menu ---
        String[] btnChucnang = {
            "Dashboard",
            "Quản lý bàn đặt",
            "Quản lý món ăn",   // Cấm nhân viên
            "Quản lý KH",
            "Quản lý nhân viên",// Cấm nhân viên
            "Quản lý hóa đơn",
            "Báo cáo",          // Cấm nhân viên
            "Khuyến mãi",       // Cấm nhân viên
        };

        String[] imgPaths = {
            "image/dashboard.png",
            "image/ban.png",
            "image/monan.png",
            "image/khachhang.png",
            "image/nhanvien.png",
            "image/hoadon.png", 
            "image/baocao.png",
            "image/khuyenmai.png",
        };

        // --- 2. DANH SÁCH CHỨC NĂNG CẤM NHÂN VIÊN ---
        List<String> chucNangCamNhanVien = Arrays.asList(
            "Quản lý món ăn", 
            "Quản lý nhân viên", 
            "Báo cáo", 
            "Khuyến mãi"
        );

        // --- Vòng lặp tạo nút ---
        for (int i = 0; i < btnChucnang.length; i++) {
            String label = btnChucnang[i];
            
            // --- 3. KIỂM TRA PHÂN QUYỀN ---
            // Nếu là Nhân viên VÀ chức năng nằm trong danh sách cấm -> Bỏ qua vòng lặp (Không tạo nút)
            if (userRole.equalsIgnoreCase("Nhân viên") && chucNangCamNhanVien.contains(label)) {
                continue; 
            }
            
            JButton btn = createMenuButton(label, imgPaths[i], colorNhat, fontMenu);

            if (label.equals("Quản lý bàn đặt")) {
                btn.setText(label + "   ▼");
                JPopupMenu popupMenu = createBanDatPopupMenu(btn); 
                btn.addActionListener(e -> {
                    setButtonActive(btn);
                    popupMenu.show(btn, 0, btn.getHeight());
                });
            } else {
                btn.addActionListener(e -> {
                    cardLayout.show(pnlContent, label);
                    setButtonActive(btn);
                });
            }
            
            if (label.equals("Dashboard")) {
                setButtonActive(btn);
            }

            pTrai.add(btn);
            pTrai.add(Box.createRigidArea(new Dimension(0, 15))); 
        }

        // Nút đăng xuất
        btndangxuat = createMenuButton("Đăng xuất", "image/dangxuat.png", colorNhat, fontMenu);
        btndangxuat.addActionListener(this);
        pTrai.add(btndangxuat);
        
        add(pTrai, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);
        setVisible(true);
    }
    
    // Helper để rút gọn tên hiển thị cho đẹp
    private String getTenRutGon(String fullName) {
        if (fullName == null) return "";
        String[] parts = fullName.split("\\s+");
        return parts[parts.length - 1]; // Lấy tên cuối cùng
    }

    private ImageIcon getIcon(String path, int width, int height) {
        URL imgURL = getClass().getResource("/" + path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            return null;
        }
    }

    private void setButtonActive(JButton btn) {
        if (currentBtn != null && currentBtn != btn) {
            currentBtn.setBackground(colorNhat);
        }
        currentBtn = btn;
        currentBtn.setBackground(colorDam);
    }

    private void addModule(JPanel panel, String key) {
        try {
            pnlContent.add(panel, key);
        } catch (Exception e) {
            pnlContent.add(new JLabel("Lỗi tải module " + key), key);
        }
    }

    private JButton createMenuButton(String text, String iconPath, Color bgColor, Font font) {
        JButton btn = new JButton(text);
        ImageIcon icon = getIcon(iconPath, 25, 25);
        if (icon != null) btn.setIcon(icon);
        
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
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(colorDam); }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentBtn) btn.setBackground(colorNhat);
            }
        });
        return btn;
    }
    
    private JPopupMenu createBanDatPopupMenu(JButton parentBtn) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(colorNhat);
        popupMenu.setBorder(BorderFactory.createLineBorder(colorDam, 1));
        
        JMenuItem itemFormDat = createMenuItem("Form Đặt Bàn", "image/reservation.png"); 
        JMenuItem itemDSDat = createMenuItem("Danh Sách Đặt Bàn", "image/list.png"); 
        
        itemFormDat.addActionListener(e -> {
            cardLayout.show(pnlContent, "PANEL_DAT_BAN");
            setButtonActive(parentBtn); 
        });
        
        itemDSDat.addActionListener(e -> {
            cardLayout.show(pnlContent, "PANEL_DS_DAT_BAN");
            setButtonActive(parentBtn); 
        });

        popupMenu.add(itemFormDat);
        popupMenu.add(itemDSDat);
        return popupMenu;
    }

    private JMenuItem createMenuItem(String text, String iconPath) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(fontMenu);
        item.setBackground(colorNhat);
        item.setPreferredSize(new Dimension(218, 40));
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        ImageIcon icon = getIcon(iconPath, 20, 20);
        if (icon != null) {
            item.setIcon(icon);
            item.setIconTextGap(10);
        }

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { item.setBackground(colorDam); }
            public void mouseExited(MouseEvent e) { item.setBackground(colorNhat); }
        });
        return item;
    }
    
    // --- 4. HÀM CHUYỂN MODULE (Public) ---
    // Hàm này cho phép các Panel con gọi để chuyển đổi màn hình (Ví dụ: Từ đặt bàn -> Gọi món)
    public void chuyenDenManHinhGoiMon(String maBan) {
        // Logic chuyển sang màn hình gọi món, ví dụ:
        try {
            GoiMon_GUI goiMon = new GoiMon_GUI(maBan);
            pnlContent.add(goiMon, "PANEL_GOI_MON");
            cardLayout.show(pnlContent, "PANEL_GOI_MON");
            
            // Reset active button vì đang ở màn hình custom không có trong menu
            if (currentBtn != null) currentBtn.setBackground(colorNhat);
            currentBtn = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi mở màn hình gọi món: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o.equals(btndangxuat)) {
             int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
             if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Đóng frame hiện tại
                // Biến static lưu tài khoản đăng nhập set về null
                DangNhap_GUI.taiKhoanDangNhap = null;
                try {
                    new DangNhap_GUI().setVisible(true); // Mở lại form đăng nhập
                } catch (Exception ex) {
                    System.exit(0);
                }
             }
        }
    }
}