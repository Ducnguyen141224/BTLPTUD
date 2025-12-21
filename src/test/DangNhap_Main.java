	package test;

import javax.swing.SwingUtilities;

import gui.DangNhap_GUI;
import gui.ManHinhChao_GUI;

public class DangNhap_Main {
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        // Chạy màn hình chào trước
	        // Sau khi loading xong, nó sẽ tự gọi DangNhap_GUI lên
	        new ManHinhChao_GUI(); 
	    });
	}
}
