import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.sql.*;
import javax.swing.*;

//import javax.swing.border.LineBorder;

public class Minesweeper extends JFrame implements ActionListener
{
	private static int x=30,y=16,minesnum=99;
	//设定雷区尺寸及地雷个数
	private int rx,ry,mines=minesnum,time=0;
	private boolean sound=false;
	Logo lg=new Logo();
	Mine[][] mine=new Mine[x][y];//建立雷区面板数组
	JPanel controller=new JPanel();
	MineField minefield=new MineField();//创建容纳雷区面板
	JPanel all=new JPanel();
	JPanel replayp=new JPanel();
	JLabel minesp=new JLabel();
	JLabel timep=new JLabel();//显示时间的面板
	static MenuBar top=new MenuBar();
	Menu mgame=new Menu("Game");
	Menu help=new Menu("Help");
	MenuItem mnew=new MenuItem("New    ");
	MenuItem msound=new MenuItem("Sound");
	MenuItem mabout=new MenuItem("About Minesweeper...");
	//MenuItem mbeg=new MenuItem("Beginner");
	//MenuItem mint=new MenuItem("Intermediate");
	//MenuItem mexp=new MenuItem("Expert");
	MenuItem mexi=new MenuItem("Exit");
	JButton replay=new JButton(new ImageIcon("playing.jpg"));
	//添加时间
	Timer timer=new Timer(1000,this);
	//添加音频
	AudioClip dong=Applet.newAudioClip(this.getClass().getResource("dong.wav"));
	AudioClip bang=Applet.newAudioClip(this.getClass().getResource("bang.wav"));
	
	Minesweeper()
	{
		if(x<9)
			x=9;
		if(y<9)
			y=9;
		if(x*y<=minesnum)
			minesnum=10;
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				mine[j][i]=new Mine();
				mine[j][i].setRx(j);
				mine[j][i].setRy(i);
			}
		setMine();
		top.add(mgame);
		top.add(help);
		help.add(mabout);
		lg.setTitle("About MineSweeper");
		lg.setSize(477,280);
		mnew.addActionListener(this);
		msound.addActionListener(this);
		mabout.addActionListener(this);
		mexi.addActionListener(this);
		//mbeg.addActionListener(this);
		mgame.add(mnew);
		mgame.add(msound);
		//mgame.add(mbeg);
		//mgame.add(mint);
		//mgame.add(mexp);
		mgame.add(mexi);
		minefield.setLayout(new GridLayout(y,x));
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
				minefield.add(mine[j][i]);
		replay.addActionListener(this);
		minesp.setFont(new Font("DigifaceWide",Font.ROMAN_BASELINE,20));
		if(mines<100)
			minesp.setText("0"+mines+"");
		else
			minesp.setText(mines+"");
		timep.setFont(new Font("DigifaceWide",Font.ROMAN_BASELINE,20));
		timep.setText("000");
		controller.setLayout(new BorderLayout());
		controller.add(minesp,BorderLayout.WEST);
		replayp.add(replay);
		controller.add(replayp,BorderLayout.CENTER);
		controller.add(timep,BorderLayout.EAST);
		this.getContentPane().add(controller,BorderLayout.NORTH);
		this.getContentPane().add(minefield,BorderLayout.CENTER);
	}
	
	public void setMine()
	{
		for(int i=1;i<=minesnum;i++)
		{
			rx=(int)(Math.random()*x);
			ry=(int)(Math.random()*y);
			if(!mine[rx][ry].getIsMine())
			{
				mine[rx][ry].setIsMine(true);
			}
			else
				i--;
		}
	}
	//判断部分
	public void test(int rx,int ry)
	{
		//算法说明:当点击一处雷区块时,该算法会判断该雷区块的坐标信息,以便于在其
		//后的探测过程中不会出现数组索引值越界异常(即小于0或大于数组x或y的值).
		//之后便会探测该雷区块周围区块的地雷个数,递归调用getIsMine()方法.并将所
		//探测过的雷区锁定.
		int minesnum=0;
		if(rx>0&&ry>0)
		{
			if(mine[rx-1][ry-1].getIsMine())
				minesnum++;
		}
		if(ry>0)
		{
			if(mine[rx][ry-1].getIsMine())
				minesnum++;
		}
		if(rx<x-1&&ry>0)
		{
			if(mine[rx+1][ry-1].getIsMine())
				minesnum++;
		}
		if(rx>0)
		{
			if(mine[rx-1][ry].getIsMine())
				minesnum++;
		}
		if(rx<x-1)
		{
			if(mine[rx+1][ry].getIsMine())
				minesnum++;
		}
		if(rx>0&&ry<y-1)
		{
			if(mine[rx-1][ry+1].getIsMine())
				minesnum++;
		}
		if(ry<y-1)
		{
			if(mine[rx][ry+1].getIsMine())
				minesnum++;
		}
		if(rx<x-1&&ry<y-1)
		{
			if(mine[rx+1][ry+1].getIsMine())
				minesnum++;
		}
		
		mine[rx][ry].setState('C');
		mine[rx][ry].setMark((byte)0);
		mine[rx][ry].setMinesnum((byte)minesnum);
		mine[rx][ry].repaint();
		
		if(minesnum==0)
		{
			if(rx>0&&ry>0)
				if(mine[rx-1][ry-1].getMark()%3!=1&&mine[rx-1][ry-1].getState()!='C')
					test(rx-1,ry-1);
			if(ry>0)
				if(mine[rx][ry-1].getMark()%3!=1&&mine[rx][ry-1].getState()!='C')
					test(rx,ry-1);
			if(rx<x-1&&ry>0)
				if(mine[rx+1][ry-1].getMark()%3!=1&&mine[rx+1][ry-1].getState()!='C')
					test(rx+1,ry-1);
			if(rx>0)
				if(mine[rx-1][ry].getMark()%3!=1&&mine[rx-1][ry].getState()!='C')
					test(rx-1,ry);
			if(rx<x-1)
				if(mine[rx+1][ry].getMark()%3!=1&&mine[rx+1][ry].getState()!='C')
					test(rx+1,ry);
			if(rx>0&&ry<y-1)
				if(mine[rx-1][ry+1].getMark()%3!=1&&mine[rx-1][ry+1].getState()!='C')
					test(rx-1,ry+1);
			if(ry<y-1&&ry<y-1)
				if(mine[rx][ry+1].getMark()%3!=1&&mine[rx][ry+1].getState()!='C')
					test(rx,ry+1);
			if(rx<x-1&&ry<y-1)
				if(mine[rx+1][ry+1].getMark()%3!=1&&mine[rx+1][ry+1].getState()!='C')
					test(rx+1,ry+1);
		}	
	}
	
	public void gameOver()
	{
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				if(mine[j][i].getIsMine())
				{
					mine[j][i].setMark((byte)0);
					mine[j][i].setState('M');
					mine[j][i].repaint();
				}
				else if(mine[j][i].getMark()%3==1)
				{
					mine[j][i].setWrong(true);
					mine[j][i].repaint();
				}
				else
					mine[j][i].setState('X');
			}
	}
	
	public void isFinished()
	{
		int probed=0;
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
				if(mine[j][i].getState()=='P')
					probed++;
		if(probed==x*y-minesnum)
		{
			for(int i=0;i<y;i++)
				for(int j=0;j<x;j++)
				{
					if(mine[j][i].getIsMine())
					{
						mine[j][i].setMark((byte)1);
						mine[j][i].repaint();
						mine[j][i].setState('X');
					}
				}
			replay.setIcon(new ImageIcon("finish.jpg"));
			replay.repaint();
			minesp.setText("000");
			timer.stop();
		}
	}
	
	public static void main(String[] args)
	{
		if(x<9)
			x=9;
		if(y<9)
			y=9;
		if(x*y<=minesnum)
			minesnum=10;
		Minesweeper minesweeper=new Minesweeper();
		minesweeper.setMenuBar(top);
		minesweeper.setTitle("Minesweeper");
		minesweeper.setResizable(false);
		minesweeper.setSize(x*20,y*20+60);
		minesweeper.setVisible(true);
		minesweeper.setDefaultCloseOperation(3);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==timer)//计时器必须使用触发器
		{
			time++;
			if(time>=100)
				timep.setText(time+"");
			else if(time<100&&time>=10)
				timep.setText("0"+time);
			else
				timep.setText("00"+time);
			timep.repaint();
			if(sound)
				dong.play();
		}
		
		if(e.getSource()==replay||e.getSource()==mnew)
		{
			for(int i=0;i<y;i++)
				for(int j=0;j<x;j++)
				{
					mine[j][i].setLock(false);
					mine[j][i].setMinesnum((byte)0);
					mine[j][i].setIsMine(false);
					mine[j][i].setState(' ');
					mine[j][i].setMark((byte)0);
					mine[j][i].setWrong(false);
					mine[j][i].setBong(false);
					mine[j][i].repaint();
				}
			setMine();
			replay.setIcon(new ImageIcon("playing.jpg"));
			replay.repaint();
			mines=minesnum;
			if(mines>=100)
				minesp.setText(mines+"");
			else if(mines<100&&mines>=10)
				minesp.setText("0"+mines);
			else
				minesp.setText("00"+mines);
			time=0;
			timer.stop();
			timep.setText("000");
		}
		
		if(e.getSource()==msound)
		{
			sound=!sound;
			if(sound)
				msound.setLabel("Sound (Y)");
			else
				msound.setLabel("Sound");
		}
		
		if(e.getSource()==mabout)
			lg.setVisible(true);
		if(e.getSource()==mexi)
			System.exit(0);
	}
	
	public class MineField extends JPanel
	{
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			for(int i=0;i<y;i++)
				for(int j=0;j<x;j++)
					mine[j][i].repaint();

		}
	}
	
	public class Mine extends JPanel implements MouseListener
	{
		private boolean isMine=false,lock=false,bong=false,wrong=false;
		private char state=' ';//状态
		private byte mark=0;
		private int rx,ry;
		private byte minesnum=0;		
		Mine()
		{
			addMouseListener(this);	
		}
		
		//访问器方法开始
		public void setIsMine(boolean isMine)
		{
			this.isMine=isMine;
		}	
		public boolean getIsMine()
		{
			return isMine;
		}
		
		public char getState()
		{
			return state;
		}
		public void setState(char state)
		{
			this.state=state;
		}
		
		public void setRx(int rx)
		{
			this.rx=rx;
		}
		public int getRx()
		{
			return rx;
		}
		
		public void setRy(int ry)
		{
			this.ry=ry;
		}
		public int getRy()
		{
			return ry;
		}
		
		public void setMark(byte mark)
		{
			this.mark=mark;
		}
		public byte getMark()
		{
			return mark;
		}
		
		public void setMinesnum(byte minesnum)
		{
			this.minesnum=minesnum;
		}
		public byte getMinesnum()
		{
			return minesnum;
		}
		
		public void setLock(boolean lock)
		{
			this.lock=lock;
		}
		public boolean getLock()
		{
			return lock;
		}
		
		public void setWrong(boolean wrong)
		{
			this.wrong=wrong;
		}
		public boolean getWrong()
		{
			return wrong;
		}
		
		public void setBong(boolean bong)
		{
			this.bong=bong;
		}
		public boolean getBong()
		{
			return bong;
		}
		
		//访问器方法结束
		
		public void paintComponent(Graphics g)
		{
			byte tempm=(byte)(mark%3);
			super.paintComponent(g);
		
			if(state==' ')
			{
				g.setColor(new Color(255,255,255));
				g.drawLine(0,0,getSize().width-2,0);
				g.drawLine(0,getSize().height-2,0,0);
				g.setColor(new Color(128,128,128));
				g.drawLine(getSize().width-1,-1,getSize().width-1,getSize().height-1);
				g.drawLine(getSize().width-1,getSize().height-1,-1,getSize().height-1);
				g.setColor(new Color(255,255,255));
				g.drawLine(0,+1,getSize().width-2,+1);
				g.drawLine(+1,getSize().height-2,+1,0);
				g.setColor(new Color(128,128,128));
				g.drawLine(getSize().width-2,-2,getSize().width-2,getSize().height-2);
				g.drawLine(getSize().width-2,getSize().height-2,-2,getSize().height-2);
				this.setBackground(new Color(192,192,192));
			}
						
			else if(state=='C'&&!lock)
			{
				timer.start();
				g.setColor(new Color(128,128,128));
				g.drawLine(0,0,getSize().width,0);
				g.drawLine(0,getSize().height,0,0);
				g.drawLine(getSize().width,0,getSize().width,getSize().height);
				g.drawLine(getSize().width,getSize().height,0,getSize().height);
				//绘制雷数
				if(minesnum==1)
				{
					g.setColor(Color.blue);
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("1",6,13);
				}
				else if(minesnum==2)
				{
					g.setColor(new Color(0,128,0));
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("2",6,13);	
				}
				else if(minesnum==3)
				{
					g.setColor(Color.red);
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("3",6,13);	
				}
				else if(minesnum==4)
				{
					g.setColor(new Color(0,0,128));
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("4",6,13);	
				}
				else if(minesnum==5)
				{
					g.setColor(new Color(128,0,0));
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("5",6,13);	
				}
				else if(minesnum==6)
				{
					g.setColor(new Color(0,128,128));
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("6",6,13);	
				}
				else if(minesnum==7)
				{
					g.setColor(Color.lightGray);
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("7",6,13);	
				}
				else if(minesnum==8)
				{
					g.setColor(Color.gray);
					g.setFont(new Font("黑体",Font.BOLD,13));
					g.drawString("2",6,13);	
				}
				this.setBackground(new Color(192,192,192));
				state='P';
				isFinished();
			}
			else if(state=='M')
			{
				//绘制地雷
				if(sound)
					dong.stop();
				g.setColor(Color.red);
				if(bong)
					g.fillRect(1,1,getSize().width-1,getSize().height-1);
				g.setColor(new Color(128,128,128));
				g.drawLine(0,0,getSize().width,0);
				g.drawLine(0,getSize().height,0,0);
				g.drawLine(getSize().width,0,getSize().width,getSize().height);
				g.drawLine(getSize().width,getSize().height,0,getSize().height);
				g.setColor(Color.black);
				g.fillOval(3,3,13,13);
				g.drawLine(1,9,17,9);
				g.drawLine(9,1,9,17);
				g.drawLine(3,3,15,15);
				g.drawLine(15,3,3,15);
				g.setColor(Color.white);
				g.fillRect(6,6,3,3);
				timer.stop();
				if(sound)
					bang.play();
				state='X';
			}
			//绘制标志旗,并在标志旗插错后绘制错误标志
			if(tempm==1)
			{
				g.setColor(Color.black);
				g.fillRect(4,14,11,2);
				g.fillRect(5,13,9,1);
				g.fillRect(8,2,2,11);
				g.setColor(Color.red);
				int[] xs={10,16,10};
				int[] ys={2,8,10};
				g.fillPolygon(xs,ys,3);
				this.setBackground(new Color(192,192,192));
				lock=true;
				if(wrong)
				{
					g.drawLine(4,3,14,13);
					g.drawLine(3,3,13,13);
					g.drawLine(13,3,3,13);
					g.drawLine(14,3,3,14);
				}
			}
			//绘制"?"标志
			if(tempm==2)
			{
				g.setColor(Color.blue);
				g.setFont(new Font("黑体",Font.BOLD,14));
				g.drawString("?",6,13);
				this.setBackground(new Color(192,192,192));
				lock=false;
			}
		}

		public void mouseClicked(MouseEvent e)
		{
			if(state!='X'&&state!='P')
			{
				if(e.isMetaDown())
				{
					state=' ';
					mark++;
					if(mark%3==1)
						mines--;
					else if(mark%3==2)
						mines++;
					if(mines>=100)
						minesp.setText(mines+"");
					else if(mines<100&&mines>=10)
						minesp.setText("0"+mines);
					else if(mines<10)
						minesp.setText("00"+mines);
					minesp.repaint();
					repaint();
				}
				else
				{
					if(!lock&&!isMine)
					{
						test(rx,ry);
					}
					else if(isMine&&!lock)
					{
						replay.setIcon(new ImageIcon("gameover.jpg"));
						replay.repaint();
						bong=true;
						gameOver();
					}
				}
			}
		}
				
		public void mousePressed(MouseEvent e)
		{
			if(state!='X')
			{
				if(e.isMetaDown());
				else
				{
					if(!lock&&!isMine)
					{
						replay.setIcon(new ImageIcon("test.jpg"));
						replay.repaint();
					}
				}
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			if(state!='X')
			{
				if(e.isMetaDown());
				else
				{
					if(!lock&&!isMine)
					{
						replay.setIcon(new ImageIcon("playing.jpg"));
						replay.repaint();
					}
				}
			}
		}
		
		public void mouseEntered(MouseEvent e)
		{}
		
		public void mouseExited(MouseEvent e)
		{}	
	}
}