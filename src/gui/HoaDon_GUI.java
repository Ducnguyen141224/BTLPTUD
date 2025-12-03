package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.HoaDon_DAO;
import entity.HoaDon;

import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDon_GUI extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtTimMaHD;
    private JSpinner chonNgayTu, chonNgayDen;

    private final HoaDon_DAO hoaDon_DAO;

    private List<HoaDon> danhSachGoc = new ArrayList<>();

    public HoaDon_GUI() {

        this.hoaDon_DAO = new HoaDon_DAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 218, 170));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Quản lý hóa đơn", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        add(taoPanelBoLoc(), BorderLayout.SOUTH);
        add(createTable(), BorderLayout.CENTER);

        loadDataToTable();
    }

    // =====================================================
    // PANEL LỌC
    // =====================================================
    private JPanel taoPanelBoLoc() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 218, 170));

        // --- panel tìm mã ---
        JPanel panelTim = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTim.setBackground(new Color(255, 218, 170));

        panelTim.add(new JLabel("Mã HĐ:"));
        txtTimMaHD = new JTextField(10);
        panelTim.add(txtTimMaHD);

        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> timTheoMaHD());
        panelTim.add(btnTim);

        // --- panel lọc ngày ---
        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelLoc.setBackground(new Color(255, 218, 170));

        panelLoc.add(new JLabel("Từ ngày:"));
        chonNgayTu = new JSpinner(new SpinnerDateModel());
        chonNgayTu.setEditor(new JSpinner.DateEditor(chonNgayTu, "dd/MM/yyyy"));
        chonNgayTu.setPreferredSize(new Dimension(120, 30));
        panelLoc.add(chonNgayTu);

        panelLoc.add(new JLabel("Đến ngày:"));
        chonNgayDen = new JSpinner(new SpinnerDateModel());
        chonNgayDen.setEditor(new JSpinner.DateEditor(chonNgayDen, "dd/MM/yyyy"));
        chonNgayDen.setPreferredSize(new Dimension(120, 30));
        panelLoc.add(chonNgayDen);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.addActionListener(e -> locTheoKhoangNgay());
        panelLoc.add(btnLoc);

        JButton btnReset = new JButton("Làm mới");
        btnReset.addActionListener(e -> resetBang());
        panelLoc.add(btnReset);

        panel.add(panelTim, BorderLayout.WEST);
        panel.add(panelLoc, BorderLayout.EAST);

        return panel;
    }

    // =====================================================
    // TẠO BẢNG
    // =====================================================
    private JScrollPane createTable() {

        String[] columns = { "Mã hóa đơn", "Mã nhân viên", "Ngày lập", "Tổng tiền" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 178, 102));
        header.setFont(new Font("Arial", Font.BOLD, 15));

        return new JScrollPane(table);
    }

    // =====================================================
    // LOAD DATA
    // =====================================================
    private void loadDataToTable() {

        tableModel.setRowCount(0);

        danhSachGoc = hoaDon_DAO.getAllHoaDon();   // ⚠️ DAO PHẢI CÓ HÀM NÀY

        for (HoaDon hd : danhSachGoc) {
            addRow(hd);
        }
    }

    // =====================================================
    // Tìm theo mã
    // =====================================================
    private void timTheoMaHD() {
        String ma = txtTimMaHD.getText().trim().toLowerCase();

        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn!");
            return;
        }

        lamMoiBang();
        boolean found = false;

        for (HoaDon hd : danhSachGoc) {
            if (hd.getMaHoaDon().toLowerCase().contains(ma)) {
                addRow(hd);
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy hóa đơn với mã: " + ma);
            resetBang();
        }
    }

    // =====================================================
    // Lọc theo ngày
    // =====================================================
    private void locTheoKhoangNgay() {

        Date tuNgay = (Date) chonNgayTu.getValue();
        Date denNgay = (Date) chonNgayDen.getValue();

        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải <= ngày kết thúc!");
            return;
        }

        // để bao cả ngày kết thúc
        denNgay = new Date(denNgay.getTime() + 86399999);

        lamMoiBang();
        boolean found = false;

        for (HoaDon hd : danhSachGoc) {

            if (hd.getNgayLap() == null) continue;

            Date ngayHD = Date.from(hd.getNgayLap().atZone(ZoneId.systemDefault()).toInstant());

            if (!ngayHD.before(tuNgay) && !ngayHD.after(denNgay)) {
                addRow(hd);
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn trong khoảng này!");
            resetBang();
        }
    }

    // =====================================================
    // ADD ROW
    // =====================================================
    private void addRow(HoaDon hd) {
    	double tongtien = hoaDon_DAO.layTongTienTheoMaHD(hd.getMaHoaDon());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        tableModel.addRow(new Object[]{
                hd.getMaHoaDon(),
                (hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : "N/A"),
                (hd.getNgayLap() != null ? dtf.format(hd.getNgayLap()) : ""),
                String.format("%,.0f", tongtien)
        });
    }

    // =====================================================
    private void resetBang() {
        lamMoiBang();
        loadDataToTable();
    }

    private void lamMoiBang() {
        tableModel.setRowCount(0);
    }
}

