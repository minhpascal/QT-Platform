package test.com.msasc.library.swing;

import java.io.File;
import java.util.Locale;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.JFileChooser;
import com.qtplaf.library.util.TextServer;

public class TestJFileChooser {

	public static void main(String[] args) {
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);
		Locale.setDefault(Locale.UK);
		
		FileNameExtensionFilter filterImg = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");
		FileNameExtensionFilter filterTxt = new FileNameExtensionFilter("Text files", "txt", "prn");
		JFileChooser chooser = new JFileChooser(session);
		chooser.setDialogTitle("Hola mamón");
		chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
		chooser.setApproveButtonText("Select");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		chooser.setAcceptAllFileFilterUsed(false);
//		chooser.addChoosableFileFilter(filterTxt);
//		chooser.addChoosableFileFilter(filterImg);
//		chooser.setFileFilter(filterImg);
		File[] files = null;
		if (chooser.showDialog(null) == JFileChooser.APPROVE_OPTION) {
			files = chooser.getSelectedFiles();
			for (File file : files) {
				System.out.println(file);
			}
		}
		System.exit(0);
	}

}
