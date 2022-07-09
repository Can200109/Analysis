package com.example.analysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.analysis.entity.DYVideo;
import com.example.analysis.entity.VideoUrl;
import com.example.analysis.entity.XHSPhotoAndText;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.analysis.utill.AUtill.*;


@CrossOrigin
@RestController
@RequestMapping("/analysis")
public class Analysis {

    @RequestMapping(value = "/dy",method = RequestMethod.POST)
    public static DYVideo parseDouYinVideo(@RequestBody VideoUrl videoUrl) throws Exception {
        DYVideo dyVideo = new DYVideo();
        String Url = videoUrl.getUrl();
        // 获取重定向后的URL地址，并从中截取item_ids
        String itemIDs = getRedirectsURL(Url).split("/")[5];
        // 使用item_ids作为参数，请求携带有带水印的视频URL的JSON数据
        String jsonString = getURLData("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + itemIDs);
        // 使用FastJSON解析为JSONObject对象
        JSONObject json = JSON.parseObject(jsonString);
        // 解析JSON数据，获取带水印的视频URL
        Object videoURL = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("video")
                .getJSONObject("play_addr").getJSONArray("url_list").get(0);


        // 封面链接
        Object coverURL = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("video")
                .getJSONObject("origin_cover").getJSONArray("url_list").get(0);

        //标题链接
        Object titleUrl = json.getJSONArray("item_list").getJSONObject(0)
                .get("desc");
        //音乐链接
        Object musicUrl = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("music")
                .getJSONObject("play_url").getJSONArray("url_list").get(0);
        //作者
        Object author = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("author")
                .get("nickname");
        //图片
        JSONArray images = json.getJSONArray("item_list").getJSONObject(0).getJSONArray("images");
        while (!(images==null)){
            List<String> imgUrls = new ArrayList<>();
            for (int i=0;i<images.size();i++){
                imgUrls.add(images.getJSONObject(i).getJSONArray("url_list").get(0).toString());
                dyVideo.setImgUrls(imgUrls);
            }
            break;
        }


        String url =videoURL.toString().replace("/playwm/", "/play/");
        dyVideo.setVideoUrl(url);
        dyVideo.setCoverUrl(coverURL.toString());
        dyVideo.setMusicUrl(musicUrl.toString());
        dyVideo.setTitleUrl(titleUrl.toString());
        dyVideo.setAuthor(author.toString());

        System.out.println(dyVideo.toString());
        return dyVideo;
    }

    @RequestMapping("/xhs")
    public XHSPhotoAndText paresXiaoHongShu(@RequestBody VideoUrl videoUrl)throws Exception{
        XHSPhotoAndText xhsPhotoAndText = new XHSPhotoAndText();
        int index = getRedirectsURL(videoUrl.getUrl()).indexOf("?share_from_user_hidden");
        String url = getRedirectsURL(videoUrl.getUrl()).substring(0,index);
        if (StringUtils.isBlank(url)) {
            xhsPhotoAndText.setMsg("小红书地址为空");
            return xhsPhotoAndText;
        }
        if (!url.contains("https://www.xiaohongshu.com") && !url.contains("http://xhslink.com")) {
            xhsPhotoAndText.setMsg("小红书地址不正确");
            return xhsPhotoAndText;
        }
        Connection connection = Jsoup.connect(url);
        Connection data = connection.headers(getHeaderMap());
        Document doc = data.get();

        //获取tag是title的所有dom文档
        //标题
        Elements elements = doc.getElementsByTag("title");
        //获取第一个元素
        Element element = elements.get(0);
        //.html是返回html
        String title = element.text();
        xhsPhotoAndText.setTitleUrl(title);
        //图片
        Elements elements1 = doc.select("img[class=note-image]");

        List<String> imgs = new ArrayList<>();
        for (Element element1 : elements1) {
            String imageUrl = "https:" + element1.attr("src");
            imgs.add(imageUrl);
        }
        Elements elements2 = doc.select("main div[class=content] p");
        List<String> texts = new ArrayList<>();
        for (Element element2 : elements2) {
            texts.add(element2.text());
        }
        Elements elements3 = doc.select("div[class=nickname] a");
        xhsPhotoAndText.setAuthor(elements3.text());
        xhsPhotoAndText.setImgUrls(imgs);
        xhsPhotoAndText.setTextUrls(texts);
        return xhsPhotoAndText;
    }
}

