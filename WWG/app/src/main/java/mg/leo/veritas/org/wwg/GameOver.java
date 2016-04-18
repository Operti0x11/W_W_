package mg.leo.veritas.org.wwg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

//--------------------------------
// Game Over ó����
//--------------------------------
public class GameOver {
	final int WAIT = 1;					// ��ư �Է� ��� ����
	final int TOUCH = 2;				// Yes, No ����
	final int BTN_YES = 1;				// ��ư ��ȣ
	final int BTN_NO = 2;
	
	private int btnWhich;				// ���� ��ư ����
	private int status = WAIT;			// ���� ����
	
	private int mx1, my1, mx2, my2;		// �޽����� ǥ���� ��ġ
	private int mw1, mw2;				// �޽��� ��
	private int x1, y1, x2, w, h;		// Yes, No ��ư ǥ���� ��ġ 
	private Bitmap imgOver, imgAgain;	// ���� ���� �޽���
	private Bitmap imgYes, imgNo;
	private Bitmap imgCong;				// ���� �޽���
	private int loop;					// ȭ�� ó���� ���� ī����
	private Rect rectYes, rectNo;		// ��ư�� ��ǥ
	
	//--------------------------------
	// ������
	//--------------------------------
	public GameOver() {
		// ��ư �̹���
		imgYes = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.btn_yes);
		imgNo = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.btn_no);

		// �޽��� �̹���
		imgOver = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.msg_over);
		imgCong = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.msg_all);
		imgAgain = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.msg_again);

		my1 = 260;		// �޽��� ǥ�� ��ġ

		// Try again
		mw2 = imgAgain.getWidth();
		mx2 = (MyGameView.Width - mw2) / 2;
		my2 = 550;

		// ��ư
		y1 = 630;		
		w = imgYes.getWidth();
		h = imgYes.getHeight();
		
		x1 = 100;
		x2 = MyGameView.Width - 100 - w;
		
		// ��ư�� ��ǥ�� Rect�� ��ȯ - Touch�� ����
		rectYes = new Rect(x1, y1, x1 + w, y1 + h);
		rectNo = new Rect(x2, y1, x2 + w, y1 + h);
		loop = 0;
	}
	
	//--------------------------------
	// GameOver & Clear All
	//--------------------------------
	public void SetOver(Canvas canvas) {
		if (MyGameView.status == MyGameView.GAMEOVER)
			mw1 = imgOver.getWidth();
		else
			mw1 = imgCong.getWidth();
		mx1 = (MyGameView.Width - mw1) / 2;
		
		switch (status) {
		case WAIT:
			DisplayAll(canvas);
			break;
		case TOUCH:
			CheckButton();
		}
	}
	
	//--------------------------------
	// Display All
	//--------------------------------
	public void DisplayAll(Canvas canvas) {
		// ��� �̹���
		canvas.drawBitmap(MyGameView.imgBack, 0, 0, null);
		// ���� ǥ��
		MyGameView.mThread.MoveAll();
		MyGameView.mThread.AttackSprite();
		MyGameView.mThread.DrawAll(canvas);
		
		loop++;
		// Message
		if (loop % 12 / 6 == 0)	{			// �޽��� ���ڰŸ�
			if (MyGameView.status == MyGameView.GAMEOVER)
				canvas.drawBitmap(imgOver, mx1, my1, null);
			else
				canvas.drawBitmap(imgCong, mx1, my1, null);
		}	
		
		// ��ư ǥ��
		canvas.drawBitmap(imgAgain, mx2, my2, null);
		canvas.drawBitmap(imgYes, x1, y1, null);
		canvas.drawBitmap(imgNo, x2, y1, null);
	}
	
	//--------------------------------
	// ��ư�� ���� ó��
	//--------------------------------
	private void CheckButton() {
		if (btnWhich == BTN_NO) {	
			MyGameView.GameOver();			// MyGameView�� �޼ҵ� ȣ��
			return;
		}
		
		// ����(��������) �ٽ� ����
		status = WAIT;						// ��ư�� status�� ���� ���·� �ǵ���
		btnWhich = 0;
		
		// View�� ǥ�õ� ��� ��ü�� �����Ѵ�
		MyGameView.mMissile.clear();		// ���� �̻���
		MyGameView.mGun.clear();			// �Ʊ� ������
		MyGameView.mBonus.clear();			// ���ʽ�
		MyGameView.mExp.clear();			// ���� �Ҳ�
		MyGameView.mBoss.InitBoss();		// ���� �ʱ�ȭ
		
		// ù�Ǻ��� ����
		//MyGameView.stageNum = 1;	
		//MyGameView.MakeStage();				// �������� ����
		//MyGameView.score = 0; 
		
		// �����Ǻ��� ����
		if (MyGameView.stageNum > MyGameView.MAX_STAGE)
			MyGameView.stageNum = MyGameView.MAX_STAGE;

		MyGameView.shipCnt = 3; 
		MyGameView.MakeStage();				// �⺻ �������� ����
		MyGameView.mShip.ResetShip();			// ���ּ� �ʱ�ȭ
		MyGameView.status = MyGameView.PROCESS; 
	}

	//-------------------------------------
	//  TouchEvent
	//-------------------------------------
	public boolean TouchEvent(int x, int y) {
		if (rectYes.contains(x, y))	btnWhich = BTN_YES;
		if (rectNo.contains(x, y)) btnWhich = BTN_NO;
		if (btnWhich != 0) status = TOUCH;
		return true;
	}
}
