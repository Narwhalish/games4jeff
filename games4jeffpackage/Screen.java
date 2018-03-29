package games4jeffpackage;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class Screen extends MouseAdapter{

	private Handler handler;
	private Main main;
	private int mx;
	private int my;
	private int fireDelay = 25;
	private int shotSpeed = 5;
	private int damage = 5;
	private int magazine = 8;
	private int bullets = 8;
	private int reloadTime = 100;
	private int time2 = 0;
	private int time = 0;
	private Weapon weapon;
	private ArrayList <Weapon> weapons = new ArrayList <Weapon> ();
	private ArrayList <Integer> ammo = new ArrayList <Integer> ();
	private boolean firing = false;
	private boolean reloading = false;

	public Screen (Handler handler, Main main){
		this.handler = handler;
		this.main = main;
	}

	public void mousePressed(MouseEvent e){
		mx = e.getX();
		my = e.getY();

		if (mouseOver(mx, my, 0, 0, 800, 800)){
			firing = true;
		}
	}

	public void mouseReleased(MouseEvent e){
		mx = e.getX();
		my = e.getY();

		time = 0;
		firing = false;
	}


	private boolean mouseOver(int mx, int my, int x1, int y1, int x2, int y2){
		if(mx > x1 && mx < x2){
			if (my > y1 && my < y2){
				return true;
			}
			else return false;
		}
		else return false;
	}

	public void tick(){
		Point a = MouseInfo.getPointerInfo().getLocation();
		Point b = main.getLocationOnScreen();
		mx = (int) a.getX() - (int)b.getX();
		my = (int) a.getY() - (int)b.getY();
		if (time2 == 0){
			if (firing){
				if (time % fireDelay == 0){
					for(int i = 0; i < handler.stuff.size(); i++){
						GameThing thing = handler.stuff.get(i);
						if (thing.getId() == "Player"){
							float x = thing.getX() + thing.getWidth()/2;
							float y = thing.getY() + thing.getHeight()/2;
							float d = (float)Math.sqrt(Math.pow((mx-(int)x),2) + Math.pow((my-(int)y),2));
							if (d != 0){
								float sVelX = ((mx - (int)x)/d*shotSpeed);
								float sVelY = ((my - (int)y)/d*shotSpeed);
								Shot shot = new Shot((int)x-thing.getWidth()/4, (int)y-thing.getHeight()/4, "Shot", damage);
								shot.setVelX(sVelX);
								shot.setVelY(sVelY);
								handler.addObject(shot);
								bullets--;
								weapon.setAmmo(bullets);
								if (bullets <= 0){
									time2 = reloadTime;
									reloading = true;
								}
							}
						}
					}
				}
				time++;
			}
		}
		else {
			time2--;
			if (time2 == 0){
				reloading = false;
				bullets = magazine;
				weapon.setAmmo(magazine);
				time = 0;
			}
		}
	}

	public void addWeapon(Weapon weapon){
		if (!weapons.contains(weapon)) {
			weapons.add(weapon);
		}
		if (weapons.size() == 1){
			setWeapon(weapon);
		}
	}

	public void setWeapon(Weapon weapon){
		this.weapon = weapon;
		//System.out.println("Switched to " + weapon.getName());
		this.fireDelay = weapon.getFireDelay();
		this.shotSpeed = weapon.getShotSpeed();
		this.damage = weapon.getDamage();
		this.magazine = weapon.getMagazine();
		this.reloadTime = weapon.getReloadTime();
		this.bullets = weapon.getAmmo();
	}

	public void changeWeapon(){
		if (!reloading && weapons.size() > 1){
			for (int i=0; i<weapons.size(); i++){
				if (weapon.equals(weapons.get(i))){
					if (i < weapons.size()-1){
						setWeapon(weapons.get(i+1));
						break;
					}
					else {
						setWeapon(weapons.get(0));
						break;
					}
				}
			}
		}
	}

	public boolean hasWeapon(){
		if (weapon == null){
			return false;
		}
		return true;
	}

	public Weapon getWeapon(){
		return weapon;
	}

	public void render(Graphics g){
		g.setColor(new Color(112, 193, 179));
		g.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
		if (weapon != null) g.drawString("Weapon: " + weapon.getName(), 600, 700);
		else g.drawString("Weapon: None", 600, 700);
		g.drawString("Bullets: " + bullets, 600, 725);
		if (reloading) g.drawString("Reloading...", 25, 25);
	}
}
