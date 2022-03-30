package cn.mcfun.name;

import java.util.HashMap;
import java.util.Map;

public class ItemName {
    public ItemName() {
    }

    public String getItemName(String id) {
        Map<String, String> itemName = new HashMap();
        itemName.put("6503", "英雄之证");
        itemName.put("6516", "凶骨");
        itemName.put("6512", "龙牙");
        itemName.put("6505", "虚影之尘");
        itemName.put("6522", "愚者之锁");
        itemName.put("6527", "万死的毒针");
        itemName.put("6530", "魔术髓液");
        itemName.put("6533", "宵泣之铁桩");
        itemName.put("6534", "振荡火药");
        itemName.put("6502", "世界树之种");
        itemName.put("6508", "鬼魂提灯");
        itemName.put("6515", "八连双晶");
        itemName.put("6509", "蛇之宝玉");
        itemName.put("6501", "凤凰羽毛");
        itemName.put("6510", "无间齿轮");
        itemName.put("6511", "禁断书页");
        itemName.put("6514", "人工生命体幼体");
        itemName.put("6513", "陨蹄铁");
        itemName.put("6524", "大骑士勋章");
        itemName.put("6526", "追忆的贝壳");
        itemName.put("6532", "枯淡勾玉");
        itemName.put("6535", "永远结冰");
        itemName.put("6537", "巨人的戒指");
        itemName.put("6536", "极光之钢");
        itemName.put("6538", "闲古铃");
        itemName.put("6541", "祸罪之箭头");
        itemName.put("6543", "光银之冠");
        itemName.put("6545", "神脉灵子");
        itemName.put("6507", "混沌之爪");
        itemName.put("6517", "蛮神心脏");
        itemName.put("6506", "龙之逆鳞");
        itemName.put("6518", "精灵根");
        itemName.put("6519", "战马的幼角");
        itemName.put("6520", "血之泪石");
        itemName.put("6521", "黑兽脂");
        itemName.put("6523", "封魔之灯");
        itemName.put("6525", "智慧之圣甲虫像");
        itemName.put("6528", "起源的胎毛");
        itemName.put("6529", "咒兽胆石");
        itemName.put("6531", "奇奇神酒");
        itemName.put("6539", "晓光炉心");
        itemName.put("6540", "九十九镜");
        itemName.put("6542", "真理之卵");
        itemName.put("6544", "煌星碎片");
        itemName.put("6546", "悠久果实");
        itemName.put("6001", "剑之辉石");
        itemName.put("6002", "弓之辉石");
        itemName.put("6003", "枪之辉石");
        itemName.put("6004", "骑之辉石");
        itemName.put("6005", "术之辉石");
        itemName.put("6006", "杀之辉石");
        itemName.put("6007", "狂之辉石");
        itemName.put("6101", "剑之魔石");
        itemName.put("6102", "弓之魔石");
        itemName.put("6103", "枪之魔石");
        itemName.put("6104", "骑之魔石");
        itemName.put("6105", "术之魔石");
        itemName.put("6106", "杀之魔石");
        itemName.put("6107", "狂之魔石");
        itemName.put("6201", "剑之秘石");
        itemName.put("6202", "弓之秘石");
        itemName.put("6203", "枪之秘石");
        itemName.put("6204", "骑之秘石");
        itemName.put("6205", "术之秘石");
        itemName.put("6206", "杀之秘石");
        itemName.put("6207", "狂之秘石");
        itemName.put("7001", "剑阶银棋");
        itemName.put("7002", "弓阶银棋");
        itemName.put("7003", "枪阶银棋");
        itemName.put("7004", "骑阶银棋");
        itemName.put("7005", "术阶银棋");
        itemName.put("7006", "杀阶银棋");
        itemName.put("7007", "狂阶银棋");
        itemName.put("7101", "剑阶金像");
        itemName.put("7102", "弓阶金像");
        itemName.put("7103", "枪阶金像");
        itemName.put("7104", "骑阶金像");
        itemName.put("7105", "术阶金像");
        itemName.put("7106", "杀阶金像");
        itemName.put("7107", "狂阶金像");
        return itemName.containsKey(id) ? (String)itemName.get(id) : null;
    }
}
