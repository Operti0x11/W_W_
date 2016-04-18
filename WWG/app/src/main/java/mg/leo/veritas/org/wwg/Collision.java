package mg.leo.veritas.org.wwg;

import java.util.Random;

//----------------------------
//  충돌 판정 - 생성자 없음
//----------------------------
public class Collision {
	private Random rnd = new Random();

	//----------------------------
	//  충돌 판정
	//----------------------------
	public void CheckCollision() {
		Check_1();					// 아군 미사일과 적과의 충돌
		Check_2();					// 적 미사일과 아군과의 충돌
		Check_3();					// 적군과 아군과의 충돌
		Check_4();					// 아군과 보너스와의 충돌
		if (MyGameView.isBoss) {	// Boss Stage
			Check_5();				// Boss 미사일과 아군과의 충돌
			Check_6();				// 아군 미사일과 보스와의 충돌
		}
	}
	
	//----------------------------
	// 아군 미사일과 적 충돌
	//----------------------------
	private void Check_1() {
		int x, y, x1, y1, w, h; 
		int r = rnd.nextInt(100) - 93;			// 0~6 - 보너스 나올 확률

		NEXT:
		for (int p = MyGameView.mGun.size() - 1; p >= 0; p--) { 
			x = MyGameView.mGun.get(p).x;		//	미사일 좌표
			y = MyGameView.mGun.get(p).y;
			
			for (int i = 0; i < 6; i++) {		// 모든 적군에 대해 조사한다.
				for (int j = 0; j < 8; j++) {			 
					if (MyGameView.mEnemy[i][j].isDead) continue;	// 사망자 무시

					x1 = MyGameView.mEnemy[i][j].x;			// 적군의 좌표
					y1 = MyGameView.mEnemy[i][j].y;
					w = MyGameView.mEnemy[i][j].w;				// 충돌을 조사할 범위
					h = MyGameView.mEnemy[i][j].h;
					
					if (Math.abs(x - x1) > w || Math.abs(y - y1) > h)	// 충돌 없음
						continue;

					if (MyGameView.isPower)							// 강화 마사일은
						MyGameView.mEnemy[i][j].shield -= 4;		// 보호막 4 감소
					else
						MyGameView.mEnemy[i][j].shield--;			// 일반 미사일은 보호막 1감소
						
					if (MyGameView.mEnemy[i][j].shield > 0) {		// 적군의 보호막이 있으면
						// 미사일 위치에 작은 폭발 처리
						MyGameView.mExp.add(new Explosion(x, y, Explosion.SMALL)); 
						MyGameView.score += (6 - i) * 100;			// 적군 레벨에 따라 100씩 득점
					} else {				 
						MyGameView.mEnemy[i][j].isDead = true;	 	// 적군 사망
						MyGameView.mMap.enemyCnt--;					// 남은 적군 수
						// 적군의 중심부에 큰 폭발 처리
						MyGameView.mExp.add(new Explosion(x1, y1, Explosion.BIG)); 
						MyGameView.score += (6 - i) * 200;			// 적군 레벨에 따라 200씩 득점

						if (r > 0)									// 보너스가 있으면
							MyGameView.mBonus.add(new Bonus(x1, y1, r));	// 보너스 생성
					}
					MyGameView.mGun.remove(p);						// 아군 미사일 제거
					continue NEXT;
				} // for j
			} // for i
		} // for p
	}

	//----------------------------
	//  적군 미사일과 아군기 충돌
	//----------------------------
	private void Check_2() {
		if (MyGameView.mShip.undead || MyGameView.mShip.isDead) return;
		
		int x, y, x1, y1, w, h;
		x = MyGameView.mShip.x;		// 아군기 좌표
		y = MyGameView.mShip.y;
		w = MyGameView.mShip.w;
		h = MyGameView.mShip.h;
		
		// 모든 적 미사일에 대해 조사
		for (int i = MyGameView.mMissile.size() - 1; i >= 0; i--) {	
			x1 = MyGameView.mMissile.get(i).x;					// �̻��� ��ǥ
			y1 = MyGameView.mMissile.get(i).y;

			if (Math.abs(x1 - x) > w || Math.abs(y1 - y) > h)	// �浹 ���� 
				continue; 
			MyGameView.mMissile.remove(i);						// �� �̻��� ����
			MyGameView.mShip.shield--;							// �Ʊ� ��ȣ�� ����
			if (MyGameView.mShip.shield >= 0) {					// ��ȣ�� ���� ������ 
				MyGameView.mExp.add(new Explosion(x1, y1, Explosion.SMALL));	// ���� �Ҳ� 
			} else {											// ��ȣ�� ������ 
				MyGameView.mShip.isDead = true;					// �Ʊ��⸦ ����
				MyGameView.shipCnt--;							// �Ʊ��⸦ ����
				MyGameView.mExp.add(new Explosion(x, y, Explosion.MYSHIP));	// ū �Ҳ�
			} // if
			break;		 			
		} // for
	}

	//----------------------------
	//  아군기와 적기와 충돌
	//----------------------------
	private void Check_3() {
		if (MyGameView.mShip.isDead) return;

		int x, y, x1, y1, w, h;
		
		x = MyGameView.mShip.x;			// 아군기 좌표
		y = MyGameView.mShip.y;
		w = MyGameView.mShip.w;
		h = MyGameView.mShip.h;
		
		for (int i = 0; i < 6; i++) {				 
			for (int j = 0; j < 8; j++) {			 
				if (MyGameView.mEnemy[i][j].isDead) continue;		// 사망자 무시

				x1 = MyGameView.mEnemy[i][j].x;
				y1 = MyGameView.mEnemy[i][j].y;
				
				if (Math.abs(x1 - x) > w ||	Math.abs(y1 - y) > h)	// 충돌 없음
					continue;
				MyGameView.mEnemy[i][j].isDead = true;	 		// 충돌이면 적군도 사망
				MyGameView.mMap.enemyCnt--;						// 남은 적군 수
				MyGameView.score += (6 - i) * 200;				// 자폭도 점수 인정
				if (MyGameView.mShip.undead) {
					MyGameView.mExp.add(new Explosion(x1, y1, Explosion.BIG));	// 적군만 사망
				} else {
					MyGameView.mShip.isDead = true;					// 아군기를 잃음
					MyGameView.shipCnt--;							// 아군기도 함께 잃음
					MyGameView.mExp.add(new Explosion(x, y, Explosion.MYSHIP));	// 아군 파괴 불꽃
				}	
				return;
			} // for j
		} // for i
	}

	//----------------------------
	//  아군기와 보너스와 충돌
	//----------------------------
	private void Check_4() {
		int x, y, x1, y1, w, h, bonus = 0;
		
		x = MyGameView.mShip.x;						// 아군기 좌표
		y = MyGameView.mShip.y;
		w = MyGameView.mShip.w;
		h = MyGameView.mShip.h;
		
		for (int i = MyGameView.mBonus.size() - 1; i >= 0; i--) {
			x1 = MyGameView.mBonus.get(i).x;	// 보너스 좌표
			y1 = MyGameView.mBonus.get(i).y;
			if (Math.abs(x - x1) > w * 2 || Math.abs(y - y1) > h * 2)	// 충돌 없음
				continue;

			bonus = MyGameView.mBonus.get(i).kind;		// 보너스 종류
			MyGameView.mBonus.remove(i);				// 보너스 제거

			switch (bonus) {
			case 1: 
				MyGameView.isDouble = true;		// Double Fire 모드
				break;
			case 2: 	
				MyGameView.isPower = true;		// 강화 미사일
				break;
			case 3: 	
				if (MyGameView.gunDelay > 6)
					MyGameView.gunDelay -= 2;	// 미사일 발사 속도
				break;
			case 4:
				MyGameView.mShip.shield = 6;	// ��ȣ�� ����
				break;
			case 5:
				MyGameView.mShip.undeadCnt = 100;	// ���� ���� ����		 
				MyGameView.mShip.undead = true;				 
				break;
			case 6:
				if (MyGameView.shipCnt < 4)		// ���ּ� 1�� �÷���
					MyGameView.shipCnt++;
			}
		} // for
	}

	//----------------------------
	//  Boss �̻��ϰ� �Ʊ�����  �浹
	//----------------------------
	private void Check_5() {
		if (MyGameView.mShip.undead) return;
		
		int x, y, x1, y1;
		int w, h;

		x = MyGameView.mShip.x;
		y = MyGameView.mShip.y;
		w = MyGameView.mShip.w;
		h = MyGameView.mShip.h;
		
		for (int i = MyGameView.mBsMissile.size() - 1; i >= 0; i--) {	
			x1 = MyGameView.mBsMissile.get(i).x;					// �̻��� ��ǥ
			y1 = MyGameView.mBsMissile.get(i).y;

			if (Math.abs(x1 - x) <= w && Math.abs(y1 - y) <= h)	{ 
				MyGameView.mBsMissile.remove(i);				// �� �̻��� ����
				MyGameView.mShip.isDead = true;					// �Ʊ��⸦ ����
				MyGameView.mExp.add(new Explosion(x, y, Explosion.MYSHIP));	
				MyGameView.shipCnt--;						
			}	
		}
	}
	
	//----------------------------
	//  �Ʊ� �̻��ϰ� Boss��  �浹
	//----------------------------
	private void Check_6() {
		int x1, x2, x3, y1, w, h;			// Boss�� Center, Left, Right, ��
		int x, y, damage = 1;				// �̻��� ��ǥ, power
		if (MyGameView.isPower) damage = 4;
		
		// Boss 3�κ� ��ǥ�� ��
		w = MyGameView.mBoss.w / 2;
		h = MyGameView.mBoss.h;
		x1 = MyGameView.mBoss.x;
		x2 = x1 - w;
		x3 = x1 + w;
		y1 = MyGameView.mBoss.y;

		for (int i = MyGameView.mGun.size() - 1; i >= 0; i--) { 
			x = MyGameView.mGun.get(i).x;		//	�̻��� ��ǥ	
			y = MyGameView.mGun.get(i).y;
		
			// Boss Center
			if (Math.abs(x - x1) < w && Math.abs(y - y1) < h ) {
				MyGameView.mBoss.shield[EnemyBoss.CENTER] -= damage;
				MyGameView.mGun.remove(i);

				// ���� (-)�� �ɶ����� ó�� - Explosion���� CENTER�� ������
				if (MyGameView.mBoss.shield[EnemyBoss.CENTER] >= 0) {
					MyGameView.mExp.add(new Explosion(x, y, Explosion.SMALL));
					MyGameView.score += 50;
					continue;
				}
				ClearAllEnemies();
				return;
			} // if
			
			// ������ ����
			if (Math.abs(x - x2) < w && Math.abs(y - y1) < h && 
					MyGameView.mBoss.shield[EnemyBoss.LEFT] > 0) {
				MyGameView.mBoss.shield[1] -= damage;
				MyGameView.mGun.remove(i);

				if (MyGameView.mBoss.shield[EnemyBoss.LEFT] > 0) {
					MyGameView.mExp.add(new Explosion(x, y, Explosion.SMALL));	
					MyGameView.score += 50;
					continue;
				}	
				
				// Boss ���� �ı�
				MyGameView.mExp.add(new Explosion(x2, y1, Explosion.BIG));
				MyGameView.score += 1000;		
				MyGameView.mGun.remove(i);
					
				if (MyGameView.mBoss.shield[EnemyBoss.RIGHT] > 0)
					MyGameView.mBoss.imgBoss = MyGameView.mBoss.imgSpt[EnemyBoss.RIGHT];
				else	
					MyGameView.mBoss.imgBoss = MyGameView.mBoss.imgSpt[EnemyBoss.CENTER];
				continue;
			} // if
			
			// ������ ������
			if (Math.abs(x - x3) < w && Math.abs(y - y1) < h && 
					MyGameView.mBoss.shield[EnemyBoss.RIGHT] > 0) {
				MyGameView.mBoss.shield[EnemyBoss.RIGHT] -= damage;
				MyGameView.mGun.remove(i);

				if (MyGameView.mBoss.shield[EnemyBoss.RIGHT] > 0) {
					MyGameView.mExp.add(new Explosion(x, y, Explosion.SMALL));	
					MyGameView.score += 50;
					continue;
				}	
				
				// Boss ������ �ı�
				MyGameView.mExp.add(new Explosion(x3, y1, Explosion.BIG));	
				MyGameView.score += 1000;		
				MyGameView.mGun.remove(i);
					
				if (MyGameView.mBoss.shield[EnemyBoss.LEFT] > 0)
					MyGameView.mBoss.imgBoss = MyGameView.mBoss.imgSpt[EnemyBoss.LEFT];
				else	
					MyGameView.mBoss.imgBoss = MyGameView.mBoss.imgSpt[EnemyBoss.CENTER];
			} // if
		} // for
	}	
	
	//----------------------------
	//  ��� �� �ı�
	//----------------------------
	private void ClearAllEnemies() {
		int x1, x2, x3, y1, w;			// Boss�� Center, Left, Right, ��

		w = MyGameView.mBoss.w / 2;
		x1 = MyGameView.mBoss.x;
		x2 = x1 - w;
		x3 = x1 + w;
		y1 = MyGameView.mBoss.y;
		
		// Boss �ı�
		MyGameView.mExp.add(new Explosion(x1, y1, Explosion.BOSS));
		MyGameView.score += 5000;
		
		// ���� �ı�
		if (MyGameView.mBoss.shield[EnemyBoss.LEFT] > 0) {
			MyGameView.mBoss.shield[EnemyBoss.LEFT] = 0;
			MyGameView.mExp.add(new Explosion(x2, y1, Explosion.BOSS));
		}
		
		// ������ �ı�
		if (MyGameView.mBoss.shield[EnemyBoss.RIGHT] > 0) {
			MyGameView.mBoss.shield[EnemyBoss.RIGHT] = 0;
			MyGameView.mExp.add(new Explosion(x3, y1, Explosion.BOSS));
		}

		// Boss Missile ��� ����
		for (BossMissile tmp : MyGameView.mBsMissile) {
			MyGameView.mExp.add(new Explosion(tmp.x, tmp.y, Explosion.BIG));
		}
		
		MyGameView.mBsMissile.clear();
		
		// ȭ�鿡 ���� �� ��� ����
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				if (MyGameView.mEnemy[i][j].shield > 0) {
					x1 = MyGameView.mEnemy[i][j].x;
					y1 = MyGameView.mEnemy[i][j].y;
					MyGameView.mExp.add(new Explosion(x1, y1, Explosion.BIG));
					MyGameView.mEnemy[i][j].shield = 0;
					MyGameView.mMap.enemyCnt--;					// ���� ���� ��
				}
			}
		} // for
		
		//Stage Clear�� Explosion���� ó����
	}
}
