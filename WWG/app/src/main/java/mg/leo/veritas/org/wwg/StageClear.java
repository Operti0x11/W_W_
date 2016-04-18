package mg.leo.veritas.org.wwg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

//--------------------------------
// ���� ���������� �Ѿ
//--------------------------------
public class StageClear {
	private int mx, my;
	private int mw;
	private Bitmap imgMsg;
	private int loop;
	
	//--------------------------------
	// ������
	//--------------------------------
	public StageClear() {
		imgMsg = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.msg_clear);
		mw = imgMsg.getWidth();
		
		mx = (MyGameView.Width - mw) / 2;
		my = 300;
		loop = 0;
	}
	
	//--------------------------------
	// Set Clear
	//--------------------------------
	public void SetClear(Canvas canvas) {
		int x, y, w, h; 
		boolean isFinish;

		// ��� �̹���
		canvas.drawBitmap(MyGameView.imgBack, 0, 0, null);
		// ���� ǥ��
		MyGameView.mThread.DrawScore(canvas);
		
		MyGameView.mShip.dir = 3;
		isFinish = MyGameView.mShip.Move();	

		x = MyGameView.mShip.x;
		y = MyGameView.mShip.y;
		w = MyGameView.mShip.w;
		h = MyGameView.mShip.h;
		
		// �Ʊ��� ǥ��
		canvas.drawBitmap(MyGameView.mShip.imgShip, x - w, y - h, null);  
		
		loop++;
		// Message ǥ��
		if (loop % 12 / 6 == 0)		// �޽��� ���ڰŸ� 
			canvas.drawBitmap(imgMsg, mx, my, null);
		
		if (isFinish) {				// �Ʊ��Ⱑ ȭ������ ����
			canvas.drawBitmap(imgMsg, mx, my, null);
			MyGameView.mShip.dir = 0;
			loop = 0;
			setNextStage();
		}
	}
	
	//--------------------------------
	// ���� �������� �غ�
	//--------------------------------
	public void setNextStage() {
		// ȭ�� ���� ��� ��ü ����
		MyGameView.mMissile.clear();	// ���� �̻���
		MyGameView.mGun.clear();		// �Ʊ� ������
		MyGameView.mBonus.clear();		// ���ʽ�
		MyGameView.mExp.clear();		// ���� �Ҳ�
		
		MyGameView.stageNum++;
		if (MyGameView.stageNum > MyGameView.MAX_STAGE) {
			MyGameView.status = MyGameView.ALL_CLEAR;			// ������ Clear
			MyGameView.stageNum = MyGameView.MAX_STAGE;
		}	
		else {
			MyGameView.MakeStage();								// ���ο� �������� �غ�
			MyGameView.status = MyGameView.PROCESS;
		}
	}
}
