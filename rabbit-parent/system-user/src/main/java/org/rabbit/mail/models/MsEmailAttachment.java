package org.rabbit.mail.models;

import com.alibaba.fastjson2.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class MsEmailAttachment {

    // @JSONField(name = "@odata.type")
    @SerializedName("@odata.type")
    public String odataType;

    public String contentBytes;

    public String contentType;

    public Boolean isInline;

    public String name;

    public static void main(String[] args) {
        MsEmailAttachment att = new MsEmailAttachment();
        att.setOdataType("#microsoft.graph.fileAttachment");
        att.setName("files.txt");
        att.setContentType("text/plain");
        System.out.println(JSON.toJSONString(att));
        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJson(att));
    }

}
