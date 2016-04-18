package mg.leo.veritas.org.wwg;

import android.util.Log;

//-----------------------------------
//���� Delay �ð�
//-----------------------------------
public class DelayTime {
	private int Delay[][] = new int[6][8];
	
	//-----------------------------------
	//  �迭�� �ֱ�
	//-----------------------------------
	public DelayTime(String str) {
		
		String tmp[] = str.split("\n");
		String s;
		for (int i = 1; i < tmp.length; i++) {
			for (int j = 0; j < 8; j++) {
				s = tmp[i].substring(j * 4, (j + 1) * 4).trim();
				if (s.equals("---")) 
					Delay[i - 1][j] = -1;
				else 
					Delay[i - 1][j] = Integer.parseInt(s);
			} // j
		} // i
		Log.v("Delay", "success");
	}
	
	//-----------------------------------
	//  Delay ��ȣ ���ϱ�
	//-----------------------------------
	public int GetDelay(int kind, int num) {
		return Delay[kind][num];
	}
}

