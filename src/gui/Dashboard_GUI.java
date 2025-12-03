package gui;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import dao.Dashboard_DAO;

public class Dashboard_GUI extends JPanel {

    private Dashboard_DAO dao = new Dashboard_DAO();

    public Dashboard_GUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 230, 200));

        add(tieuDe(), BorderLayout.NORTH);
        add(noiDungChinh(), BorderLayout.CENTER);
    }

    // ================================
    // 1. Panel tiêu đề
    // ================================
    private JPanel tieuDe() {
        JPanel pnl = new JPanel();
        pnl.setBackground(new Color(255, 200, 150));
        JLabel lbl = new JLabel("Dashboard");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        pnl.add(lbl);
        return pnl;
    }

    // ================================
    // 2. Nội dung chính
    // ================================
    private JPanel noiDungChinh() {

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(255, 230, 200));

        pnl.add(thongKeTong());
        pnl.add(panelBieuDo());
        pnl.add(panelTop3());

        return pnl;
    }

    // ================================
    // 3. 4 ô thống kê đầu trang
    // ================================
    private JPanel thongKeTong() {

        double doanhThuNgay = dao.getDoanhThuHomNay();
        int soMonBan = dao.getSoMonBanHomNay();
        double doanhThuThang = dao.getDoanhThuThangNay();
        double doanhThuQuy = dao.getDoanhThuQuyNay();

        JPanel pnl = new JPanel(new GridLayout(1, 4, 15, 10));
        pnl.setBackground(new Color(255, 230, 200));
        pnl.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        pnl.add(taoBox("Tổng doanh thu hôm nay", format(doanhThuNgay), new Color(200, 255, 200), true));
        pnl.add(taoBox("Số lượng món bán hôm nay", String.valueOf(soMonBan), new Color(255, 210, 210), false));
        pnl.add(taoBox("Doanh thu tháng này", format(doanhThuThang), new Color(210, 225, 255), true));
        pnl.add(taoBox("Doanh thu quý này", format(doanhThuQuy), new Color(200, 255, 200), true));

        return pnl;
    }

    private JPanel taoBox(String title, String value, Color bg, boolean addVND) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(bg);
        pnl.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lbl1 = new JLabel(title);
        lbl1.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lbl2 = new JLabel(value + (addVND ? " VND" : ""), SwingConstants.LEFT);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 20));

        pnl.add(lbl1, BorderLayout.NORTH);
        pnl.add(lbl2, BorderLayout.CENTER);

        return pnl;
    }


    private String format(double val) {
        return String.format("%,.0f", val);
    }

    // ================================
    // 4. Panel chứa Pie + Bar chart
    // ================================
    private JPanel panelBieuDo() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 20, 10));
        pnl.setBackground(new Color(255, 230, 200));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        pnl.add(bieuDoPie());
        pnl.add(bieuDoBar());

        return pnl;
    }

    // --- Biểu đồ Pie ---
    private JPanel bieuDoPie() {

        Map<String, Integer> data = dao.getTiLeTheoKhungGio();

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (String k : data.keySet()) {
            dataset.setValue(k, data.get(k));
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Tỉ lệ khung giờ",
                dataset,
                true, true, false);

        ChartPanel panel = new ChartPanel(chart);
        return panel;
    }

    // --- Biểu đồ Bar ---
    private JPanel bieuDoBar() {

        Map<String, Integer> data = dao.getThongKeDatBanTuanNay();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String day : data.keySet()) {
            dataset.setValue(data.get(day), "Lượt đặt", day);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Thống kê đặt bàn trong tuần",
                "Ngày",
                "Số lượt",
                dataset);

        ChartPanel panel = new ChartPanel(chart);
        return panel;
    }

    // ================================
    // 5. Panel Top 3 món bán chạy
    // ================================
    private JPanel panelTop3() {

        List<Map<String, Object>> list = dao.getTop3MonBanChay();

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(new Color(255, 230, 200));
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel lbl = new JLabel("Top 3 món ăn bán chạy", SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        wrapper.add(lbl, BorderLayout.NORTH);

        JPanel pnl = new JPanel(new GridLayout(1, 3, 20, 10));
        pnl.setBackground(new Color(255, 230, 200));

        for (Map<String, Object> item : list) {
            pnl.add(taoMon(
                    item.get("tenMon").toString(),
                    (int) item.get("soLuong")
            ));
        }

        wrapper.add(pnl, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel taoMon(String ten, int luot) {

        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(new Color(255, 200, 150));
        pnl.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        JLabel lblTen = new JLabel(ten, SwingConstants.CENTER);
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel lblLuot = new JLabel(luot + " lượt", SwingConstants.CENTER);
        lblLuot.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        pnl.add(lblTen, BorderLayout.CENTER);
        pnl.add(lblLuot, BorderLayout.SOUTH);

        return pnl;
    }
}
