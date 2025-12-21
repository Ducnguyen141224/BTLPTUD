	package test;

import javax.swing.SwingUtilities;

import gui.DangNhap_GUI;
import gui.ManHinhChao_GUI;

public class DangNhap_Main {
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	       
	        new ManHinhChao_GUI(); 
	    });
	}
}
