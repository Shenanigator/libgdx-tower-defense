package gamedev.entity;


public class Tower {
	private int damage, x, y;
	private float attackRange, attackRate,
		attackTimer;
	
	public Tower(int damage, float attackRange, float attackRate) {
		this.damage = damage;
		this.attackRange = attackRange;
		this.attackRate = attackRate;
		x = -50;
		y = -50;
		attackTimer = 0;
	}

	public int getDamage() {
		return damage;
	}

	public float getAttackRange() {
		return attackRange;
	}

	public float getAttackRate() {
		return attackRate;
	}

	public float getAttackTimer() {
		return attackTimer;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
