package com.zhangxin.gpt.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.Map;

/**
 * @author zhangxin
 * @date 2023-06-07 11:32
 */
@XStreamAlias("xml")
public class ImageMsg  extends BaseMsg{

    @XStreamAlias("Image")
    private Image image;

    public ImageMsg(Map<String, String> requestMap, String mediaId) {
        super(requestMap);
        this.setMsgType("image");
        this.image = new Image(mediaId);
    }

    @Data
    @XStreamAlias("Image")
    public static class Image {
        @XStreamAlias("MediaId")
        private String mediaId;

        public Image(String mediaId) {
            this.mediaId = mediaId;
        }
    }


}
