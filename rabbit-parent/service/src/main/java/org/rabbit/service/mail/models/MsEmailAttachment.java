package org.rabbit.service.mail.models;

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

}
