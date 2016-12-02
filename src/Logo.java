import java.awt.*;
import javax.swing.*;

public class Logo extends JFrame
{
	JLabel lphoto=new JLabel(new ImageIcon("Logo.jpg"));
	JLabel ver=new JLabel("Version:1.01");
	JLabel cr=new JLabel("Copyright 2004-2007 CATMoocat Corporation");
	JPanel textp=new JPanel();
	
	Logo()
	{
		textp.setLayout(new GridLayout(2,1));
		ver.setHorizontalAlignment(0);
		cr.setHorizontalAlignment(0);
		textp.add(ver);
		textp.add(cr);
		this.add(lphoto,BorderLayout.NORTH);
		this.add(textp,BorderLayout.CENTER);
	}
}