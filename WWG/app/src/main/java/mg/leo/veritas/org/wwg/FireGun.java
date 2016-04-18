package mg.leo.veritas.org.wwg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//--------------------------------
// �Ʊ� �̻���
//--------------------------------
public class FireGun {
	public int x, y;		// ��ǥ
	public int w, h;		// ���� ����
	public boolean isDead;	// ���
	public Bitmap imgGun;	// �̻��� �̹���

	public int kind;		// �̻��� ���� (0:����, 1:��ȭ)
	private float sy;		// �̵� �ӵ�
		
	//--------------------------------
	// ������
	//--------------------------------
	public FireGun(int x, int y) {
		this.x = x;
		this.y = y;
		
		kind = (MyGameView.isPower) ? 1 : 0;	// �̻��� ����
		imgGun = BitmapFactory.decodeResource(MyGameView.mContext.getResources(), R.drawable.missile1 + kind);
		
		w = imgGun.getWidth() / 2;
		h = imgGun.getHeight() / 2;
		sy = -10;
		Move();
	}

	//--------------------------------
	// Move
	//--------------------------------
	public boolean Move() {
		y += sy;
		return (y < 0);
	}

}
