package gui;

import dao.BanDat_DAO;
import dao.BaoCao_DAO;
import entity.MonAn;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.toedter.calendar.JDateChooser;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.axis.NumberAxis;

public class BaoCao_GUI extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Components
    private JTable tblTopMon;
    private DefaultTableModel modelTopMon;
    private JLabel lblTongDoanhThu, lblDonDatBan, lblDoanhThuMon, lblSoLuongHD;
    private JPanel pnlChartContainer;
    private JDateChooser txtTuNgay, txtDenNgay;
    private JComboBox<String> cboThongKeTheo;
    private JButton btnLoc;
   
    
    
    private ChartPanel chartPanel = null; 


    private final Color PRIMARY_BG = new Color(255, 204, 153); 
    private final Color CONTENT_BG = Color.WHITE; 
    private final Color ACCENT_COLOR = new Color(51, 153, 255); 

    // DAO
    private final BaoCao_DAO baoCaoDAO = new BaoCao_DAO();
    
    public BaoCao_GUI() {
        // --- 1. CẤU TRÚC CHUNG ---
        setLayout(new BorderLayout(10, 10));
        setBackground(PRIMARY_BG); 
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));



        // --- 2. PANEL NORTH (Tiêu đề và Bộ lọc) ---
        JPanel pnlNorth = new JPanel();
        pnlNorth.setLayout(new BoxLayout(pnlNorth, BoxLayout.Y_AXIS));
        pnlNorth.setBackground(PRIMARY_BG);
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("BÁO CÁO THỐNG KÊ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(new Color(139, 69, 19));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlNorth.add(lblTitle);
        pnlNorth.add(Box.createVerticalStrut(20)); 

        // 2.1. BỘ LỌC
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlFilter.setBackground(PRIMARY_BG); 
        pnlFilter.setAlignmentX(Component.CENTER_ALIGNMENT); 
        
        txtTuNgay = new JDateChooser(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        txtTuNgay.setDateFormatString("dd/MM/yyyy");
        txtTuNgay.setPreferredSize(new Dimension(140, 30));

        txtDenNgay = new JDateChooser(new Date());
        txtDenNgay.setDateFormatString("dd/MM/yyyy");
        txtDenNgay.setPreferredSize(new Dimension(140, 30));

        cboThongKeTheo = new JComboBox<>(new String[]{"Ngày", "Tháng", "Năm"});
        cboThongKeTheo.setPreferredSize(new Dimension(100, 30));
    

        btnLoc = new JButton("Lọc"); 
        btnLoc.setBackground(ACCENT_COLOR);
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setFocusPainted(false);
        btnLoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoc.setPreferredSize(new Dimension(80, 30));

        pnlFilter.add(new JLabel("Từ ngày:"));
        pnlFilter.add(txtTuNgay);
        pnlFilter.add(new JLabel("Đến ngày:"));
        pnlFilter.add(txtDenNgay);
        pnlFilter.add(new JLabel("Thống kê theo:"));
        pnlFilter.add(cboThongKeTheo);
        pnlFilter.add(btnLoc); 

        pnlNorth.add(pnlFilter);
        pnlNorth.add(Box.createVerticalStrut(40)); 
        
        add(pnlNorth, BorderLayout.NORTH); 

        // --- 3. PANEL CHÍNH GIỮA (Ô TỔNG HỢP) ---
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS)); 
        pnlCenter.setBackground(PRIMARY_BG); 
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); 
        
        // 3.1. Ô TỔNG HỢP
        JPanel pnlSummary = new JPanel();
        pnlSummary.setBackground(PRIMARY_BG);
        pnlSummary.setLayout(new BoxLayout(pnlSummary, BoxLayout.X_AXIS));
        
        Dimension summaryMax = new Dimension(1100, 140); 
        pnlSummary.setMaximumSize(summaryMax);
        pnlSummary.setAlignmentX(Component.CENTER_ALIGNMENT); 
        
        lblTongDoanhThu = new JLabel("0₫", SwingConstants.CENTER);
        lblDonDatBan = new JLabel("0₫", SwingConstants.CENTER); 
        lblDoanhThuMon = new JLabel("0₫", SwingConstants.CENTER);
        lblSoLuongHD = new JLabel("0", SwingConstants.CENTER);

        pnlSummary.add(Box.createHorizontalGlue()); 
        pnlSummary.add(createSummaryBox("Tổng doanh thu", lblTongDoanhThu, new Color(66, 133, 244)));
        pnlSummary.add(Box.createRigidArea(new Dimension(25, 0)));
        pnlSummary.add(createSummaryBox("Đơn đặt bàn", lblDonDatBan, new Color(52, 168, 83))); 
        pnlSummary.add(Box.createRigidArea(new Dimension(25, 0)));
        pnlSummary.add(createSummaryBox("Doanh thu món ăn", lblDoanhThuMon, new Color(251, 188, 5)));
        pnlSummary.add(Box.createRigidArea(new Dimension(25, 0)));
        pnlSummary.add(createSummaryBox("Số lượng hóa đơn", lblSoLuongHD, new Color(234, 67, 53)));
        pnlSummary.add(Box.createHorizontalGlue()); 
        
        pnlCenter.add(pnlSummary);
        pnlCenter.add(Box.createVerticalStrut(25)); 

        add(pnlCenter, BorderLayout.CENTER); 

        // --- 4. BIỂU ĐỒ + TOP MÓN (SOUTH) ---
        JPanel pnlSouth = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlSouth.setBackground(PRIMARY_BG); 
        pnlSouth.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); 

        // 4.1. Biểu đồ Container
        pnlChartContainer = new JPanel(new BorderLayout());
        pnlChartContainer.setBackground(CONTENT_BG); 
        pnlChartContainer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        // Panel tiêu đề riêng cho Biểu đồ
        JPanel pnlChartTitle = new JPanel(new BorderLayout());
        pnlChartTitle.setBackground(CONTENT_BG);
        JLabel lblChartTitle = new JLabel("Biểu đồ doanh thu theo ngày", SwingConstants.CENTER); 
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblChartTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        pnlChartTitle.add(lblChartTitle, BorderLayout.NORTH);
        
        // KHỞI TẠO BIỂU ĐỒ BAN ĐẦU Ở ĐÂY
        createChartPanel(new DefaultCategoryDataset(), "Ngày"); 
        
        // CHỈ THÊM LẦN ĐẦU TIÊN
        pnlChartContainer.add(pnlChartTitle, BorderLayout.NORTH);
        pnlChartContainer.add(chartPanel, BorderLayout.CENTER); // Thêm ChartPanel đã được khởi tạo
        pnlSouth.add(pnlChartContainer);

        // 4.2. Top món bán chạy
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(PRIMARY_BG); 
        pnlTop.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel lblTop = new JLabel("Top 5 món bán chạy", SwingConstants.CENTER);
        lblTop.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTop.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        pnlTop.add(lblTop, BorderLayout.NORTH);

        String[] cols = {"Hạng", "Tên món ăn", "Số lượng"};
        modelTopMon = new DefaultTableModel(cols, 0);
        tblTopMon = new JTable(modelTopMon);
        tblTopMon.setRowHeight(30);
        tblTopMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblTopMon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblTopMon.getTableHeader().setBackground(new Color(220, 220, 220));
        
        JScrollPane scroll = new JScrollPane(tblTopMon);
        scroll.setBackground(CONTENT_BG);
        scroll.getViewport().setBackground(CONTENT_BG); 
        pnlTop.add(scroll, BorderLayout.CENTER);
        pnlSouth.add(pnlTop);

        add(pnlSouth, BorderLayout.SOUTH); 

        // --- 5. XỬ LÝ SỰ KIỆN & LOAD DỮ LIỆU BAN ĐẦU ---
        btnLoc.addActionListener(e -> handleLocAction());
        SwingUtilities.invokeLater(this::handleLocAction);
    }
    
   
    // CÁC PHƯƠNG THỨC XỬ LÝ CHỨC NĂNG
  
    
    private void handleLocAction() {
        Date tuNgayUtil = txtTuNgay.getDate();
        Date denNgayUtil = txtDenNgay.getDate();
        
        // --- 1. KIỂM TRA RỖNG ---
        if (tuNgayUtil == null || denNgayUtil == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn đầy đủ 'Từ ngày' và 'Đến ngày'!", 
                "Thiếu thông tin", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Chuyển đổi sang LocalDate để so sánh
        LocalDate tuNgay = tuNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate denNgay = denNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // --- 2. KIỂM TRA LOGIC NGÀY ---
        if (tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(this, 
                "'Từ ngày' phải nhỏ hơn hoặc bằng 'Đến ngày'!", 
                "Lỗi chọn ngày", 
                JOptionPane.ERROR_MESSAGE);
            return; // Dừng lại, không tải dữ liệu
        }
        
        // --- 3. KIỂM TRA NGÀY TƯƠNG LAI (Optional - Tùy chọn) ---
        // Nếu không muốn cho phép chọn ngày tương lai:
        /*
        if (tuNgay.isAfter(LocalDate.now()) || denNgay.isAfter(LocalDate.now())) {
             JOptionPane.showMessageDialog(this, "Ngày chọn không được vượt quá ngày hiện tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
             return;
        }
        */

        // --- 4. TẢI DỮ LIỆU NẾU HỢP LỆ ---
        String thongKeTheo = (String) cboThongKeTheo.getSelectedItem();
        
        updateSummaryBoxes(tuNgay, denNgay);
        updateTopMonTable(tuNgay, denNgay, 5);
        updateChartData(tuNgay, denNgay, thongKeTheo);
    }
    private void updateSummaryBoxes(LocalDate tuNgay, LocalDate denNgay) {
        double tongDoanhThu = baoCaoDAO.tinhTongDoanhThu(tuNgay, denNgay);
        double tongTienDatBan = baoCaoDAO.tinhTongTienDatBan(tuNgay, denNgay);
        int soLuongHD = baoCaoDAO.tinhTongSoLuongHoaDon(tuNgay, denNgay);
        
        double doanhThuMon = tongDoanhThu - tongTienDatBan; 
        if (doanhThuMon < 0) doanhThuMon = tongDoanhThu;

        lblTongDoanhThu.setText(String.format("%,.0f₫", tongDoanhThu));
        lblDonDatBan.setText(String.format("%,.0f₫", tongTienDatBan));
        lblDoanhThuMon.setText(String.format("%,.0f₫", doanhThuMon));
        lblSoLuongHD.setText(String.valueOf(soLuongHD));
    }
    
    private void updateTopMonTable(LocalDate tuNgay, LocalDate denNgay, int limit) {
        modelTopMon.setRowCount(0); 
        
        Map<MonAn, Integer> topMon = baoCaoDAO.getTopMonAnBanChay(tuNgay, denNgay, limit);
        
        if (topMon.isEmpty()) {
            modelTopMon.addRow(new Object[]{"", "Không có dữ liệu trong kỳ.", ""});
            return;
        }
        
        int hang = 1;
        for (Map.Entry<MonAn, Integer> entry : topMon.entrySet()) {
            modelTopMon.addRow(new Object[]{
                String.valueOf(hang++),
                entry.getKey().getTenMonAn(),
                entry.getValue()
            });
        }
    }

    private void updateChartData(LocalDate tuNgay, LocalDate denNgay, String thongKeTheo) {
        DefaultCategoryDataset newDataset = new DefaultCategoryDataset();
        String title, categoryLabel;
        
        // 1. Thiết lập tiêu đề và nhãn
        if (thongKeTheo.equals("Ngày")) {
             title = "Biểu đồ doanh thu theo ngày";
             categoryLabel = "Ngày";
        } else if (thongKeTheo.equals("Tháng")) {
             title = "Biểu đồ doanh thu theo tháng";
             categoryLabel = "Tháng";
        } else { // Năm
             title = "Biểu đồ doanh thu theo năm";
             categoryLabel = "Năm";
        }

        // 2. Lấy dữ liệu thực từ DAO
        Map<String, Double> duLieuDoanhThu = baoCaoDAO.getDoanhThuTheoNhom(tuNgay, denNgay, thongKeTheo);
        
        // 3. Đổ dữ liệu vào Dataset
        if (duLieuDoanhThu.isEmpty()) {
         
            newDataset.addValue(0, "Doanh thu", "Không có dữ liệu");
        } else {
            for (Map.Entry<String, Double> entry : duLieuDoanhThu.entrySet()) {
                // entry.getKey() là 'yyyy-MM-dd', 'yyyy-MM', hoặc 'yyyy'
                newDataset.addValue(entry.getValue(), "Doanh thu", entry.getKey());
            }
        }
        
        // 4. Cập nhật biểu đồ
        replaceChartPanel(newDataset, categoryLabel);
        
        // 5. Cập nhật tiêu đề hiển thị
        
        Component[] components = pnlChartContainer.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComponents = ((JPanel) comp).getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JLabel) {
                        ((JLabel) subComp).setText(title);
                        break;
                    }
                }
                break; 
            }
        }


        pnlChartContainer.revalidate();
        pnlChartContainer.repaint();
    }
    
  
    private void replaceChartPanel(DefaultCategoryDataset dataset, String categoryLabel) {
        JFreeChart chart = ChartFactory.createBarChart(
                "", categoryLabel, "Doanh thu (₫)", dataset,
                PlotOrientation.VERTICAL, true, true, false
        );

     
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 15));
        chart.setBackgroundPaint(CONTENT_BG); 
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(CONTENT_BG); 
        plot.setOutlineVisible(false);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new java.text.DecimalFormat("#,###")); 
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(51, 153, 255)); 
        renderer.setItemMargin(0.05);
        
        chart.removeLegend(); 
        
        ChartPanel newChartPanel = new ChartPanel(chart);
        newChartPanel.setPreferredSize(new Dimension(450, 400));
        
      
        if (chartPanel != null) { 
             pnlChartContainer.remove(chartPanel);
        }
        chartPanel = newChartPanel;
        pnlChartContainer.add(chartPanel, BorderLayout.CENTER);
    }
    
  
    private void createChartPanel(DefaultCategoryDataset dataset, String categoryLabel) {
        // Dữ liệu mẫu ban đầu (Nếu không muốn dữ liệu trống)
        dataset = new DefaultCategoryDataset();
        dataset.addValue(5000000, "Doanh thu", LocalDate.now().minusDays(2).toString());
        dataset.addValue(8000000, "Doanh thu", LocalDate.now().minusDays(1).toString());
        dataset.addValue(12000000, "Doanh thu", LocalDate.now().toString());

        JFreeChart chart = ChartFactory.createBarChart(
                "", categoryLabel, "Doanh thu (₫)", dataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        
      
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 15));
        chart.setBackgroundPaint(CONTENT_BG); 
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(CONTENT_BG); 
        plot.setOutlineVisible(false);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new java.text.DecimalFormat("#,###")); 
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(51, 153, 255)); 
        renderer.setItemMargin(0.05);
        
        chart.removeLegend();
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(450, 400));
        // KHÔNG THÊM VÀO CONTAINER Ở ĐÂY, CHỈ GÁN CHO BIẾN chartPanel
    }

    private JPanel createSummaryBox(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20) 
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16)); 

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); 

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        Dimension boxSize = new Dimension(240, 140); 
        panel.setPreferredSize(boxSize);
        panel.setMaximumSize(boxSize); 
        panel.setOpaque(true);

        return panel;
    }
    

    // ====== TEST FRAME ======
//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        JFrame frame = new JFrame("Báo cáo thống kê");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1200, 850); 
//        frame.setLocationRelativeTo(null);
//        frame.add(new BaoCao_GUI());
//        frame.setVisible(true);
//    }
}