package cn.chonor.project_1;


/**
 * Created by ASUS on 2017/10/31.
 */

public class GwentCard {
    private String name=new String();
    private Integer img;//存放该张昆特牌对应图片
    private int power;//存放该张昆特牌基础点数
    public int now_power;//存放该张昆特牌经过加成/削弱后的点数
    private String type;//存放该张昆特牌特殊属性，如间谍、医生、天气卡、诱饵等
    private boolean isHero;//该张昆特牌是否为金卡，金卡不受其他卡效果影响
    private int col;//该张昆特牌所在列数，1/2/3->近战列/远程列/攻城列，0可以进入所有列，4可以进入1/2列
    private int id;//该种昆特牌的ID，同袍之情/种族召唤
    public boolean isDouble;//是否经过号角的翻倍
    String property;//卡牌属性
    public GwentCard(Integer img,int power,String type,boolean isHero,int col,int id,String name,String property){
        this.img=img;
        this.power=power;
        this.now_power=power;
        this.type=type;
        this.isHero=isHero;
        this.col=col;
        this.id=id;
        this.name=name;
        this.isDouble=false;
        this.property=property;
    }
    public Integer getImg(){
        return this.img;
    }
    public int getPower(){
        return this.power;
    }
    public String getType(){
        return this.type;
    }
    public boolean getisHero(){return this.isHero;}
    public int getCol(){return this.col;}
    public int getId(){return this.id;}
    public String getProperty(){return this.property;}
    public String getName(){return this.name;}
    public void setCol(int col){
        this.col=col;
    }
}

