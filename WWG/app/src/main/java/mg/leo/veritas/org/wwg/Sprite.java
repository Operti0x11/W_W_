package mg.leo.veritas.org.wwg;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

//--------------------------------
// ���� ĳ����
//--------------------------------
public class Sprite {
	final static int ENTER = 1;			// ĳ���� ����
	final static int BEGINPOS = 2;		// ���� �������� ���� ���� ��ǥ ���
	final static int POSITION = 3;		// ���� �������� �̵� ��
	final static int SYNC = 4;			// ���� �������� �����
	final static int ATTACK = 5;		// ������
	final static int BEGINBACK = 6;		// View��  ����� �ٽ� ������ �غ�
	final static int BACKPOS = 7;		// �ٽ� ���� ��
	
	public int x, y;					// ĳ������ ��ǥ
	public int w, h;					// ũ��
	public boolean isDead;				// �������ΰ�?
	public int shield;					// ��ȣ��
	public int status;					// ����

	public Bitmap imgSprite;			// �������� �̹���	

	private SinglePath sPath; 			// ĳ���Ͱ� �̵��� Path 1�� (���� �� ���� ��Ʈ)
	private float sx, sy;				// ĳ���� �̵� �ӵ� 
	private int sncX;					// ��ũ ��ġ�κ��� ������ �ִ� �Ÿ�			
	private Bitmap imgSpt[] = new Bitmap[16];
	private int sKind, sNum;			// ĳ������ ������ ��ȣ
	private int pNum, col;				// Path ��ȣ�� ������ ���
	private int delay, dir, len;		// ����� �����ð�, ������ ����, ���� �Ÿ�
	private int posX, posY;				// �̵��ؾ� �� ������ ��ǥ
	private int aKind;					// ���� ��Ʈ ��ȣ
	private Random rnd = new Random();
	
	private int diff[] = {8, 6, 4};		// EASY, MEDIUM, HARD 
	private int df;						// ���̵�

	//--------------------------------
	// ������
	//--------------------------------
	public Sprite() {
		// ���� ����
	}
	
	//--------------------------------
	// Sprite �����
	//--------------------------------
	public void MakeSprite(int kind, int num) {
		sKind = kind;
		sNum = num;
		
		// ���ʿ��� ĳ����
		if (MyGameView.mMap.GetSelection(kind, num) == -1) { 
			isDead = true;
			return;
		}
	
		int enemy =  MyGameView.mMap.GetEnemyNum(kind, num);
		imgSpt[0] = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.enemy00 + enemy);
		
		int sw = imgSpt[0].getWidth();
		int sh = imgSpt[0].getHeight();
		w = sw / 2;
		h = sh / 2;

		// 16�������� ȸ���� �̹��� �����
		Canvas canvas = new Canvas();
		for (int i = 1; i < 16; i++) {
			imgSpt[i] = Bitmap.createBitmap(sw, sh, Config.ARGB_8888);
			canvas.setBitmap(imgSpt[i]);
			canvas.rotate(22.5f, w, h);
			canvas.drawBitmap(imgSpt[0], 0, 0, null);
		}
		ResetSprite();
	}

	//--------------------------------
	// Reset Sprite
	//--------------------------------
	public void ResetSprite() {
		pNum = MyGameView.mMap.GetSelection(sKind, sNum);	// Path ��ȣ
		delay = MyGameView.mMap.GetDelay(sKind, sNum);		// Delay �ð� �б�	
		shield = MyGameView.mMap.GetShield(sKind, sNum);	// ��ȣ�� �б�

		posX = MyGameView.mMap.GetPosX(sKind, sNum);		// �������� ��ġ
		posY = MyGameView.mMap.GetPosY(sKind, sNum);
		
		GetPath(pNum);										// pNum���� ���� Path �б�
		status = ENTER;
		isDead = false;
		df = MyGameView.difficult;							// ���� ���̵� - �Ѿ� �߻��

	}
	
	//--------------------------------
	// Path - Path 1�� �б�
	//--------------------------------
	public void GetPath(int num) {
		sPath = MyGameView.mMap.GetPath(num);	// Path �б�
		// Path�� ���� ��ǥ  
		if (sPath.startX != -99) 
			x = sPath.startX;
		if (sPath.startY != -99) 
			y = sPath.startY;
		col = 0;
		GetDir(col);
	}

	//--------------------------------
	// GetDir - ����ġ�� ����� �Ÿ�
	//--------------------------------
	private void GetDir(int col) {
		dir = sPath.dir[col];			// �̵��� ����
		len = sPath.len[col];			// �̵��� �Ÿ�

		sx = MyGameView.mMap.sx[dir];	// �̵� �ӵ� 
		sy = MyGameView.mMap.sy[dir];
		imgSprite = imgSpt[dir];		// �������� �̹���
	}

	//--------------------------------
	// Move
	//--------------------------------
	public void Move() {
		if (isDead && (sKind != 5 || sNum != 0)) return;

		switch (status) {
			case ENTER :			// ĳ���� ����
				EnterSprite();
				break;
			case BEGINPOS:			// ���� ���� ��ġ ���
				BeginPos();
				break;
			case POSITION :			// ���� ���� ��ġ�� �̵���
				Position();
				break;
			case SYNC :				// ���� ���� ��ġ���� ��� ��
				MakeSync();
				break;
			case ATTACK :			// ������
				Attack();
				break;
			case BEGINBACK :		// Ż���� ���� �غ� ��
				BeginBackPos();
				break;
			case BACKPOS :			// Ż���� ���� ��
				BackPosition();
		}
	}
		
	//--------------------------------
	// Enter Sprite
	//--------------------------------
	public void EnterSprite() {
		if (--delay >= 0) return;
		
		x += (int) (sx * 8);
		y += (int) (sy * 8);
		
		// ĳ���� ����� �Ʊ��� ������ ���� ����
		int dr = rnd.nextInt(5) + 6;   // 6~10: �߻� ����   
		if (len % 15 == 0)
			ShootMissile(dr);	

		len--;
		if (len >= 0) return;

		col++;	
		if (col < sPath.dir.length) { 
			GetDir(col);					// ���� ��� ã��
		}	
		else {	
			status = BEGINPOS;		// ����� ���̸� ���� �������� �̵�
		}
	}
	
	//--------------------------------
	// BeginPos - ���� �������� �̵� �غ�
	//--------------------------------
	public void BeginPos() {
		// ������ Path �б�
		if (x < posX + MyGameView.mMap.syncCnt)			// �̵� ���� ����
			dir = 2;									// �ϵ�(NW)��
		else
			dir = 14;									// �ϼ�(NW)��
		
		if (y < posY) 
			dir = (dir == 2) ? 6 : 10;
		
		sx = MyGameView.mMap.sx[dir];					// �̵� ���⿡ ���� �ӵ� ���
		sy = MyGameView.mMap.sy[dir];
		imgSprite = imgSpt[dir];						// �� ������ �̹���
		status = POSITION;								// �������� �̵� �غ� ��
	}
	
	//--------------------------------
	// Position - ���� �������� �̵� ��
	//--------------------------------
	public void Position() {
		x += (int) (sx * 8);							// �̵�	
		y += (int) (sy * 8);
		
		// ��ũ ������ �������� �־����� �� �����Ƿ� ���� �ٽ� ���
		if (x < posX + MyGameView.mMap.syncCnt)			// �̵� ���� ����
			dir = 2;									// �ϵ�(NW)��
		else
			dir = 14;									// �ϼ�(NW)��
		
		if (y < posY) 
			dir = (dir == 2) ? 6 : 10;
		
		// ���� ��ǥ ��
		if (Math.abs(y - posY) <= 4) {					// ���� ��ġ ����
			y = posY;
			if (x < posX + MyGameView.mMap.syncCnt)		// �¿� ���� ����
				dir = 4;								// 3�ù���
			else
				dir = 12;								// 9�� ����
		}
		
		// ���� ��ǥ ��
		if (Math.abs(x - (MyGameView.mMap.syncCnt + posX)) <= 4) {
			x = posX + MyGameView.mMap.syncCnt;
			dir = 0;						// 12�� ����
		}

		if (y == posY && x == posX + MyGameView.mMap.syncCnt) {
			imgSprite = imgSpt[0];			// �������� ��ġ ����
			sx = 1;
			status = SYNC;					// �¿�� �̵��ϸ� ���� ��� ���
			return;							// ��ũ ���� ��
		}	

		sx = MyGameView.mMap.sx[dir];		// ������ ������ ���� ���� ��ġ��
		sy = MyGameView.mMap.sy[dir];		// ��� �̵�
		imgSprite = imgSpt[dir];
	}
	
	//--------------------------------
	// Sync & Move - ��ũ�� �����ϸ� �̵�
	//--------------------------------
	public void MakeSync() {
		sncX = (int) MyGameView.mMap.sx[MyGameView.mMap.dir];	// �¿� �̵����� ��� 
		x += sncX;								// �� �Ǵ� ��� �̵�

		// Sync ����	
		if (sKind == 5 && sNum == 0) {			// �� ĳ���Ͱ� ��ũ�� �����Ѵ�
			MyGameView.mMap.syncCnt += sncX;	// ���� �����ڰ� �¿�� �̵��� �Ÿ�
			MyGameView.mMap.dirCnt++;			// ���� �������� �̵��� �Ÿ�
			if (MyGameView.mMap.dirCnt >= MyGameView.mMap.dirLen) {
				MyGameView.mMap.dirCnt = 0;		// ���� ������ ���� ����
				MyGameView.mMap.dirLen = 104;	// �ݴ� �������� �̵��� �Ÿ�	
				MyGameView.mMap.dir = 16 - MyGameView.mMap.dir;	// �̵����� ����
			}
		}
	}

	//--------------------------------
	// Begin Attack - ���� ��Ʈ ����
	//--------------------------------
	public void BeginAttack(int aKind) {
		if (isDead || (sKind == 5 && sNum == 0)) return;	// ��ũ ������ 
		this.aKind = aKind;									// ���ݿ� �������� ����
		GetPath(aKind + 10);
		status = ATTACK;
	}
	
	//--------------------------------
	// Attack
	//--------------------------------
	public void Attack() {
		x += (int) (sx * 8);			// ���� ��Ʈ�� ������
		y += (int) (sy * 8);
		
		// ������ ���� ��ġ�� ��Ż�� - Ż����
		if (y < - 164 || y > MyGameView.Height + 164 ||
			x < -164 || x > MyGameView.Width + 164) {
			status = BEGINBACK;			// ���� ���� �غ�
			return;
		}
		
		len--;
		if (len >= 0) return;			// ���� �������� ��� �̵� ��
		
		col++;							// ���� ��ȯ
		if (col < sPath.dir.length) {
			GetDir(col);				// ���� ��ȯ �� ���� ���� 
			if (dir >=  6 && dir <= 10)
				ShootMissile(dir);
		}	
		else {	
			status = BEGINPOS;			// ������ ������ �������� ���� �������� ����
		}
	}
	
	//--------------------------------
	// Ż���� ���� �غ� 
	//--------------------------------
	public void BeginBackPos() {
		// GetPath(pNum);
		y = -32;												// ���� ������ (View�� ���)
		x = posX + MyGameView.mMap.syncCnt;						// �ڽ��� ���� ���� ��ġ ���
		
		imgSprite = imgSpt[0];									// ������ �ٶ󺸴� �ڼ��� ����
		status = BACKPOS;
	}
	
	//--------------------------------
	// View�� ������ ���� ���� ��ġ�� �̵�
	//--------------------------------
	public void BackPosition() {
		// ���� ������ �¿� ��������� �̵����ΰ� ���
		sncX = (int) MyGameView.mMap.sx[MyGameView.mMap.dir];	
		y += 2;			// ���� �ӵ��� 2
		x += sncX;		// ���� ������ �̵� ����� ���߾� �¿�� �̵��ϸ� ����	

		// �������� ������ ������ ���� ��Ʈ�� �ٽ� ���� ����
		if (Math.abs(y - posY) <= 4) {
			GetPath(aKind + 10);
			status = ATTACK;
		}
	}
	
	//--------------------------------
	// Shoot Missile - �̻��� �߻�
	//--------------------------------
	private void ShootMissile(int dir) {
		if (rnd.nextInt(10) >= diff[df])
			MyGameView.mMissile.add(new Missile(x, y, dir));
	}
	
}
