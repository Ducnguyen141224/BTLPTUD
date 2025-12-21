package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ManHinhChao_GUI extends JWindow {
    
    private JProgressBar progressBar;

    public ManHinhChao_GUI() {
        // 1. Cấu hình cơ bản
        setSize(600, 350); // Kích thước màn hình chào
        setLocationRelativeTo(null); // Căn giữa màn hình
        setLayout(new BorderLayout());

        // 2. Phần HÌNH ẢNH TRUNG TÂM
        JLabel lblAnhNen = new JLabel();
        ImageIcon icon = getIcon("image/man_hinh_cho.png", 600, 330); 
        
        if (icon != null) {
            lblAnhNen.setIcon(icon);
        } else {
 
            lblAnhNen.setText("PHẦN MỀM QUẢN LÝ NHÀ HÀNG");
            lblAnhNen.setHorizontalAlignment(SwingConstants.CENTER);
            lblAnhNen.setFont(new Font("Arial", Font.BOLD, 20));
            lblAnhNen.setBackground(new Color(255, 255, 255));
            lblAnhNen.setOpaque(true);
        }
        add(lblAnhNen, BorderLayout.CENTER);

        // 3. Phần THANH LOADING (JProgressBar)
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(255, 140, 0));
        progressBar.setBackground(Color.WHITE); 
        progressBar.setPreferredSize(new Dimension(600, 20)); 
        progressBar.setBorder(null);
        
        add(progressBar, BorderLayout.SOUTH);

        // Hiển thị lên
        setVisible(true);

        // 4. CHẠY LOADING (Giả lập chạy % từ 0 -> 100)
        chayLoading();
    }

    private void chayLoading() {
        
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    progressBar.setValue(i);
                    
                    // Cập nhật text hiển thị (tùy chọn)
                    if (i < 30) progressBar.setString("Đang khởi động modules...");
                    else if (i < 70) progressBar.setString("Đang kết nối cơ sở dữ liệu...");
                    else if (i < 90) progressBar.setString("Đang tải tài nguyên hình ảnh...");
                    else progressBar.setString("Hoàn tất!");

                    
                    Thread.sleep(30); 
                }
                
                
                this.dispose(); // 1. Tắt màn hình chào
                
                // 2. Mở màn hình Đăng Nhập
                SwingUtilities.invokeLater(() -> {
                    new DangNhap_GUI().setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Hàm lấy ảnh chuẩn (để sau này đóng gói JAR không bị lỗi)
    private ImageIcon getIcon(String path, int width, int height) {
        URL imgURL = getClass().getResource("/" + path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

//    
//    public static void main(String[] args) {
//        new ManHinhChao_GUI();
//    }
}