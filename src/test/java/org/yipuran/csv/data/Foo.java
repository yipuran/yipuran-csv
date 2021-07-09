package org.yipuran.csv.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Foo {
	private int i;
	private Integer ii;
	private long l;
	private Long ll;
	private double d;
	private Double dd;
	private float f;
	private Float ff;
	private short t;
	private Short tt;
	private boolean flg;
	private Boolean flgB;
	private String info;
	private LocalDate date;
	private LocalDateTime datetime;
	private LocalTime time;
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public Integer getIi() {
		return ii;
	}
	public void setIi(Integer ii) {
		this.ii = ii;
	}
	public long getL() {
		return l;
	}
	public void setL(long l) {
		this.l = l;
	}
	public Long getLl() {
		return ll;
	}
	public void setLl(Long ll) {
		this.ll = ll;
	}
	public double getD() {
		return d;
	}
	public void setD(double d) {
		this.d = d;
	}
	public Double getDd() {
		return dd;
	}
	public void setDd(Double dd) {
		this.dd = dd;
	}
	public float getF() {
		return f;
	}
	public void setF(float f) {
		this.f = f;
	}
	public Float getFf() {
		return ff;
	}
	public void setFf(Float ff) {
		this.ff = ff;
	}
	public short getT() {
		return t;
	}
	public void setT(short t) {
		this.t = t;
	}
	public Short getTt() {
		return tt;
	}
	public void setTt(Short tt) {
		this.tt = tt;
	}
	public boolean isFlg() {
		return flg;
	}
	public void setFlg(boolean flg) {
		this.flg = flg;
	}
	public Boolean getFlgB() {
		return flgB;
	}
	public void setFlgB(Boolean flgB) {
		this.flgB = flgB;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalDateTime getDatetime() {
		return datetime;
	}
	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("i="); sb.append(i); sb.append(",ii="); sb.append(ii);
		sb.append(",l="); sb.append(l); sb.append(",ll="); sb.append(ll);
		sb.append(",d="); sb.append(d); sb.append(",dd="); sb.append(dd);
		sb.append(",f="); sb.append(f); sb.append(",ff="); sb.append(ff);
		sb.append(",t="); sb.append(t); sb.append(",tt="); sb.append(tt);
		sb.append(",flg="); sb.append(flg); sb.append(",flgB="); sb.append(flgB);
		sb.append(",info="); sb.append(info);
		sb.append(",date="); sb.append(date);
		sb.append(",datetime="); sb.append(datetime);
		sb.append(",time="); sb.append(time);
		sb.append(")");
		return sb.toString();
	}
}
