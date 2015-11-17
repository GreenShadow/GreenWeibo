package com.greenshadow.openweibo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FaceUtil {
	private static Map<String, String> faceMap;
	private static ArrayList<String> faceList;

	// face
	static {
		faceMap = new HashMap<String, String>();
		faceMap.put("[囧]", "jiong");
		faceMap.put("[萌]", "meng");
		faceMap.put("[织]", "zhi");
		faceMap.put("[礼物]", "liwu");
		faceMap.put("[喜]", "xi");
		faceMap.put("[围脖]", "weibo");
		faceMap.put("[音乐]", "yinyue");
		faceMap.put("[绿丝带]", "lvsidai");
		faceMap.put("[蛋糕]", "dangao");
		faceMap.put("[蜡烛]", "lazhu");
		faceMap.put("[干杯]", "ganbei");
		faceMap.put("[男孩儿]", "nanhaier");
		faceMap.put("[女孩儿]", "nvhaier");
		faceMap.put("[肥皂]", "feizao");
		faceMap.put("[浪]", "lang");
		faceMap.put("[伤心]", "shangxin");
		faceMap.put("[猪头]", "zhutou");
		faceMap.put("[熊猫]", "xiongmao");
		faceMap.put("[NO]", "NO");
		faceMap.put("[兔子]", "tuzi");
		faceMap.put("[握手]", "woshou");
		faceMap.put("[作揖]", "zuoyi");
		faceMap.put("[赞]", "zan");
		faceMap.put("[耶]", "ye");
		faceMap.put("[good]", "good");
		faceMap.put("[ok]", "ok");
		faceMap.put("[来]", "lai");
		faceMap.put("[拳头]", "quantou");
		faceMap.put("[威武]", "weiwu");
		faceMap.put("[鲜花]", "xianhua");
		faceMap.put("[钟]", "zhong");
		faceMap.put("[浮云]", "fuyun");
		faceMap.put("[飞机]", "feiji");
		faceMap.put("[月亮]", "yueliang");
		faceMap.put("[太阳]", "taiyang");
		faceMap.put("[微风]", "weifeng");
		faceMap.put("[下雨]", "xiayu");
		faceMap.put("[给力]", "geili");
		faceMap.put("[神马]", "shenma");
		faceMap.put("[围观]", "weiguan");
		faceMap.put("[话筒]", "huatong");
		faceMap.put("[奥特曼]", "aoteman");
		faceMap.put("[草泥马]", "caonima");
		faceMap.put("[带着微博去旅行]", "daizheweiboqulvxing");
		faceMap.put("[歪果仁夏克立]", "waiguorenxiakeli");
		faceMap.put("[陆毅]", "luyi");
		faceMap.put("[哆啦A梦微笑]", "duolaamengweixiao");
		faceMap.put("[哆啦A梦花心]", "duolaamenghuaxin");
		faceMap.put("[微笑]", "weixiao");
		faceMap.put("[嘻嘻]", "xixi");
		faceMap.put("[哈哈]", "haha");
		faceMap.put("[爱你]", "aini");
		faceMap.put("[挖鼻]", "wabi");
		faceMap.put("[吃惊]", "chijing");
		faceMap.put("[晕]", "yun");
		faceMap.put("[泪]", "lei");
		faceMap.put("[馋嘴]", "chanzui");
		faceMap.put("[抓狂]", "zhuakuang");
		faceMap.put("[哼]", "heng");
		faceMap.put("[可爱]", "keai");
		faceMap.put("[怒]", "nu");
		faceMap.put("[汗]", "han");
		faceMap.put("[害羞]", "haixiu");
		faceMap.put("[睡]", "shui");
		faceMap.put("[钱]", "qian");
		faceMap.put("[偷笑]", "touxiao");
		faceMap.put("[笑cry]", "xiaocry");
		faceMap.put("[doge]", "doge");
		faceMap.put("[喵喵]", "miaomiao");
		faceMap.put("[酷]", "ku");
		faceMap.put("[衰]", "shuai");
		faceMap.put("[闭嘴]", "bizui");
		faceMap.put("[鄙视]", "bishi");
		faceMap.put("[色]", "se");
		faceMap.put("[鼓掌]", "guzhang");
		faceMap.put("[悲伤]", "beishang");
		faceMap.put("[思考]", "sikao");
		faceMap.put("[生病]", "shengbing");
		faceMap.put("[亲亲]", "qinqin");
		faceMap.put("[怒骂]", "numa");
		faceMap.put("[太开心]", "taikaixin");
		faceMap.put("[右哼哼]", "youhengheng");
		faceMap.put("[傲慢]", "aoman");
		faceMap.put("[拜拜]", "baibai");
		faceMap.put("[打脸]", "dalian");
		faceMap.put("[顶]", "ding");
		faceMap.put("[感冒]", "ganmao");
		faceMap.put("[哈欠]", "haqian");
		faceMap.put("[黑线]", "heixian");
		faceMap.put("[互粉]", "hufen");
		faceMap.put("[挤眼]", "jiyan");
		faceMap.put("[可怜]", "kelian");
		faceMap.put("[困]", "kun");
		faceMap.put("[沙尘暴]", "shachenbao");
		faceMap.put("[傻眼]", "shayan");
		faceMap.put("[吐]", "tu");
		faceMap.put("[委屈]", "weiqu");
		faceMap.put("[心]", "xin");
		faceMap.put("[嘘]", "xu");
		faceMap.put("[阴险]", "yinxian");
		faceMap.put("[疑问]", "yiwen");
		faceMap.put("[失望]", "shiwang");
		faceMap.put("[左哼哼]", "zuohengheng");

		faceList = new ArrayList<String>();
		for (String key : faceMap.keySet())
			faceList.add(key);
	}

	public static Map<String, String> getFaceMap() {
		return faceMap;
	}

	public static ArrayList<String> getFaceList() {
		return faceList;
	}
}
