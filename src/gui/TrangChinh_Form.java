package gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL; // Import thêm URL
import javax.swing.*;

import connectDB.ConnectDB;

public class TrangChinh_Form extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private CardLayout cardLayout;
    private JPanel pnlContent;
    private JButton btndangxuat;
    
    // --- 1. BIẾN QUẢN LÝ TRẠNG THÁI ACTIVE ---
    private JButton currentBtn = null; // Lưu nút đang được chọn
    
    // Màu sắc chủ đạo
    private Color colorNen = new Color(255, 216, 164);
    private Color colorNhat = new Color(255, 231, 188);
    private Color colorDam = new Color(255, 178, 44);
    private Font fontMenu = new Font("Segoe UI", Font.BOLD, 14);

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
        
        // --- 1. SỬA LOGO (Dùng hàm getIcon mới) ---
        JLabel lblLogo = new JLabel();
        ImageIcon logoIcon = getIcon("image/logo.png", 200, 150); // Bỏ "src/"
        
        if (logoIcon != null) {
            lblLogo.setIcon(logoIcon);
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
            pTrai.add(lblLogo);
        } else {
            // Fallback nếu không thấy ảnh
            JLabel lblText = new JLabel("TRIPLE ND");
            lblText.setFont(new Font("Arial", Font.BOLD, 24));
            lblText.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblText.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
            pTrai.add(lblText);
        }
        
        pTrai.setBackground(colorNen);

        // --- Content phải (CardLayout) ---
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);

        // Khởi tạo các module
        try {
            pnlContent.add(new Dashboard_GUI(), "Dashboard");
        } catch (Exception e) {
            pnlContent.add(new JPanel(), "Dashboard");
        }

        try {
            BanDat_GUI pnlDatBan = new BanDat_GUI();
            DanhSachBanDat_GUI pnlDanhSach = new DanhSachBanDat_GUI();
            pnlDatBan.setDataRefreshListener(() -> pnlDanhSach.refreshData());
            pnlDanhSach.setDataRefreshListener(() -> pnlDatBan.refreshData());
            pnlContent.add(pnlDatBan, "PANEL_DAT_BAN");
            pnlContent.add(pnlDanhSach, "PANEL_DS_DAT_BAN");
        } catch (Exception e) {
            pnlContent.add(new JLabel("Lỗi tải module Đặt bàn"), "PANEL_DAT_BAN");
            pnlContent.add(new JLabel("Lỗi tải module Danh sách"), "PANEL_DS_DAT_BAN");
        }

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
            "Quản lý món ăn",
            "Quản lý KH",
            "Quản lý nhân viên",
            "Quản lý hóa đơn",
            "Báo cáo",
            "Khuyến mãi",
        };

        // --- 2. SỬA ĐƯỜNG DẪN ẢNH (Bỏ "src/") ---
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

        // --- Vòng lặp tạo nút ---
        for (int i = 0; i < btnChucnang.length; i++) {
            String label = btnChucnang[i];
            
            // Hàm createMenuButton đã được sửa để dùng getIcon bên dưới
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

        // Nút đăng xuất (Sửa đường dẫn ảnh)
        btndangxuat = createMenuButton("Đăng xuất", "image/dangxuat.png", colorNhat, fontMenu);
        btndangxuat.addActionListener(this);
        pTrai.add(btndangxuat);
        
        add(pTrai, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);
        setVisible(true);
    }
    
    // --- 3. HÀM HỖ TRỢ LẤY ẢNH TỪ RESOURCE (QUAN TRỌNG) ---
    /**
     * Tải ảnh từ thư mục resource (src/image), tự động resize.
     * @param path Đường dẫn (ví dụ: "image/logo.png") - KHÔNG CÓ "src/"
     * @param width Chiều rộng muốn resize
     * @param height Chiều cao muốn resize
     * @return ImageIcon hoặc null nếu không tìm thấy
     */
    private ImageIcon getIcon(String path, int width, int height) {
        // Thêm dấu "/" vào đầu để tìm từ gốc thư mục src (classpath root)
        URL imgURL = getClass().getResource("/" + path);
        
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("Lỗi: Không tìm thấy ảnh tại đường dẫn: /" + path);
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
        
        // --- 4. SỬA ĐOẠN TẢI ẢNH BẰNG HÀM getIcon ---
        ImageIcon icon = getIcon(iconPath, 25, 25);
        if (icon != null) {
            btn.setIcon(icon);
        }
        
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
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorDam);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentBtn) {
                    btn.setBackground(colorNhat);
                }
            }
        });
        
        return btn;
    }
    
    private JPopupMenu createBanDatPopupMenu(JButton parentBtn) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(colorNhat);
        popupMenu.setBorder(BorderFactory.createLineBorder(colorDam, 1));
        
        // --- 5. SỬA ĐƯỜNG DẪN ẢNH POPUP (Bỏ "src/") ---
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
        
        // --- 6. SỬA ĐOẠN TẢI ẢNH MENU CON ---
        ImageIcon icon = getIcon(iconPath, 20, 20);
        if (icon != null) {
            item.setIcon(icon);
            item.setIconTextGap(10);
        }

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(colorDam);
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(colorNhat);
            }
        });
        
        return item;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o.equals(btndangxuat)) {
             int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
             if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                try {
                    new DangNhap_GUI().setVisible(true); 
                } catch (Exception ex) {
                    System.exit(0);
                }
             }
        }
    }
    
    public static void main(String[] args) {
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new TrangChinh_Form().setVisible(true);
        });
    }
}