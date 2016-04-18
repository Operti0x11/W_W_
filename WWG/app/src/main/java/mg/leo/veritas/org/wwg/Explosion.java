package mg.leo.veritas.org.wwg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//---------------------------------
// ����
//---------------------------------
public class Explosion {
	final static int BIG = 0; 
	final static int SMALL = 1; 
	final static int MYSHIP = 2; 
	final static int BOSS = 3; 
	
	public int x, y;			// ��ǥ
	public int w, h;			// ���� ����
	public boolean isDead;		// ���
	public Bitmap imgExp;		// �̹���
	
	private Bitmap imgTemp[] = new Bitmap[6];
	private int kind;			// ���� ���� (1:���� ����, 0:ū ����, 2:�Ʊ��� ����, 3:����)
	private int expCnt = -1;	// ���� ���� ī���
	private int delay = 15;		// �Ʊ��� ���� ó�� �� ���� �ð�
	
	//---------------------------------
	// ������
	//---------------------------------
	public Explosion(int x, int y, int kind) {
		this.x = x;
		this.y = y;
		this.kind = kind;
		
		int n = kind;
		if (n == BOSS) n = BIG;

		for (int i = 0; i < 6; i++) {
			imgTemp[i] = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.exp00 + n * 6 + i);
		}

		w = imgTemp[0].getWidth() / 2;
		h = imgTemp[0].getHeight() / 2;
	}
	
	//---------------------------------
	// ����
	//---------------------------------
	public boolean Explode() {
		expCnt++;
		int num = expCnt; 
		
		// �Ʊ��� & Boss�� õõ�� ���ĵǵ��� ó��
		if (kind == MYSHIP || kind == BOSS ) {
			num = expCnt / 3; 
			if (num > 5) num = 5;
		}
		
		// ����, ���� ó��
		if (expCnt == 1) {
			switch (kind) {
			case SMALL:
				if (MyGameView.isSound)
					MyGameView.sdPool.play(MyGameView.sdExp0, 1, 1, 9, 0, 1);
				if (MyGameView.isVibe)
					MyGameView.vibe.vibrate(50);
				break;
			case BIG:
				if (MyGameView.isSound)
					MyGameView.sdPool.play(MyGameView.sdExp1, 1, 1, 9, 0, 1);
				if (MyGameView.isVibe)
					MyGameView.vibe.vibrate(100);
				break;
			case MYSHIP:	
				if (MyGameView.isSound)
					MyGameView.sdPool.play(MyGameView.sdExp2, 1, 1, 9, 0, 1);
				if (MyGameView.isVibe)
					MyGameView.vibe.vibrate(100);
				break;
			case BOSS:	
				if (MyGameView.isSound)
					MyGameView.sdPool.play(MyGameView.sdExp3, 1, 1, 9, 0, 1);
				if (MyGameView.isVibe)
					MyGameView.vibe.vibrate(100);
			}
		}
		
		imgExp = imgTemp[num];			// �Ҳ� ǥ��
		if (num < 5) return false;		// ���� ���� ��
		
		switch (kind) {
		case SMALL:
			return true;
		case MYSHIP:	
			return ResetGunShip();
		default :
			return CheckClear();
		}
		
	}

	//----------------------------
	//  �������� Clear ����
	//----------------------------
	public static boolean CheckClear() {
		// �������� ������
		if (MyGameView.mMap.enemyCnt > 0 || MyGameView.mExp.size() > 1)
			return true;
		
		// �Ϲ� �������� ����
		if (MyGameView.stageNum % MyGameView.BOSS_COUNT > 0) { 
			MyGameView.status = MyGameView.STAGE_CLEAR;	
			return true;
		}
		
		// Boss �������� ���� ��
		if (MyGameView.mBoss.shield[EnemyBoss.CENTER] > 0) 
			return true;
		
		// Boss �������� ����
		if (MyGameView.mBoss.shield[EnemyBoss.CENTER] < 0 ) {
			MyGameView.mBoss.y = -60;						// Boss ȭ�鿡�� ����
			MyGameView.mBoss.shield[EnemyBoss.CENTER] = 0;	// 0���� �ʱ�ȭ - �߿���
			MyGameView.isBoss = false;
			MyGameView.status = MyGameView.STAGE_CLEAR;
			return true;
		}
			
		// Boss Stage �߰� - Boss CENTER�� 0�� ��
		MyGameView.MakeBossStage();
		return true;
	}

	//----------------------------
	//  �Ʊ��� �ʱ�ȭ
	//----------------------------
	public boolean ResetGunShip() {
		if (--delay > 0) return false;
		
		if (MyGameView.shipCnt >= 0) {
			MyGameView.mShip.ResetShip();

			// �� ���ּ��� ���ʽ��� ��� ��ȿ�� ó���� 
			MyGameView.isPower = MyGameView.isDouble = false;
			MyGameView.gunDelay = 15;
		} else { 
			MyGameView.mShip.y = -40;	// GameOver	
			MyGameView.status = MyGameView.GAMEOVER; 
		}
		return true;
	}
	
}
