package mg.leo.veritas.org.wwg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//--------------------------------
// ���� Boss
//--------------------------------
public class EnemyBoss {
	final static int CENTER = 0;		// �߾�
	final static int LEFT = 1;			// ����
	final static int RIGHT = 2;			// ������
	final static int BOTH = 3;			// ��ü

	public int x, y;					// ĳ������ ��ǥ
	public int w, h;					// ũ��
	public int imgNum;					// �̹��� ��ȣ
	public int shield[] = {0, 0, 0};	// ��ȣ��

	public Bitmap imgBoss;				// ������ �̹���	
	public Bitmap imgSpt[];

	private int sx, sy;					// ĳ���� �̵� �ӵ�
	private int dir;					// �¿� �̵� ����
	private int loop;
	private int arShield[] = {20, 30, 40};	// ���̵��� ���� ��ȣ��

	//--------------------------------
	// ������
	//--------------------------------
	public EnemyBoss() {
		shield = new int [3];
		imgSpt = new Bitmap[4];
		
		for (int i = 0; i < 4; i++) 
			imgSpt[i] = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.boss0 + i);
		
		w = imgSpt[3].getWidth() / 2;
		h = imgSpt[3].getHeight() / 2;
	}
	
	//--------------------------------
	// Boss �ʱ�ȭ
	//--------------------------------
	public void InitBoss() {
		shield[LEFT] = shield[RIGHT] = arShield[MyGameView.difficult];
		shield[CENTER] = shield[LEFT] * 2;

		x = MyGameView.Width / 2;
		y = -60;
		
		sy = 4;
		sx = 4;
		dir = 0;
		loop = 0;
		imgBoss = imgSpt[BOTH];
	}
	
	//--------------------------------
	// Boss �̵�
	//--------------------------------
	public void Move() {
		x += sx * dir;
		y += sy;
		if (y > 100) {
			sy = 0;
			if (dir == 0) dir = 1;
		}
		
		if (x < 100 || x > MyGameView.Width - 100)
			dir = -dir;
		
		loop++;
		if (loop % 50 > 0) return;
		MyGameView.mBsMissile.add(new BossMissile(x, y, CENTER));
		if (shield[LEFT] > 0)
			MyGameView.mBsMissile.add(new BossMissile(x - w / 2, y, LEFT));
		if (shield[RIGHT] > 0)
			MyGameView.mBsMissile.add(new BossMissile(x + w / 2, y, RIGHT));
	}
}
