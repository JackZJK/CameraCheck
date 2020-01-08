package com.sudiyi.apps.cameracheck;

public enum CameraStatusEnum {
    CAMERA_EXIST("True"), CAMERA_NOT_EXIST("False"), CAMERA_OK("True"), CAMERA_NOT_OK("False");

    private String cameraStatus;

    /**
     * 构造函数，枚举类型只能为私有
     */
    CameraStatusEnum(String value) {
        this.cameraStatus = value;
    }

    //自定义方法
    public String getValue() {
        return cameraStatus;
    }
}
