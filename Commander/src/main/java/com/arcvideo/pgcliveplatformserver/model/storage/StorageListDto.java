package com.arcvideo.pgcliveplatformserver.model.storage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "storages")
@XmlAccessorType(XmlAccessType.FIELD)
public class StorageListDto {

    @XmlElement(name = "storage")
    private List<StorageDto> storages;

    public List<StorageDto> getStorages() {
        return storages;
    }

    public void setStorages(List<StorageDto> storage) {
        this.storages = storage;
    }
}
